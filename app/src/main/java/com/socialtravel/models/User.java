package com.socialtravel.models;

public class User {

    //Propiedades de la clase
    private String id;
    private String email;
    private String username;
    //private String password; //Para guardar la contraseña (estaría feo).

    //Constructor
    public User() {

    }

    public User(String id, String email, String username) {
        this.id = id;
        this.email = email;
        this.username = username;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
