package com.gotaca.gotaca;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class HistoryPointBaseLayout extends Fragment {

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup content, Bundle savedInstanceState){

        view = inflater.inflate(R.layout.history_point_layout_view, content, false);
        setHasOptionsMenu(true);

        return view;
    }
}
