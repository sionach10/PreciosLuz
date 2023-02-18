package com.precioLuz.fragments;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.precioLuz.R;
import com.precioLuz.adapters.PricesAdapter;
import com.precioLuz.models.PreciosJSON;
import com.precioLuz.utils.JsonPricesParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PricesFragment extends Fragment {
    public PricesFragment() {
        //Required empty public constructor
    }

    //Variables globales
    View mView;
    Toolbar mToolbar;
    ListView listaItemsPrecios;
    TextView fecha;
    List<PreciosJSON> mList = new ArrayList<>();
    PricesAdapter mAdapter;
    ImageButton btnCalendar;

    private final Calendar mCalendar = Calendar.getInstance();
    private final int day = mCalendar.get(Calendar.DAY_OF_MONTH);
    private final int month = mCalendar.get(Calendar.MONTH); //Los meses los devuelve de 0 a 11.
    private final int year = mCalendar.get(Calendar.YEAR);
    String [] fechasBusqueda = new String[2];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        mView = inflater.inflate(R.layout.fragment_prices, container, false);
        mToolbar = mView.findViewById(R.id.toolbar);
        listaItemsPrecios= mView.findViewById(R.id.listaItemsPrecios);
        fecha = mView.findViewById(R.id.date);
        btnCalendar = mView.findViewById(R.id.btnCalendar);

        return mView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        leerWS();
        //leerWSESIOS();

        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int _year, int _month, int _dayOfMonth) {
                        try {
                            fechasBusqueda = obtenerFechasFromDatePicker(_dayOfMonth, _month+1, _year);//El datePicker usa los meses de 0 a 11.

                            leerWSESIOS(fechasBusqueda);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, year, month, day); //Variables para inicializar el calendario en el día de hoy.
                datePickerDialog.show();
            }
        });
    }

    private String[] obtenerFechasFromDatePicker(int dayOfMonth, int month, int year) throws ParseException {

        String [] _fechasBusqueda = new String[2];

        String _date = String.valueOf(dayOfMonth) + "/" + String.valueOf(month) + "/" + String.valueOf(year);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Calendar calendar = Calendar.getInstance();
        try{
            calendar.setTime(sdf.parse(_date));
            _date = sdf.format(calendar.getTime());
        }
        catch (ParseException e){
            e.printStackTrace();
        }
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        String _dateNext = sdf.format(calendar.getTime());

        _fechasBusqueda[0] = _date;
        _fechasBusqueda[1] = _dateNext;
        return _fechasBusqueda;
    }


    private void leerWS() {
        String url = "https://api.preciodelaluz.org/v1/prices/all?zone=PCB";
        StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);//Recibimos el JSON.

                    //Lo parseamos a un objeto del tipo PreciosJSON.
                    PreciosJSON[] preciosJSON = JsonPricesParser.obtenerJsonHoras(jsonObject);

                    //Añadimos los preciosJSON a una lista.
                    mList.addAll(Arrays.asList(preciosJSON));

                    //Modificamos campo fecha.
                    fecha.setText(preciosJSON[0].getDate());

                    //Creamos el adaptador con los datos de la lista.
                    mAdapter = new PricesAdapter(requireContext(),R.layout.item_lista_precios, mList);

                    listaItemsPrecios.setAdapter(mAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(requireContext()).add(postRequest);
    }

    private void leerWSESIOS(String [] _fechasBusqueda) {

        String url = "https://api.esios.ree.es/indicators/10391?geo_ids[]=8741&start_date="+_fechasBusqueda[0]+"&end_date="+_fechasBusqueda[1];
        StringRequest getRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);//Recibimos el JSON.

                    //Lo parseamos a un objeto del tipo PreciosJSON.
                    PreciosJSON[] preciosJSON = JsonPricesParser.obtenerJsonHoras(jsonObject);

                    //Añadimos los preciosJSON a una lista.
                    mList.addAll(Arrays.asList(preciosJSON));

                    //Modificamos campo fecha.
                    fecha.setText(preciosJSON[0].getDate());

                    //Creamos el adaptador con los datos de la lista.
                    mAdapter = new PricesAdapter(requireContext(),R.layout.item_lista_precios, mList);

                    listaItemsPrecios.setAdapter(mAdapter);

                } catch (JSONException e) {
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
                headers.put("Authorization", "Token token=\"f8b7eac4ecd150b5e83f2e2f8c218064c8afb9e33eadcd077eef7b243f69bd46\"");
                return headers;
            }
        };
        Volley.newRequestQueue(requireContext()).add(getRequest);
    }


}
