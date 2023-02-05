package com.precioLuz.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.precioLuz.R;
import com.precioLuz.models.PreciosJSON;

import java.util.List;

public class PricesAdapter extends ArrayAdapter {

    public PricesAdapter(Context context, String[] hours) {
        super (context, 0, hours);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {

        //Obteniendo una instancia del inflater
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Salvando la referencia del View de la fila
        View mView = convertView;

        //Comprobando si el View no existe
        if (convertView == null) {
            //Si no existe, entonces inflarlo
            mView = inflater.inflate(
                    R.layout.fragment_prices,
                    parent,
                    false);
        }

        //Obteniendo instancias de los elementos
        TextView date = mView.findViewById(R.id.date);
        TextView priceColor = mView.findViewById(R.id.priceColor);
        TextView hour = mView.findViewById(R.id.hour);
        TextView price = mView.findViewById(R.id.price);

        //Obteniendo instancia de la Tarea en la posición actual
        PreciosJSON item = (PreciosJSON) getItem(position);

        date.setText(item.getDate());

        if(item.isCheap()) {
            priceColor.setBackgroundColor(Color.GREEN);
        }
        else
            priceColor.setBackgroundColor(Color.RED);

        hour.setText(item.getHour());
        price.setText((int) item.getPrice());

        //Devolver al ListView la fila creada
        return mView;
    }
}
