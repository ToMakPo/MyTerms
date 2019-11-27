package com.example.myterms.term;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myterms.R;

import static com.example.myterms.application.Codes.RESULT_CONFIRMED;
import static com.example.myterms.application.MyFunctions.showToast;

public class TermSelectActivity extends AppCompatActivity {
    private TermSelectAdapter adapter;
    
    private String selectedTermErrorMessage;
    private ImageButton selectedTermErrorIcon;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_select);
        buildActivity();
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void buildActivity() {
        Intent received = getIntent();
        Term selectedTerm = received.getParcelableExtra("selectedTerm");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Term Selector");
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        selectedTermErrorIcon = findViewById(R.id.selected_term_error_icon);
        
        RecyclerView.LayoutManager termLayoutManager = new LinearLayoutManager(this);
        adapter = new TermSelectAdapter(selectedTerm);
        RecyclerView termRecycler = findViewById(R.id.term_recycler);
        termRecycler.setLayoutManager(termLayoutManager);
        termRecycler.setAdapter(adapter);
    }
    
    private boolean checkSelectedTermExists() {
        if (adapter.getSelectedTerm() == null) {
            selectedTermErrorMessage = "You have not selected any terms for this course.";
            return false;
        }
    
        return true;
    }
    private boolean checkInputs() {
        boolean bSelectedTerm = checkSelectedTermExists();
        selectedTermErrorIcon.setVisibility(bSelectedTerm ? View.GONE : View.VISIBLE);
        
        return bSelectedTerm;
    }
    
    public void showSelectedTermErrorMessage(View view) {
        showToast(this, selectedTermErrorMessage);
    }
    
    public void confirmSelection(View view) {
        if (checkInputs()) {
            Intent intent = new Intent();
            intent.putExtra("selectedTerm", adapter.getSelectedTerm());
            setResult(RESULT_CONFIRMED, intent);
            close(view);
        }
    }
    public void close(View view) {
        finish();
    }
}
