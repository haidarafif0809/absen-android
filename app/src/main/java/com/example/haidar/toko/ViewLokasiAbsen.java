package com.example.haidar.toko;

/**
 * Created by haidar on 02/05/17.
 */

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class ViewLokasiAbsen extends AppCompatActivity implements View.OnClickListener  {



    private EditText editTextId;
    private EditText editTextLokasi;
    private EditText editTextLatitude;
    private EditText editTextLongitude;

    private Button buttonUpdate;
    private Button buttonDelete;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_lokasi);

        Intent intent = getIntent();

        id = intent.getStringExtra(Config.LOKASI_ID);

        editTextId = (EditText) findViewById(R.id.editTextId);
        editTextLokasi = (EditText) findViewById(R.id.editTextLokasi);
        editTextLatitude = (EditText) findViewById(R.id.editTextLatitude);
        editTextLongitude = (EditText) findViewById(R.id.editTextLongitude);

        buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
        buttonDelete = (Button) findViewById(R.id.buttonDelete);

        buttonUpdate.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);

        editTextId.setText(id);
        editTextId.setEnabled(false);

        getLokasi();
    }

    private void getLokasi(){
        class GetLokasi extends AsyncTask<Void,Void,String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ViewLokasiAbsen.this,"Fetching...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                showLokasi(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_GET_LOKASI,id);
                return s;
            }
        }
        GetLokasi ge = new GetLokasi();
        ge.execute();
    }


    private void showLokasi(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);
            JSONObject c = result.getJSONObject(0);
            String lokasi = c.getString(Config.TAG_NAMA_LOKASI);
            String latitude = c.getString(Config.TAG_LATITUDE);
            String longitude = c.getString(Config.TAG_LONGITUDE);

            editTextLatitude.setText(latitude);
            editTextLongitude.setText(longitude);
            editTextLokasi.setText(lokasi);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateLokasi(){

        final String lokasi = editTextLokasi.getText().toString().trim();
        final String latitude = editTextLatitude.getText().toString().trim();
        final String longitude = editTextLongitude.getText().toString().trim();

        class UpdateLokasi extends AsyncTask<Void,Void,String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ViewLokasiAbsen.this,"Updating...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(ViewLokasiAbsen.this,s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(Config.KEY_LOKASI_ID,id);
                hashMap.put(Config.KEY_NAMA_LOKASI,lokasi);
                hashMap.put(Config.KEY_LATITUDE,latitude);
                hashMap.put(Config.KEY_LONGITUDE,longitude);

                RequestHandler rh = new RequestHandler();

                String s = rh.sendPostRequest(Config.URL_UPDATE_LOKASI,hashMap);

                return s;
            }
        }

        UpdateLokasi ue = new UpdateLokasi();
        ue.execute();
    }

    private void deleteLokasi(){
        class DeleteLokasi extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ViewLokasiAbsen.this, "Updating...", "Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(ViewLokasiAbsen.this, s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_DELETE_LOKASI, id);
                return s;
            }
        }

        DeleteLokasi de = new DeleteLokasi();
        de.execute();
    }

    private void confirmDeleteLokasi(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Apakah anda yakin ingin menghapus Lokasi ini?");

        alertDialogBuilder.setPositiveButton("Ya",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        deleteLokasi();
                        startActivity(new Intent(ViewLokasiAbsen.this,ViewAllLokasi.class));
                    }
                });

        alertDialogBuilder.setNegativeButton("Tidak",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }



    @Override
    public void onClick(View v) {

        if(v == buttonUpdate){
            updateLokasi();
        }

        if(v == buttonDelete){
            confirmDeleteLokasi();
        }
    }
}
