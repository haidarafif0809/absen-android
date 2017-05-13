package com.example.haidar.toko;

/**
 * Created by haidar on 10/05/17.
 */


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Math.round;


public class AbsenMasuk  extends AppCompatActivity implements OnClickListener, AdapterView.OnItemSelectedListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private Button buttonMasuk;
    private EditText editTextNik;
    private  EditText editTextPassword;
    private String JSON_STRING;
    private  Spinner spinnerLokasi;
    private TextView textLatitude,textLongitude,textJarakLokasiAbsen;

    private   List<String> nama_lokasi_absen = new ArrayList<String>();
    private   List<String> latitude_lokasi_absen = new ArrayList<String>();
    private   List<String> longitude_lokasi_absen = new ArrayList<String>();

    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    LocationManager locationManager ;
    boolean GpsStatus ;



    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters
    public static final Integer LOCATION = 0x1;
    public double latitude_saat_ini,longitude_saat_ini,latitude_absen,longitude_absen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.absen_masuk_activity);

        //Initializing views
        editTextNik = (EditText) findViewById(R.id.editTextNik);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        textLatitude = (TextView) findViewById(R.id.latitude);
        textLongitude = (TextView) findViewById(R.id.longitude);
        textJarakLokasiAbsen = (TextView) findViewById(R.id.jarakKeLokasi);

        buttonMasuk = (Button) findViewById(R.id.buttonMasuk);

        //Setting listeners to button
        buttonMasuk.setOnClickListener(this);


        // Spinner element
       spinnerLokasi = (Spinner) findViewById(R.id.lokasiAbsen);

        // Spinner click listener
        spinnerLokasi.setOnItemSelectedListener(this);

        getJSON();

        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();

        }

        CheckGpsStatus();


    }

    private void konfirmasiSettingGps(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Aplikasi ini membutuhkan GPS akurasi tinggi, Aktifkan GPS?");

        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void CheckGpsStatus(){

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(GpsStatus == true)
        {
            Toast.makeText(this,"Location Services Is Enabled", Toast.LENGTH_LONG).show();
        }else {
            konfirmasiSettingGps();
        }

    }



    private void displayLocation() {



        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {


            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION);

            }
        }

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            latitude_saat_ini=  mLastLocation.getLatitude();
            longitude_saat_ini = mLastLocation.getLongitude();

            textLatitude.setText(String.valueOf(latitude_saat_ini));
            textLongitude.setText(String.valueOf(longitude_saat_ini));

        } else {

           String alert =  "(Couldn't get the location. Make sure location is enabled on the device)";

            Toast.makeText(this,alert, Toast.LENGTH_LONG).show();



        }
    }



    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }
    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }




    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        String latitude =  latitude_lokasi_absen.get(position);
        String longitude =  longitude_lokasi_absen.get(position);

        Location loc1 = new Location("");
        loc1.setLatitude(latitude_saat_ini);
        loc1.setLongitude(longitude_saat_ini);

        Location loc2 = new Location("");
        loc2.setLatitude(Double.valueOf(latitude));
        loc2.setLongitude(Double.valueOf(longitude));

        float distanceInMeters = loc1.distanceTo(loc2);


        textJarakLokasiAbsen.setText(String.valueOf(round(distanceInMeters)));
        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Jarak Ke Lokasi Absen : " + String.valueOf(round(distanceInMeters)) + " m", Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }


    //proses absen masuk
    private void prosesAbsenMasuk(){

        final String nik = editTextNik.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String lokasi = spinnerLokasi.getSelectedItem().toString();

        latitude_saat_ini = 0;
        longitude_saat_ini = 0;



        class ProsesAbsenMasuk extends AsyncTask<Void,Void,String>{

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(AbsenMasuk.this,"Processing...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s.equals("1")){
                    Toast.makeText(getApplicationContext(),
                            "Berhasil Absen Masuk", Toast.LENGTH_LONG).show();
                }
                else if(s.equals("2")){

                    editTextNik.requestFocus();
                    editTextNik.setError( "Anda Sudah Melakukan Absen Masuk!" );
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
                params.put(Config.KEY_LATITUDE,String.valueOf(latitude_saat_ini));
                params.put(Config.KEY_LONGITUDE,String.valueOf(longitude_saat_ini));


                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_PROSES_ABSEN_MASUK, params);
                return res;
            }
        }
        //untuk mengambil latitude dan longitude terbaru
        CheckGpsStatus();

        displayLocation();

        if (GpsStatus == true){
            if (validasiForm() == true  && (latitude_saat_ini != 0  && latitude_saat_ini != 0  )){
                ProsesAbsenMasuk pam = new ProsesAbsenMasuk();
                pam.execute();
            }
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
                String latitude = jo.getString(Config.TAG_LATITUDE);
                String longitude = jo.getString(Config.TAG_LONGITUDE);




                nama_lokasi_absen.add(nama);
                latitude_lokasi_absen.add(latitude);
                longitude_lokasi_absen.add(longitude);


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Spinner Drop down elements

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nama_lokasi_absen);

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
                loading = ProgressDialog.show(AbsenMasuk.this,"Fetching Data","Wait...",false,false);
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

        if (v == buttonMasuk){
            prosesAbsenMasuk();
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }
}
