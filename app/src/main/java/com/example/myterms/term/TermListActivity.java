package com.example.myterms.term;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myterms.R;

import static com.example.myterms.application.Codes.REQUEST_CREATE_TERM;
import static com.example.myterms.application.Codes.REQUEST_EDIT_TERM;
import static com.example.myterms.application.Codes.REQUEST_VIEW_TERM;
import static com.example.myterms.application.Codes.RESULT_SAVED;

public class TermListActivity extends AppCompatActivity {
    TermListAdapter termListAdapter;
    CheckBox listToggle;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_list);
    
        buildActivity();
    }
    
    private void buildActivity() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Term List");
    
        //////////////////////
        ///   LIST VIEW    ///
        //////////////////////
        listToggle = findViewById(R.id.view_active_checkbox);
    
        RecyclerView.LayoutManager termLayoutManager = new LinearLayoutManager(this);
        termListAdapter = new TermListAdapter(this, listToggle.isChecked());
        RecyclerView termRecycler = findViewById(R.id.term_recycler);
        termRecycler.setLayoutManager(termLayoutManager);
        termRecycler.setAdapter(termListAdapter);
    }
    
    public void toggleList(View view) {
        termListAdapter.setViewActive(listToggle.isChecked());
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
        
        termListAdapter.refresh();
    }
    
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra("requestCode", requestCode);
        super.startActivityForResult(intent, requestCode);
    }
    
    // TODO: 11/17/2019 prevent button from being hit twice.
}
