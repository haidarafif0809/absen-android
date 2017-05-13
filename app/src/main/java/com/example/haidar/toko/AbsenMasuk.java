package com.example.haidar.toko;

/**
 * Created by haidar on 10/05/17.
 */


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
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
    private TextView textLatitude,textLongitude,textJarakLokasiAbsen,textBatasJarakAbsen;



    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();

   //variable untuk kebutuhan lokasi
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;
    LocationManager locationManager ;
    boolean GpsStatus ;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private   List<String> nama_lokasi_absen = new ArrayList<String>();
    private   List<String> latitude_lokasi_absen = new ArrayList<String>();
    private   List<String> longitude_lokasi_absen = new ArrayList<String>();
    private   List<String> data_batas_jarak_absen = new ArrayList<String>();


    // Location updates intervals in sec

    public static final Integer LOCATION = 0x1;
    public double latitude_saat_ini,longitude_saat_ini,latitude_absen,longitude_absen;
    public int batas_jarak_absen;
    public float jarak_ke_lokasi_absen;

    //variable untuk kebutuhan upload foto
    private ImageView imageView;
    private Bitmap bitmap;
    private static final int CAMERA_REQUEST = 1888;
    private boolean status_ambil_foto;



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
        textBatasJarakAbsen = (TextView) findViewById(R.id.textBatasJarakAbsen);
        buttonMasuk = (Button) findViewById(R.id.buttonMasuk);
        imageView = (ImageView) findViewById(R.id.imageView);
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


    //PROSES UNTUK MENGAKTIFKAN KAMERA
    private void ambilFoto() {

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }
    // PROSES SETEALH AMBIL FOTO
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");

            imageView.setImageBitmap(bitmap);
            status_ambil_foto = true;

            prosesAbsenMasuk();
        }
    }


    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
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
        String batas_jarak = data_batas_jarak_absen.get(position);
        batas_jarak_absen = Integer.valueOf(data_batas_jarak_absen.get(position));

        Location loc1 = new Location("");
        loc1.setLatitude(latitude_saat_ini);
        loc1.setLongitude(longitude_saat_ini);

        Location loc2 = new Location("");
        loc2.setLatitude(Double.valueOf(latitude));
        loc2.setLongitude(Double.valueOf(longitude));

         jarak_ke_lokasi_absen = loc1.distanceTo(loc2);


        textJarakLokasiAbsen.setText(String.valueOf(round(jarak_ke_lokasi_absen)));
        textBatasJarakAbsen.setText(batas_jarak);

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Jarak Ke Lokasi Absen : " + String.valueOf(round(jarak_ke_lokasi_absen)) + " m", Toast.LENGTH_LONG).show();
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



        class ProsesAbsenMasuk extends AsyncTask<Bitmap,Void,String>{

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
                    Toast.makeText(getApplicationContext(), "Berhasil Absen Masuk", Toast.LENGTH_LONG).show();
                    editTextNik.setText("");
                    editTextNik.setError(null);
                    editTextPassword.setText("");
                    editTextNik.requestFocus();
                    imageView.setImageBitmap(null);

                }
                else if(s.equals("2")){

                    editTextPassword.setText("");
                    imageView.setImageBitmap(null);
                    editTextNik.requestFocus();
                    editTextNik.setError( "Anda Sudah Melakukan Absen Masuk!" );
                }
                else if(s.equals("0")){

                    imageView.setImageBitmap(null);
                    editTextPassword.setText("");
                    editTextNik.requestFocus();
                    editTextNik.setError( "Username atau Password yang dimasukkan salah!" );
                }


            }

            @Override
            protected String doInBackground(Bitmap... params) {

                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);

                HashMap<String,String> data = new HashMap<>();

                data.put(Config.KEY_NIK,nik);
                data.put(Config.KEY_PASSWORD,password);
                data.put(Config.KEY_LOKASI,lokasi);
                data.put(Config.KEY_LATITUDE,String.valueOf(latitude_saat_ini));
                data.put(Config.KEY_LONGITUDE,String.valueOf(longitude_saat_ini));
                data.put(Config.KEY_UPLOAD,uploadImage);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_PROSES_ABSEN_MASUK, data);
                return res;
            }
        }

        //untuk mengambil latitude dan longitude terbaru
        CheckGpsStatus();

        displayLocation();


        if (GpsStatus == true){

            if (jarak_ke_lokasi_absen <= batas_jarak_absen){
                if (validasiForm() == true  && (latitude_saat_ini != 0  && latitude_saat_ini != 0  )){
                    if (bitmap != null) {
                        ProsesAbsenMasuk pam = new ProsesAbsenMasuk();
                        pam.execute(bitmap);
                    }


                }
            }
            else {
                editTextNik.requestFocus();
                editTextNik.setError( "Lokasi Anda Terlalu Jauh Dari Lokasi Absen!" );
            }

        }

    }



    //untuk menampilkan lokasi di spinner
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
                String batas_jarak = jo.getString(Config.TAG_BATAS_JARAK);




                nama_lokasi_absen.add(nama);
                latitude_lokasi_absen.add(latitude);
                longitude_lokasi_absen.add(longitude);
                data_batas_jarak_absen.add(batas_jarak);


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

        if (item.getItemId() ==  R.id.absen_masuk) {
            startActivity( new Intent(this, AbsenMasuk.class));
        }
        if (item.getItemId() ==  R.id.absen_pulang) {
            startActivity( new Intent(this, AbsenPulang.class));

        }
        if (item.getItemId() ==  R.id.admin) {
            startActivity( new Intent(this, LoginActivity.class));

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        if (v == buttonMasuk){
            ambilFoto();
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
