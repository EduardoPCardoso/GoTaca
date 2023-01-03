package com.gotaca.gotaca;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class Tickets extends AppCompatActivity {

    TextView nextbttn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lottery);

        nextbttn = (TextView) findViewById(R.id.tutorial_bttn_a_1);
        nextbttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), TicketsTwo.class));
            }
        });
    }
}

