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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.type.Date;
import com.precioLuz.R;
import com.precioLuz.adapters.PricesAdapter;
import com.precioLuz.models.PreciosJSON;
import com.precioLuz.utils.JsonPricesParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;


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

    private Calendar mCalendar = Calendar.getInstance();
    private int day = mCalendar.get(Calendar.DAY_OF_MONTH);
    private int month = mCalendar.get(Calendar.MONTH);
    private int year = mCalendar.get(Calendar.YEAR);


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

        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        day = dayOfMonth;
                        Log.w("Estoy aqui: ", String.valueOf(day));
                        //Meter leerWS con la nueva fecha.
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        leerWS();

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


}
