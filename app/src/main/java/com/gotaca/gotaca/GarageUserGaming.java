package com.gotaca.gotaca;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class GarageUserGaming extends Fragment{

    private View v;

    String uIDUser, levelStage;
    long cumulativePoints;

    TextView cumulativePointsView, levelView;

    public static final String CUMULATIVE_POINT = "Pontuação Acumulativa";
    public static final String LEVEL = "Level";

    FirebaseUser mCurrentUser;
    FirebaseAuth mAuth;
    FirebaseFirestore db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup content, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.garage_user_gaming_view, content, false);
        setHasOptionsMenu(true);

        instanceFirebase();
        verifyUserDoc();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle state){
        super.onActivityCreated(state);
        v = getView();
    }

    public void instanceFirebase(){
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        uIDUser = mCurrentUser.getUid();
    }

    public void verifyUserDoc(){
        DocumentReference docRef = db.collection("Users").document(uIDUser).collection("Data").document("Gamification");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()){
                    cumulativePoints = (long) document.get(CUMULATIVE_POINT);
                    setLevel(cumulativePoints);
                }
            }
        });
    }

    public void setLevel(long cumulativePointsGame){
        if (cumulativePointsGame < 500){
            levelStage = "Caixa de papelão"; //Desenho de uma caixa de papelão
            upDateLevel(levelStage);
        } if (cumulativePointsGame >= 500 && cumulativePointsGame < 5000){
            levelStage = "Velotrol";
            upDateLevel(levelStage);
        } if (cumulativePointsGame >= 5000 && cumulativePointsGame < 15000){
            levelStage = "Bicicleta";
            upDateLevel(levelStage);
        } if (cumulativePointsGame >= 15000 && cumulativePointsGame < 30000){
            levelStage = "Carro";
            upDateLevel(levelStage);
        } if (cumulativePointsGame >= 30000 && cumulativePointsGame < 50000){
            levelStage = "Super";
            upDateLevel(levelStage);
        } if (cumulativePointsGame >= 50000 && cumulativePointsGame < 75000){
            levelStage = "Jatinho";
            upDateLevel(levelStage);
        } if (cumulativePointsGame >= 75000){
            levelStage = "OVNI"; //desenho de uma nave alienigina
            upDateLevel(levelStage);
        }
    }

    public void upDateLevel(String levelName){
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(LEVEL, levelName);
        db.collection("Users").document(uIDUser).collection("Data").document("Gamification").update(dataMap);
        createView(levelName);
    }

    public void createView(String teste){
        cumulativePointsView = (TextView) v.findViewById(R.id.points_T);
        cumulativePointsView.setText(String.valueOf(cumulativePoints));

        levelView = (TextView) v.findViewById(R.id.level_T);
        levelView.setText(teste);
    }
}
