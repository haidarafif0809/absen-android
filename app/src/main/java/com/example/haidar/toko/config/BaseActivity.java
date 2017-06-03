package com.example.haidar.toko.config;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.haidar.toko.front_activity.PresensiMasuk;
import com.example.haidar.toko.front_activity.PresensiPulang;
import com.example.haidar.toko.front_activity.GantiPasswordActivity;
import com.example.haidar.toko.admin_activity.LoginActivity;
import com.example.haidar.toko.R;
import com.example.haidar.toko.front_activity.UserHadirActivity;

/**
 * Created by haidar on 23/05/17.
 */

public class BaseActivity  extends AppCompatActivity {

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
        if (item.getItemId() ==  R.id.menu_ganti_password) {
            startActivity( new Intent(this, GantiPasswordActivity.class));

        }

        return super.onOptionsItemSelected(item);
    }

}
