package com.precioLuz.fragments;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.precioLuz.R;
import com.precioLuz.adapters.PricesAdapter;
import com.precioLuz.models.PreciosJSON;
import com.precioLuz.utils.JsonPricesParser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class PricesFragment extends Fragment {
    public PricesFragment() {
        //Required empty public constructor
    }

    //Variables globales
    View mView;
    Toolbar mToolbar;
    ListView listaItemsPrecios;
    ArrayAdapter adaptador;
    HttpURLConnection connection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        mView = inflater.inflate(R.layout.fragment_prices, container, false);
        mToolbar = mView.findViewById(R.id.toolbar);
        listaItemsPrecios= mView.findViewById(R.id.listaItemsPrecios);

        try {
            new JsonTask().execute(new URL("https://api.preciodelaluz.org/v1/prices/all?zone=PCB"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return mView;
    }

    public class JsonTask extends AsyncTask<URL, Void, List<PreciosJSON>> {

        @Override
        protected List<PreciosJSON> doInBackground(URL... urls) {
            List<PreciosJSON> preciosJSONList = null;

            try {
                // Establecer la conexión
                connection = (HttpURLConnection)urls[0].openConnection();
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(10000);

                // Obtener el estado del recurso
                int statusCode = connection.getResponseCode();

                if(statusCode!=200) { //Si no es correcta la respuesta, rellenamos el array con errores.
                    preciosJSONList = new ArrayList<>();
                    preciosJSONList.add(new PreciosJSON("error", null, false, false, null, 0, null ));

                } else {

                    // Parsear el flujo con formato JSON
                    InputStream in = new BufferedInputStream(connection.getInputStream());

                    JsonPricesParser parser = new JsonPricesParser();
                    //GsonAnimalParser parser = new GsonAnimalParser();

                    preciosJSONList = parser.readJsonStream(in);


                }

            } catch (Exception e) {
                e.printStackTrace();

            }finally {
                connection.disconnect();
            }
            return preciosJSONList;
        }

        @Override
        protected void onPostExecute(List<PreciosJSON> preciosJSONList) {
            //Asignar los objetos de Json parseados al adaptador
            if(preciosJSONList!=null) {
                adaptador = new PricesAdapter(getContext(), preciosJSONList);
                listaItemsPrecios.setAdapter(adaptador);
            }else{
                Toast.makeText(
                                getContext(),
                                "Ocurrió un error de Parsing Json",
                                Toast.LENGTH_SHORT)
                        .show();
            }

        }
    }

}
