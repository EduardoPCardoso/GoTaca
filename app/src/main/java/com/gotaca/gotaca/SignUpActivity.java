package com.gotaca.gotaca;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    AutoCompleteTextView mNameSignUp, mEmailSignUp;
    EditText mPassSignup, mConfirmPass;
    TextView mSignUpForm;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mNameSignUp = (AutoCompleteTextView) findViewById(R.id.name_signup);
        mEmailSignUp = (AutoCompleteTextView) findViewById(R.id.email_signup);
        mPassSignup = (EditText) findViewById(R.id.pass_sign_up);
        mConfirmPass = (EditText) findViewById(R.id.confirm_pass_sign_up);
        mSignUpForm = (TextView) findViewById(R.id.sign_up_form_button);

        mSignUpForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeToString(mNameSignUp, mEmailSignUp, mPassSignup, mConfirmPass);
            }
        });
    }

    public void changeToString(AutoCompleteTextView mNameSignUp, AutoCompleteTextView mEmailSignUp, EditText mPassSignup, EditText mConfirmPass){
        String NameSignUp = mNameSignUp.getText().toString();
        String EmailSignUp = mEmailSignUp.getText().toString();
        String PassSignUp = mPassSignup.getText().toString();
        String ConfimPass = mConfirmPass.getText().toString();

        verifyFields(NameSignUp, EmailSignUp, PassSignUp, ConfimPass);
        /*
        if (verifyPass(PassSignUp, ConfimPass) == true){
            signUpAuth(NameSignUp, EmailSignUp, PassSignUp);
            //signUpGo(NameSignUp, EmailSignUp, PassSignUp);
        }*/
    }

    protected void verifyFields(String nameSignUp, String emailSignUp, String passSignUp, String confimPass){

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(nameSignUp)) {
            mNameSignUp.setError(getString(R.string.error_field_required));
            focusView = mNameSignUp;
            cancel = true;
        }
        if (TextUtils.isEmpty(emailSignUp)) {
            mEmailSignUp.setError(getString(R.string.error_field_required));
            focusView = mEmailSignUp;
            cancel = true;
        } else if (!isEmailValid(emailSignUp)) {
            mEmailSignUp.setError(getString(R.string.error_invalid_email));
            focusView = mEmailSignUp;
            cancel = true;
        }
        if (TextUtils.isEmpty(passSignUp)) {
            mPassSignup.setError(getString(R.string.error_field_required));
            focusView = mPassSignup;
            cancel = true;
        }else if (!isPasswordValid(passSignUp)) {
            mPassSignup.setError(getString(R.string.error_invalid_password));
            focusView = mPassSignup;
            cancel = true;
        }
        if (TextUtils.isEmpty(confimPass)) {
            mConfirmPass.setError(getString(R.string.error_field_required));
            focusView = mConfirmPass;
            cancel = true;
        }else if (!isPasswordValid(confimPass)) {
            mConfirmPass.setError(getString(R.string.error_invalid_password));
            focusView = mConfirmPass;
            cancel = true;
        }
        if (cancel){
            focusView.requestFocus();
        } else if (verifyPass(passSignUp, confimPass)){
                signUpAuth(nameSignUp, emailSignUp, passSignUp);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }
    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    public boolean verifyPass(String passSignUp, String confimPass){
        if (passSignUp.equals(confimPass)){
            return true;
        } else {
            Toast.makeText(SignUpActivity.this, "Confirmação diferente da senha.", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void signUpGo(String NameSignUp, String EmailSignUp, String PassSignUp){
        db = FirebaseFirestore.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        String uIDUser = mCurrentUser.getUid();

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("Name", NameSignUp);
        dataMap.put("E-mail", EmailSignUp);
        dataMap.put("Password", PassSignUp);
        db.collection("Users").document(uIDUser).collection("Data")
                .document("Profile").set(dataMap);
        updateUI();
        //signUpAuth(EmailSignUp, PassSignUp);
    }

    private void signUpAuth(final String nameSignUp, final String emailSignUp, final String passSignUp){
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(emailSignUp, passSignUp)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            signUpGo(nameSignUp, emailSignUp, passSignUp);
                        } else {
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUI(){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
