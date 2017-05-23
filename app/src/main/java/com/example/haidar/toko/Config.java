package com.example.haidar.toko;

/**
 * Created by haidar on 08/04/17.
 */

public class Config {
    //Address of our scripts of the CRUD




    public static final String urlAplikasi = "https://aac1945.com/absen_kosasih";
    public static final String URL_ADD_LOKASI = urlAplikasi+"/android/tambah_lokasi.php";
    public static final String URL_GET_ALL_LOKASI = urlAplikasi+"/android/data_lokasi.php";

    public static final String URL_GET_LOKASI = urlAplikasi+"/android/pilih_lokasi.php?id=";
    public static final String URL_UPDATE_LOKASI = urlAplikasi+"/android/update_lokasi.php";
    public static final String URL_DELETE_LOKASI = urlAplikasi+"/android/hapus_lokasi.php?id=";

    public static final String URL_VALIDASI_LOGIN_ADMIN = urlAplikasi+"/android/validasi_login_admin.php?id=";
    public static final String URL_PROSES_ABSEN_MASUK = urlAplikasi+"/android/absen_masuk.php";
    public static final String URL_PROSES_ABSEN_PULANG = urlAplikasi+"/android/absen_pulang.php";

    public static final String URL_ADD_USER = urlAplikasi+"/android/tambah_user.php";
    public static final String URL_GET_ALL_USER = urlAplikasi+"/android/data_user.php";
    public static final String URL_UPLOAD_FOTO = urlAplikasi+"/android/upload_foto.php";
    public static final String URL_USER_HADIR = urlAplikasi+"/android/karyawan_hadir.php";


    //Keys that will be used to send the request to php scripts
    public static final String KEY_UPLOAD = "image";
    public static final String KEY_LOKASI_ID = "id";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_NAMA_LOKASI = "nama";


    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_NAMA = "nama";

    //proses absen
    public static final String KEY_NIK = "nik";
    public static final String KEY_LOKASI = "lokasi";
    public static final String KEY_BATAS_JARAK = "batas_jarak_absen";




    //JSON Tags
    public static final String TAG_JSON_ARRAY="result";
    public static final String TAG_LOKASI_ID = "id";
    public static final String TAG_LATITUDE = "latitude";
    public static final String TAG_LONGITUDE = "longitude";
    public static final String TAG_NAMA_LOKASI = "nama";
    public static final String TAG_BATAS_JARAK = "batas_jarak_absen";
    public static final String TAG_WAKTU_MASUK = "waktu_masuk";

    public static final String TAG_USERNAME = "username";
    public static final String TAG_PASSWORD = "password";
    public static final String TAG_NAMA = "nama";
    public static final String TAG_NIK = "nik";
    public static final String TAG_ID = "id";

    //lokasi id to pass with intent
    public static final String LOKASI_ID = "id";
    public static final String USER_ID = "id";




}
