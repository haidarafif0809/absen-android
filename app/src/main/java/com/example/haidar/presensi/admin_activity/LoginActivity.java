package com.example.haidar.presensi.admin_activity;

/**
 * Created by haidar on 02/05/17.
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.haidar.presensi.front_activity.PresensiMasuk;
import com.example.haidar.presensi.front_activity.PresensiPulang;
import com.example.haidar.presensi.R;
import com.example.haidar.presensi.front_activity.UserHadirActivity;
import com.example.haidar.presensi.config.Config;
import com.example.haidar.presensi.config.RequestHandler;

import java.util.HashMap;



public class LoginActivity extends AppCompatActivity implements OnClickListener {

    private EditText editTextUsername;
    private EditText editTextPassword;

    private Button buttonSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initializing views
        editTextUsername = (EditText) findViewById(R.id.username);
        editTextPassword = (EditText) findViewById(R.id.password);

        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);

        //Setting listeners to button
        buttonSignIn.setOnClickListener(this);

    }


    //Adding an employee
    private void validasiLogin(){

        final String username = editTextUsername.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        class ValidasiLogin extends AsyncTask<Void,Void,String>{

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LoginActivity.this,"Processing...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s.equals("1")){
                    keHalamanAdmin();
                }
                else if(s.equals("0")){
                    editTextUsername.requestFocus();
                    editTextUsername.setError( "Username atau Password yang dimasukkan salah!" );
                }

            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put(Config.KEY_USERNAME,username);
                params.put(Config.KEY_PASSWORD,password);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_VALIDASI_LOGIN_ADMIN, params);
                return res;
            }
        }

        if (validasiForm() == true ){
            ValidasiLogin vl = new ValidasiLogin();
            vl.execute();
        }





    }

    private void keHalamanAdmin(){

        Toast.makeText(LoginActivity.this,"Berhasil Login",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    private boolean validasiForm(){

        if( editTextUsername.getText().toString().trim().equals(""))
        {
            editTextUsername.setError( "Username Harus Di Isi" );

            editTextUsername.setHint("Isi dengan username");

            return false;
        }
        else if(editTextPassword.getText().toString().trim().equals("")){

            editTextPassword.setError(" Password Harus Di Isi" );

            editTextPassword.setHint("Isi dengan Password");

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
            startActivity( new Intent(this, PresensiMasuk.class));
        }
        if (item.getItemId() ==  R.id.absen_pulang) {
            startActivity( new Intent(this, PresensiPulang.class));

        }
        if (item.getItemId() ==  R.id.admin) {
            startActivity( new Intent(this, LoginActivity.class));

        }
        if (item.getItemId() ==  R.id.user_hadir) {
            startActivity( new Intent(this, UserHadirActivity.class));

        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        if(v == buttonSignIn){
            validasiLogin();
        }
    }
}
