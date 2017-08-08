package com.example.haidar.presensi.front_activity;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.haidar.presensi.R;
import com.example.haidar.presensi.adapter.RecyclerViewAdapter;
import com.example.haidar.presensi.config.BaseActivity;
import com.example.haidar.presensi.config.CrudService;
import com.example.haidar.presensi.config.Result;
import com.example.haidar.presensi.config.Value;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserHadirActivity extends BaseActivity  implements SearchView.OnQueryTextListener{


    private List<Result> results = new ArrayList<>();
    private RecyclerViewAdapter viewAdapter;

    RecyclerView recyclerView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_hadir);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        viewAdapter = new RecyclerViewAdapter(this, results);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(viewAdapter);

        loadDataAbsen();

    }

    private void loadDataAbsen() {


        CrudService crud = new CrudService();
        crud.tampilAbsen(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {

                String value = response.body().getValue();
                progressBar.setVisibility(View.GONE);
                if (value.equals("1")) {
                    results = response.body().getResult();
                    viewAdapter = new RecyclerViewAdapter(UserHadirActivity.this, results);
                    recyclerView.setAdapter(viewAdapter);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {

                Toast.makeText(UserHadirActivity.this, "Terjadi Kesalahan!", Toast.LENGTH_SHORT).show();

                t.printStackTrace();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataAbsen();
    }



    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        CrudService crud = new CrudService();
        crud.cariAbsen(newText, new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                String value = response.body().getValue();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                if (value.equals("1")) {
                    results = response.body().getResult();
                    viewAdapter = new RecyclerViewAdapter(UserHadirActivity.this, results);
                    recyclerView.setAdapter(viewAdapter);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(UserHadirActivity.this, "Terjadi Kesalahan!", Toast.LENGTH_SHORT).show();

                t.printStackTrace();
            }
        });

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("Cari Nama Karyawan");
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(this);
        return true;
    }
}
