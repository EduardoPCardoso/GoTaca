package com.gotaca.gotaca;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;


public class FirestoreStationsTest {


    @Test
    public void retrieves(){
        FirestoreStations firestoreStations = new FirestoreStations();
        FirebaseFirestore db = firestoreStations.setUpFirebase();
        firestoreStations.loadDataFromFirebase(db);
    }
}