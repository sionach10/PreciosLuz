package com.precioLuz.models;

import java.math.BigDecimal;

public class RespuestaESIOS {
    private PreciosJSON[] preciosJSON;
    private BigDecimal media;
    private String horaValle;
    private String horaPunta;
    private String precioValle;
    private String precioPunta;


    public RespuestaESIOS(PreciosJSON[] preciosJSON, BigDecimal media, String horaValle, String horaPunta, String precioValle, String precioPunta) {
        this.preciosJSON = preciosJSON;
        this.media = media;
        this.horaValle = horaValle;
        this.horaPunta = horaPunta;
        this.precioValle = precioValle;
        this.precioPunta = precioPunta;
    }

    public PreciosJSON[] getPreciosJSON() {
        return preciosJSON;
    }

    public void setPreciosJSON(PreciosJSON[] preciosJSON) {
        this.preciosJSON = preciosJSON;
    }

    public BigDecimal getMedia() {
        return media;
    }

    public void setMedia(BigDecimal media) {
        this.media = media;
    }

    public String getHoraValle() {
        return horaValle;
    }

    public void setHoraValle(String horaValle) {
        this.horaValle = horaValle;
    }

    public String getHoraPunta() {
        return horaPunta;
    }

    public void setHoraPunta(String horaPunta) {
        this.horaPunta = horaPunta;
    }

    public String getPrecioValle() {
        return precioValle;
    }

    public void setPrecioValle(String precioValle) {
        this.precioValle = precioValle;
    }

    public String getPrecioPunta() {
        return precioPunta;
    }

    public void setPrecioPunta(String precioPunta) {
        this.precioPunta = precioPunta;
    }
}
