package com.precioLuz.models;

import java.util.HashMap;
import java.util.Map;

public class EnergyByTechnology {
    //Variables
    Map<Integer, Float> carbon = new HashMap<>();
    Map<Integer, Float> nuclear = new HashMap<>();
    Map<Integer, Float> hidraulica = new HashMap<>();
    Map<Integer, Float> ciclo_combinado = new HashMap<>();
    Map<Integer, Float> eolica = new HashMap<>();
    Map<Integer, Float> solar_termica = new HashMap<>();
    Map<Integer, Float> solar_fotovoltaica = new HashMap<>();
    Map<Integer, Float> cogen = new HashMap<>();

    //Constructor vacio
    public EnergyByTechnology() {}

    public Map<Integer, Float> getCarbon() {
        return carbon;
    }

    public void setCarbon(Map<Integer, Float> carbon) {
        this.carbon = carbon;
    }

    public Map<Integer, Float> getNuclear() {
        return nuclear;
    }

    public void setNuclear(Map<Integer, Float> nuclear) {
        this.nuclear = nuclear;
    }

    public Map<Integer, Float> getHidraulica() {
        return hidraulica;
    }

    public void setHidraulica(Map<Integer, Float> hidraulica) {
        this.hidraulica = hidraulica;
    }

    public Map<Integer, Float> getCiclo_combinado() {
        return ciclo_combinado;
    }

    public void setCiclo_combinado(Map<Integer, Float> ciclo_combinado) {
        this.ciclo_combinado = ciclo_combinado;
    }

    public Map<Integer, Float> getEolica() {
        return eolica;
    }

    public void setEolica(Map<Integer, Float> eolica) {
        this.eolica = eolica;
    }

    public Map<Integer, Float> getSolar_termica() {
        return solar_termica;
    }

    public void setSolar_termica(Map<Integer, Float> solar_termica) {
        this.solar_termica = solar_termica;
    }

    public Map<Integer, Float> getSolar_fotovoltaica() {
        return solar_fotovoltaica;
    }

    public void setSolar_fotovoltaica(Map<Integer, Float> solar_fotovoltaica) {
        this.solar_fotovoltaica = solar_fotovoltaica;
    }

    public Map<Integer, Float> getCogen() {
        return cogen;
    }

    public void setCogen(Map<Integer, Float> cogen) {
        this.cogen = cogen;
    }
}
