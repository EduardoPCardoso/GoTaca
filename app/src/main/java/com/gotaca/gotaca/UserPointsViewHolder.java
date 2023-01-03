package com.gotaca.gotaca;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class UserPointsViewHolder extends RecyclerView.ViewHolder {

    public TextView dateView, tacasView, reasonView;

    public UserPointsViewHolder (View itemView){
        super(itemView);

        dateView = itemView.findViewById(R.id.date_point_t);
        tacasView = itemView.findViewById(R.id.tacas_point_t);
        reasonView = itemView.findViewById(R.id.reason_point_t);
    }
}
