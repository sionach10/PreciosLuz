package com.precioLuz.adapters;

import android.content.Context;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.precioLuz.R;
import com.precioLuz.models.PreciosJSON;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.List;

public class PricesAdapter extends RecyclerView.Adapter<PricesAdapter.PriceViewHolder> {

    private List<PreciosJSON> priceList;
    private boolean magnitudeEnergy;

    //Constructor
    public PricesAdapter(List<PreciosJSON> priceList, boolean switchEnergia) {
        this.priceList = priceList;
        magnitudeEnergy = switchEnergia;
    }


    @NonNull
    @Override
    public PricesAdapter.PriceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_precios, parent, false);
        return new PriceViewHolder(view);
    }

    public static class PriceViewHolder extends RecyclerView.ViewHolder {
        TextView hour, price;
        ImageView iconClock;
        RelativeLayout relativeLayout;

        public PriceViewHolder(@NonNull View itemView) {
            super(itemView);
            hour = itemView.findViewById(R.id.hour);
            price = itemView.findViewById(R.id.price);
            iconClock = itemView.findViewById(R.id.iconClock);
            relativeLayout = itemView.findViewById(R.id.relativeLayoutItemListaPrecios);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PriceViewHolder holder, int position) {

        PreciosJSON priceItem = priceList.get(position);

        String unit = magnitudeEnergy ? " €/MWh" : " €/KWh";
        BigDecimal priceCalculated = magnitudeEnergy ?
                BigDecimal.valueOf(Double.parseDouble(priceItem.getPrice())).setScale(2, RoundingMode.HALF_UP) :         // Redondear a 2 decimales si magnitudeEnergy es true;
                BigDecimal.valueOf(Double.parseDouble(priceItem.getPrice()) / 1000.0).setScale(3, RoundingMode.HALF_UP); // Redondear a 3 decimales si magnitudeEnergy es false;

        //Asignación de valores.
        holder.hour.setText(priceItem.getHour());
        holder.price.setText(String.format("%s%s", priceCalculated, unit));
        holder.iconClock.setImageResource(priceItem.isCheap() ? R.drawable.ic_clock_foreground_green : R.drawable.ic_clock_foreground_red);

        // Obtener la hora actual
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (currentHour == position) {
            holder.relativeLayout.setBackgroundResource(R.drawable.item_background_current_hour);
        }



    }

    @Override
    public int getItemCount() {
        return priceList.size();
    }

}