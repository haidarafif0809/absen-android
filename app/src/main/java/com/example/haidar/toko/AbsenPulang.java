package com.example.haidar.toko;

/**
 * Created by haidar on 10/05/17.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AbsenPulang extends AppCompatActivity implements OnClickListener, AdapterView.OnItemSelectedListener {

    private Button buttonPulang;
    private EditText editTextNik;
    private  EditText editTextPassword;
    private String JSON_STRING;
    private  Spinner spinnerLokasi;

    private   List<String> categories = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.absen_pulang_activity);

        //Initializing views
        editTextNik = (EditText) findViewById(R.id.editTextNik);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        buttonPulang = (Button) findViewById(R.id.buttonPulang);

        //Setting listeners to button
        buttonPulang.setOnClickListener(this);


        // Spinner element
        spinnerLokasi = (Spinner) findViewById(R.id.lokasiAbsen);

        // Spinner click listener
        spinnerLokasi.setOnItemSelectedListener(this);

        getJSON();


    }

    //proses absen masuk
    private void prosesAbsenPulang(){

        final String nik = editTextNik.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String lokasi = spinnerLokasi.getSelectedItem().toString();

        class ProsesAbsenPulang extends AsyncTask<Void,Void,String>{

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(AbsenPulang.this,"Processing...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s.equals("1")){
                    Toast.makeText(getApplicationContext(),
                            "Berhasil Absen Pulang", Toast.LENGTH_LONG).show();
                }
                else if(s.equals("2")){

                    editTextNik.requestFocus();
                    editTextNik.setError( "Anda belum melakukan absen masuk atau Sudah Melakukan Absen Pulang!" );
                }
                else if(s.equals("0")){
                    editTextNik.requestFocus();
                    editTextNik.setError( "Username atau Password yang dimasukkan salah!" );
                }



            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put(Config.KEY_NIK,nik);
                params.put(Config.KEY_PASSWORD,password);
                params.put(Config.KEY_LOKASI,lokasi);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_PROSES_ABSEN_PULANG, params);
                return res;
            }
        }

        if (validasiForm() == true ){
            ProsesAbsenPulang pam = new ProsesAbsenPulang();
            pam.execute();
        }





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




                categories.add(nama);


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Spinner Drop down elements

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerLokasi.setAdapter(dataAdapter);

    }

    private void getJSON(){
        class GetJSON extends AsyncTask<Void,Void,String>{

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(AbsenPulang.this,"Fetching Data","Wait...",false,false);
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




    private boolean validasiForm(){

        if( editTextNik.getText().toString().trim().equals(""))
        {
            editTextNik.setError( "Username /  Nik Harus Di Isi" );



            return false;
        }
        else if(editTextPassword.getText().toString().trim().equals("")){

            editTextPassword.setError(" Password Harus Di Isi" );

            return false;
        }

        return true;
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

        switch (item.getItemId()) {
            case R.id.absen_masuk:
                //your code

                startActivity( new Intent(this, AbsenMasuk.class));

            case R.id.absen_pulang:
                //your code
                startActivity(new Intent(this, AbsenPulang.class));
            case R.id.admin:
                //your code
                startActivity(new Intent(this, LoginActivity.class));



            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onClick(View v) {

        if (v == buttonPulang){
            prosesAbsenPulang();

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
