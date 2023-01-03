package com.gotaca.gotaca;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Lottery extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseUser mCurrentUser;
    FirebaseAuth mAuth;
    String uIDUser, promoCodeTS;
    String award, awardCompl,nextDate, winner, numberOfCouponsString,rules;
    int tacasAmount, numberOfCoupons, couponValueInt;
    long tacasAmountLong, couponValue;
    EditText mPromoCodeT;

    TextView couponsT, awardT, awardComplT,dateT, winnerT, sendCodeBttn,rulesT;

    public static final String TACA_POINT = "Pontuação Tacas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lottery_screen);

        getInstances();

        mPromoCodeT = (EditText) findViewById(R.id.promo_code_t);

        sendCodeBttn = findViewById(R.id.send_code_bttn_2);
        sendCodeBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promoCodeTS = mPromoCodeT.getText().toString();
                verifyField(promoCodeTS);
            }
        });

    }

    private void getInstances() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        uIDUser = mCurrentUser.getUid();
        loadData();
    }

    private void verifyField(String promoCodeTS){
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(promoCodeTS)) {
            mPromoCodeT.setError(getString(R.string.error_field_required));
            focusView = mPromoCodeT;
            cancel = true;
        }
        if (cancel){
            focusView.requestFocus();
        } if (!TextUtils.isEmpty(promoCodeTS)){
            sendPromoCode();
        }
    }

    private void sendPromoCode(){

        String timeInMillis = String.valueOf(Calendar.getInstance().getTimeInMillis());

        db = FirebaseFirestore.getInstance();
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("uID", uIDUser);
        dataMap.put("PromoCode", promoCodeTS);
        db.collection("Promotional codes").document(timeInMillis).set(dataMap);

        Toast.makeText(this, "Código enviado.\nLogo as Tacas serão adicionadas à sua conta.",Toast.LENGTH_SHORT).show();
    }

    private void loadData() {
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Sorteio").document("Campos");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    award = document.getString("award");
                    awardCompl = document.getString("award_complement");
                    nextDate = document.getString("nextDate");
                    winner = document.getString("winner");
                    rules = document.getString("rules");
                    couponValue = (long) document.get("couponValue");

                    couponValueInt = (int) couponValue;

                    awardT = (TextView) findViewById(R.id.award_t);
                    awardT.setText(award);
                    awardComplT = (TextView) findViewById(R.id.award_compl_t);
                    awardComplT.setText(awardCompl);
                    dateT = (TextView) findViewById(R.id.date_t);
                    dateT.setText(nextDate);
                    winnerT = (TextView) findViewById(R.id.winner_t);
                    winnerT.setText(winner);
                    rulesT = findViewById(R.id.rules_t);
                    rulesT.setText(rules);

                    getUserTacas();
                }
            }
        });
    }

    private void getUserTacas() {
        DocumentReference docRef = db.collection("Users").document(uIDUser).collection("Data").document("Gamification");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    tacasAmountLong = (long) document.get(TACA_POINT);
                    tacasAmount = Integer.parseInt(String.valueOf(tacasAmountLong));
                    getCoupons();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getCoupons() {
        //String couponValueString = String.valueOf(couponValue);
        if (tacasAmountLong >= couponValue && couponValueInt != 0) {
            numberOfCoupons = tacasAmount/couponValueInt;
            numberOfCouponsString = String.valueOf(numberOfCoupons);
            couponsT = (TextView) findViewById(R.id.coupons_t);
            couponsT.setText(numberOfCouponsString);
            } else {
            numberOfCouponsString = "0";
            couponsT = (TextView) findViewById(R.id.coupons_t);
            couponsT.setText(numberOfCouponsString);
        }
    }
}
