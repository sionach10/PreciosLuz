package com.precioLuz.utils;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.precioLuz.R;
import com.precioLuz.models.EnergyByTechnology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PieChartUtility {


    public PieChartUtility() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void crearGrafico(View mView, EnergyByTechnology datos) {

        LineChart lineChart = mView.findViewById(R.id.lineChart);
        PieChart chart = mView.findViewById(R.id.pieChart);

        //Ocultar lineChart.
        int width = 0;
        int height = 0;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
        lineChart.setLayoutParams(lp);
        chart.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));


        //Opciones del grafico:
        configurarPieChart(chart);

        obtenerDatos(chart, datos);

        chart.invalidate(); // refresh
    }


    private static void configurarPieChart(PieChart chart) {
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);
        chart.setDragDecelerationFrictionCoef(0.95f);
        chart.setCenterText(generateCenterSpannableText());
        chart.setExtraOffsets(22.f, 0.f, 22.f, 0.f);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);
        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);
        chart.setDrawCenterText(true);
        chart.setRotationAngle(0);
        chart.setRotationEnabled(true);
        chart.animateY(1400, Easing.EaseInOutQuad);

        // chart.spin(2000, 0, 360);
        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        //l.setDrawInside(false);
        l.setEnabled(true);


    }

    private static SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Porcentaje de cada tecnología\n sobre el total generado \n By AppSolutions");
        //s.setSpan(new RelativeSizeSpan(1.5f), 0, 14, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 0, s.length() - 15, 0);
        //s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(.85f), s.length()-16, s.length(), 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 16, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 16, s.length(), 0);
        return s;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static void obtenerDatos(PieChart chart, EnergyByTechnology datos) {

        ArrayList<PieEntry> entriesPercent = new ArrayList<PieEntry>();

        LinkedHashMap<String, Integer> energiaAgregada = new LinkedHashMap<>();//Hasmap que mantiene el orden de inserción.

        //Sumamos la energia de cada tecnología a lo largo de todas las horas de ese día.
        //Con la función sum() nos ahorramos el bucle for recorriendo las horas.
        energiaAgregada.put("Carbon", (int) Math.floor(datos.getCarbon().values().stream().mapToDouble(Float::floatValue).sum()));
        energiaAgregada.put("Nuclear", (int) Math.floor(datos.getNuclear().values().stream().mapToDouble(Float::floatValue).sum()));
        energiaAgregada.put("Hidraulica", (int) Math.floor(datos.getHidraulica().values().stream().mapToDouble(Float::floatValue).sum()));
        energiaAgregada.put("Ciclo Combinado", (int) Math.floor(datos.getCiclo_combinado().values().stream().mapToDouble(Float::floatValue).sum()));
        energiaAgregada.put("Eolica", (int) Math.floor(datos.getEolica().values().stream().mapToDouble(Float::floatValue).sum()));
        energiaAgregada.put("Solar termica", (int) Math.floor(datos.getSolar_termica().values().stream().mapToDouble(Float::floatValue).sum()));
        energiaAgregada.put("Solar fotovoltaica", (int) Math.floor(datos.getSolar_fotovoltaica().values().stream().mapToDouble(Float::floatValue).sum()));
        energiaAgregada.put("Cogeneración", (int) Math.floor(datos.getCogen().values().stream().mapToDouble(Float::floatValue).sum()));


        for(String key : energiaAgregada.keySet()) {
            entriesPercent.add(new PieEntry(energiaAgregada.get(key),key));

        }


        //Colores de cada serie:
        int [] colorClassArray = new int[] {
                ContextCompat.getColor(chart.getContext(), R.color.carbon),
                ContextCompat.getColor(chart.getContext(), R.color.nuclear),
                ContextCompat.getColor(chart.getContext(), R.color.hidraulica),
                ContextCompat.getColor(chart.getContext(), R.color.ciclo_combinado),
                ContextCompat.getColor(chart.getContext(), R.color.eolica),
                ContextCompat.getColor(chart.getContext(), R.color.solar_termica),
                ContextCompat.getColor(chart.getContext(), R.color.solar_fotovoltaica),
                ContextCompat.getColor(chart.getContext(), R.color.cogen)
        };

        PieDataSet pieDataSet = new PieDataSet(entriesPercent, "");
        pieDataSet.setColors(colorClassArray);
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setValueLineColor(ContextCompat.getColor(chart.getContext(), R.color.purple_700));
        pieDataSet.setValueTextColor(R.color.purple_700);
        pieDataSet.setValueTextSize(12f);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter(chart));
        chart.setEntryLabelColor(R.color.purple_700);

        chart.setData(pieData);
    }

}
