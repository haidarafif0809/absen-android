package com.example.haidar.presensi.admin_activity;

/**
 * Created by haidar on 11/05/17.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.haidar.presensi.R;
import com.example.haidar.presensi.config.RequestHandler;
import com.example.haidar.presensi.config.Config;

import java.util.HashMap;

public class User  extends AppCompatActivity implements OnClickListener  {


    private EditText editTextNama;
    private EditText editTextPassword;
    private EditText editTextNik;

    private Button buttonAdd;
    private Button buttonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        //Initializing views
        editTextNama = (EditText) findViewById(R.id.editTextNama);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextNik = (EditText) findViewById(R.id.editTextNik);

        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonView = (Button) findViewById(R.id.buttonView);

        //Setting listeners to button
        buttonAdd.setOnClickListener(this);
        buttonView.setOnClickListener(this);


    }

    //Adding an employee
    private void tambahUser(){

        final String nama = editTextNama.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String nik = editTextNik.getText().toString().trim();

        class TambahUser extends AsyncTask<Void,Void,String>{

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(User.this,"Adding...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                if (s.equals("1")){
                    Toast.makeText(User.this,"Berhasil Tambah User",Toast.LENGTH_LONG).show();
                    kosongkanInput();

                }
                else if(s.equals("0")){
                    editTextNik.requestFocus();
                    editTextNik.setError("Nik / Username yang anda masukkan sudah terdaftar!");
                }

            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put(Config.KEY_NAMA,nama);
                params.put(Config.KEY_PASSWORD,password);
                params.put(Config.KEY_NIK,nik);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_ADD_USER, params);
                return res;
            }
        }

        if (validasiForm() == true ){
            TambahUser ae = new TambahUser();
            ae.execute();
        }





    }


    private void kosongkanInput(){
        editTextPassword.setText("");
        editTextNik.setText("");
        editTextNama.setText("");
    }


    private boolean validasiForm(){


        if( editTextNama.getText().toString().trim().equals(""))
        {
            editTextNama.requestFocus();
            editTextNama.setError( "Nama User Harus Di Isi" );

            return false;
        }
        else if(editTextPassword.getText().toString().trim().equals("")){

            editTextPassword.requestFocus();

            editTextPassword.setError( "Password Harus Di Isi" );


            return false;
        }

        else if(editTextNik.getText().toString().trim().equals("")){

            editTextNik.requestFocus();
            editTextNik.setError( "Nik / Username Harus Di Isi" );

            editTextNik.setHint("Isi dengan nama Lokasi Absen");

            return false;
        }



        return true;
    }




    @Override
    public void onClick(View v) {

        if(v == buttonAdd){

            tambahUser();

            }

        if(v == buttonView){
            startActivity(new Intent(this,ViewAllUser.class));
        }
    }
}
