package com.example.anonymous.smartgat;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by anonymous on 17/03/18.
 */

public class SigninFragment extends Fragment {
    private FirebaseAuth mAuth;
    private EditText userET, passET;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        final View view = inflater.inflate(R.layout.fragment_sigin, container, false);
        progressBar = view.findViewById(R.id.signInPB);
        progressBar.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        userET = view.findViewById(R.id.userET);
        passET = view.findViewById(R.id.passET);
        TextView resetTV = view.findViewById(R.id.resetTV);
        TextView registerTV = view.findViewById(R.id.registerTV);
        Button loginBT = view.findViewById(R.id.loginBT);

        resetTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetClicked(v);
            }
        });

        registerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerClicked(v);
            }
        });
        loginBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginClicked(v);
            }
        });
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    getFragmentManager().beginTransaction().replace(R.id.loginFragment, new LoggedInFragment()).commit();
                }
            }
        };
        return view;
    }

    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(authStateListener); //firebaseAuth is of class FirebaseAuth
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }


    public void loginClicked(final View view1) {
        try {
            //final
            progressBar.setVisibility(View.VISIBLE);
            mAuth.signOut();
            mAuth.signInWithEmailAndPassword(userET.getText().toString(), passET.getText().toString())
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if (mAuth.getCurrentUser() != null) {
                                    if (!mAuth.getCurrentUser().isEmailVerified()) {
                                        progressBar.setVisibility(View.GONE);
                                        mAuth.getCurrentUser().sendEmailVerification();
                                        LoginActivity.showToast(getView().getContext(), "A verification mail is sent to " + mAuth.getCurrentUser().getEmail() + ".");
                                    } else {
                                        try{
                                            progressBar.setVisibility(View.GONE);
                                            getFragmentManager().beginTransaction().replace(R.id.loginFragment,new LoggedInFragment()).commit();
                                        }catch (Exception e){

                                        }
                                    }
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            LoginActivity.showToast(view1.getContext(), e.getMessage());
        }
    }
    public void resetClicked(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        final EditText input = new EditText(view.getContext());
        input.setHint("Email");
        input.setSingleLine();
        builder.setTitle("Reset Password");
        FrameLayout container = new FrameLayout(view.getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 50;
        params.rightMargin = 50;
        input.setLayoutParams(params);
        container.addView(input);
        builder.setView(container);

        builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.sendPasswordResetEmail(input.getText().toString().trim()).addOnCompleteListener(getActivity(),
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        LoginActivity.showToast(view.getContext(), "Reset Email successfully sent.");
                                    } else {
                                        LoginActivity.showToast(view.getContext(), task.getException().getMessage());
                                    }
                                }
                            });
                } catch (Exception e) {
                    LoginActivity.showToast(view.getContext(), e.getMessage());
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();

        dialog.show();
    }
    public void registerClicked(View view) {
        startActivity(new Intent(view.getContext(), RegisterActivity.class));
    }

}
