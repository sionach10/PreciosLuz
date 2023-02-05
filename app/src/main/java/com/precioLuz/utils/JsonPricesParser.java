package com.precioLuz.utils;

import android.os.Debug;

import com.precioLuz.models.PreciosJSON;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonPricesParser {

    public static PreciosJSON[] obtenerJsonHoras(JSONObject jsonObject) throws JSONException {

        JSONObject[] horasJSON = new JSONObject[24];
        PreciosJSON[] horasPrecios;

        horasJSON[0] = jsonObject.getJSONObject("00-01");
        horasJSON[1] = jsonObject.getJSONObject("01-02");
        horasJSON[2] = jsonObject.getJSONObject("02-03");
        horasJSON[3] = jsonObject.getJSONObject("03-04");
        horasJSON[4] = jsonObject.getJSONObject("04-05");
        horasJSON[5] = jsonObject.getJSONObject("05-06");
        horasJSON[6] = jsonObject.getJSONObject("06-07");
        horasJSON[7] = jsonObject.getJSONObject("07-08");
        horasJSON[8] = jsonObject.getJSONObject("08-09");
        horasJSON[9] = jsonObject.getJSONObject("09-10");
        horasJSON[10] = jsonObject.getJSONObject("10-11");
        horasJSON[11] = jsonObject.getJSONObject("11-12");
        horasJSON[12] = jsonObject.getJSONObject("12-13");
        horasJSON[13] = jsonObject.getJSONObject("13-14");
        horasJSON[14] = jsonObject.getJSONObject("14-15");
        horasJSON[15] = jsonObject.getJSONObject("15-16");
        horasJSON[16] = jsonObject.getJSONObject("16-17");
        horasJSON[17] = jsonObject.getJSONObject("17-18");
        horasJSON[18] = jsonObject.getJSONObject("18-19");
        horasJSON[19] = jsonObject.getJSONObject("19-20");
        horasJSON[20] = jsonObject.getJSONObject("20-21");
        horasJSON[21] = jsonObject.getJSONObject("21-22");
        horasJSON[22] = jsonObject.getJSONObject("22-23");
        horasJSON[23] = jsonObject.getJSONObject("23-24");

        horasPrecios = parserPreciosJSON(horasJSON);

        return horasPrecios;
    }

    //Convertimos los JSONObject en objetos tipo PreciosJSON
    private static PreciosJSON[] parserPreciosJSON(JSONObject[] horasJSON) throws JSONException {

        PreciosJSON[] _horasPrecios = new PreciosJSON[24];

        for(int i=0; i<horasJSON.length; i++) {
            _horasPrecios[i] = new PreciosJSON(); //Necesario inicializar el objeto.
            _horasPrecios[i].setDate(horasJSON[i].getString("date"));
            _horasPrecios[i].setHour(horasJSON[i].getString("hour"));
            _horasPrecios[i].setCheap(horasJSON[i].getBoolean("is-cheap"));
            _horasPrecios[i].setUnderAvg(horasJSON[i].getBoolean("is-under-avg"));
            _horasPrecios[i].setMarket(horasJSON[i].getString("market"));
            _horasPrecios[i].setPrice(horasJSON[i].getDouble("price"));
            _horasPrecios[i].setUnits(horasJSON[i].getString("units"));
        }

        return _horasPrecios;
    }
}
