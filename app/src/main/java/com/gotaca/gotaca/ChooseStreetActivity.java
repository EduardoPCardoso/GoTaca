package com.gotaca.gotaca;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;

public class ChooseStreetActivity extends AppCompatActivity {

    String FILENAME = "Chosed street";
    int currentChosenPreference;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        currentChosenPreference = extras.getInt("corredor selecionado");

        String testeToString = String.valueOf(currentChosenPreference);
        saveInCache(currentChosenPreference);
    }
    private void saveInCache(int choosedStreet){
        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(choosedStreet);
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
