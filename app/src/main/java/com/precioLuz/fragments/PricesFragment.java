package com.precioLuz.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.precioLuz.R;
import com.precioLuz.adapters.PricesAdapter;
import com.precioLuz.models.PreciosJSON;
import com.precioLuz.providers.CalendarDatePickerProvider;
import com.precioLuz.utils.JsonPricesParser;
import com.precioLuz.utils.LineChart;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;


public class PricesFragment extends Fragment {

    //Variables
    private View mView;
    private RecyclerView rvListaPrecios;
    private PricesAdapter pricesAdapter;
    private List<PreciosJSON> priceList = new ArrayList<>();
    com.github.mikephil.charting.charts.LineChart linePricesChart;
    TextView fecha;
    SwitchMaterial switchEnergia;
    SwitchMaterial switchGrafica;
    SpotsDialog mDialog; //Cargando
    private final Calendar mCalendar = Calendar.getInstance();
    private final int startDay = mCalendar.get(Calendar.DAY_OF_MONTH);
    private final int startMonth = mCalendar.get(Calendar.MONTH); //Los meses los devuelve de 0 a 11.
    private final int startYear = mCalendar.get(Calendar.YEAR);
    String [] fechasBusqueda = new String[2];
    String [] fechasDefault = new String[2];
    private AdView mAdView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Inicializamos variables
        mView = inflater.inflate(R.layout.fragment_prices, container, false);
        rvListaPrecios = mView.findViewById(R.id.listaItemsPrecios);
        rvListaPrecios.setLayoutManager(new LinearLayoutManager(getContext()));
        linePricesChart = mView.findViewById(R.id.linePricesChart);
        fecha = mView.findViewById(R.id.date);
        switchEnergia = mView.findViewById(R.id.switchEnergia);
        switchGrafica = mView.findViewById(R.id.switchGrafica);
        mDialog = new SpotsDialog(getContext());
        mDialog.show();
        mDialog.setMessage("Cargando");

        //Banner anuncio
        mAdView = mView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //Inicializamos las fechas de hoy y mañana por defecto.
        try {
            fechasDefault = CalendarDatePickerProvider.obtenerFechasFromDatePicker(startDay, startMonth+1, startYear);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Lectura y carga de datos:
        leerWSESIOS(fechasDefault);

        switchEnergia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    switchEnergia.setText("MWh");
                    //Creamos el adaptador con los datos de la lista.
                    pricesAdapter = new PricesAdapter(priceList, true);

                    //Vaciamos la lista al cambiar de día:
                    rvListaPrecios.setAdapter(null);

                    //Cargamos los datos del adapter a la vista.
                    rvListaPrecios.setAdapter(pricesAdapter);
                }
                else{
                    switchEnergia.setText("KWh");
                    //Creamos el adaptador con los datos de la lista.
                    pricesAdapter = new PricesAdapter(priceList, false);

                    //Vaciamos la lista al cambiar de día:
                    rvListaPrecios.setAdapter(null);

                    //Cargamos los datos del adapter a la vista.
                    rvListaPrecios.setAdapter(pricesAdapter);
                }
            }
        });

        pricesAdapter = new PricesAdapter(priceList, switchGrafica.isChecked());
        rvListaPrecios.setAdapter(pricesAdapter);

        return mView;
    }

    private void leerWSESIOS(String [] _fechasBusqueda) {

        String url = "https://api.esios.ree.es/indicators/10391?geo_ids[]=8741&start_date="+_fechasBusqueda[0]+"&end_date="+_fechasBusqueda[1];
        StringRequest getRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);//Recibimos el JSON.

                    //Lo parseamos a un objeto del tipo PreciosJSON.
                    PreciosJSON[] preciosJSON = JsonPricesParser.obtenerJsonHoras(jsonObject, _fechasBusqueda);

                    //Añadimos los preciosJSON a una lista, previamente borrada.
                    priceList.clear();
                    priceList.addAll(Arrays.asList(preciosJSON));

                    if(switchGrafica.isChecked()) {//Grafica
                        //switchEnergia.setVisibility(View.INVISIBLE);
                        //LineChart.crearGrafico(mView, rvListaPrecios);
                    }
                    else {//Lista

                        //Creamos el adaptador con los datos de la lista.
                        pricesAdapter = new PricesAdapter(priceList, switchEnergia.isChecked());

                        //Vaciamos la lista al cambiar de día:
                        rvListaPrecios.setAdapter(null);

                        //Cargamos los datos del adapter a la vista.
                        rvListaPrecios.setAdapter(pricesAdapter);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mDialog.dismiss();
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
