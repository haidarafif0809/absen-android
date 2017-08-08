package com.example.haidar.presensi.config;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by HaidarAfif on 07/08/17.
 */

public interface RegisterApi {

    @GET("tampil_data_absen_retrofit.php")
    Call<Value> tampilAbsen();

    @GET("tampil_data_lokasi_retrofit.php")
    Call<Value> tampilLokasi();

    @GET("tampil_data_user_retrofit.php")
    Call<Value> tampilUser();

    @FormUrlEncoded
    @POST("cari_data_absen_retrofit.php")
    Call<Value> searchAbsen(@Field("search") String search);

    @FormUrlEncoded
    @POST("absen_masuk_retrofit.php")
    Call<Value> absenMasuk(@Field("nik") String nik,
                       @Field("password") String password,
                       @Field("lokasi") String lokasi,
                       @Field("latitude") String latitude ,
                       @Field("longitude") String longitude,
                       @Field("image") String image);
    @FormUrlEncoded
    @POST("absen_pulang_retrofit.php")
    Call<Value> absenPulang(@Field("nik") String nik,
                       @Field("password") String password,
                       @Field("lokasi") String lokasi,
                       @Field("latitude") String latitude ,
                       @Field("longitude") String longitude,
                       @Field("image") String image);


}
