package com.example.anonymous.smartgat;


import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anonymous.smartgat.R;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView zXingScannerView;
    public static String scanResult;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
    }

    public void scan(View view){
        zXingScannerView =new ZXingScannerView(getApplicationContext());
        setContentView(zXingScannerView);
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();

    }

    @Override
    protected void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        zXingScannerView.stopCameraPreview();
        scanResult = result.getText();
     //   startActivity(new Intent(ScannerActivity.this,WebActivity.class));
    }

}
