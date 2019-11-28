package com.example.myterms.course;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myterms.R;
import com.example.myterms.application.Date;
import com.example.myterms.mentor.Mentor;
import com.example.myterms.mentor.MentorSelectActivity;
import com.example.myterms.term.Term;
import com.example.myterms.term.TermSelectActivity;

import java.util.ArrayList;

import static com.example.myterms.application.Codes.REQUEST_EDIT_COURSE;
import static com.example.myterms.application.Codes.REQUEST_SELECT_MENTOR;
import static com.example.myterms.application.Codes.REQUEST_SELECT_TERM;
import static com.example.myterms.application.Codes.RESULT_CONFIRMED;
import static com.example.myterms.application.Codes.RESULT_SAVED;
import static com.example.myterms.application.MyFunctions.showToast;
import static com.example.myterms.course.Course.Status;
import static com.example.myterms.course.Course.Status.COMPLETED;
import static com.example.myterms.course.Course.Status.DROPPED;
import static com.example.myterms.course.Course.Status.IN_PROGRESS;
import static com.example.myterms.course.Course.Status.PLAN_TO_TAKE;

public class CourseEditActivity extends AppCompatActivity {
    private boolean editing;
    private Course course;
    
    private Term term;
    private String termErrorMessage;
    private TextView termDisplay;
    private ImageView termErrorIcon;

    private String title;
    private String titleErrorMessage;
    private EditText titleTextBox;
    private ImageView titleErrorIcon;

    private ArrayList<Mentor> mentors;
    private String mentorsErrorMessage;
    private TextView mentorDisplay;
    private ImageView mentorsErrorIcon;

    private int credits;
    private String creditsErrorMessage;
    private EditText creditsTextBox;
    private ImageView creditsErrorIcon;

    private Date startDate;
    private String startDateErrorMessage;
    private TextView startDateDisplay;
    private ImageView startDateErrorIcon;
    private DatePickerDialog startDateDialog;
    private TextView startAlarmDisplay;
    private DatePickerDialog startAlarmDateDialog;
    private TimePickerDialog startAlarmTimeDialog;
    private Date startAlarm;

    private Date endDate;
    private String endDateErrorMessage;
    private TextView endDateDisplay;
    private ImageView endDateErrorIcon;
    private DatePickerDialog endDateDialog;
    private TextView endAlarmDisplay;
    private DatePickerDialog endAlarmDateDialog;
    private TimePickerDialog endAlarmTimeDialog;
    private Date endAlarm;

    private Status status;
    private TextView statusDisplay;
    private String statusErrorMessage;
    private ImageView statusErrorIcon;
    
    private RelativeLayout selectStatusPopup;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_edit);
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
        editing = requestCode == REQUEST_EDIT_COURSE;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Course " + (editing ? "Editor" : "Creator"));
        actionBar.setDisplayHomeAsUpEnabled(true);
    
        ///////////////////////////////
        ///          TERM           ///
        ///////////////////////////////
        termErrorMessage = "";
        termDisplay = findViewById(R.id.term_display);
        termErrorIcon = findViewById(R.id.term_error_icon);
        termDisplay.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                selectTerm(view);
            }
        });
        
        ///////////////////////////////
        ///          TITLE          ///
        ///////////////////////////////
        titleErrorMessage = "";
        titleErrorIcon = findViewById(R.id.title_error_icon);
        titleTextBox = findViewById(R.id.title_textbox);
        titleTextBox.requestFocus();
        titleTextBox.setOnFocusChangeListener((v, hasFocus) -> {
        if (!hasFocus) {
            if (title.isEmpty()) {
                title = titleTextBox.getText().toString();
    
                if (titleErrorIcon.getVisibility() == View.VISIBLE) {
                    if (checkTitleExists() && checkTitleIsUnique()) {
                        titleErrorIcon.setVisibility(View.GONE);
                    }
                } else {
                    if (!checkTitleIsUnique()) {
                        titleErrorIcon.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                title = titleTextBox.getText().toString();
    
                titleErrorIcon.setVisibility(checkTitleIsUnique() ? View.GONE : View.VISIBLE);
            }
        }
        });
    
        ///////////////////////////////
        ///         MENTORS         ///
        ///////////////////////////////
        mentorsErrorMessage = "";
        mentorDisplay = findViewById(R.id.mentors_display);
        mentorsErrorIcon = findViewById(R.id.mentors_error_icon);
        mentorDisplay.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                selectMentors(view);
            }
        });
    
        ///////////////////////////////
        ///         CREDITS         ///
        ///////////////////////////////
        creditsErrorMessage = "";
        creditsErrorIcon = findViewById(R.id.credits_error_icon);
        creditsTextBox = findViewById(R.id.credits_textbox);
        creditsTextBox.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String credString = creditsTextBox.getText().toString();
                if (!credString.isEmpty())
                    credits = Integer.parseInt(credString);
                else
                    credits = -1;

                if (creditsErrorIcon.getVisibility() == View.VISIBLE)
                    creditsErrorIcon.setVisibility(checkCreditsExists() ? View.GONE : View.VISIBLE);
            }
        });
    
        ///////////////////////////////
        ///       START DATE        ///
        ///////////////////////////////
        startDateErrorMessage = "";
        startDateDisplay = findViewById(R.id.start_date_display);
        startDateErrorIcon = findViewById(R.id.start_date_error_icon);
        startDateDisplay.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                selectStartDate(view);
            }
        });
        startAlarmDisplay = findViewById(R.id.start_alarm_display);
    
        ///////////////////////////////
        ///        END DATE         ///
        ///////////////////////////////
        endDateErrorMessage = "";
        endDateDisplay = findViewById(R.id.end_date_display);
        endDateErrorIcon = findViewById(R.id.end_date_error_icon);
        endDateDisplay.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                selectEndDate(view);
            }
        });
        endAlarmDisplay = findViewById(R.id.end_alarm_display);
    
        ///////////////////////////////
        ///         STATUS          ///
        ///////////////////////////////
        statusErrorMessage = "";
        statusDisplay = findViewById(R.id.status_display);
        statusErrorIcon = findViewById(R.id.status_error_icon);
        statusDisplay.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                selectStatus(view);
            }
        });
    
        ///////////////////////////////
        ///   SELECT STATUS POPUP   ///
        ///////////////////////////////
        selectStatusPopup = findViewById(R.id.select_status_popup);
        Button planToTakeButton = findViewById(R.id.status_plan_to_take_button);
        planToTakeButton.setOnClickListener(view -> setStatus(PLAN_TO_TAKE));
        Button inProgressButton = findViewById(R.id.status_in_progress_button);
        inProgressButton.setOnClickListener(view -> setStatus(IN_PROGRESS));
        Button completedButton = findViewById(R.id.status_completed_button);
        completedButton.setOnClickListener(view -> setStatus(COMPLETED));
        Button droppedButton = findViewById(R.id.status_dropped_button);
        droppedButton.setOnClickListener(view -> setStatus(DROPPED));
        
        if (editing) {
            ((TextView) findViewById(R.id.header)).setText(R.string.edit_course);

            course = received.getParcelableExtra("course");
            if (course == null) throw new RuntimeException("Could not get course from received.");
            
            term = course.getTerm();
            termDisplay.setText(term.getTitle());

            title = course.getTitle();
            titleTextBox.setText(title);

            mentors = course.getMentors();
            mentorDisplay.setText(getMentorDisplay());

            credits = course.getCredits();
            creditsTextBox.setText(getCreditDisplay());

            startDate = course.getStartDate();
            startDateDisplay.setText(startDate.getLongDateDisplay());
            startAlarm = course.getStartAlarm();

            endDate = course.getEndDate();
            endDateDisplay.setText(endDate.getLongDateDisplay());
            endAlarm = course.getEndAlarm();

            status = course.getStatus();
            statusDisplay.setText(status.getName());
        } else {
            course = null;

            term = received.getParcelableExtra("term");
            if (term == null) throw new RuntimeException("Could not get term from received.");
            
            termDisplay.setText(term.getTitle());
            
            title = "";
    
            mentors = new ArrayList<>();
    
            credits = -1;
    
            startDate = term.getStartDate();
            startAlarm = Date.of(startDate).setTime(9, 0);
    
            endDate = term.getEndDate();
            endAlarm = Date.of(endDate).setTime(9, 0);
            
            status = PLAN_TO_TAKE;
            statusDisplay.setText(status.getName());
        }
    
        startDateDialog = new DatePickerDialog(this,
                (view, year, month, day) -> setStartDate(Date.of(year, month + 1, day)), startDate.getYear(), startDate.getMonth() - 1, startDate.getDay());
        startDateDialog.setTitle("Select Start Date");
        updateStartAlarmDisplay();
        startAlarmDateDialog = new DatePickerDialog(this, (view, year, month, day) -> {
            tempStartAlarm = Date.of(year, month + 1, day);
            startAlarmTimeDialog.show();
        }, startAlarm.getYear(), startAlarm.getMonth() - 1, startAlarm.getDay());
        startAlarmTimeDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> setStartAlarm(hourOfDay, minute), startAlarm.getHour(), startAlarm.getMinte(), true);
    
        endDateDialog = new DatePickerDialog(this,
                (view, year, month, day) -> setEndDate(Date.of(year, month + 1, day)), endDate.getYear(), endDate.getMonth() - 1, endDate.getDay());
        endDateDialog.setTitle("Select End Date");
        updateEndAlarmDisplay();
        endAlarmDateDialog = new DatePickerDialog(this, (view, year, month, day) -> {
            tempEndAlarm = Date.of(year, month + 1, day);
            endAlarmTimeDialog.show();
        }, endAlarm.getYear(), endAlarm.getMonth() - 1, endAlarm.getDay());
        endAlarmTimeDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> setEndAlarm(hourOfDay, minute), endAlarm.getHour(), endAlarm.getMinte(), true);
    }

    public void selectTerm(View view) {
        Intent intent = new Intent(this, TermSelectActivity.class);
        intent.putExtra("selectedTerm", term);
        startActivityForResult(intent, REQUEST_SELECT_TERM);
    }
    
    public String getMentorDisplay() {
        String output = "";

        for (Mentor mentor : mentors) {
            if (!output.isEmpty()) output += ", ";
            output += mentor.getName();
        }

        return output;
    }
    public void selectMentors(View view) {
        long[] mentorIDs = new long[mentors.size()];

        for (int i = 0; i < mentors.size(); i++) {
            mentorIDs[i] = mentors.get(i).getId();
        }
        
        Intent intent = new Intent(this, MentorSelectActivity.class);
        intent.putExtra("selectedMentorIDs", mentorIDs);
        startActivityForResult(intent, REQUEST_SELECT_MENTOR);
    }
    
    public String getCreditDisplay() {
        return credits >= 0 ? String.valueOf(credits) : "";
    }
    
    public void selectStartDate(View view) {
        startDateDialog.show();
    }
    private void setStartDate(Date date) {
        long delta = startAlarm.millisUntil(startDate);
        
        this.startDate = date;
        updateDateDisplay(startDateDisplay, startDate);
        
        if (startDateErrorIcon.getVisibility() == View.VISIBLE)
            startDateErrorIcon.setVisibility(checkStartDateExists() ? View.GONE : View.VISIBLE);
        
        if (startDateErrorIcon.getVisibility() != View.VISIBLE && startDateDisplay.getText().length() > 0)
            startDateErrorIcon.setVisibility(checkStartDateIsInTerm() ? View.GONE : View.VISIBLE);
        
        if (endDateDisplay.getText().length() > 0)
            endDateErrorIcon.setVisibility(checkEndDateIsAfterStartDate() ? View.GONE : View.VISIBLE);
    
        startAlarm = Date.of(startDate).addMilliseconds(delta);
        updateStartAlarmDisplay();
    
        endDateDisplay.requestFocus();
    }
    private Date tempStartAlarm;
    private void setStartAlarm(int hour, int minute) {
        startAlarm = tempStartAlarm.setTime(hour, minute);
        updateStartAlarmDisplay();
    }
    private void updateStartAlarmDisplay() {
        startAlarmDisplay.setText(startAlarm.getFormatted("%tb %<te\n%<tl:%<tM %<tp"));
    }
    public void selectStartAlarmDate(View view) {
        startAlarmDateDialog.show();
    }
    
    public void selectEndDate(View view) {
        endDateDialog.show();
    }
    private void setEndDate(Date date) {
        long delta = endAlarm.millisUntil(endDate);
        
        this.endDate = date;
        updateDateDisplay(endDateDisplay, endDate);
        
        if (endDateErrorIcon.getVisibility() == View.VISIBLE)
            endDateErrorIcon.setVisibility(checkEndDateExists() ? View.GONE : View.VISIBLE);
        
        if (endDateErrorIcon.getVisibility() != View.VISIBLE && endDateDisplay.getText().length() > 0)
            endDateErrorIcon.setVisibility(checkEndDateIsAfterStartDate() ? View.GONE : View.VISIBLE);
        
        if (endDateErrorIcon.getVisibility() != View.VISIBLE && endDateDisplay.getText().length() > 0)
            endDateErrorIcon.setVisibility(checkEndDateIsInTerm() ? View.GONE : View.VISIBLE);
        
        endAlarm = Date.of(endDate).addMilliseconds(delta);
        updateEndAlarmDisplay();
    
        statusDisplay.requestFocus();
    }
    private Date tempEndAlarm;
    private void setEndAlarm(int hour, int minute) {
        endAlarm = tempEndAlarm.setTime(hour, minute);
        updateEndAlarmDisplay();
    }
    private void updateEndAlarmDisplay() {
        endAlarmDisplay.setText(endAlarm.getFormatted("%tb %<te\n%<tl:%<tM %<tp"));
    }
    public void selectEndAlarmDate(View view) {
        endAlarmDateDialog.show();
    }
    
    public void updateDateDisplay(TextView display, Date date) {
        display.setText(date.getLongDateDisplay());
    }
    
    public void selectStatus(View view) {
        selectStatusPopup.setVisibility(View.VISIBLE);
    }
    public void setStatus(Status status) {
        this.status = status;
        
        statusDisplay.setText(status.getName());
        
        statusErrorIcon.setVisibility(View.GONE);
        
        selectStatusPopup.setVisibility(View.GONE);
    }
    
    private boolean checkTermExists() {
        
        if (term == null) {
            termErrorMessage = "Please select a term for this course.";
            return false;
        }

        return true;
    }
    private boolean checkTitleExists() {
        title = titleTextBox.getText().toString();
        if (title.isEmpty()) {
            titleErrorMessage = "Please put in a title for this course.";
            return false;
        }
        return true;
    }
    private boolean checkTitleIsUnique() {
        if (title.isEmpty()) return true;
        if (!Course.titleIsUnique(term, title, course)) {
            titleErrorMessage = "Course titles must be unique within it's title. Please try something else.";
            return false;
        }
        return true;
    }
    private boolean checkMentorsExists() {
        if (mentors.isEmpty()) {
            mentorsErrorMessage = "Please select at least one mentor for this course.";
            return false;
        }

        return true;
    }
    private boolean checkCreditsExists() {
        String value = creditsTextBox.getText().toString();
        if (!value.isEmpty()) {
            credits = Integer.parseInt(value);
            if (credits < 0) {
                creditsErrorMessage = "Please set the number of credits for this course.";
                return false;
            }
            return true;
        }
        return false;

    }
    private boolean checkStartDateExists() {
        if (startDateDisplay.getText().length() == 0) {
            startDateErrorMessage = "Please put in a start date for this course.";
            return false;
        }

        return true;
    }
    private boolean checkStartDateIsInTerm() {
        if (!startDate.isBetween(term.getStartDate(), term.getEndDate())) {
            startDateErrorMessage = "Start date is not between the create and end dates of the term.";
            return false;
        }

        return true;
    }
    private boolean checkEndDateExists() {
        if (endDateDisplay.getText().length() == 0) {
            endDateErrorMessage = "Please put in an end date for this course.";
            return false;
        }
        
        return true;
    }
    private boolean checkEndDateIsAfterStartDate() {
        if (endDate.onBefore(startDate)) {
            endDateErrorMessage = "The given end date is for before the given start date.";
            return false;
        }
        
        return true;
    }
    private boolean checkEndDateIsInTerm() {
        if (!endDate.isBetween(term.getStartDate(), term.getEndDate())) {
            endDateErrorMessage = "End date is not between the create and end dates of the term.";
            return false;
        }
        
        return true;
    }
    private boolean checkInputs() {
        boolean bTerm = checkTermExists();
        termErrorIcon.setVisibility(bTerm ? View.GONE : View.VISIBLE);
        
        boolean bTitle = checkTitleExists() && checkTitleIsUnique();
        titleErrorIcon.setVisibility(bTitle ? View.GONE : View.VISIBLE);
    
        boolean bMentors = checkMentorsExists();
        mentorsErrorIcon.setVisibility(bMentors ? View.GONE : View.VISIBLE);
    
        boolean bCredits = checkCreditsExists();
        creditsErrorIcon.setVisibility(bCredits ? View.GONE : View.VISIBLE);
    
        boolean bStartDate = checkStartDateExists() && checkStartDateIsInTerm();
        startDateErrorIcon.setVisibility(bStartDate ? View.GONE : View.VISIBLE);
    
        boolean bEndDate = checkEndDateExists() && checkEndDateIsAfterStartDate() && checkEndDateIsInTerm();
        endDateErrorIcon.setVisibility(bEndDate ? View.GONE : View.VISIBLE);
    
        return bTerm && bTitle && bMentors && bCredits && bStartDate && bEndDate;
    }

    public void showTermErrorMessage(View view) {
        showToast(this, termErrorMessage);
    }
    public void showTitleErrorMessage(View view) {
        showToast(this, titleErrorMessage);
    }
    public void showMentorsErrorMessage(View view) {
        showToast(this, mentorsErrorMessage);
    }
    public void showCreditsErrorMessage(View view) {
        showToast(this, creditsErrorMessage);
    }
    public void showStartDateErrorMessage(View view) {
        showToast(this, startDateErrorMessage);
    }
    public void showEndDateErrorMessage(View view) {
        showToast(this, endDateErrorMessage);
    }
    public void showStatusErrorMessage(View view) {
        showToast(this, statusErrorMessage);
    }

    public void saveCourse(View view) {
        if (checkInputs()) {
            if (editing) {
                course.update(term, title, mentors, credits, startDate, endDate, startAlarm, endAlarm, status);
            } else {
                course = Course.create(term, title, mentors, credits, startDate, endDate, startAlarm, endAlarm, status);
            }
            Intent intent = new Intent();
            intent.putExtra("course", course);
            setResult(RESULT_SAVED, intent);
            close(view);
        }
    }
    public void close(View view) {
        finish();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_SELECT_TERM) {
            if (resultCode == RESULT_CONFIRMED) {
                Term selectedTerm = data.getParcelableExtra("selectedTerm");
                if (selectedTerm == null) throw new RuntimeException("Could not pull selected term from data.");
                
                if (!selectedTerm.equals(term)) {
                    this.term = selectedTerm;
                    
                    termDisplay.setText(term.getTitle());
                    
                    if (termErrorIcon.getVisibility() == View.VISIBLE)
                        termErrorIcon.setVisibility(checkTermExists() ? View.GONE : View.VISIBLE);
                    
                    if (!title.isEmpty())
                        titleErrorIcon.setVisibility(checkTitleIsUnique() ? View.GONE : View.VISIBLE);
                    
                    if (!checkStartDateIsInTerm()) {
                        startDate = term.getStartDate();
                        startDateDialog.updateDate(startDate.getYear(), startDate.getMonth(), startDate.getDay());
                        startDateErrorIcon.setVisibility(View.VISIBLE);
                    }
                    
                    if (!checkEndDateIsAfterStartDate() || !checkEndDateIsInTerm()) {
                        endDate = term.getEndDate();
                        endDateDialog.updateDate(endDate.getYear(), endDate.getMonth(), endDate.getDay());
                        endDateErrorIcon.setVisibility(View.VISIBLE);
                    }
                }
            }
            
            titleTextBox.requestFocus();
        }
        
        if (requestCode == REQUEST_SELECT_MENTOR) {
            if (resultCode == RESULT_CONFIRMED) {
                long[] ids = data.getLongArrayExtra("selectedMentorIDs");
                
                mentors = ids != null
                        ? Mentor.findAllByIDs(ids)
                        : new ArrayList<>();
                
                mentorDisplay.setText(getMentorDisplay());
                
                if (mentorsErrorIcon.getVisibility() == View.VISIBLE)
                    mentorsErrorIcon.setVisibility(checkMentorsExists() ? View.GONE : View.VISIBLE);
            }
    
            creditsTextBox.requestFocus();
        }
    }
    
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra("requestCode", requestCode);
        super.startActivityForResult(intent, requestCode);
    }
}
