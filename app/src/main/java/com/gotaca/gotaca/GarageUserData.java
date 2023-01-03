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

public class GarageUserData extends Fragment {

    private View v;
    String uIDUser, nameUser, emailUser;
    public static final String NAME_KEY = "Name";
    public static final String EMAIL_KEY = "E-mail";

    TextView userName, userEmail;

    FirebaseUser mCurrentUser;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup content, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.garage_user_data_view, content, false);
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
        DocumentReference docRef = db.collection("Users").document(uIDUser).collection("Data").document("Profile");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()){
                    nameUser = (String) document.get(NAME_KEY);
                    emailUser = (String) document.get(EMAIL_KEY);
                    createView(nameUser, emailUser);
                }
            }
        });
    }

    public void createView(String nameUser, String emailUser){
        userName = (TextView) v.findViewById(R.id.user_name);
        userEmail = (TextView) v.findViewById(R.id.user_email);
        userName.setText(nameUser);
        userEmail.setText(emailUser);
    }

}
