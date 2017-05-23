package com.example.haidar.toko;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class UserHadirActivity extends AppCompatActivity {

    private ListView listDataHadir;

    private String JSON_STRING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_hadir);

        listDataHadir = (ListView) findViewById(R.id.list_user_hadir);

        getJSON();
    }

    private void showHadir(){

        JSONObject jsonObject = null;
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);

            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);

                String nama = jo.getString(Config.TAG_NAMA);
                String nik = jo.getString(Config.TAG_NIK);
                String waktu_masuk = jo.getString(Config.TAG_WAKTU_MASUK);

                HashMap<String,String> lokasi = new HashMap<>();
                lokasi.put(Config.TAG_NAMA,nama);
                lokasi.put(Config.TAG_NIK,nik);
                lokasi.put(Config.TAG_WAKTU_MASUK,waktu_masuk);


                list.add(lokasi);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListAdapter adapter = new SimpleAdapter(
                UserHadirActivity.this, list, R.layout.list_user_hadir,
                new String[]{Config.TAG_NAMA,Config.TAG_NIK,Config.TAG_WAKTU_MASUK},
                new int[]{R.id.nama,R.id.nik, R.id.waktu_masuk});

        listDataHadir.setAdapter(adapter);
    }

    private void getJSON(){
        class GetJSON extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(UserHadirActivity.this,"Fetching Data","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                showHadir();
            }

            @Override
            protected String doInBackground(Void... params) {

                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(Config.URL_USER_HADIR);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }
    // membuat menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        if (item.getItemId() ==  R.id.absen_masuk) {
            startActivity( new Intent(this, AbsenMasuk.class));
        }
        if (item.getItemId() ==  R.id.absen_pulang) {
            startActivity( new Intent(this, AbsenPulang.class));

        }
        if (item.getItemId() ==  R.id.admin) {
            startActivity( new Intent(this, LoginActivity.class));

        }
        if (item.getItemId() ==  R.id.user_hadir) {
            startActivity( new Intent(this, UserHadirActivity.class));

        }


        return super.onOptionsItemSelected(item);
    }




}
