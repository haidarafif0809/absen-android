package com.example.haidar.presensi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.haidar.presensi.R;
import com.example.haidar.presensi.config.Config;
import com.example.haidar.presensi.config.Result;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by HaidarAfif on 07/08/17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>  {


    private Context context;
    private List<Result> results;


    public RecyclerViewAdapter(Context context, List<Result> results) {

        this.context = context;
        this.results = results;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_user_hadir, parent, false);
        ViewHolder holder = new ViewHolder(v);

        return holder;

    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {

        Result result = results.get(position);
        holder.txtNama.setText(result.getNama());
        holder.txtNik.setText(result.getNik());
        holder.txtWaktuMasuk.setText(result.getWaktuMasuk());

        Picasso.with(context)
                .load(Config.BASE_URL + result.getFotoMasuk())
                .placeholder(R.mipmap.ic_clock)   // optional
                .error(R.mipmap.ic_clock)
                .into(holder.fotoMasuk);


    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        public TextView txtNama,txtNik,txtWaktuMasuk;
        public ImageView fotoMasuk;


        public ViewHolder(View itemView) {
            super(itemView);

            txtNama = (TextView) itemView.findViewById(R.id.nama);
            txtNik = (TextView) itemView.findViewById(R.id.nik);
            txtWaktuMasuk = (TextView) itemView.findViewById(R.id.waktu_masuk);
            fotoMasuk = (ImageView) itemView.findViewById(R.id.fotoMasuk);


        }


    }
}
