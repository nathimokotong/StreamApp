package com.example.android.testrun.FirebaseRecycle;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.testrun.Data.Artist;
import com.example.android.testrun.Data.ItemClickListener;
import com.example.android.testrun.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Manto on 18-Jan-17.
 */

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
    Context c;
    ArrayList<Artist> artists;

    public MyAdapter(Context c, ArrayList<Artist> artists) {
        this.c = c;
        this.artists = artists;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(c).inflate(R.layout.design_row_cardview, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Artist s = artists.get(position);

        holder.songTxt.setText(s.getSongName());

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                playsong(s.getDownloadu());
            }
        });
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    //playsong
    private void playsong(String urilink) {
        Toast.makeText(c, urilink, Toast.LENGTH_SHORT).show();
    }
}
