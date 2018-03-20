package com.example.anonymous.smartgat;

import android.app.Fragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class LoggedInFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        View view = inflater.inflate(R.layout.fragment_loggedin, container, false);
        mAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    getFragmentManager().beginTransaction().replace(R.id.loginFragment, new SigninFragment()).commit();
                }
            }
        };
        TextView nameLoggedIn = view.findViewById(R.id.nameLoggedIn);
        nameLoggedIn.setText(mAuth.getCurrentUser().getDisplayName());
        TextView emailLoggedIn = view.findViewById(R.id.emailLoggedIn);
        emailLoggedIn.setText(mAuth.getCurrentUser().getEmail());
        view.findViewById(R.id.resetLoggedIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.sendPasswordResetEmail(mAuth.getCurrentUser().getEmail());
            }
        });
        view.findViewById(R.id.logoutLoggedIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                getFragmentManager().beginTransaction().replace(R.id.loginFragment,new SigninFragment()).commit();
            }
        });
        return view;
    }
}