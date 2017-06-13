package com.example.haidar.toko.admin_activity;

/**
 * Created by haidar on 01/05/17.
 */
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.haidar.toko.R;
import com.example.haidar.toko.config.RequestHandler;
import com.example.haidar.toko.config.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class ViewAllLokasi extends AppCompatActivity implements ListView.OnItemClickListener {

    private ListView listDataLokasi;

    private String JSON_STRING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_lokasi);
        listDataLokasi = (ListView) findViewById(R.id.list_data_lokasi);
        listDataLokasi.setOnItemClickListener(this);
        getJSON();
    }


    private void showLokasi(){
        JSONObject jsonObject = null;
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);

            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);

                String id = jo.getString(Config.TAG_LOKASI_ID);
                String nama = jo.getString(Config.TAG_NAMA_LOKASI);
                String latitude = jo.getString(Config.TAG_LATITUDE);
                String longitude = jo.getString(Config.TAG_LONGITUDE);

                HashMap<String,String> lokasi = new HashMap<>();
                lokasi.put(Config.TAG_NAMA_LOKASI,nama);
                lokasi.put(Config.TAG_LATITUDE,latitude);
                lokasi.put(Config.TAG_LONGITUDE,longitude);
                lokasi.put(Config.TAG_LOKASI_ID,id);

                list.add(lokasi);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListAdapter adapter = new SimpleAdapter(
                ViewAllLokasi.this, list, R.layout.list_data_lokasi,
                new String[]{Config.TAG_LOKASI_ID,Config.TAG_NAMA_LOKASI,Config.TAG_LATITUDE,Config.TAG_LONGITUDE},
                new int[]{R.id.id,R.id.nama, R.id.latitude,R.id.longitude});

        listDataLokasi.setAdapter(adapter);
    }

    private void getJSON(){
        class GetJSON extends AsyncTask<Void,Void,String>{

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ViewAllLokasi.this,"Fetching Data","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                showLokasi();
            }

            @Override
            protected String doInBackground(Void... params) {

                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(Config.URL_GET_ALL_LOKASI);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(this, ViewLokasiAbsen.class);
        HashMap<String,String> map =(HashMap)parent.getItemAtPosition(position);
        String lokasiId = map.get(Config.TAG_LOKASI_ID).toString();
        intent.putExtra(Config.LOKASI_ID,lokasiId);
        startActivity(intent);
    }
}
