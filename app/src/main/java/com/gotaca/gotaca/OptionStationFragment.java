package com.gotaca.gotaca;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class OptionStationFragment extends Fragment {

    private String gasStation, etanolStation;
    float gasPriceF, etanolPriceF, testPrice;
    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup content, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.option_station_view, content, false);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle state){
        super.onActivityCreated(state);
        v = getView();
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            gasStation = extras.getString("gasStation");
            etanolStation = extras.getString("etanolStation");
            bestFuel(gasStation, etanolStation);
        }
    }

    private void bestFuel (String gasPrice, String etanoPrice){
        gasPriceF = Float.valueOf(gasPrice);
        etanolPriceF = Float.parseFloat(etanoPrice);
        if (gasPriceF == 0){
            TextView bestFuelt = (TextView)v.findViewById(R.id.best_fuel_t);
            bestFuelt.setText("Etanol");
        }
        if (etanolPriceF == 0){
            TextView bestFuelt = (TextView)v.findViewById(R.id.best_fuel_t);
            bestFuelt.setText("Gasolina");
        }
        if (gasPriceF != 0 && etanolPriceF != 0){
            testPrice = etanolPriceF/gasPriceF;
            if (testPrice <= 0.7){
                TextView bestFuelt = (TextView)v.findViewById(R.id.best_fuel_t);
                bestFuelt.setText("Etanol");
            } else {
                TextView bestFuelt = (TextView)v.findViewById(R.id.best_fuel_t);
                bestFuelt.setText("Gasolina");
            }
        }
    }
}
