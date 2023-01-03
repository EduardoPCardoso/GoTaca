package com.gotaca.gotaca;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UserLIst extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    RecyclerView mRecyclerView;
    ArrayList<UserGoTacaBase> userArrayList;
    RecyclerViewAdapterUser adapterUsers;

    public static final String point = "Pontuação Tacas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_user);

        userArrayList = new ArrayList<>();

        setUpFirebase();
        setUpRecyclerView();
        loadDataFromFirebase();

    }

    protected void setUpFirebase(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    protected void setUpRecyclerView(){
        mRecyclerView = findViewById(R.id.recycler_view_user);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    protected void loadDataFromFirebase() {

        db.collectionGroup("Data")
                .whereGreaterThan(point,400)
                .orderBy(point, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot querySnapshot : task.getResult()) {
                            UserGoTacaBase userGoTacaBase = new UserGoTacaBase(
                                    querySnapshot.getReference().getPath().split("/")[1],
                                    querySnapshot.getLong(point));
                            String temp1 = querySnapshot.getString("Name");
                            long temp2 = querySnapshot.getLong(point);
                            userArrayList.add(userGoTacaBase);
                            adapterUsers = new RecyclerViewAdapterUser(getBaseContext(), UserLIst.this, userArrayList);
                            mRecyclerView.setAdapter(adapterUsers);
                    }
                    Log.d("debug", "onSuccess: ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserLIst.this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.backToMain){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
