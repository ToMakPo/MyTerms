package com.example.myterms.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myterms.R;
import com.example.myterms.application.Codes;
import com.example.myterms.term.Term;
import com.example.myterms.term.TermCardAdapter;
import com.example.myterms.term.TermEditActivity;
import com.example.myterms.term.TermViewActivity;

public class DashboardActivity extends AppCompatActivity implements Codes {
    private static final String TAG = "app: DashboardActivity";
    
    TermCardAdapter termCardAdapter;
    CheckBox listToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        buildActivity();
    }

    private void buildActivity() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Dashboard");
        
        //////////////////////
        ///   LIST VIEW    ///
        //////////////////////
        listToggle = findViewById(R.id.view_active_check_box);

        RecyclerView.LayoutManager termLayoutManager = new LinearLayoutManager(this);
        termCardAdapter = new TermCardAdapter(this, listToggle.isChecked());
        RecyclerView termRecycler = findViewById(R.id.term_recycler);
        termRecycler.setLayoutManager(termLayoutManager);
        termRecycler.setAdapter(termCardAdapter);
    }

    public void toggleList(View view) {
        termCardAdapter.setViewActive(listToggle.isChecked());
    }

    public void createNewTerm(View view) {
        Intent intent = new Intent(this, TermEditActivity.class);
        startActivityForResult(intent, REQUEST_CREATE_TERM);
    }
    public void editTerm(Term term) {
        Intent intent = new Intent(this, TermEditActivity.class);
        intent.putExtra("term", term);
        startActivityForResult(intent, REQUEST_EDIT_TERM);
    }
    public void viewTerm(Term term) {
        Intent intent = new Intent(this, TermViewActivity.class);
        intent.putExtra("term", term);
        startActivityForResult(intent, REQUEST_VIEW_TERM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_CREATE_TERM) {
            if (resultCode == RESULT_SAVED) {
                Term term = data.getParcelableExtra("term");
                viewTerm(term);
            }
        }

        termCardAdapter.refresh();
    }
    
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra("requestCode", requestCode);
        super.startActivityForResult(intent, requestCode);
    }
    
    // TODO: 11/17/2019 prevent button from being hit twice.
}
