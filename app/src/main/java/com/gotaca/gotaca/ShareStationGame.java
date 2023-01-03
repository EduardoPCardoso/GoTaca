package com.gotaca.gotaca;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ShareStationGame extends AppCompatActivity {

    long tacasAmount, cumulativePoints;
    public static final String TACA_POINT = "Pontuação Tacas";
    public static final String CUMULATIVE_POINT = "Pontuação Acumulativa";

    FirebaseFirestore db;
    String uIDUser, nameSt, adressSt, gasSt, etanolSt, dateSt;
    Double latitudeSt, longitudeSt;

    Date time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        uIDUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nameSt = extras.getString("nameStation");
            adressSt = extras.getString("adressStation");
            gasSt = extras.getString("gasStation");
            etanolSt = extras.getString("etanolStation");
            dateSt = extras.getString("updateDateStation");
            latitudeSt = extras.getDouble("latitude");
            longitudeSt = extras.getDouble("longitude");
        }
        shareStationIntent(nameSt, adressSt, gasSt, etanolSt);
    }

    public void sharedStationPoint(){
        DocumentReference docRef = db.collection("Users").document(uIDUser).collection("Data").document("Gamification");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    tacasAmount = (long) document.get(TACA_POINT) + 20;
                    cumulativePoints = (long) document.get(CUMULATIVE_POINT) + 20;
                    sendPoints(tacasAmount, cumulativePoints);
                } else {
                    tacasAmount = 20;
                    cumulativePoints = 20;
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
        backtoViewStation();
    }

    public void sendToHistory(){

        time = Calendar.getInstance().getTime();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String actualDate = simpleDateFormat.format(time);
        String timeInMillis = String.valueOf(Calendar.getInstance().getTimeInMillis());

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(TACA_POINT, 20);
        dataMap.put("Reason", "Tacas por compartilhar um preço");
        dataMap.put("Date", actualDate);
        dataMap.put("timeInMillis", timeInMillis);
        db.collection("Users").document(uIDUser).collection("Data").document("Gamification")
                .collection("Tacas History").document(timeInMillis).set(dataMap);
    }

    public void shareStationIntent(String nameStationS, String adressStationS, String gasStationS, String etanolStationS){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "GoTaca\n" + nameStationS + "\n" + adressStationS + "\n" + "Gasolina C. = R$ " + gasStationS + "\n" + "Etanol = R$ " + etanolStationS
                + "\nhttps://play.google.com/store/apps/details?id=com.gotaca.gotaca");
        sendIntent.setType("text/plain");
        startActivityForResult(Intent.createChooser(sendIntent, "Compartilhar preços com..."),2);
    }

    public void backtoViewStation(){
        Intent intent = new Intent(this, ViewStationActivity.class);
        intent.putExtra("nameStation", nameSt);
        intent.putExtra("adressStation", adressSt);
        intent.putExtra("gasStation", gasSt);
        intent.putExtra("etanolStation", etanolSt);
        intent.putExtra("updateDateStation", dateSt);
        intent.putExtra("latitude", latitudeSt);
        intent.putExtra("longitude", longitudeSt);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 2 && resultCode == RESULT_OK){
            Toast.makeText(this, "Você acabou de ganhar 20 Tacas por compartilhar um posto.", Toast.LENGTH_LONG).show();
            sharedStationPoint();
        } else {
            Toast.makeText(this, "Infelizmente, ocorreu um erro. Gentileza tentar novamente mais tarde.", Toast.LENGTH_LONG).show();
        }
    }
}
