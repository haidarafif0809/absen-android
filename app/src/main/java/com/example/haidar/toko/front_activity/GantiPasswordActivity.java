package com.example.haidar.toko.front_activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.haidar.toko.R;
import com.example.haidar.toko.config.BaseActivity;
import com.example.haidar.toko.config.Config;
import com.example.haidar.toko.config.RequestHandler;

import java.util.HashMap;

import static com.example.haidar.toko.R.id.editTextNik;

public class GantiPasswordActivity extends BaseActivity implements View.OnClickListener {

    private EditText edtPasswordLama,edtPasswordBaru,edtNik;
    private Button btnGantiPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ganti_password);

        edtPasswordLama = (EditText) findViewById(R.id.editTextPasswordLama);
        edtPasswordBaru = (EditText) findViewById(R.id.editTextPasswordBaru);
        edtNik = (EditText) findViewById(editTextNik);

        btnGantiPassword = (Button) findViewById(R.id.buttonGantiPassword);

        btnGantiPassword.setOnClickListener(this);

    }


    //Adding an employee
    private void prosesGantiPassword() {

        final String nik = edtNik.getText().toString().trim();
        final String password_lama = edtPasswordLama.getText().toString().trim();
        final String password_baru = edtPasswordBaru.getText().toString().trim();



        class ProsesGantiPassword extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(GantiPasswordActivity.this, "Processing...", "Wait...", false, false);
                loading.setCancelable(false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();


                if (s.equals("2")){
                    edtNik.setText("");
                    edtPasswordBaru.setText("");
                    edtPasswordLama.setText("");
                    edtNik.requestFocus();
                    edtNik.setError("Nik Atau Password Salah!");
                }
                else if (s.equals("1")){
                    Toast.makeText(GantiPasswordActivity.this, "Berhasil Mengganti Password", Toast.LENGTH_LONG).show();
                    //pindah ke absen masuk
                    startActivity(new Intent(GantiPasswordActivity.this,AbsenMasuk.class));

                }

            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                params.put(Config.KEY_NIK, nik);
                params.put(Config.KEY_PASSWORD_BARU,password_baru);
                params.put(Config.KEY_PASSWORD_LAMA,password_lama);


                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_GANTI_PASSWORD, params);
                return res;
            }
        }

        if (validasiForm() == true) {
            ProsesGantiPassword ae = new ProsesGantiPassword();
            ae.execute();
        }


    }

    private boolean validasiForm() {

        if( edtNik.getText().toString().trim().equals(""))
        {
            edtNik.setError( "Username /  Nik Harus Di Isi" );


            return false;
        }
        else  if( edtPasswordLama.getText().toString().trim().equals(""))
        {
            edtPasswordLama.setError( "Password Lama Harus Di Isi" );


            return false;
        }
        else  if( edtPasswordBaru.getText().toString().trim().equals(""))
        {
            edtPasswordBaru.setError( "Password Lama Harus Di Isi" );


            return false;
        }   else  if( edtPasswordLama.getText().toString().trim().equals(edtPasswordBaru.getText().toString().trim()))
        {
            edtPasswordLama.setError( "Password baru tidak boleh sama dengan yang lama !" );


            return false;
        }

        return  true;

    }




    @Override
    public void onClick(View v) {

        if (v == btnGantiPassword){
            prosesGantiPassword();
        }

    }
}
