package com.example.visorieti.questionario;

public class Questionario {

//    private String llave;

    private String entrevistaId;
    private String datos;
    private String notas;

    public String getNotas() { return notas; }

    public void setNotas(String notas) { this.notas = notas; }

//    public String getLlave() {
//        return llave;
//    }
//
//    public void setLlave(String llave) {
//        this.llave = llave;
//    }

    public String getDatos() {
        return datos;
    }

    public void setDatos(String datos) {
        this.datos = datos;
    }

    public String getEntrevistaId() {
        return entrevistaId;
    }

    public void setEntrevistaId(String entrevistaId) {
        this.entrevistaId = entrevistaId;
    }

    public Questionario(String llave, String datos, String notas, String entrevistaId) {
//        this.llave = llave;
        this.entrevistaId = entrevistaId;
        this.datos = datos;
        this.notas = notas;
    }
}
