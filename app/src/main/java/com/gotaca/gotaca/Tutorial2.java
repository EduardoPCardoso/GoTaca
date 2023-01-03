package com.gotaca.gotaca;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class Tutorial2 extends AppCompatActivity {

    TextView backbttn, nextbttn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_2);

        backbttn = (TextView) findViewById(R.id.tutorial_bttn_a_21);
        backbttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Tutorial1.class));
            }
        });

        nextbttn = (TextView) findViewById(R.id.tutorial_bttn_a_22);
        nextbttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Tutorial3.class));
            }
        });
    }
}
