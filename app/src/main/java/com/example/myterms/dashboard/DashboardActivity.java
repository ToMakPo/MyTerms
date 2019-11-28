package com.example.myterms.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myterms.R;
import com.example.myterms.term.Term;
import com.example.myterms.term.TermListActivity;
import com.example.myterms.term.TermViewActivity;
import com.example.myterms.tracker.ProgressTrackerActivity;

import static com.example.myterms.application.Codes.REQUEST_VIEW_TERM;

public class DashboardActivity extends AppCompatActivity {
    Term currentTerm;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        buildActivity();
    }

    private void buildActivity() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Dashboard");
        
        currentTerm = Term.findCurrentTerm();
        
        findViewById(R.id.current_term_button).setVisibility(currentTerm == null ? View.GONE : View.VISIBLE);
    }
    
    public void viewCurrentTerm(View view) {
        Intent intent = new Intent(this, TermViewActivity.class);
        intent.putExtra("term", currentTerm);
        startActivityForResult(intent, REQUEST_VIEW_TERM);
    }
    
    public void viewAllTerms(View view) {
        Intent intent = new Intent(this, TermListActivity.class);
        startActivity(intent);
    }
    
    public void viewProgressTracker(View view) {
        Intent intent = new Intent(this, ProgressTrackerActivity.class);
        startActivity(intent);
    }
    
    // TODO: 11/17/2019 prevent button from being hit twice.
}
