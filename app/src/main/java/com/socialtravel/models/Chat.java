package com.socialtravel.models;

public class Chat {

    private String idUser1;
    private String idUser2;
    private boolean isWritting;
    private long timestamp;

    public Chat() {

    }

    public Chat(String idUser1, String idUser2, boolean isWritting, long timestamp) {
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.isWritting = isWritting;
        this.timestamp = timestamp;
    }

    public boolean isWritting() {
        return isWritting;
    }

    public void setWritting(boolean writting) {
        isWritting = writting;
    }

    public String getIdUser1() {
        return idUser1;
    }

    public void setIdUser1(String idUser1) {
        this.idUser1 = idUser1;
    }

    public String getIdUser2() {
        return idUser2;
    }

    public void setIdUser2(String idUser2) {
        this.idUser2 = idUser2;
    }



    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
