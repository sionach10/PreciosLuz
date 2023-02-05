package com.precioLuz.models;

public class PreciosJSON {
    private String date;
    private String hour;
    private boolean isCheap;
    private boolean isUnderAvg;
    private String market;
    private double price;
    private String units;

    //Constructor vacío.
    public PreciosJSON() {

    }

    public PreciosJSON(String date, String hour, boolean isCheap, boolean isUnderAvg, String market, double price, String units) {
        this.date = date;
        this.hour = hour;
        this.isCheap = isCheap;
        this.isUnderAvg = isUnderAvg;
        this.market = market;
        this.price = price;
        this.units = units;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }
}
