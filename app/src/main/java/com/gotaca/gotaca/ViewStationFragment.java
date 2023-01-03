package com.gotaca.gotaca;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ViewStationFragment extends Fragment implements OnMapReadyCallback {

    private String nameStation, adressStation, gasStation, etanolStation, whoUpdatedStation;
    private String dateStation;
    float gasPriceF, etanolPriceF, testPrice;
    private Double latitude, longitude;
    private View v;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup content, Bundle savedInstanceState){

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mini_map);
        mapFragment.getMapAsync(this);

        View view = inflater.inflate(R.layout.station_view, content, false);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap map = googleMap;
        LatLng Coord = new LatLng(latitude, longitude);
        map.addMarker(new MarkerOptions().position(Coord).title(nameStation));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(Coord, 16));
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                callMap(null);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityCreated(Bundle state){
        super.onActivityCreated(state);
        v = getView();
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            nameStation = extras.getString("nameStation");
            adressStation = extras.getString("adressStation");
            gasStation = extras.getString("gasStation");
            etanolStation = extras.getString("etanolStation");
            dateStation = extras.getString("updateDateStation");
            whoUpdatedStation = extras.getString("whoUpdated");
            latitude = extras.getDouble("latitude");
            longitude = extras.getDouble("longitude");
            updateView(nameStation, adressStation, gasStation, etanolStation, dateStation, whoUpdatedStation);
        }
    }

    public void updateView(String nameStation, String adressStation, String gasStation, String etanolStation, String dateStation, String whoUpdatedSt){

        TextView name = (TextView)v.findViewById(R.id.name);
        name.setText(nameStation);
        //TextView adress = (TextView)v.findViewById(R.id.adress);
        //adress.setText(adressStation);
        TextView gasolinac = (TextView)v.findViewById(R.id.gasolinac);
        gasolinac.setText(gasStation);
        TextView etanol = (TextView)v.findViewById(R.id.etanol);
        etanol.setText(etanolStation);
        TextView date = (TextView)v.findViewById(R.id.update_date_t);
        date.setText(dateStation);
        TextView whoUpdated = (TextView)v.findViewById(R.id.who_update_t);
        whoUpdated.setText(whoUpdatedSt);

        int brand = R.drawable.br_logo;

        switch (nameStation){
            case "Posto Petrobras" : brand = R.drawable.br_logo; break;
            case "Posto Ale" : brand = R.drawable.ale_logo; break;
            case "Posto Ipiranga" : brand = R.drawable.ipiranga_logo; break;
            case "Posto Shell" : brand = R.drawable.shell_logo; break;
            case "Bandeira Branca" : brand = R.drawable.bandeira_branca; break;
        }

        ImageView distributor = (ImageView) v.findViewById(R.id.distributor);
        distributor.setImageResource(brand);
        distributor.setScaleType(ImageView.ScaleType.FIT_END);

        //bestFuel(gasStation, etanolStation);
    }

    private void bestFuel (String gasPrice, String etanoPrice){
        gasPriceF = Float.valueOf(gasPrice);
        etanolPriceF = Float.parseFloat(etanoPrice);
        if (gasPriceF == 0){
            TextView bestFuelt = (TextView)v.findViewById(R.id.best_fuel_t);
            bestFuelt.setText(" Etanol");
        }
        if (etanolPriceF == 0){
            TextView bestFuelt = (TextView)v.findViewById(R.id.best_fuel_t);
            bestFuelt.setText(" Gasolina");
        }
        if (gasPriceF != 0 && etanolPriceF != 0){
            testPrice = etanolPriceF/gasPriceF;
            if (testPrice <= 0.7){
                TextView bestFuelt = (TextView)v.findViewById(R.id.best_fuel_t);
                bestFuelt.setText(" Etanol");
            } else {
                TextView bestFuelt = (TextView)v.findViewById(R.id.best_fuel_t);
                bestFuelt.setText(" Gasolina");
            }
        }
    }

    private void callMap(View view){
        Uri uri;
        uri = Uri.parse("geo:0,0?q=" +adressStation);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
