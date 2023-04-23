package com.precioLuz.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.precioLuz.models.EnergyByTechnology;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class TxtParser {

    private static final String separador = ";";

    public static EnergyByTechnology obtenerSeriesPorTecnologia(String response) throws FileNotFoundException {

        EnergyByTechnology energyByTechnology = new EnergyByTechnology();
        String linea = "";
        Map<Integer, Float> carbon = new HashMap<>();
        Map<Integer, Float> nuclear = new HashMap<>();
        Map<Integer, Float> hidraulica = new HashMap<>();
        Map<Integer, Float> ciclo_combinado = new HashMap<>();
        Map<Integer, Float> eolica = new HashMap<>();
        Map<Integer, Float> solar_termica = new HashMap<>();
        Map<Integer, Float> solar_fotovoltaica = new HashMap<>();
        Map<Integer, Float> cogen = new HashMap<>();

        try{
            BufferedReader bf = new BufferedReader(new StringReader(response));

            for(int i = 0; i< 28; i++) {
                linea = bf.readLine();
                String [] campos = linea.split(separador);

                if(i>2 && campos.length != 0) {//Las dos primeras lineas son el titulo del fichero.

                    //Parseo de nulos por 0s, separador de miles y separador decimal:
                    for(int j = 0; j<campos.length;j++) {
                        campos[j] = campos[j].replace(".", "");
                        campos[j] = campos[j].replace(",", ".");
                        campos[j] = (Objects.equals(campos[j], ""))?"0":campos[j];
                    }

                    carbon.put(Integer.valueOf(campos[1]), Float.valueOf(campos[2]));
                    nuclear.put(Integer.valueOf(campos[1]), Float.valueOf(campos[5]));
                    hidraulica.put(Integer.valueOf(campos[1]), Float.valueOf(campos[6]));
                    ciclo_combinado.put(Integer.valueOf(campos[1]), Float.valueOf(campos[7]));
                    eolica.put(Integer.valueOf(campos[1]), Float.valueOf(campos[8]));
                    solar_termica.put(Integer.valueOf(campos[1]), Float.valueOf(campos[9]));
                    solar_fotovoltaica.put(Integer.valueOf(campos[1]), Float.valueOf(campos[10]));
                    cogen.put(Integer.valueOf(campos[1]), Float.valueOf(campos[11]));
                }
            }

            energyByTechnology.setCarbon(carbon);
            energyByTechnology.setNuclear(nuclear);
            energyByTechnology.setHidraulica(hidraulica);
            energyByTechnology.setCiclo_combinado(ciclo_combinado);
            energyByTechnology.setEolica(eolica);
            energyByTechnology.setSolar_termica(solar_termica);
            energyByTechnology.setSolar_fotovoltaica(solar_fotovoltaica);
            energyByTechnology.setCogen(cogen);

        }
        catch (Exception e){
            Log.d("Error: ", String.valueOf(e));
        }
        return energyByTechnology;
    }

}
