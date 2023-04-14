package com.precioLuz.models;

public class PreciosJSON {
    //Variables
    private String hour;
    private boolean isCheap;
    private boolean isUnderAvg;
    private String price;

    //Constructor vacío.
    public PreciosJSON() {}

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public boolean isCheap() {
        return isCheap;
    }

    public void setCheap(boolean cheap) {
        isCheap = cheap;
    }

    public boolean isUnderAvg() {
        return isUnderAvg;
    }

    public void setUnderAvg(boolean underAvg) {
        isUnderAvg = underAvg;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
