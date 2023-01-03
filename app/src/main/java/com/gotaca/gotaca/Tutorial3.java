package com.gotaca.gotaca;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class Tutorial3 extends AppCompatActivity {

    TextView backbttn ,nextbttn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_3);

        backbttn = (TextView) findViewById(R.id.tutorial_bttn_a_31);
        backbttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Tutorial2.class));
            }
        });

        nextbttn = (TextView) findViewById(R.id.tutorial_bttn_a_32);
        nextbttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}