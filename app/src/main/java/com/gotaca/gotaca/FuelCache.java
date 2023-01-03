package com.gotaca.gotaca;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;

public class FuelCache extends AppCompatActivity {

    int fuelChoosed;
    String FILENAME = "Chosed fuel";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fuelChoosed = extras.getInt("combustível");
        }

        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(fuelChoosed);
            fos.close();
            backtoMain();
        } catch (Exception e) {
            e.printStackTrace();
            backtoMain();
        }
    }

    public void backtoMain(){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

}


