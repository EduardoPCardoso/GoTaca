package com.gotaca.gotaca;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewViewHolderUser extends RecyclerView.ViewHolder{

    public TextView name, point;

    public RecyclerViewViewHolderUser (View itemView){
        super(itemView);

        name = itemView.findViewById(R.id.name_t);
        point = itemView.findViewById(R.id.point_t);
    }
}