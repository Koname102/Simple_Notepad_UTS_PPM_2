package com.diyas.uts_ppm_2.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "catatan")
public class Catatan {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String judul;
    private String isi;
    private String tanggal;

    // Getter dan Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getIsi() {
        return isi;
    }

    public void setIsi(String isi) {
        this.isi = isi;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}

