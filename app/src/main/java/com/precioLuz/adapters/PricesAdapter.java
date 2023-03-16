package com.precioLuz.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.precioLuz.R;
import com.precioLuz.models.PreciosJSON;

import java.text.DecimalFormat;
import java.util.List;

public class PricesAdapter extends ArrayAdapter<PreciosJSON> {

    private List<PreciosJSON> mList;
    private Context mContext;
    private int resourceLayout;
    private boolean switchEnergia;
    private final String magnitud = " €/KWh";
    private DecimalFormat formato2d = new DecimalFormat("#.##");
    //Constructor
    public PricesAdapter(@NonNull Context context, int resource, List<PreciosJSON> objects, boolean switchEnergia) {
        super(context, resource, objects);
        this.mList = objects;
        this.mContext = context;
        this.resourceLayout = resource;
        this.switchEnergia = switchEnergia;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {

        //Salvando la referencia del View de la fila
        View mView = convertView;

        //Comprobando si el View no existe
        if (mView == null) {
            //Si no existe, entonces inflarlo
            mView = LayoutInflater.from(mContext).inflate(resourceLayout, null);
        }


        //Obteniendo item de la Tarea en la posición actual
        PreciosJSON item = getItem(position);

        //Obteniendo instancias de los elementos
        //TextView date = mView.findViewById(R.id.date);
        ImageView priceColor = mView.findViewById(R.id.priceColor);
        TextView hour = mView.findViewById(R.id.hour);
        TextView price = mView.findViewById(R.id.price);

        //date.setText(item.getDate());
        hour.setText(item.getHour());
        //Adaptamos valor a KWh/MWh:
        if(switchEnergia) { //MWh
            float division = Float.parseFloat(item.getPrice());
            price.setText(formato2d.format(division) + magnitud);
            if(item.isCheap())
                priceColor.setImageResource(R.drawable.mood_green);
            else
                priceColor.setImageResource(R.drawable.mood_red);
        }
        else { //KWh
            float division = Float.parseFloat(item.getPrice())/1000.0f;
            price.setText(formato2d.format(division) + magnitud);
            if(item.isCheap())
                priceColor.setImageResource(R.drawable.mood_green);
            else
                priceColor.setImageResource(R.drawable.mood_red);
        }


        //Devolver al ListView la fila creada
        return mView;
    }
}
