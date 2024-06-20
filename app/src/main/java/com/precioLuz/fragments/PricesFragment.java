package com.precioLuz.fragments;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.MenuProvider;
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
import com.precioLuz.models.RespuestaESIOS;
import com.precioLuz.providers.CalendarDatePickerProvider;
import com.precioLuz.utils.JsonPricesParser;
import com.precioLuz.utils.LineChart;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    TextView fecha, precioMedio, horaValle, horaPunta, precioValle, precioPunta;
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
    private RespuestaESIOS respuestaESIOSCardView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Indica que este fragmento tiene un menú de opciones
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        this.getActivity().setTitle("Precio horario");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemCalendar:
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int _year, int _month, int _dayOfMonth) {
                        try {
                            fechasBusqueda = CalendarDatePickerProvider.obtenerFechasFromDatePicker(_dayOfMonth, _month + 1, _year); //El datePicker usa los meses de 0 a 11.
                            fecha.setText(fechasBusqueda[0]);
                            leerWSESIOS(fechasBusqueda);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, startYear, startMonth, startDay); //Variables para inicializar el calendario en el día de hoy.
                datePickerDialog.getDatePicker().setMaxDate(mCalendar.getTimeInMillis()); //bloqueamos que no hagan clic en fechas futuras.
                datePickerDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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
        precioMedio = mView.findViewById(R.id.dailyAverageValue);
        precioValle = mView.findViewById(R.id.horaVallePrice);
        precioPunta = mView.findViewById(R.id.horaPuntaPrice);
        horaValle = mView.findViewById(R.id.horaValleValue);
        horaPunta = mView.findViewById(R.id.horaPuntaValue);
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

                //Seteamos valores del cardView
                BigDecimal bdPrecioValle = new BigDecimal(respuestaESIOSCardView.getPrecioValle());
                BigDecimal bdPrecioMedio = respuestaESIOSCardView.getMedia();
                BigDecimal bdPrecioPunta = new BigDecimal(respuestaESIOSCardView.getPrecioPunta());
                BigDecimal bdmil = new BigDecimal("1000");


                if(isChecked) {
                    switchEnergia.setText("MWh");
                    precioValle.setText(respuestaESIOSCardView.getPrecioValle() + "€/MWh");
                    precioMedio.setText(respuestaESIOSCardView.getMedia() + "€/MWh");
                    precioPunta.setText(respuestaESIOSCardView.getPrecioPunta() + "€/MWh");

                    //Creamos el adaptador con los datos de la lista.
                    pricesAdapter = new PricesAdapter(priceList, true);

                    //Vaciamos la lista al cambiar de día:
                    rvListaPrecios.setAdapter(null);

                    //Cargamos los datos del adapter a la vista.
                    rvListaPrecios.setAdapter(pricesAdapter);
                }
                else{
                    switchEnergia.setText("KWh");
                    precioValle.setText(bdPrecioValle.divide(bdmil,3, RoundingMode.HALF_UP) + "€/KWh");
                    precioMedio.setText(bdPrecioMedio.divide(bdmil,3, RoundingMode.HALF_UP) + "€/KWh");
                    precioPunta.setText(bdPrecioPunta.divide(bdmil,3, RoundingMode.HALF_UP) + "€/KWh");

                    //Creamos el adaptador con los datos de la lista.
                    pricesAdapter = new PricesAdapter(priceList, false);

                    //Vaciamos la lista al cambiar de día:
                    rvListaPrecios.setAdapter(null);

                    //Cargamos los datos del adapter a la vista.
                    rvListaPrecios.setAdapter(pricesAdapter);
                }
            }
        });

        switchGrafica.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    switchEnergia.setVisibility(View.INVISIBLE);
                    LineChart.crearGrafico(mView, priceList);
                }
                else {
                    switchEnergia.setVisibility(View.VISIBLE);
                    //Ocultar LineChart.
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, 0);
                    linePricesChart.setLayoutParams(lp);

                    rvListaPrecios.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

                }
            }
        });

        //Este solo se ejecuta la primera vez.
        pricesAdapter = new PricesAdapter(priceList, switchGrafica.isChecked());
        rvListaPrecios.setAdapter(pricesAdapter);

        return mView;
    }

    private void leerWSESIOS(String [] _fechasBusqueda) {

        String url = "https://api.esios.ree.es/indicators/10391?geo_ids[]=8741&start_date="+_fechasBusqueda[0]+"&end_date="+_fechasBusqueda[1];
        StringRequest getRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);//Recibimos el JSON.

                    //Lo parseamos a un objeto del tipo PreciosJSON.
                    RespuestaESIOS respuestaESIOS = JsonPricesParser.obtenerJsonHoras(jsonObject, _fechasBusqueda);

                    //Añadimos los preciosJSON a una lista, previamente borrada.
                    priceList.clear();
                    priceList.addAll(Arrays.asList(respuestaESIOS.getPreciosJSON()));

                    if(switchGrafica.isChecked()) {//Grafica
                        switchEnergia.setVisibility(View.INVISIBLE);
                        LineChart.crearGrafico(mView, priceList);
                    }
                    else {//Lista

                        //Me guardo los valores de cardView para modificarlos fuera de esta funcion
                        respuestaESIOSCardView = respuestaESIOS;

                        //Seteamos valores del cardView
                        BigDecimal bdPrecioValle = new BigDecimal(respuestaESIOS.getPrecioValle());
                        BigDecimal bdPrecioMedio = respuestaESIOS.getMedia();
                        BigDecimal bdPrecioPunta = new BigDecimal(respuestaESIOS.getPrecioPunta());
                        BigDecimal bdmil = new BigDecimal("1000");

                        //Seteamos valores del cardView
                        horaValle.setText(respuestaESIOS.getHoraValle());
                        precioValle.setText(bdPrecioValle.divide(bdmil,3, RoundingMode.HALF_UP) + "€/KWh");
                        precioMedio.setText(bdPrecioMedio.divide(bdmil,3, RoundingMode.HALF_UP) + "€/KWh");
                        horaPunta.setText(respuestaESIOS.getHoraPunta());
                        precioPunta.setText(bdPrecioPunta.divide(bdmil,3, RoundingMode.HALF_UP) + "€/KWh");




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
