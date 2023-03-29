package com.precioLuz.models;

public class User {

    //Propiedades de la clase
    private String id;
    private String email;
    private long timestamp;
    private boolean notifications;

    //Constructor
    public User() {

    }

    public User(String id, String email, long timestamp, boolean notifications) {
        this.id = id;
        this.email = email;
        this.timestamp = timestamp;
        this.notifications = notifications;
    }

    //Metodos Getter and Setter:
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }
}
