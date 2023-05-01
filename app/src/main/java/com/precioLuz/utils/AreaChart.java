package com.precioLuz.utils;

import android.graphics.Color;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.precioLuz.R;
import com.precioLuz.models.EnergyByTechnology;

import java.util.ArrayList;
import java.util.List;

public class AreaChart {


    public AreaChart() {
    }

    public static void crearGrafico(View mView, EnergyByTechnology datos) {

        // in this example, a LineChart is initialized from xml
        LineChart chart = mView.findViewById(R.id.lineChart);
        final int fillColorCarbon = ContextCompat.getColor(chart.getContext(), R.color.carbon);
        chart.setBackgroundColor(Color.WHITE);
        chart.setGridBackgroundColor(fillColorCarbon);
        chart.setDrawGridBackground(true);


        //Opciones del grafico:
        configurarEjesChart(chart);

        // use the interface ILineDataSet
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

        obtenerDatos(chart, datos, dataSets);

        LineData data = new LineData(dataSets);
        chart.setData(data);
        chart.invalidate(); // refresh
    }

    private static void configurarEjesChart(LineChart chart) {
        chart.getLegend().setWordWrapEnabled(true);
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setEnabled(false);
        chart.setDrawBorders(true);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0);
        Description text = chart.getDescription();
        text.setText("Energía horaria por tecnología");

    }

    private static void obtenerDatos(LineChart chart, EnergyByTechnology datos, List<ILineDataSet> dataSets) {
        List<Entry> serieCarbon = new ArrayList<Entry>();
        List<Entry> serieNuclear = new ArrayList<Entry>();
        List<Entry> serieHidraulica = new ArrayList<Entry>();
        List<Entry> serieCiclo_combinado = new ArrayList<Entry>();
        List<Entry> serieEolica = new ArrayList<Entry>();
        List<Entry> serieSolar_termica = new ArrayList<Entry>();
        List<Entry> serieSolar_fotovoltaica = new ArrayList<Entry>();
        List<Entry> serieCogen = new ArrayList<Entry>();

        //Vamos a pintar las series acumulativas.
        for (int i = 1; i<=datos.getCarbon().size(); i++) {
            serieCarbon.add(new Entry(i, datos.getCarbon().get(i)));
            serieNuclear.add(new Entry(i, datos.getNuclear().get(i)
                    +datos.getCarbon().get(i)));
            serieHidraulica.add(new Entry(i, datos.getHidraulica().get(i)
                    +datos.getNuclear().get(i)
                    +datos.getCarbon().get(i)));
            serieCiclo_combinado.add(new Entry(i, datos.getCiclo_combinado().get(i)
                    +datos.getHidraulica().get(i)
                    +datos.getNuclear().get(i)
                    +datos.getCarbon().get(i)));
            serieEolica.add(new Entry(i, datos.getEolica().get(i)+
                    +datos.getCiclo_combinado().get(i)
                    +datos.getHidraulica().get(i)
                    +datos.getNuclear().get(i)
                    +datos.getCarbon().get(i)));
            serieSolar_termica.add(new Entry(i, datos.getSolar_termica().get(i)
                    +datos.getEolica().get(i)+
                    +datos.getCiclo_combinado().get(i)
                    +datos.getHidraulica().get(i)
                    +datos.getNuclear().get(i)
                    +datos.getCarbon().get(i)));
            serieSolar_fotovoltaica.add(new Entry(i, datos.getSolar_fotovoltaica().get(i)
                    +datos.getSolar_termica().get(i)
                    +datos.getEolica().get(i)+
                    +datos.getCiclo_combinado().get(i)
                    +datos.getHidraulica().get(i)
                    +datos.getNuclear().get(i)
                    +datos.getCarbon().get(i)));
            serieCogen.add(new Entry(i, datos.getCogen().get(i)
                    +datos.getSolar_fotovoltaica().get(i)
                    +datos.getSolar_termica().get(i)
                    +datos.getEolica().get(i)+
                    +datos.getCiclo_combinado().get(i)
                    +datos.getHidraulica().get(i)
                    +datos.getNuclear().get(i)
                    +datos.getCarbon().get(i)));
        }

        LineDataSet dataSet1 = new LineDataSet(serieCarbon, "Carbon"); // add entries to dataset
        dataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet1.setColor(ContextCompat.getColor(chart.getContext(), R.color.nuclear));
        dataSet1.setDrawCircles(false);
        dataSet1.setLineWidth(2f);
        dataSet1.setCircleRadius(3f);
        dataSet1.setCircleRadius(3f);
        dataSet1.setFillAlpha(255);
        dataSet1.setDrawFilled(true);
        dataSet1.setFillColor(ContextCompat.getColor(chart.getContext(), R.color.nuclear));
        dataSet1.setHighLightColor(Color.rgb(244, 117, 117));
        dataSet1.setDrawCircleHole(false);
        dataSet1.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                // change the return value here to better understand the effect
                //return 550;
                return chart.getAxisLeft().getAxisMaximum();
            }
        });


        LineDataSet dataSet2 = new LineDataSet(serieNuclear, "Nuclear"); // add entries to dataset
        dataSet2.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet2.setColor(ContextCompat.getColor(chart.getContext(), R.color.nuclear));
        dataSet2.setDrawCircles(false);
        dataSet2.setLineWidth(2f);
        dataSet2.setCircleRadius(3f);
        dataSet2.setFillAlpha(255);
        dataSet2.setDrawFilled(true);
        dataSet2.setFillColor(ContextCompat.getColor(chart.getContext(), R.color.hidraulica));
        dataSet2.setHighLightColor(Color.rgb(244, 117, 117));
        dataSet2.setDrawCircleHole(false);
        dataSet2.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                // change the return value here to better understand the effect
                //return 550;
                return chart.getAxisLeft().getAxisMaximum();
            }
        });


        LineDataSet dataSet3 = new LineDataSet(serieHidraulica, "Hidraulica"); // add entries to dataset
        dataSet3.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet3.setColor(ContextCompat.getColor(chart.getContext(), R.color.hidraulica));
        dataSet3.setDrawCircles(false);
        dataSet3.setLineWidth(2f);
        dataSet3.setCircleRadius(3f);
        dataSet3.setFillAlpha(255);
        dataSet3.setDrawFilled(true);
        dataSet3.setFillColor(ContextCompat.getColor(chart.getContext(), R.color.ciclo_combinado));
        dataSet3.setHighLightColor(Color.rgb(244, 117, 117));
        dataSet3.setDrawCircleHole(false);
        dataSet3.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                // change the return value here to better understand the effect
                //return 550;
                return chart.getAxisLeft().getAxisMaximum();
            }
        });

        LineDataSet dataSet4 = new LineDataSet(serieCiclo_combinado, "Ciclo Combinado"); // add entries to dataset
        dataSet4.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet4.setColor(ContextCompat.getColor(chart.getContext(), R.color.ciclo_combinado));
        dataSet4.setDrawCircles(false);
        dataSet4.setLineWidth(2f);
        dataSet4.setCircleRadius(3f);
        dataSet4.setFillAlpha(255);
        dataSet4.setDrawFilled(true);
        dataSet4.setFillColor(ContextCompat.getColor(chart.getContext(), R.color.eolica));
        dataSet4.setHighLightColor(Color.rgb(244, 117, 117));
        dataSet4.setDrawCircleHole(false);
        dataSet4.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                // change the return value here to better understand the effect
                //return 550;
                return chart.getAxisLeft().getAxisMaximum();
            }
        });

        LineDataSet dataSet5 = new LineDataSet(serieEolica, "Eolica"); // add entries to dataset
        dataSet5.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet5.setColor(ContextCompat.getColor(chart.getContext(), R.color.eolica));
        dataSet5.setDrawCircles(false);
        dataSet5.setLineWidth(2f);
        dataSet5.setCircleRadius(3f);
        dataSet5.setFillAlpha(255);
        dataSet5.setDrawFilled(true);
        dataSet5.setFillColor(ContextCompat.getColor(chart.getContext(), R.color.solar_termica));
        dataSet5.setHighLightColor(Color.rgb(244, 117, 117));
        dataSet5.setDrawCircleHole(false);
        dataSet5.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                // change the return value here to better understand the effect
                //return 550;
                return chart.getAxisLeft().getAxisMaximum();
            }
        });


        LineDataSet dataSet6 = new LineDataSet(serieSolar_termica, "Solar Termica"); // add entries to dataset
        dataSet6.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet6.setColor(ContextCompat.getColor(chart.getContext(), R.color.solar_termica));
        dataSet6.setDrawCircles(false);
        dataSet6.setLineWidth(2f);
        dataSet6.setCircleRadius(3f);
        dataSet6.setFillAlpha(255);
        dataSet6.setDrawFilled(true);
        dataSet6.setFillColor(ContextCompat.getColor(chart.getContext(), R.color.solar_fotovoltaica));
        dataSet6.setHighLightColor(Color.rgb(244, 117, 117));
        dataSet6.setDrawCircleHole(false);
        dataSet6.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                // change the return value here to better understand the effect
                // return 0;
                return chart.getAxisLeft().getAxisMaximum();
            }
        });

        LineDataSet dataSet7 = new LineDataSet(serieSolar_fotovoltaica, "Solar Fotovoltaica"); // add entries to dataset
        dataSet7.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet7.setColor(ContextCompat.getColor(chart.getContext(), R.color.solar_fotovoltaica));
        dataSet7.setDrawCircles(false);
        dataSet7.setLineWidth(2f);
        dataSet7.setCircleRadius(3f);
        dataSet7.setFillAlpha(255);
        dataSet7.setDrawFilled(true);
        dataSet7.setFillColor(ContextCompat.getColor(chart.getContext(), R.color.cogen));
        dataSet7.setDrawCircleHole(false);
        dataSet7.setHighLightColor(Color.rgb(244, 117, 117));
        dataSet7.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                // change the return value here to better understand the effect
                // return 600;
                return chart.getAxisLeft().getAxisMaximum();
            }
        });

        LineDataSet dataSet8 = new LineDataSet(serieCogen, "Cogeneracion"); // add entries to dataset
        dataSet8.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet8.setColor(ContextCompat.getColor(chart.getContext(), R.color.cogen));
        dataSet8.setDrawCircles(false);
        dataSet8.setLineWidth(2f);
        dataSet8.setCircleRadius(3f);
        dataSet8.setFillAlpha(255);
        dataSet8.setDrawFilled(true);
        dataSet8.setFillColor(Color.WHITE);
        dataSet8.setDrawCircleHole(false);
        dataSet8.setHighLightColor(Color.rgb(244, 117, 117));

        dataSet8.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                // change the return value here to better understand the effect
                // return 600;
                return chart.getAxisLeft().getAxisMaximum();
            }
        });



        dataSets.add(dataSet1);
        dataSets.add(dataSet2);
        dataSets.add(dataSet3);
        dataSets.add(dataSet4);

        dataSets.add(dataSet5);
        dataSets.add(dataSet6);
        dataSets.add(dataSet7);
        dataSets.add(dataSet8);
    }

}
