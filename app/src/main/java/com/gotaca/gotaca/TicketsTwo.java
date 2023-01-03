package com.gotaca.gotaca;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class TicketsTwo extends AppCompatActivity{

    TextView backbttn, nextbttn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lottery_second_screen);

        backbttn = (TextView) findViewById(R.id.tutorial_bttn_a_21);
        backbttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Tickets.class));
            }
        });

        nextbttn = (TextView) findViewById(R.id.tutorial_bttn_a_22);
        nextbttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HomeUser.class));
            }
        });
    }

}
