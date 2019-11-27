package com.example.myterms.assessment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myterms.R;
import com.example.myterms.application.Date;
import com.example.myterms.course.Course;
import com.example.myterms.course.CourseViewActivity;

import static com.example.myterms.application.Codes.RESULT_DELETED;
import static com.example.myterms.application.Codes.RESULT_SAVED;
import static com.example.myterms.application.MyFunctions.showToast;
import static com.example.myterms.assessment.Assessment.TYPE_OBJECTIVE;

public class AssessmentEditActivity extends AppCompatActivity {
    private boolean editing;
    private Assessment assessment;

    private int type;
    private Course course;
    
    private String name;
    private String nameErrorMessage;
    private TextView nameTextBox;
    private ImageView nameErrorIcon;
    
    private String description;
    private TextView descriptionTextBox;
    
    private Date completionDate;
    private String completionDateErrorMessage;
    private TextView completionDateDisplay;
    private ImageView completionDateErrorIcon;
    private DatePickerDialog completionDateDialog;
    
    private boolean complete;
    
    private TextView alarmDisplay;
    private DatePickerDialog alarmDateDialog;
    private TimePickerDialog alarmTimeDialog;
    private Date alarm;
    
    private Button saveButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_edit);
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
        type = received.getIntExtra("type", -1);
        int action = received.getIntExtra("action", -1);
        editing = action == CourseViewActivity.ACTION_EDIT;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Assessment " + (editing ? "Editor" : "Creator"));
        actionBar.setDisplayHomeAsUpEnabled(true);
    
        ///////////////////////////
        ///       COURSE        ///
        ///////////////////////////
        course = received.getParcelableExtra("course");
        if (course == null) throw new RuntimeException("Could not get course from received.");
        TextView courseDisplay = findViewById(R.id.course_display);
        courseDisplay.setText(course.getTitle());
    
        ///////////////////////////
        ///        NAME         ///
        ///////////////////////////
        nameErrorMessage = "";
        nameErrorIcon = findViewById(R.id.name_error_icon);
        nameTextBox = findViewById(R.id.name_textbox);
        nameTextBox.requestFocus();
        nameTextBox.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                if (name.isEmpty()) {
                    name = nameTextBox.getText().toString();
                
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
                    name = nameTextBox.getText().toString();
                
                    nameErrorIcon.setVisibility(checkNameIsUnique() ? View.GONE : View.VISIBLE);
                }
            }
        });
    
        ///////////////////////////
        ///     DESCRIPTION     ///
        ///////////////////////////
        descriptionTextBox = findViewById(R.id.description_textbox);
        descriptionTextBox.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                if (description.isEmpty()) {
                    description = descriptionTextBox.getText().toString();
                }
            }
        });
    
        ///////////////////////////
        ///   COMPLETION DATE   ///
        ///////////////////////////
        completionDateErrorMessage = "";
        completionDateDisplay = findViewById(R.id.completion_date_display);
        completionDateErrorIcon = findViewById(R.id.completion_date_error_icon);
        completionDateDisplay.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                selectCompletionDate(view);
            }
        });
    
        CheckBox completeCheckBox = findViewById(R.id.complete_checkbox);
        completeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> complete = isChecked);
    
        ///////////////////////////
        ///        ALARM        ///
        ///////////////////////////
        alarmDisplay = findViewById(R.id.alarm_display);
    
        ///////////////////////////
        ///       BUTTONS       ///
        ///////////////////////////
        saveButton = findViewById(R.id.save_button);
        Button deleteButton = findViewById(R.id.delete_button);
    
        if (editing) {
            ((TextView) findViewById(R.id.header)).setText(type == TYPE_OBJECTIVE ? R.string.edit_objective : R.string.edit_performance);
        
            assessment = received.getParcelableExtra("assessment");
            if (assessment == null) throw new RuntimeException("Could not get assessment from received.");
        
            name = assessment.getName();
            nameTextBox.setText(name);
        
            description = assessment.getDescription();
            descriptionTextBox.setText(description);
        
            completionDate = assessment.getCompletionDate();
            completionDateDisplay.setText(completionDate.getDateDisplay());
            
            complete = assessment.isComplete();
            completeCheckBox.setChecked(complete);
            
            alarm = assessment.getAlarm();
    
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            ((TextView) findViewById(R.id.header)).setText(type == TYPE_OBJECTIVE ? R.string.new_objective : R.string.new_performance);
            
            assessment = null;
        
            name = "";
    
            description = "";
    
            completionDate = course.getEndDate();
            alarm = Date.of(completionDate).setTime(9, 0);
            
            complete = false;
        }
        
        updateAlarmDisplay();
        alarmDateDialog = new DatePickerDialog(this, (view, year, month, day) -> {
            tempAlarm = Date.of(year, month + 1, day);
            alarmTimeDialog.show();
        }, alarm.getYear(), alarm.getMonth() - 1, alarm.getDay());
        alarmTimeDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> setAlarm(hourOfDay, minute), alarm.getHour(), alarm.getMinte(), true);
    
        completionDateDialog = new DatePickerDialog(this, (view, year, month, day) -> setCompletionDate(Date.of(year, month + 1, day)), completionDate.getYear(), completionDate.getMonth() - 1, completionDate.getDay());
    }
    
    private boolean checkNameExists() {
        name = nameTextBox.getText().toString();
        if (name.isEmpty()) {
            nameErrorMessage = "Please put in a name for this assessment.";
            return false;
        }
        return true;
    }
    private boolean checkNameIsUnique() {
        if (name.isEmpty()) return true;
        if (!Assessment.nameIsUnique(course, name, assessment)) {
            nameErrorMessage = "Assessment names must be unique within it's name. Please try something else.";
            return false;
        }
        return true;
    }
    private boolean checkCompletionDateExists() {
        if (completionDateDisplay.getText().length() == 0) {
            completionDateErrorMessage = "Please put in an expected completion date for this assessment.";
            return false;
        }
        
        return true;
    }
    private boolean checkCompletionDateIsInCourse() {
        if (!completionDate.isBetween(course.getStartDate(), course.getEndDate())) {
            completionDateErrorMessage = "Expected completion date is not between the create and end dates of the course.";
            return false;
        }
        
        return true;
    }
    private boolean checkInputs() {
        boolean bName = checkNameExists() && checkNameIsUnique();
        nameErrorIcon.setVisibility(bName ? View.GONE : View.VISIBLE);
        
        boolean bCompletionDate = checkCompletionDateExists() && checkCompletionDateIsInCourse();
        completionDateErrorIcon.setVisibility(bCompletionDate ? View.GONE : View.VISIBLE);
    
        return bName && bCompletionDate;
    }
    
    public void selectCompletionDate(View view) {
        completionDateDialog.show();
    }
    private void setCompletionDate(Date date) {
        long delta = alarm.millisUntil(completionDate);
        
        this.completionDate = date;
        completionDateDisplay.setText(date.getDateDisplay());
        
        if (completionDateErrorIcon.getVisibility() == View.VISIBLE)
            completionDateErrorIcon.setVisibility(checkCompletionDateExists() ? View.GONE : View.VISIBLE);
        
        if (completionDateErrorIcon.getVisibility() != View.VISIBLE && completionDateDisplay.getText().length() > 0)
            completionDateErrorIcon.setVisibility(checkCompletionDateIsInCourse() ? View.GONE : View.VISIBLE);
        
        alarm = Date.of(completionDate).addMilliseconds(delta);
        updateAlarmDisplay();
        
        saveButton.requestFocus();
    }
    
    private Date tempAlarm;
    private void setAlarm(int hour, int minute) {
        alarm = tempAlarm.setTime(hour, minute);
        updateAlarmDisplay();
    }
    private void updateAlarmDisplay() {
        alarmDisplay.setText(alarm.getFormatted("%tb %<te\n%<tl:%<tM %<tp"));
    }
    public void selectAlarmDate(View view) {
        alarmDateDialog.show();
    }
    
    public void showNameErrorMessage(View view) {
        showToast(this, nameErrorMessage);
    }
    public void showCompletionDateErrorMessage(View view) {
        showToast(this, completionDateErrorMessage);
    }

    public void saveAssessment(View view) {
        if (checkInputs()) {
            if (editing) {
                assessment.update(name, description, completionDate, alarm, complete);
            } else {
                assessment = Assessment.create(course, type, name, description, completionDate, alarm, complete);
            }
            Intent intent = new Intent();
            intent.putExtra("assessment", assessment);
            setResult(RESULT_SAVED, intent);
            close(view);
        }
    }
    public void deleteAssessment(View view) {
        assessment.delete();
        setResult(RESULT_DELETED);
        close(view);
    }
    public void close(View view) {
        finish();
    }
}
