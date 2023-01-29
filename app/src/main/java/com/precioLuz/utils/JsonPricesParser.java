package com.precioLuz.utils;

import android.util.JsonReader;

import com.precioLuz.models.PreciosJSON;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class JsonPricesParser {

    public List<PreciosJSON> readJsonStream(InputStream in) throws IOException {
        // Nueva instancia JsonReader
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            // Leer Array
            return leerArrayPreciosJSON(reader);
        } finally {
            reader.close();
        }

    }

    public List leerArrayPreciosJSON(JsonReader reader) throws IOException {

        // Lista temporal
        ArrayList prices = new ArrayList();

        reader.beginObject();
        while (reader.hasNext()) {
            // Leer objeto
            prices.add(leerPrecioH(reader));
        }
        reader.endArray();
        return prices;
    }

    public PreciosJSON leerPrecioH(JsonReader reader) throws IOException {
        String date= null;
        String hour= null;
        boolean isCheap= false;
        boolean isUnderAvg= false;
        String market= null;
        double price= 0.0f;
        String units= null;

        reader.beginObject();

        while(reader.hasNext()) {
            String name = reader.nextName();

            switch (name) {
                case "date":
                    date = reader.nextString();
                    break;
                case "hour":
                    hour = reader.nextString();
                    break;
                case "is-Cheap":
                    isCheap = reader.nextBoolean();
                    break;
                case "isUnder-Avg":
                    isUnderAvg = reader.nextBoolean();
                    break;
                case "market":
                    market = reader.nextString();
                    break;
                case "price":
                    price = reader.nextDouble();
                    break;
                case "units":
                    units = reader.nextString();
                    break;
            }
        }

        reader.endObject();
        return new PreciosJSON(date, hour, isCheap, isUnderAvg, market, price, units);


    }
}
