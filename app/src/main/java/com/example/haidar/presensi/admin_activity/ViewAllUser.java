package com.example.haidar.presensi.admin_activity;

/**
 * Created by haidar on 11/05/17.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.haidar.presensi.R;
import com.example.haidar.presensi.adapter.RecyclerUserAdapter;
import com.example.haidar.presensi.config.CrudService;
import com.example.haidar.presensi.config.Result;
import com.example.haidar.presensi.config.Value;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ViewAllUser extends AppCompatActivity  {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private List<Result> results = new ArrayList<>();
    private  RecyclerUserAdapter viewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_user);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewUser);

        viewAdapter = new RecyclerUserAdapter(this, results);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(viewAdapter);

        showUser();
    }

    private void showUser(){

        CrudService crud = new CrudService();
        crud.tampilUser(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                String value = response.body().getValue();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                if (value.equals("1")) {
                    results = response.body().getResult();
                    viewAdapter = new RecyclerUserAdapter(ViewAllUser.this, results);
                    recyclerView.setAdapter(viewAdapter);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
            t.printStackTrace();
            }
        });


    }

}
