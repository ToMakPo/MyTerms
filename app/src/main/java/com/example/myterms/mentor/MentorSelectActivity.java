package com.example.myterms.mentor;

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

import static com.example.myterms.application.Codes.REQUEST_CREATE_MENTOR;
import static com.example.myterms.application.Codes.REQUEST_EDIT_MENTOR;
import static com.example.myterms.application.Codes.RESULT_CONFIRMED;
import static com.example.myterms.application.Codes.RESULT_DELETED;
import static com.example.myterms.application.Codes.RESULT_SAVED;
import static com.example.myterms.application.MyFunctions.showToast;

public class MentorSelectActivity extends AppCompatActivity {
    private MentorSelectAdapter adapter;
    
    private String selectedMentorsErrorMessage;
    private ImageButton selectedMentorsErrorIcon;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_select);
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
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Mentor Selector");
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        Intent received = getIntent();
        long[] selectedIDs = received.getLongArrayExtra("selectedMentorIDs");
        if (selectedIDs == null) throw new RuntimeException("Was not able to get any ids.");
        
        selectedMentorsErrorIcon = findViewById(R.id.selected_mentors_error_icon);
        
        RecyclerView.LayoutManager mentorLayoutManager = new LinearLayoutManager(this);
        adapter = new MentorSelectAdapter(this, selectedIDs);
        RecyclerView mentorRecycler = findViewById(R.id.mentor_recycler);
        mentorRecycler.setLayoutManager(mentorLayoutManager);
        mentorRecycler.setAdapter(adapter);
    }
    
    private boolean checkSelectedMentorsExists() {
        if (adapter.getSelectedMentors().length == 0) {
            selectedMentorsErrorMessage = "You have not selected any mentors for this course.";
            return false;
        }
        
        return true;
    }
    private boolean checkInputs() {
        boolean bSelectedMentors = checkSelectedMentorsExists();
        selectedMentorsErrorIcon.setVisibility(bSelectedMentors ? View.GONE : View.VISIBLE);
        
        return bSelectedMentors;
    }
    
    public void showSelectedMentorsErrorMessage(View view) {
        showToast(this, selectedMentorsErrorMessage);
    }
    
    public void createMentor(View view) {
        Intent intent = new Intent(this, MentorEditActivity.class);
        startActivityForResult(intent, REQUEST_CREATE_MENTOR);
    }
    public void editMentor(Mentor mentor) {
        Intent intent = new Intent(this, MentorEditActivity.class);
        intent.putExtra("mentor", mentor);
        startActivityForResult(intent, REQUEST_EDIT_MENTOR);
    }
    
    public void confirmSelection(View view) {
        if (checkInputs()) {
            Intent intent = new Intent();
            intent.putExtra("selectedMentorIDs", adapter.getSelectedMentors());
            setResult(RESULT_CONFIRMED, intent);
            close(view);
        }
    }
    public void close(View view) {
        finish();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    
        adapter.refresh();
        
        if (requestCode == REQUEST_CREATE_MENTOR) {
            if (resultCode == RESULT_SAVED) {
                Mentor mentor = data.getParcelableExtra("mentor");
                if (mentor != null) adapter.addMentorToSelected(mentor);
            }
        }
        if (requestCode == REQUEST_EDIT_MENTOR) {
            if (resultCode == RESULT_DELETED) {
                Mentor mentor = data.getParcelableExtra("mentor");
                if (mentor != null) adapter.removeMentorFromSelected(mentor);
            }
        }
    }
    
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra("requestCode", requestCode);
        super.startActivityForResult(intent, requestCode);
    }
}
