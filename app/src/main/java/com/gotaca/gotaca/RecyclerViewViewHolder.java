package com.gotaca.gotaca;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewViewHolder extends RecyclerView.ViewHolder{

    public TextView name, adress, gasolina, etanol, updateDate;
    public ImageView distributor;

    public RecyclerViewViewHolder (View itemView){
        super(itemView);

        name = itemView.findViewById(R.id.name);
        adress = itemView.findViewById(R.id.adress);
        gasolina = itemView.findViewById(R.id.gasolinac);
        etanol = itemView.findViewById(R.id.etanol);
        distributor = itemView.findViewById(R.id.distributor);
        updateDate = itemView.findViewById(R.id.update_date);
    }
}
