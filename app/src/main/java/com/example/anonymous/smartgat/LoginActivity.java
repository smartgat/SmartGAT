package com.example.anonymous.smartgat;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class LoginActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ZXingScannerView.ResultHandler {


    private static Toast mToast = null;
    private ZXingScannerView zXingScannerView;
    public static String scanResult = "null",url_to_open="http://tourism.rajasthan.gov.in/tourist-destinations.html";
    private DrawerLayout drawer;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener ;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    url_to_open = "http://tourism.rajasthan.gov.in/tourist-destinations.html";
                    changeFragment(new WebViewFragment());
                    return true;
                case R.id.navigation_scan:
                    scan(item.getActionView());
                    return true;
                case R.id.navigation_profile:
                    if(mAuth.getCurrentUser() != null){
                        changeFragment(new LoggedInFragment());
                    }else{
                        changeFragment(new SigninFragment());
                    }
                    return true;
            }
            return false;
        }
    };

    private void changeFragment(Fragment fragment){
        getFragmentManager().popBackStackImmediate();
        getFragmentManager().beginTransaction().replace(R.id.loginFragment,fragment).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        mAuth = FirebaseAuth.getInstance();
        setTitle("Smart-GAT");


        NavigationView navigationView =  findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);

        final TextView navigationNameTV = header.findViewById(R.id.navigationNameTV);
        final TextView navigationEmailTV = header.findViewById(R.id.navigationEmailTV);



        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    navigationNameTV.setText(mAuth.getCurrentUser().getDisplayName());
                    navigationEmailTV.setText(mAuth.getCurrentUser().getEmail());
                }
            }
        };


        getFragmentManager().beginTransaction().add(R.id.loginFragment, new WebViewFragment()).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onBackPressed() {
        try {
            if (zXingScannerView.isShown()) {
                zXingScannerView.stopCameraPreview();
                zXingScannerView.stopCamera();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        } catch (Exception e) {
            try {
                drawer = findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setIcon(R.drawable.ic_exit_to_app_black_24dp)
                            .setTitle("Exit")
                            .setMessage("Are you sure you want to exit?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finishAffinity();
                                    System.exit(0);
                                }
                            }).setNegativeButton("No", null).show();

                }
            } catch (Exception e1) {
            }
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.feedback) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final EditText input = new EditText(this);
            input.setHint("Description");
            input.setSingleLine();
            builder.setTitle("Feedback");
            FrameLayout container = new FrameLayout(this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 50;
            params.rightMargin = 50;
            input.setLayoutParams(params);
            container.addView(input);
            builder.setView(container);

            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String user = "Anonymous";
                    if(mAuth.getCurrentUser() != null) {
                        user = mAuth.getCurrentUser().getDisplayName()+" ("+mAuth.getCurrentUser().getEmail()+")";
                    }
                    try {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("Feedback");
                        myRef.push().setValue(user+" : "+input.getText().toString()).addOnCompleteListener(
                                new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isComplete()) {
                                            showToast(LoginActivity.this, "Feedback successfuly sent.");
                                        } else {
                                            showToast(LoginActivity.this, task.getException().getMessage());
                                        }
                                    }
                                }
                        );
                    } catch (Exception e) {
                        showToast(getApplicationContext(), e.getMessage());
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

        } else if (id == R.id.aboutUs) {
            startActivity(new Intent(LoginActivity.this,AboutUsActivity.class));
        }else if(id == R.id.hotels){
            url_to_open = "http://rtdc.tourism.rajasthan.gov.in/Client/HotelList.aspx";
            changeFragment(new WebViewFragment());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }






    public static void showToast(Context context, String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        mToast.show();
    }



    public void scan(View view) {
        zXingScannerView = new ZXingScannerView(getApplicationContext());
        setContentView(zXingScannerView);
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();

    }


    @Override
    public void handleResult(Result result) {
        zXingScannerView.removeAllViews();
        zXingScannerView.stopCamera();
        setContentView(R.layout.activity_scanner);
        scanResult = result.getText();
        showToast(getApplicationContext(), result.getText());
        startActivity(new Intent(LoginActivity.this, WebActivity.class));

    }
}
