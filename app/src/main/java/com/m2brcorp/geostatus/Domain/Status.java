package com.m2brcorp.geostatus.Domain;

/**
 * Created by vinic on 04/05/2017.
 */

public class Status {

    private String status;

    private String data;

    private String hora;

    public Status(String status, String data, String hora) {
        this.status = status;
        this.data = data;
        this.hora = hora;
    }

    public Status() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}
