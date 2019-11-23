package com.example.myterms.note;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myterms.R;
import com.example.myterms.application.Codes;
import com.example.myterms.course.Course;

import static com.example.myterms.application.MyFunctions.showToast;

public class NoteEditActivity extends AppCompatActivity implements Codes {
    private boolean editing;
    private Note note;

    private Course course;
    private TextView courseDisplay;
    
    private String message;
    private String messageErrorMessage;
    private TextView messageTextBox;
    private ImageView messageErrorIcon;
    
    private Button saveButton;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);
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
        int requestCode = received.getIntExtra("requestCode", -1);
        editing = requestCode == REQUEST_EDIT_NOTE;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Note " + (editing ? "Editor" : "Creator"));
        actionBar.setDisplayHomeAsUpEnabled(true);
    
        ///////////////////////////
        ///       COURSE        ///
        ///////////////////////////
        course = received.getParcelableExtra("course");
        if (course == null) throw new RuntimeException("Could not get course from received.");
        courseDisplay = findViewById(R.id.course_display);
        courseDisplay.setText(course.getTitle());
    
        ///////////////////////////
        ///       MESSAGE       ///
        ///////////////////////////
        messageErrorMessage = "";
        messageErrorIcon = findViewById(R.id.message_error_icon);
        messageTextBox = findViewById(R.id.message_text_box);
        messageTextBox.requestFocus();
        messageTextBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (message.isEmpty()) {
                        message = messageTextBox.getText().toString();
                    
                        if (message.isEmpty()
                                && messageErrorIcon.getVisibility() == View.VISIBLE
                                && checkMessageExists()) {
                            messageErrorIcon.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
    
        ///////////////////////////
        ///       BUTTONS       ///
        ///////////////////////////
        saveButton = findViewById(R.id.save_button);
        deleteButton = findViewById(R.id.delete_button);
    
        if (editing) {
            ((TextView) findViewById(R.id.header)).setText(R.string.edit_note);
        
            note = received.getParcelableExtra("note");
            if (note == null) throw new RuntimeException("Could not get note from received.");
    
            message = note.getMessage();
            messageTextBox.setText(message);
    
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            note = null;
    
            message = "";
        }
    }
    
    private boolean checkMessageExists() {
        message = messageTextBox.getText().toString();
        if (message.isEmpty()) {
            messageErrorMessage = "Please put in a message for this note.";
            return false;
        }
        return true;
    }
    private boolean checkInputs() {
        boolean bMessage = checkMessageExists();
        messageErrorIcon.setVisibility(bMessage ? View.GONE : View.VISIBLE);
    
        return bMessage;
    }
    
    public void showMessageErrorMessage(View view) {
        showToast(this, messageErrorMessage);
    }

    public void saveNote(View view) {
        if (checkInputs()) {
            if (editing) {
                note.update(message);
            } else {
                note = Note.create(course, message);
            }
            Intent intent = new Intent();
            intent.putExtra("note", note);
            setResult(RESULT_SAVED, intent);
            close(view);
        }
    }
    public void deleteNote(View view) {
        note.delete();
        setResult(RESULT_DELETED);
        close(view);
    }
    public void close(View view) {
        finish();
    }
}
