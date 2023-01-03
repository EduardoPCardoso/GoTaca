package com.gotaca.gotaca;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private AutoCompleteTextView emailForget;
    private TextView sendEmailBttn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password);

        emailForget = (AutoCompleteTextView) findViewById(R.id.forget_email);
        sendEmailBttn = (TextView) findViewById(R.id.send_email_bttn);
        mAuth = FirebaseAuth.getInstance();

        sendEmailBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getEmail = emailForget.getText().toString().trim();
                change_pass(getEmail);
            }
        });
    }

    private void change_pass(String getEmail) {
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(getEmail)) {
            emailForget.setError(getString(R.string.error_field_required));
            focusView = emailForget;
            cancel = true;
        } else if (!isEmailValid(getEmail)) {
            emailForget.setError(getString(R.string.error_invalid_email));
            focusView = emailForget;
            cancel = true;
        }
        if (cancel){
            focusView.requestFocus();
        } else {
            sendEmail(getEmail);
        }
    }

    private void sendEmail(String emailToSend){
        mAuth.sendPasswordResetEmail(emailToSend)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ForgetPassword.this, "E-mail de redefinição de senha encaminhado.", Toast.LENGTH_LONG).show();
                        backtoLogin();
                    }
                });
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    public void backtoLogin(){
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

}
