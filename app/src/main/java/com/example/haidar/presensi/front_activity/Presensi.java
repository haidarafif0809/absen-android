package com.example.haidar.presensi.front_activity;

/**
 * Created by haidar on 10/05/17.
 */


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
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

import com.example.haidar.presensi.R;
import com.example.haidar.presensi.admin_activity.MainActivity;
import com.example.haidar.presensi.config.BaseActivity;
import com.example.haidar.presensi.config.CrudService;
import com.example.haidar.presensi.config.Result;
import com.example.haidar.presensi.config.Value;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.kishan.askpermission.AskPermission;
import com.kishan.askpermission.ErrorCallback;
import com.kishan.askpermission.PermissionCallback;
import com.kishan.askpermission.PermissionInterface;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Math.round;
import static java.sql.Types.NULL;


public class Presensi extends BaseActivity implements OnClickListener,
        AdapterView.OnItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener , PermissionCallback,
        ErrorCallback {

    private static final int REQUEST_PERMISSIONS = 20;
    private Button buttonMasuk;
    private EditText editTextNik, editTextPassword,editTextKeterangan;
    private String JSON_STRING;
    private  Spinner spinnerLokasi;
    private TextView textJarakLokasiAbsen,textBatasJarakAbsen;

    private String encodedImage;

    private ProgressDialog progress;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = true;
    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 5000; // 10 sec
    private static int FATEST_INTERVAL = 2000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

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
    private List<Result> results = new ArrayList<>();



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

    private FloatingActionButton fabMyLocation;

    private int STATUS_ABSEN;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.absen_masuk_activity);

        //Initializing views
        editTextNik = (EditText) findViewById(R.id.editTextNik);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextKeterangan = (EditText) findViewById(R.id.editTextKeterangan);
        textJarakLokasiAbsen = (TextView) findViewById(R.id.jarakKeLokasi);
        textBatasJarakAbsen = (TextView) findViewById(R.id.textBatasJarakAbsen);
        buttonMasuk = (Button) findViewById(R.id.buttonMasuk);
        imageView = (ImageView) findViewById(R.id.imageView);
        fabMyLocation = (FloatingActionButton) findViewById(R.id.fabMyLocation);
        //Setting listeners to button
        buttonMasuk.setOnClickListener(this);

        fabMyLocation.setOnClickListener(this);

        editTextNik.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)

                    cekStatusAbsen(editTextNik.getText().toString());
                 }
        });

        // Spinner element
       spinnerLokasi = (Spinner) findViewById(R.id.lokasiAbsen);

        // Spinner click listener
        spinnerLokasi.setOnItemSelectedListener(this);
        reqPermission();

        showLokasi();


        CheckGpsStatus();

        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();

        }


    }
    //mengecek status absen apakah masuk atau sudah pulang
    private void cekStatusAbsen(String s) {

        if (!s.equals("")){

            CrudService crud = new CrudService();
            crud.statusAbsen(s, new Callback<Value>() {
                @Override
                public void onResponse(Call<Value> call, Response<Value> response) {
                    String value = response.body().getValue();
                    //nik salah
                    if (value.equals("0")) {

                        editTextNik.setError("Nik Tidak Terdaftar");
                        editTextNik.requestFocus();
                    }
                    //sudah absen masuk
                    else if(value.equals("2")) {
                        buttonMasuk.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_pulang, 0, 0, 0);
                        buttonMasuk.setText("Presensi Pulang");
                        STATUS_ABSEN = 1;
                    }
                    //sudah absen pulang
                    else if(value.equals("3")) {
                        buttonMasuk.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_kerja, 0, 0, 0);
                        buttonMasuk.setText("Presensi Masuk");
                        STATUS_ABSEN = 2;
                    }

                }

                @Override
                public void onFailure(Call call, Throwable t) {

                }
            });
        }
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

            getStringImage(bitmap);

            prosesAbsen();
        }
    }

    //mengubah foto menjadi bitmap agar bisa di kirim ke server
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
         encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    private void alertSatuTombol(String alert){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(alert);


        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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

        }else {
            konfirmasiSettingGps();
        }

    }



    private void displayLocation() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                reqPermission();

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



            if (batas_jarak_absen != NULL) {
                update_jarak_absen();
            }


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

        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        stopLocationUpdates();
    }


    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 10 meters

    }

    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            reqPermission();
        }
        createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
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

        update_jarak_absen();

    }


    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    //proses absen masuk
    private void prosesAbsen(){

        final String nik = editTextNik.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String keterangan = editTextKeterangan.getText().toString().trim();
        final String lokasi = spinnerLokasi.getSelectedItem().toString();
        latitude_saat_ini = 0;
        longitude_saat_ini = 0;

        //untuk mengambil latitude dan longitude terbaru
        CheckGpsStatus();

        displayLocation();

        if (GpsStatus == true){

            if (jarak_ke_lokasi_absen <= batas_jarak_absen){
                if (validasiForm() == true  && (latitude_saat_ini != 0  && latitude_saat_ini != 0  )){
                    if (bitmap != null) {
                        if(STATUS_ABSEN == 2){
                            absenMasuk(nik, password, lokasi,keterangan);
                        }
                        else if (STATUS_ABSEN == 1){
                            absenPulang(nik,password,lokasi,keterangan);
                        }

                    }
                }
            }
            else {
                editTextNik.requestFocus();
                editTextNik.setError( "Lokasi Anda Terlalu Jauh Dari Lokasi Absen!" );
            }

        }

    }

    private void absenMasuk(String nik, String password, String lokasi, String keterangan) {

        //membuat progress dialog
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Proses Absen Masuk ...");
        progress.show();


        CrudService crud = new CrudService();
        crud.absenMasuk(nik, password, lokasi, String.valueOf(latitude_saat_ini), String.valueOf(longitude_saat_ini), encodedImage,keterangan, new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                String value = response.body().getValue();
                String message = response.body().getMessage();
                progress.dismiss();
                if (value.equals("1")) {
                    alertSatuTombol(message);
                    kosongkanForm();
                } else {
                    alertSatuTombol(message);
                    editTextPassword.setText("");
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                progress.dismiss();
                alertSatuTombol("Terjadi Kesalahan! Restart Aplikasi dan Cek Koneksi Internet!");
                t.printStackTrace();
            }
        });
    }

    private  void absenPulang(String nik, String password, String lokasi, String keterangan){
        //membuat progress dialog
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Proses Absen Pulang ...");
        progress.show();


        CrudService crud = new CrudService();
        crud.absenPulang(nik, password, lokasi, String.valueOf(latitude_saat_ini), String.valueOf(longitude_saat_ini), encodedImage, keterangan,new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                String value = response.body().getValue();
                String message = response.body().getMessage();
                progress.dismiss();
                if (value.equals("1")) {
                    alertSatuTombol(message);
                    kosongkanForm();
                } else {
                    alertSatuTombol(message);

                    editTextPassword.setText("");
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                progress.dismiss();
                alertSatuTombol("Terjadi Kesalahan! Restart Aplikasi dan Cek Koneksi Internet!");
                t.printStackTrace();
            }
        });

    }


    private  void kosongkanForm(){

        editTextNik.setText("");
        editTextPassword.setText("");
        editTextKeterangan.setText("");
        imageView.setImageBitmap(null);
    }

    //untuk menampilkan lokasi di spinner
    private void showLokasi(){

        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Loading ...");
        progress.show();

        CrudService crud = new CrudService();
        crud.tampilLokasi(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {

                progress.dismiss();
                String value = response.body().getValue();

                if (value.equals("1")) {
                    results = response.body().getResult();

                    for (int i=0; i<results.size(); i++) {

                        Result result = results.get(i);

                        nama_lokasi_absen.add(result.getNama());
                        latitude_lokasi_absen.add(result.getLatitude());
                        longitude_lokasi_absen.add(result.getLongitude());
                        data_batas_jarak_absen.add(result.getBatas_jarak_absen());
                    }
                    // Creating adapter for spinner
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Presensi.this, android.R.layout.simple_spinner_item, nama_lokasi_absen);

                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // attaching data adapter to spinner
                    spinnerLokasi.setAdapter(dataAdapter);

                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                progress.dismiss();
                alertSatuTombol("Terjadi Kesalahan! Restart Aplikasi dan Cek Koneksi Internet!");
                t.printStackTrace();

            }
        });

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

    @Override
    public void onClick(View v) {

        if (v == buttonMasuk){
            ambilFoto();
        }

        if (v == fabMyLocation){

            startLocationUpdates();
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

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        // Assign the new location
        mLastLocation = location;

        Toast.makeText(getApplicationContext(), "Lokasi Diperbarui",
                Toast.LENGTH_SHORT).show();

        // Displaying the new location on UI
        displayLocation();

    }

    private void update_jarak_absen(){

        Integer position = spinnerLokasi.getSelectedItemPosition();
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
        Toast.makeText(Presensi.this, "Jarak Ke Lokasi Absen : " + String.valueOf(round(jarak_ke_lokasi_absen)) + " m", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onShowRationalDialog(final PermissionInterface permissionInterface, int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("We need permissions for this app.");
        builder.setPositiveButton("oke", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                permissionInterface.onDialogShown();
            }
        });
        builder.setNegativeButton("cancel", null);
        builder.show();
    }

    @Override
    public void onShowSettings(final PermissionInterface permissionInterface, int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("We need permissions for this app. Open setting screen?");
        builder.setPositiveButton("oke", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                permissionInterface.onSettingsShown();
            }
        });
        builder.show();
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    @Override
    public void onPermissionsDenied(int requestCode) {
        alertSatuTombol("Aplikasi Membutuhkan izin Lokasi untuk dapat di gunakan!");
        reqPermission();
    }
    private void reqPermission() {
        new AskPermission.Builder(this).setPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .setCallback(this)
                .setErrorCallback(this)
                .request(REQUEST_PERMISSIONS);
    }
}
