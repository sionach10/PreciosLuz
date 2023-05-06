package com.precioLuz.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.precioLuz.R;
import com.precioLuz.activities.MainActivity;
import com.precioLuz.models.EnergyByTechnology;
import com.precioLuz.providers.AuthProvider;
import com.precioLuz.providers.CalendarDatePickerProvider;
import com.precioLuz.utils.AreaChart;
import com.precioLuz.utils.PieChartUtility;
import com.precioLuz.utils.TxtParser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class ChartsFragment extends Fragment {

    View mView;
    AuthProvider mAuthProvider;
    private final Calendar mCalendar = Calendar.getInstance();
    private int startDay = mCalendar.get(Calendar.DAY_OF_MONTH);
    private int startMonth = mCalendar.get(Calendar.MONTH); //Los meses los devuelve de 0 a 11.
    private int startYear = mCalendar.get(Calendar.YEAR);
    TextView fecha;
    SwitchMaterial switchCharts;
    boolean switchIsChecked;
    String [] fechasBusqueda = new String[2];
    String [] fechasDefault = new String[2];
    String [] fechaBusqueda_recibida = new String[2];
    SpotsDialog mDialog; //Cargando
    EnergyByTechnology energyByTechnology;


    public ChartsFragment() {
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
        this.getActivity().setTitle("Energía por tecnología");
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
            case R.id.itemCalendar:
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int _year, int _month, int _dayOfMonth) {
                        try {
                            fechasBusqueda = CalendarDatePickerProvider.obtenerFechasFromDatePicker(_dayOfMonth, _month+1, _year);//El datePicker usa los meses de 0 a 11.

                            //Modificamos campo fecha.
                            fecha.setText(fechasBusqueda[0]);
                            leerTxtFromWeb(fechasBusqueda);

                        } catch (ParseException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, startYear, startMonth, startDay); //Variables para inicializar el calendario en el día de hoy.+
                datePickerDialog.getDatePicker().setMaxDate(mCalendar.getTimeInMillis());//bloqueamos que no hagan clic en fechas futuras.
                datePickerDialog.show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_charts, container, false);
        mDialog = new SpotsDialog(getContext());
        fecha = mView.findViewById(R.id.dateCharts);
        switchCharts = mView.findViewById(R.id.switchCharts);
        switchCharts.setChecked(false);

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
        mDialog.show();
        mDialog.setMessage("Cargando");

        try {
            if(fechaBusqueda_recibida[0] != null) {
                startDay = Integer.parseInt(fechaBusqueda_recibida[0].substring(0,2));
                startMonth = Integer.parseInt(fechaBusqueda_recibida[0].substring(3,5))-1; //Ya que van de 0 a 11.
                startYear = Integer.parseInt(fechaBusqueda_recibida[0].substring(6,10));
            }

            fechasDefault = CalendarDatePickerProvider.obtenerFechasFromDatePicker(startDay, startMonth+1, startYear);

            fecha.setText(fechasDefault[0]);
            leerTxtFromWeb(fechasDefault);
            mDialog.dismiss();

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        switchCharts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    switchIsChecked = true;
                    PieChartUtility.crearGrafico(mView, energyByTechnology);
                }
                else {
                    switchIsChecked = false;
                    AreaChart.crearGrafico(mView, energyByTechnology);
                }
            }
        });
    }

    public void leerTxtFromWeb(String [] _fechasBusqueda) throws IOException {

        //Replace format
        String fecha = _fechasBusqueda[0].replace("/", "_");

        String url = "https://www.omie.es/sites/default/files/dados/AGNO_"+fecha.substring(6,10)+"/MES_"+fecha.substring(3,5)+"/TXT/INT_PBC_TECNOLOGIAS_H_9_"+fecha+"_"+fecha+".TXT";

        StringRequest getRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(String response) {

                //Lo parseamos a un objeto del tipo EnergyByTechnology.
                try {
                    energyByTechnology = TxtParser.obtenerSeriesPorTecnologia(response);

                    if(switchIsChecked) {
                        PieChartUtility.crearGrafico(mView, energyByTechnology);
                    }
                    else {
                        AreaChart.crearGrafico(mView, energyByTechnology);
                    }

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