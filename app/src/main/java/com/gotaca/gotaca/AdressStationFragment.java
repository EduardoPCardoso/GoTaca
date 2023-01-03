package com.gotaca.gotaca;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AdressStationFragment extends Fragment {

    private String adressStation;
    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup content, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.station_adress_view, content, false);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle state){
        super.onActivityCreated(state);
        v = getView();
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            adressStation = extras.getString("adressStation");
            updateView(adressStation);
        }
    }

    public void updateView(String adressStation){

        TextView adress = (TextView)v.findViewById(R.id.adress);
        adress.setText(adressStation);
    }

}
