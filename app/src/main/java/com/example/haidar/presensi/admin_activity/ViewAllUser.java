package com.example.haidar.toko.admin_activity;

/**
 * Created by haidar on 11/05/17.
 */

import android.app.ProgressDialog;
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


public class ViewAllUser extends AppCompatActivity implements ListView.OnItemClickListener {


    private ListView listDataUser;

    private String JSON_STRING;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);
        listDataUser = (ListView) findViewById(R.id.list_data_user);
        listDataUser.setOnItemClickListener(this);
        getJSON();
    }

    private void showUser(){
        JSONObject jsonObject = null;
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);

            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);

                String id = jo.getString(Config.TAG_ID);
                String nama = jo.getString(Config.TAG_NAMA);
                String nik = jo.getString(Config.TAG_NIK);

                HashMap<String,String> lokasi = new HashMap<>();
                lokasi.put(Config.TAG_NAMA,nama);
                lokasi.put(Config.TAG_NIK,nik);
                lokasi.put(Config.TAG_ID,id);

                list.add(lokasi);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListAdapter adapter = new SimpleAdapter(
                ViewAllUser.this, list, R.layout.list_data_user,
                new String[]{Config.TAG_ID,Config.TAG_NIK,Config.TAG_NAMA},
                new int[]{R.id.id,R.id.nik, R.id.nama});

        listDataUser.setAdapter(adapter);
    }

    private void getJSON(){
        class GetJSON extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ViewAllUser.this,"Fetching Data","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                showUser();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(Config.URL_GET_ALL_USER);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
