package com.example.haidar.presensi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.haidar.presensi.R;
import com.example.haidar.presensi.config.Result;

import java.util.List;

/**
 * Created by HaidarAfif on 08/08/17.
 */

public class RecyclerUserAdapter extends RecyclerView.Adapter<RecyclerUserAdapter.MyViewHolder>  {

    private Context context;
    private List<Result> results;

    public RecyclerUserAdapter(Context context, List<Result> results) {

        this.context = context;
        this.results = results;
    }


    @Override
    public RecyclerUserAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_data_user, parent, false);
        RecyclerUserAdapter.MyViewHolder holder = new RecyclerUserAdapter.MyViewHolder(v);

        return holder;

    }

    @Override
    public void onBindViewHolder(RecyclerUserAdapter.MyViewHolder holder, int position) {
        Result result = results.get(position);
        holder.txtNama.setText(result.getNama());
        holder.txtNik.setText(result.getNik());
        holder.txtId.setText(result.getId());
    }

    @Override
    public int getItemCount() {
       return results.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder  {

        public TextView txtNama,txtNik,txtId;


        public MyViewHolder(View itemView) {
            super(itemView);

            txtNama = (TextView) itemView.findViewById(R.id.nama);
            txtNik = (TextView) itemView.findViewById(R.id.nik);
            txtId = (TextView) itemView.findViewById(R.id.id);


        }


    }
}
