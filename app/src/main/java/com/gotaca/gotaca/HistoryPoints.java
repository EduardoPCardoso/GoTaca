package com.gotaca.gotaca;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HistoryPoints extends Fragment{

    RecyclerView mRecyclerViewPoint;
    FirebaseUser mCurrentUser;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    private View view;

    String uIDUser, tacasPoint;
    ArrayList<HistoryUserPoints> pointsArrayList;
    UserPointsAdapter adapterPoints;

    long tacasObj;

    public static final String TACAS_POINT = "Pontuação Tacas";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup content, Bundle savedInstanceState){

        view = inflater.inflate(R.layout.point_list, content, false);
        setHasOptionsMenu(true);

        pointsArrayList = new ArrayList<>();
        instanceFirebase();
        setUpRecyclerView();
        loadDataFromFirebase();
        loadTacasFromFirebase();

        return view;
    }

    protected void instanceFirebase(){
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        uIDUser = mCurrentUser.getUid();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void setUpRecyclerView(){
        mRecyclerViewPoint = view.findViewById(R.id.recycler_view_point);
        mRecyclerViewPoint.setHasFixedSize(true);
        mRecyclerViewPoint.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    protected void loadDataFromFirebase() {
        if (pointsArrayList.size() > 0)
            pointsArrayList.clear();

        db.collection("Users").document(uIDUser).collection("Data").document("Gamification").collection("Tacas History")
                .orderBy("timeInMillis", Query.Direction.DESCENDING)
                .limit(8)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot querySnapshot : task.getResult()) {
                            HistoryUserPoints historyPoints = new HistoryUserPoints(
                                    querySnapshot.getId(),
                                    querySnapshot.getString("Date"),
                                    querySnapshot.getLong("Pontuação Tacas"),
                                    querySnapshot.getString("Reason"));

                            pointsArrayList.add(historyPoints);
                            adapterPoints = new UserPointsAdapter(getContext(), HistoryPoints.this, pointsArrayList);
                            mRecyclerViewPoint.setAdapter(adapterPoints);
                        }
                    }
                });

    }

    protected void loadTacasFromFirebase(){

        DocumentReference docRef = db.collection("Users").document(uIDUser).collection("Data").document("Gamification");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()){
                    tacasObj = document.getLong(TACAS_POINT);
                    String tacas = String.valueOf(tacasObj);
                    TextView tacasView = (TextView) view.findViewById(R.id.tacas_history_t);
                    tacasView.setText(tacas);
                }
            }
        });
    }
}
