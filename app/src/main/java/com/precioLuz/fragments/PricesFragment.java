package com.precioLuz.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.precioLuz.R;
import com.precioLuz.activities.HomeActivity;
import com.precioLuz.activities.MainActivity;
import com.precioLuz.adapters.PricesAdapter;
import com.precioLuz.models.PreciosJSON;
import com.precioLuz.providers.AuthProvider;
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

import dmax.dialog.SpotsDialog;


public class PricesFragment extends Fragment {
    public PricesFragment() {
        //Required empty public constructor
    }

    //Variables globales
    AuthProvider mAuthProvider;
    View mView;
    ListView listaItemsPrecios;
    TextView fecha;
    List<PreciosJSON> mList = new ArrayList<>();
    PricesAdapter mAdapter;
    SwitchMaterial switchEnergia;
    SpotsDialog mDialog; //Cargando


    private final Calendar mCalendar = Calendar.getInstance();
    private final int startDay = mCalendar.get(Calendar.DAY_OF_MONTH);
    private final int startMonth = mCalendar.get(Calendar.MONTH); //Los meses los devuelve de 0 a 11.
    private final int startYear = mCalendar.get(Calendar.YEAR);
    String [] fechasBusqueda = new String[2];
    String [] fechasDefault = new String[2];

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mAuthProvider = new AuthProvider();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
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
                            fechasBusqueda = obtenerFechasFromDatePicker(_dayOfMonth, _month+1, _year);//El datePicker usa los meses de 0 a 11.

                            leerWSESIOS(fechasBusqueda);

                        } catch (ParseException e) {
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
        mView = inflater.inflate(R.layout.fragment_prices, container, false);
        listaItemsPrecios= mView.findViewById(R.id.listaItemsPrecios);
        fecha = mView.findViewById(R.id.date);
        switchEnergia = mView.findViewById(R.id.switchEnergia);
        mDialog = new SpotsDialog(getContext());

        //Inicializamos las fechas de hoy y mañana por defecto.
        try {
            fechasDefault = obtenerFechasFromDatePicker(startDay, startMonth+1, startYear);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return mView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDialog.show();
        mDialog.setMessage("Cargando");

        leerWSESIOS(fechasDefault);

        switchEnergia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    switchEnergia.setText("MWh");
                    //Creamos el adaptador con los datos de la lista.
                    mAdapter = new PricesAdapter(requireContext(),R.layout.item_lista_precios, mList, isChecked); //TODO

                    //Vaciamos la lista al cambiar de día:
                    listaItemsPrecios.setAdapter(null);

                    //Cargamos los datos del adapter a la vista.
                    listaItemsPrecios.setAdapter(mAdapter);
                }
                else{
                    switchEnergia.setText("KWh");
                    //Creamos el adaptador con los datos de la lista.
                    mAdapter = new PricesAdapter(requireContext(),R.layout.item_lista_precios, mList, isChecked); //TODO

                    //Vaciamos la lista al cambiar de día:
                    listaItemsPrecios.setAdapter(null);

                    //Cargamos los datos del adapter a la vista.
                    listaItemsPrecios.setAdapter(mAdapter);
                }
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
                    mList.clear();
                    mList.addAll(Arrays.asList(preciosJSON));

                    //Modificamos campo fecha.
                    fecha.setText(_fechasBusqueda[0]);

                    //Creamos el adaptador con los datos de la lista.
                    mAdapter = new PricesAdapter(requireContext(),R.layout.item_lista_precios, mList, switchEnergia.isChecked()); //TODO

                    //Vaciamos la lista al cambiar de día:
                    listaItemsPrecios.setAdapter(null);

                    //Cargamos los datos del adapter a la vista.
                    listaItemsPrecios.setAdapter(mAdapter);

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
