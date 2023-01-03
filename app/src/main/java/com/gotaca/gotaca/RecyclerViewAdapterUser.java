package com.gotaca.gotaca;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapterUser extends RecyclerView.Adapter<RecyclerViewViewHolderUser> {

    UserLIst userLIst;
    ArrayList<UserGoTacaBase> userArrayList;

    protected LayoutInflater layoutInflater;
    protected Context context;

    public RecyclerViewAdapterUser (Context context, UserLIst userLIst, ArrayList<UserGoTacaBase> userArrayList){
        this.context = context;
        this.userLIst = (UserLIst) userLIst;
        this.userArrayList = userArrayList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public RecyclerViewViewHolderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(userLIst.getBaseContext());
        View view = layoutInflater.inflate(R.layout.user_list_element, parent, false);
        return new RecyclerViewViewHolderUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewViewHolderUser holder, int position) {
        holder.name.setText(userArrayList.get(position).getName());
        holder.point.setText(userArrayList.get(position).getPoint());
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }
}

