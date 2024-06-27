package com.precioLuz.utils;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.precioLuz.R;
import com.precioLuz.models.EnergyByTechnology;
import com.precioLuz.models.PreciosJSON;

import java.util.ArrayList;
import java.util.List;

public class LineChart {


    public LineChart() {
    }

    public static void crearGrafico(View mView, List<PreciosJSON> mList) {

        com.github.mikephil.charting.charts.LineChart lineChart = mView.findViewById(R.id.linePricesChart);

        //Opciones del grafico:
        configurarEjesChart(lineChart);

        // use the interface ILineDataSet
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

        obtenerDatos(lineChart, mList, dataSets);

        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.invalidate(); // refresh
    }

    private static void configurarEjesChart(com.github.mikephil.charting.charts.LineChart chart) {
        final int fillColorLine = ContextCompat.getColor(chart.getContext(), R.color.nuclear);
        chart.setBackgroundColor(Color.WHITE);
        chart.setGridBackgroundColor(fillColorLine);
        chart.setDrawGridBackground(true);
        chart.getLegend().setWordWrapEnabled(true);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setTextSize(12f);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisLeft().setTextSize(12f);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setEnabled(false);
        chart.setDrawBorders(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0);
        Description text = chart.getDescription();
        text.setText("Precios horarios en MW/hora");

    }

    private static void obtenerDatos(com.github.mikephil.charting.charts.LineChart chart, List<PreciosJSON> mList, List<ILineDataSet> dataSets) {

        List<Entry> seriePrecios = new ArrayList<Entry>();

        //Cargamos la serie de precios.
        for (int i = 0; i<mList.size(); i++) {
            seriePrecios.add(new Entry(i, Float.parseFloat(mList.get(i).getPrice())));
        }

        LineDataSet dataSet1 = new LineDataSet(seriePrecios, "Precios horarios en MW/hora"); // add entries to dataset
        dataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet1.setColor(ContextCompat.getColor(chart.getContext(), R.color.purple_700));
        dataSet1.setDrawCircles(false);
        dataSet1.setLineWidth(2f);
        dataSet1.setCircleRadius(3f);
        dataSet1.setCircleRadius(3f);
        dataSet1.setFillAlpha(255);
        dataSet1.setDrawFilled(true);
        dataSet1.setFillColor(ContextCompat.getColor(chart.getContext(), R.color.white));
        dataSet1.setHighLightColor(Color.rgb(244, 117, 117));
        dataSet1.setDrawCircleHole(false);
        dataSet1.setValueTextSize(11f);
        dataSet1.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                // change the return value here to better understand the effect
                //return 550;
                return chart.getAxisLeft().getAxisMaximum();
            }
        });
        dataSets.add(dataSet1);
    }

}
