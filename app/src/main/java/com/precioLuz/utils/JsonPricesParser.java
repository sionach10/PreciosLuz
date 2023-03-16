package com.precioLuz.utils;

import android.content.Intent;
import android.os.Build;
import android.os.Debug;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.type.DateTime;
import com.precioLuz.models.PreciosJSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class JsonPricesParser {

    public static PreciosJSON[] obtenerJsonHoras(JSONObject jsonObject, String[] fechas) throws JSONException {

        ArrayList<String> listaPrecios = new ArrayList<>();
        ArrayList<String> listaHoras = new ArrayList<>();

        PreciosJSON[] preciosJSON;

        JSONArray jsonValues = jsonObject.getJSONObject("indicator").getJSONArray("values");

        for(int i=0; i<24; i++) { //Como en la API viene a parte de las 24h del día, la primera hora del día siguiente, debemos limitar el for a 24 periodos.
            JSONObject json = jsonValues.getJSONObject(i);
            listaPrecios.add(json.getString("value"));
            listaHoras.add(json.getString("datetime"));
        }

        preciosJSON = parserJSON(listaPrecios, listaHoras);

        return preciosJSON;
    }

    //Convertimos los JSONObject en objetos tipo PreciosJSON
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static PreciosJSON[] parserJSON(ArrayList<String> _listaPrecios, ArrayList<String> _listaHoras) throws JSONException {

        PreciosJSON[] _preciosJSON = new PreciosJSON[24];
        float suma = 0;
        float media = 0;
        DecimalFormat df = new DecimalFormat("00");

        for(int i=0; i<_listaPrecios.size(); i++) {
            _preciosJSON[i] = new PreciosJSON(); //Necesario inicializar el objeto.
            int nextHour = Integer.parseInt(_listaHoras.get(i).substring(11,13))+1;
            String nextHourS = df.format(nextHour);

            _preciosJSON[i].setHour(_listaHoras.get(i).substring(11,13)+"-"+ nextHourS+"h");
            _preciosJSON[i].setPrice(_listaPrecios.get(i));
            suma+=Float.parseFloat(_listaPrecios.get(i));
        }

        media = suma/_listaPrecios.size();

        for(int i=0; i<_listaPrecios.size(); i++){
            _preciosJSON[i].setCheap(Float.parseFloat(_listaPrecios.get(i)) <= media); //Forma rapida de asignar true/false.
            _preciosJSON[i].setUnderAvg(Float.parseFloat(_listaPrecios.get(i)) <= media);
        }

        return _preciosJSON;
    }

}
