package com.example.myterms.application;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myterms.dashboard.DashboardActivity;

public class MainActivity extends AppCompatActivity implements Codes {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openDashboard();
    }

    private void openDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivityForResult(intent, REQUEST_VIEW_DASHBOARD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) openDashboard();
        else finish();
    }
    
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra("requestCode", requestCode);
        super.startActivityForResult(intent, requestCode);
    }
}