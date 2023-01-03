package com.gotaca.gotaca;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewViewHolder> {

    MainActivity mainActivity;
    ArrayList<Station> stationArrayList;


    protected View.OnClickListener onClickListener; //Para expandir a vista de um recyclerview.
    protected LayoutInflater layoutInflater;
    protected Context context;

    //Para expandir a vista de um recyclerview.
    public void setOnClickListener(View.OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    public RecyclerViewAdapter (Context context, MainActivity mainActivity, ArrayList<Station> stationArrayList){
        this.context = context;
        this.mainActivity = (MainActivity) mainActivity;
        this.stationArrayList = stationArrayList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public RecyclerViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mainActivity.getBaseContext());
        View view = layoutInflater.inflate(R.layout.list_element, parent, false);
        view.setOnClickListener(onClickListener); //Para expandir a vista de um recyclerview.
        return new RecyclerViewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewViewHolder holder, int position) {
        holder.name.setText(stationArrayList.get(position).getName());
        holder.adress.setText(stationArrayList.get(position).getAdress());
        holder.gasolina.setText(stationArrayList.get(position).getGasolinac());
        holder.etanol.setText(stationArrayList.get(position).getEtanol());
        holder.updateDate.setText(stationArrayList.get(position).getUpdateDate());
        //holder.itemView.setOnClickListener();


        int id = R.drawable.bandeira_branca;

        switch (stationArrayList.get(position).getName()){
            case "Posto Petrobras" : id = R.drawable.br_logo; break;
            case "Posto Ale" : id = R.drawable.ale_logo; break;
            case "Posto Ipiranga" : id = R.drawable.ipiranga_logo; break;
            case "Posto Shell" : id = R.drawable.shell_logo; break;
            case "Bandeira Branca" : id = R.drawable.bandeira_branca; break;
        }

        holder.distributor.setImageResource(id);
        holder.distributor.setScaleType(ImageView.ScaleType.FIT_END);

        /*int id = R.drawable.bandeira_branca;

        switch (station.getDistributor()) {
            case IPIRANGA: id = R.drawable.ipiranga_logo; break;
            case ALE: id = R.drawable.ale_logo; break;
            case SHELL: id = R.drawable.shell_logo; break;
            case PETROBRAS: id = R.drawable.br_logo; break;
            case BRANCA: id = R.drawable.bandeira_branca; break;
        }
            holder.distributor.setImageResource(id);
            holder.distributor.setScaleType(ImageView.ScaleType.FIT_END);*/
    }

    @Override
    public int getItemCount() {
        return stationArrayList.size();
    }
}
