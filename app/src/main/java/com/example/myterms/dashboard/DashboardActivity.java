package com.example.myterms.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myterms.R;
import com.example.myterms.term.Term;
import com.example.myterms.term.TermViewActivity;

import static com.example.myterms.application.Codes.REQUEST_VIEW_TERM;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "app: DashboardActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        buildActivity();
    }

    private void buildActivity() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Dashboard");
        
    }
    
    public void viewCurrentTerm(View view) {}
    
    public void viewAllTerms(Term term) {
        Intent intent = new Intent(this, TermViewActivity.class);
        intent.putExtra("term", term);
        startActivityForResult(intent, REQUEST_VIEW_TERM);
    }
    
    public void viewProgressTracker(View view) {}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra("requestCode", requestCode);
        super.startActivityForResult(intent, requestCode);
    }
    
    // TODO: 11/17/2019 prevent button from being hit twice.
}
