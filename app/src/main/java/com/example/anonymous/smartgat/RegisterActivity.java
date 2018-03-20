package com.example.anonymous.smartgat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {
    private EditText rEmailET, rPassET, rConfirmPassET,rNameET;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        rEmailET = findViewById(R.id.rEmailET);
        rPassET = findViewById(R.id.rPassET);
        rConfirmPassET = findViewById(R.id.rConfirmPassET);
        rNameET = findViewById(R.id.rNameET);
        mAuth = FirebaseAuth.getInstance();
    }


    public void rRegisterClicked(View view) {
        if (rPassET.getText().toString().equals(rConfirmPassET.getText().toString())) {
            try {
                mAuth.createUserWithEmailAndPassword(rEmailET.getText().toString(), rPassET.getText().toString()).addOnCompleteListener(this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(rNameET.getText().toString()).build();

                                    mAuth.getCurrentUser().updateProfile(profileUpdates);
                                   mAuth.sendPasswordResetEmail(rEmailET.getText().toString()).addOnCompleteListener(
                                           new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {
                                                   if (task.isSuccessful()) {
                                                       LoginActivity.showToast(getApplicationContext(), "Reset Email successfully sent.");
                                                   } else {
                                                       LoginActivity.showToast(getApplicationContext(), task.getException().getMessage());
                                                   }
                                               }
                                           }
                                   );
                                    LoginActivity.showToast(getApplicationContext(), "Register Successful.");
                                } else {
                                    LoginActivity.showToast(getApplicationContext(), task.getException().getMessage());
                                }
                            }
                        });
            } catch (Exception e) {
                LoginActivity.showToast(getApplicationContext(), e.getMessage());
            }
        } else {
            LoginActivity.showToast(getApplicationContext(), "Passwords are not same.");
        }
    }

}
