package com.example.christian.meinesachen;

public class Model {
    private int id;
    private String sache;
    private String preis;
    private String datum;
    private byte[] image1;
    private byte[] image2;

    public Model(int id, String sache, String preis, String datum, byte[] image1, byte[] image2) {
        this.id = id;
        this.sache = sache;
        this.preis = preis;
        this.datum = datum;
        this.image1 = image1;
        this.image2 = image2;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSache() {
        return sache;
    }

    public void setSache(String sache) {
        this.sache = sache;
    }

    public String getPreis() {
        return preis;
    }

    public void setPreis(String preis) {
        this.preis = preis;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public byte[] getImage1() {
        return image1;
    }

    public void setImage1(byte[] image) {
        this.image1 = image1;
    }

    public byte[] getImage2() {
        return image2;
    }

    public void setImage2(byte[] image2) {
        this.image2 = image2;
    }
}

