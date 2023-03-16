package com.precioLuz.models;

public class PreciosJSON {
    private String hour;
    private boolean isCheap;
    private boolean isUnderAvg;
    private String price;

    //Constructor vacío.
    public PreciosJSON() {}

    public PreciosJSON(String date, String hour, boolean isCheap, boolean isUnderAvg, String market, String price, String units) {
        this.hour = hour;
        this.isCheap = isCheap;
        this.isUnderAvg = isUnderAvg;
        this.price = price;
    }

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
