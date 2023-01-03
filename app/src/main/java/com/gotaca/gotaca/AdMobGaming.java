package com.gotaca.gotaca;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdMobGaming extends AppCompatActivity {

    long tacasAmount, cumulativePoints;
    public static final String TACA_POINT = "Pontuação Tacas";
    public static final String CUMULATIVE_POINT = "Pontuação Acumulativa";

    FirebaseFirestore db;
    FirebaseUser mCurrentUser;
    FirebaseAuth mAuth;
    String uIDUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        uIDUser = mCurrentUser.getUid();

        sendPricePoint();
    }

    public void sendPricePoint(){
        DocumentReference docRef = db.collection("Users").document(uIDUser).collection("Data").document("Gamification");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    tacasAmount = (long) document.get(TACA_POINT) + 25;
                    cumulativePoints = (long) document.get(CUMULATIVE_POINT) + 25;
                    sendPoints(tacasAmount, cumulativePoints);
                } else {
                    tacasAmount = 25;
                    cumulativePoints = 25;
                    sendPoints(tacasAmount, cumulativePoints);
                }
            }
        });
    }

    protected void sendPoints(long tacas, long cumulative){
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(TACA_POINT, tacas);
        dataMap.put(CUMULATIVE_POINT, cumulative);
        db.collection("Users").document(uIDUser).collection("Data").document("Gamification").update(dataMap);
        sendToHistory();
    }

    public void sendToHistory(){

        Date time = Calendar.getInstance().getTime();

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        final String actualDate = simpleDateFormat.format(time);
        String timeInMillis = String.valueOf(Calendar.getInstance().getTimeInMillis());

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(TACA_POINT, 25);
        dataMap.put("Reason", "Tacas por assistir um vídeo");
        dataMap.put("Date", actualDate);
        dataMap.put("timeInMillis", timeInMillis);
        db.collection("Users").document(uIDUser).collection("Data").document("Gamification")
                .collection("Tacas History").document(timeInMillis).set(dataMap);
        Toast.makeText(this, "Você acabou de ganhar 25 Tacas por assistir ao vídeo.", Toast.LENGTH_LONG).show();

        backToMain();
    }

    private void backToMain(){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

    }

}
