package com.example.haidar.presensi.config;

import com.example.haidar.presensi.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by HaidarAfif on 07/08/17.
 */

public class CrudService {
    private RegisterApi registerApi;

    public CrudService () {

        OkHttpClient.Builder okhttpBuilder = new OkHttpClient().newBuilder();
        okhttpBuilder.connectTimeout(60, TimeUnit.SECONDS);
        okhttpBuilder.writeTimeout(60, TimeUnit.SECONDS);
        okhttpBuilder.readTimeout(60, TimeUnit.SECONDS);
        okhttpBuilder.retryOnConnectionFailure(true);

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okhttpBuilder.addInterceptor(interceptor);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .client(okhttpBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        registerApi = retrofit.create(RegisterApi.class);

    }


    public void tampilAbsen( Callback callback){
        registerApi.tampilAbsen().enqueue(callback);
    }

    public void tampilUser( Callback callback){
        registerApi.tampilUser().enqueue(callback);
    }

    public void tampilLokasi( Callback callback){
        registerApi.tampilLokasi().enqueue(callback);
    }

    public void cariAbsen(String search, Callback callback){
        registerApi.searchAbsen(search).enqueue(callback);
    }
    public void statusAbsen(String nik, Callback callback){
        registerApi.statusAbsen(nik).enqueue(callback);
    }

    public void absenMasuk(String nik,String password,String lokasi,String latidue,String longitude, String image,Callback callback){
        registerApi.absenMasuk(nik,password,lokasi,latidue,longitude,image).enqueue(callback);
    }
    public void absenPulang(String nik,String password,String lokasi,String latidue,String longitude, String image,Callback callback){
        registerApi.absenPulang(nik,password,lokasi,latidue,longitude,image).enqueue(callback);
    }
}
