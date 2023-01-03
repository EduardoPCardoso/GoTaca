package com.gotaca.gotaca;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewStationActivity extends AppCompatActivity {

    View view;
    String nameStation, adressStation, gasStation, etanolStation, dateStation, whoUpdatedStation;
    Double latitude, longitude;
    FirebaseFirestore db;
    FirebaseUser mCurrentUser;
    FirebaseAuth mAuth;
    String uIDUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.station_view_fragment);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        uIDUser = mCurrentUser.getUid();

        //TESTE
        db = FirebaseFirestore.getInstance(); // TESTE

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nameStation = extras.getString("nameStation");
            adressStation = extras.getString("adressStation");
            gasStation = extras.getString("gasStation");
            etanolStation = extras.getString("etanolStation");
            dateStation = extras.getString("updateDateStation");
            whoUpdatedStation = extras.getString("whoUpdated");
            latitude = extras.getDouble("latitude");
            longitude = extras.getDouble("longitude");
        }

        FloatingActionButton getPriceBttn = (FloatingActionButton) findViewById(R.id.fAB3);
        getPriceBttn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                shareStation(nameStation, adressStation, gasStation, etanolStation, dateStation, latitude, longitude);
                //getPrice();
            }
        });

        FloatingActionButton callMapBttn = (FloatingActionButton) findViewById(R.id.fAB4);
        callMapBttn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                callMap(null);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void shareStation(String nameStationS, String adressStationS, String gasStationS, String etanolStationS, String dateStationS, Double latitudeStation, Double longitudeStation){

        Intent intent = new Intent(this, ShareStationGame.class);
        intent.putExtra("nameStation", nameStationS);
        intent.putExtra("adressStation", adressStationS);
        intent.putExtra("gasStation", gasStationS);
        intent.putExtra("etanolStation", etanolStationS);
        intent.putExtra("updateDateStation", dateStationS);
        intent.putExtra("latitude", latitudeStation);
        intent.putExtra("longitude", longitudeStation);
        startActivityForResult(intent,0);
    }

    private void callMap(View view){
        Uri uri;
        uri = Uri.parse("geo:0,0?q=" +adressStation);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
