package com.example.myterms.mentor;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myterms.R;
import com.example.myterms.application.Codes;

import static com.example.myterms.application.MyFunctions.showToast;

public class MentorEditActivity extends AppCompatActivity implements Codes {
    private boolean editing;
    private Mentor mentor;
    
    private String name;
    private TextView nameTextBox;
    private ImageButton nameErrorIcon;
    private String nameErrorMessage;
    
    private String phone;
    private TextView phoneTextBox;
    private ImageButton phoneErrorIcon;
    private String phoneErrorMessage;
    
    private String email;
    private TextView emailTextBox;
    private ImageButton emailErrorIcon;
    private String emailErrorMessage;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_edit);
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
        editing = requestCode == REQUEST_EDIT_MENTOR;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Mentor " + (editing ? "Editor" : "Creator"));
        actionBar.setDisplayHomeAsUpEnabled(true);
    
        //////////////////////
        ///      NAME      ///
        //////////////////////
        nameErrorMessage = "";
        nameTextBox = findViewById(R.id.name_text_box);
        nameErrorIcon = findViewById(R.id.name_error_icon);
        nameTextBox.requestFocus();
        nameTextBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    name = nameTextBox.getText().toString();
    
                    if (name.isEmpty()) {
                        if (nameErrorIcon.getVisibility() == View.VISIBLE) {
                            if (checkNameExists() && checkNameIsUnique()) {
                                nameErrorIcon.setVisibility(View.GONE);
                            }
                        } else {
                            if (!checkNameIsUnique()) {
                                nameErrorIcon.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        nameErrorIcon.setVisibility(checkNameIsUnique() ? View.GONE : View.VISIBLE);
                    }
                }
            }
        });
    
        //////////////////////
        ///      PHONE     ///
        //////////////////////
        phoneErrorMessage = "";
        phoneTextBox = findViewById(R.id.phone_text_box);
        phoneErrorIcon = findViewById(R.id.phone_error_icon);
        phoneTextBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    phone = phoneTextBox.getText().toString();
                    
                    if (phone.isEmpty() 
                            && phoneErrorIcon.getVisibility() == View.VISIBLE 
                            && checkPhoneExists()) {
                        phoneErrorIcon.setVisibility(View.GONE);
                    }
                }
            }
        });
    
        //////////////////////
        ///      EMAIL     ///
        //////////////////////
        emailErrorMessage = "";
        emailTextBox = findViewById(R.id.email_text_box);
        emailErrorIcon = findViewById(R.id.email_error_icon);
        emailTextBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    email = emailTextBox.getText().toString();
                    
                    if (phone.isEmpty()
                            && emailErrorIcon.getVisibility() == View.VISIBLE
                            && checkEmailExists()) {
                        emailErrorIcon.setVisibility(View.GONE);
                    }
                }
            }
        });
        
        if (editing) {
            ((TextView) findViewById(R.id.header)).setText(R.string.edit_mentor);
    
            mentor = received.getParcelableExtra("mentor");
            if (mentor == null) throw new RuntimeException("Could not get mentor from received.");
    
            name = mentor.getName();
            nameTextBox.setText(name);
    
            phone = mentor.getPhoneDisplay();
            phoneTextBox.setText(phone);
    
            email = mentor.getEmailAddress();
            emailTextBox.setText(email);
            
//            deleteButton.setVisibility(View.VISIBLE); // TODO: 11/13/2019 create options for deleting mentors 
        } else {
            mentor = null;
            
            name = "";
    
            phone = "";
    
            email = "";
        }
    }
    
    private boolean checkNameExists() {
        name = nameTextBox.getText().toString();
        if (name.isEmpty()) {
            nameErrorMessage = "Please put in a name for this mentor.";
            return false;
        }
        return true;
    }
    private boolean checkNameIsUnique() {
        if (name.isEmpty()) return true;
        if (!Mentor.nameIsUnique(name, mentor)) {
            nameErrorMessage = "The mentor's name must be unique. Please try something else.";
            return false;
        }
        return true;
    }
    private boolean checkPhoneExists() {
        phone = phoneTextBox.getText().toString();
        if (phone.isEmpty()) {
            phoneErrorMessage = "Please put in a phone number for this mentor.";
            return false;
        }
        return true;
    }
    private boolean checkEmailExists() {
        email = emailTextBox.getText().toString();
        if (email.isEmpty()) {
            emailErrorMessage = "Please put in a email address for this mentor.";
            return false;
        }
        return true;
    }
    private boolean checkEmailFormat() {
        email = emailTextBox.getText().toString();
        String pattern = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        
        if (!email.matches(pattern)) {
            emailErrorMessage = "The email you entered is not formatted correctly.";
            return false;
        }
        return true;
    }
    private boolean checkInputs() {
        boolean bName = checkNameExists() && checkNameIsUnique();
        nameErrorIcon.setVisibility(bName ? View.GONE : View.VISIBLE);
        
        boolean bPhone = checkPhoneExists();
        phoneErrorIcon.setVisibility(bPhone ? View.GONE : View.VISIBLE);
        
        boolean bEmail = checkEmailExists() && checkEmailFormat();
        emailErrorIcon.setVisibility(bEmail ? View.GONE : View.VISIBLE);
        
        return bName && bPhone && bEmail;
    }
    
    public void showNameErrorMessage(View view) {
        showToast(this, nameErrorMessage);
    }
    public void showPhoneErrorMessage(View view) {
        showToast(this, phoneErrorMessage);
    }
    public void showEmailErrorMessage(View view) {
        showToast(this, emailErrorMessage);
    }
    
    public void saveMentor(View view) {
        if (checkInputs()) {
            if (editing) {
                mentor.update(name, phone, email);
            } else {
                mentor = Mentor.create(name, phone, email);
            }
            Intent intent = new Intent();
            intent.putExtra("mentor", mentor);
            setResult(RESULT_SAVED, intent);
            close(view);
        }
    }
    public void deleteMentor(View view) {
        mentor.delete();
    
        setResult(RESULT_DELETED);
        close(view);
    }
    public void close(View view) {
        finish();
    }
}
