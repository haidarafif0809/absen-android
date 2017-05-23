package com.example.haidar.toko;

/**
 * Created by haidar on 28/04/17.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import com.google.android.gms.location.LocationServices;

import java.util.HashMap;

public class LokasiAbsen extends AppCompatActivity implements OnClickListener, OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    //Defining views
    private EditText editTextNama;
    private EditText editTextLatitude;
    private EditText editTextLongitude;
    private EditText editTextBatasJarak;

    private Button buttonAdd,buttonView,buttonGetLoc;


    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    public static final Integer LOCATION = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_lokasi);

        //Initializing views
        editTextNama = (EditText) findViewById(R.id.textLokasi);
        editTextLatitude = (EditText) findViewById(R.id.textLatitude);
        editTextLongitude = (EditText) findViewById(R.id.textLongitude);
        editTextBatasJarak = (EditText) findViewById(R.id.textBatasJarak);

        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonView = (Button) findViewById(R.id.buttonView);
        buttonGetLoc = (Button) findViewById(R.id.buttonGetLoc);
        //Setting listeners to button
        buttonAdd.setOnClickListener(this);
        buttonView.setOnClickListener(this);
        buttonGetLoc.setOnClickListener(this);

        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        // Show location button click listener
        buttonGetLoc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                displayLocation();
            }
        });

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
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            editTextLatitude.setText(String.valueOf(latitude));
            editTextLongitude.setText(String.valueOf(longitude));

        } else {
            editTextLatitude.requestFocus();
            editTextLatitude.setError("(Couldn't get the location. Make sure location is enabled on the device)");
        }
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


    //Adding an employee
    private void tambahLokasi() {

        final String nama = editTextNama.getText().toString().trim();
        final String latitude = editTextLatitude.getText().toString().trim();
        final String longitude = editTextLongitude.getText().toString().trim();
        final String batas_jarak = editTextBatasJarak.getText().toString().trim();


        class TambahLokasi extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LokasiAbsen.this, "Adding...", "Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(LokasiAbsen.this, s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                params.put(Config.KEY_NAMA_LOKASI, nama);
                params.put(Config.KEY_LATITUDE, latitude);
                params.put(Config.KEY_LONGITUDE, longitude);
                params.put(Config.KEY_BATAS_JARAK, batas_jarak);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_ADD_LOKASI, params);
                return res;
            }
        }

        if (validasiForm() == true) {
            TambahLokasi ae = new TambahLokasi();
            ae.execute();
        }


    }


    private boolean validasiForm() {


        if (editTextNama.getText().toString().trim().equals("")) {

            editTextNama.requestFocus();
            editTextNama.setError("Nama Lokasi Harus Di Isi");

            return false;
        } else if (editTextLatitude.getText().toString().trim().equals("")) {

            editTextLatitude.requestFocus();
            editTextLatitude.setError("Latitude Lokasi Harus Di Isi");

            return false;
        } else if (editTextLongitude.getText().toString().trim().equals("")) {

            editTextLongitude.requestFocus();
            editTextLongitude.setError("Latitude Lokasi Harus Di Isi");

            return false;
        }
        else if (editTextBatasJarak.getText().toString().trim().equals("")) {

            editTextBatasJarak.requestFocus();
            editTextBatasJarak.setError("Batas Jarak Absen Harus Di Isi");

            return false;
        }



        return true;
    }


    @Override
    public void onClick(View v) {

        if (v == buttonAdd) {
            tambahLokasi();
        }

        if (v == buttonView) {
            startActivity(new Intent(this, ViewAllLokasi.class));
        }

        if (v == buttonGetLoc) {
            mGoogleApiClient.connect();
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
