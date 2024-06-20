package com.precioLuz.utils;

import android.content.Intent;
import android.os.Build;
import android.os.Debug;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.precioLuz.models.PreciosJSON;
import com.precioLuz.models.RespuestaESIOS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class JsonPricesParser {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static RespuestaESIOS obtenerJsonHoras(JSONObject jsonObject, String[] fechas) throws JSONException {

        ArrayList<String> listaPrecios = new ArrayList<>();
        ArrayList<String> listaHoras = new ArrayList<>();
        RespuestaESIOS respuestaESIOS = null;

        JSONArray jsonValues = jsonObject.getJSONObject("indicator").getJSONArray("values");

        for(int i=0; i<24; i++) { //Como en la API viene a parte de las 24h del día, la primera hora del día siguiente, debemos limitar el for a 24 periodos.
            JSONObject json = jsonValues.getJSONObject(i);
            listaPrecios.add(json.getString("value"));
            listaHoras.add(json.getString("datetime"));
        }

        respuestaESIOS = parserJSON(listaPrecios, listaHoras);

        return respuestaESIOS;
    }

    //Convertimos los JSONObject en objetos tipo PreciosJSON
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static RespuestaESIOS parserJSON(ArrayList<String> _listaPrecios, ArrayList<String> _listaHoras) throws JSONException {

        PreciosJSON[] _preciosJSON = new PreciosJSON[24];
        float suma = 0;
        float _precioMedio;
        BigDecimal precioMedio;
        String horaValle = "";
        String horaPunta = "";
        String precioValle = "9999";//Inicializamos a un valor alto
        String precioPunta = "-500";//Inicializamos a un valor bajo

        DecimalFormat df = new DecimalFormat("00");

        for(int i=0; i<_listaPrecios.size(); i++) {
            _preciosJSON[i] = new PreciosJSON(); //Necesario inicializar el objeto.
            int nextHour = Integer.parseInt(_listaHoras.get(i).substring(11,13))+1;
            String nextHourS = df.format(nextHour);

            _preciosJSON[i].setHour(_listaHoras.get(i).substring(11,13)+"-"+ nextHourS+"h");
            _preciosJSON[i].setPrice(_listaPrecios.get(i));
            suma+=Float.parseFloat(_listaPrecios.get(i));

            if (Float.parseFloat(_preciosJSON[i].getPrice())<Float.parseFloat(precioValle)) {
                precioValle = _preciosJSON[i].getPrice();
                horaValle=_preciosJSON[i].getHour();
            }

            if (Float.parseFloat(_preciosJSON[i].getPrice())>Float.parseFloat(precioPunta)) {
                precioPunta = _preciosJSON[i].getPrice();
                horaPunta=_preciosJSON[i].getHour();
            }

        }

        _precioMedio = suma/_listaPrecios.size();
        precioMedio = new BigDecimal(_precioMedio).setScale(2, RoundingMode.HALF_UP);

        for(int i=0; i<_listaPrecios.size(); i++){
            _preciosJSON[i].setCheap(Float.parseFloat(_listaPrecios.get(i)) <= _precioMedio); //Forma rapida de asignar true/false.
        }

        return new RespuestaESIOS(_preciosJSON, precioMedio, horaValle, horaPunta, precioValle, precioPunta);
    }

}
