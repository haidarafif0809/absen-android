package com.example.haidar.presensi.config;

/**
 * Created by HaidarAfif on 07/08/17.
 */

public class Result {
    String nik;
    String nama;
    String jam_masuk;
    String tanggal_masuk;
    String foto_masuk;
    String waktu_masuk;
    String latitude;

    public String getLongitude() {
        return longitude;
    }

    String longitude;

    public String getBatas_jarak_absen() {
        return batas_jarak_absen;
    }

    String batas_jarak_absen;

    public String getId() {
        return id;
    }

    String id;

    public String getNik() {
        return nik;
    }

    public String getNama() {
        return nama;
    }

    public String getJamMasuk() {
        return jam_masuk;
    }

    public String getTanggalMasuk() {
        return tanggal_masuk;
    }
    public String getFotoMasuk() {
        return foto_masuk;
    }
    public String getWaktuMasuk() {
        return waktu_masuk;
    }
    public String getLatitude() {
        return latitude;
    }

}
