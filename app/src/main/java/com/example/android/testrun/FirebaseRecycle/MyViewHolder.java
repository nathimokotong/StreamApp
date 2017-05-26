package com.example.android.testrun.FirebaseRecycle;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.testrun.Data.ItemClickListener;
import com.example.android.testrun.R;

/**
 * Created by Manto on 18-Jan-17.
 */

public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView songTxt;
    ImageButton buttonplay;
    ItemClickListener itemClickListener;

    public MyViewHolder(View itemView) {
        super(itemView);

        songTxt = (TextView) itemView.findViewById(R.id.songnameID);


        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        this.itemClickListener.onItemClick(this.getLayoutPosition());
    }
}
