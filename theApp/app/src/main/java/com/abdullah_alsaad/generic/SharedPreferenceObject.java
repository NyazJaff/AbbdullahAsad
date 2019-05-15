package com.abdullah_alsaad.generic;

public class SharedPreferenceObject {

    String name, mp3URL,pdfURL;

    public SharedPreferenceObject(String name, String mp3URL, String pdfURL) {
        this.name = name;
        this.mp3URL = mp3URL;
        this.pdfURL = pdfURL;
    }

    public SharedPreferenceObject() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMp3URL() {
        return mp3URL;
    }

    public void setMp3URL(String mp3URL) {
        this.mp3URL = mp3URL;
    }

    public String getPdfURL() {
        return pdfURL;
    }

    public void setPdfURL(String pdfURL) {
        this.pdfURL = pdfURL;
    }
}
