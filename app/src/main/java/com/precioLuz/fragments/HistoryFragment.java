package com.precioLuz.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.precioLuz.R;
import com.precioLuz.activities.MainActivity;
import com.precioLuz.models.EnergyByTechnology;
import com.precioLuz.providers.AuthProvider;
import com.precioLuz.providers.CalendarDatePickerProvider;
import com.precioLuz.utils.AreaChart;
import com.precioLuz.utils.TxtParser;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HistoryFragment extends Fragment {

    View mView;
    AuthProvider mAuthProvider;
    private final Calendar mCalendar = Calendar.getInstance();
    private final int startDay = mCalendar.get(Calendar.DAY_OF_MONTH);
    private final int startMonth = mCalendar.get(Calendar.MONTH); //Los meses los devuelve de 0 a 11.
    private final int startYear = mCalendar.get(Calendar.YEAR);
    String [] fechasBusqueda = new String[2];
    String [] fechasDefault = new String[2];

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mAuthProvider = new AuthProvider();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.itemCalendar).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.itemLogout:
                mAuthProvider.logout();
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); //Limpiamos historial del botón atras.
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_history, container, false);

        //Inicializamos las fechas de hoy y mañana por defecto.
        try {
            fechasDefault = CalendarDatePickerProvider.obtenerFechasFromDatePicker(startDay, startMonth+1, startYear);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            leerTxtFromWeb(fechasDefault);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void leerTxtFromWeb(String [] _fechasBusqueda) throws IOException {

        //Replace format
        String fecha = _fechasBusqueda[0].replace("/", "_");

        String url = "https://www.omie.es/sites/default/files/dados/AGNO_"+fecha.substring(6,10)+"/MES_"+fecha.substring(3,5)+"/TXT/INT_PBC_TECNOLOGIAS_H_9_"+fecha+"_"+fecha+".TXT";

        StringRequest getRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Lo parseamos a un objeto del tipo EnergyByTechnology.
                try {
                    EnergyByTechnology energyByTechnology = TxtParser.obtenerSeriesPorTecnologia(response);
                    //Todo: Pintar la grafica con los datos de energyByTechnlogy.
                    //Gráfico:
                    AreaChart.crearGrafico(mView, energyByTechnology);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ResponseError: ", String.valueOf(error));
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("x-api-key", "f8b7eac4ecd150b5e83f2e2f8c218064c8afb9e33eadcd077eef7b243f69bd46");
                return headers;
            }
        };
        Volley.newRequestQueue(requireContext()).add(getRequest);
    }
}