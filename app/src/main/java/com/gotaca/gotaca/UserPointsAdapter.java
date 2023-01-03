package com.gotaca.gotaca;

import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class UserPointsAdapter extends RecyclerView.Adapter<UserPointsViewHolder> {

    HistoryPoints historyPoints;
    ArrayList<HistoryUserPoints> pointsArrayList;


    protected View.OnClickListener onClickListener; //Para expandir a vista de um recyclerview.
    protected LayoutInflater teste;
    protected Context context;

    //Para expandir a vista de um recyclerview.
    public void setOnClickListener(View.OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    public UserPointsAdapter (Context context, HistoryPoints historyPoints, ArrayList<HistoryUserPoints> pointsArrayList){
        this.context = context;
        this.historyPoints = (HistoryPoints) historyPoints;
        this.pointsArrayList = pointsArrayList;
        teste = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public UserPointsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(historyPoints.getContext());
        View view = layoutInflater.inflate(R.layout.list_point, parent, false);
        view.setOnClickListener(onClickListener); //Para expandir a vista de um recyclerview.
        return new UserPointsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserPointsViewHolder holder, int position) {
        holder.dateView.setText(pointsArrayList.get(position).getDate());
        holder.tacasView.setText(pointsArrayList.get(position).getTacas());
        holder.reasonView.setText(pointsArrayList.get(position).getReason());

    }

    @Override
    public int getItemCount() {
        return pointsArrayList.size();
    }

}
