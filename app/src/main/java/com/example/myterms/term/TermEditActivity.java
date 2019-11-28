package com.example.myterms.term;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myterms.R;
import com.example.myterms.application.Date;

import static com.example.myterms.application.Codes.REQUEST_EDIT_TERM;
import static com.example.myterms.application.Codes.RESULT_DELETED;
import static com.example.myterms.application.Codes.RESULT_SAVED;
import static com.example.myterms.application.MyFunctions.showToast;

public class TermEditActivity extends AppCompatActivity {
    private static final String TAG = "app: TermEditActivity";
    
    private boolean editing;
    private Term term;
    
    private String title;
    private String titleErrorMessage;
    private EditText titleTextBox;
    private ImageView titleErrorIcon;
    
    private Date startDate;
    private String startDateErrorMessage;
    private TextView startDateDisplay;
    private ImageView startDateErrorIcon;
    private DatePickerDialog startDateDialog;
    
    private Date endDate;
    private String endDateErrorMessage;
    private TextView endDateDisplay;
    private ImageView endDateErrorIcon;
    private DatePickerDialog endDateDialog;

    private RelativeLayout deleteTermErrorPopup;
    
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_edit);

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
        editing = requestCode == REQUEST_EDIT_TERM;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Term " + (editing ? "Editor" : "Creator"));
        actionBar.setDisplayHomeAsUpEnabled(true);
    
        //////////////////////
        ///     TITLE      ///
        //////////////////////
        titleErrorMessage = "";
        titleErrorIcon = findViewById(R.id.title_error_icon);
        titleTextBox = findViewById(R.id.title_textbox);
        titleTextBox.requestFocus();
        titleTextBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
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
            }
        });
    
        //////////////////////
        ///   START DATE   ///
        //////////////////////
        startDateErrorMessage = "";
        startDateDisplay = findViewById(R.id.start_date_display);
        startDateErrorIcon = findViewById(R.id.start_date_error_icon);
        startDateDisplay.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    selectStartDate(view);
                }
            }
        });
    
        //////////////////////
        ///    END DATE    ///
        //////////////////////
        endDateErrorMessage = "";
        endDateDisplay = findViewById(R.id.end_date_display);
        endDateErrorIcon = findViewById(R.id.end_date_error_icon);
        endDateDisplay.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    selectEndDate(view);
                }
            }
        });

        ///////////////////////////////////
        ///   DELETE DATE ERROR POPUP   ///
        ///////////////////////////////////
        deleteTermErrorPopup = findViewById(R.id.delete_term_error_popup);
    
    
        if (editing) {
            ((TextView) findViewById(R.id.header)).setText(R.string.edit_term);
    
            term = received.getParcelableExtra("term");
            if (term == null) throw new RuntimeException("Could not get term from received.");
        
            title = term.getTitle();
            titleTextBox.setText(title);
        
            startDate = term.getStartDate();
            startDateDisplay.setText(startDate.getLongDateDisplay());
        
            endDate = term.getEndDate();
            endDateDisplay.setText(endDate.getLongDateDisplay());
            
            findViewById(R.id.delete_button).setVisibility(View.VISIBLE);
        } else {
            term = null;
        
            title = "";
            
            startDate = Date.today().getFirstOfMonth();
        
            endDate = Date.today().getEndOfMonth();
        }
    
        startDateDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        setStartDate(Date.of(year, month + 1, day));
                    }
                }, startDate.getYear(), startDate.getMonth() - 1, startDate.getDay());
        startDateDialog.setTitle("Select Start Date");
        endDateDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        setEndDate(Date.of(year, month + 1, day));
                    }
                }, endDate.getYear(), endDate.getMonth() - 1, endDate.getDay());
        endDateDialog.setTitle("Select End Date");
    
        saveButton = findViewById(R.id.save_button);
    }
    
    public void selectStartDate(View view) {
        startDateDialog.show();
    }
    private void setStartDate(Date date) {
        this.startDate = date;
        updateDateDisplay(startDateDisplay, startDate);
        
        if (startDateErrorIcon.getVisibility() == View.VISIBLE)
            startDateErrorIcon.setVisibility(checkStartDateExists() ? View.GONE : View.VISIBLE);
        
        if (endDateDisplay.getText().length() > 0)
            endDateErrorIcon.setVisibility(checkEndDateIsAfterStartDate() ? View.GONE : View.VISIBLE);
        
        endDateDisplay.requestFocus();
    }
    
    public void selectEndDate(View view) {
        endDateDialog.show();
    }
    private void setEndDate(Date date) {
        this.endDate = date;
        updateDateDisplay(endDateDisplay, endDate);
        
        if (endDateErrorIcon.getVisibility() == View.VISIBLE)
            endDateErrorIcon.setVisibility(checkEndDateExists() ? View.GONE : View.VISIBLE);
        
        if (endDateErrorIcon.getVisibility() != View.VISIBLE && endDateDisplay.getText().length() > 0)
            endDateErrorIcon.setVisibility(checkEndDateIsAfterStartDate() ? View.GONE : View.VISIBLE);
            
        saveButton.requestFocus();
    }
    
    public void updateDateDisplay(TextView display, Date date) {
        display.setText(date.getLongDateDisplay());
    }
    
    private boolean checkTitleExists() {
        title = titleTextBox.getText().toString();
        if (title.isEmpty()) {
            titleErrorMessage = "Please put in a title for this term.";
            return false;
        }
        return true;
    }
    private boolean checkTitleIsUnique() {
        if (title.isEmpty()) return true;
        if (!Term.titleIsUnique(title, term)) {
            titleErrorMessage = "Term titles must be unique within it's title. Please try something else.";
            return false;
        }
        return true;
    }
    private boolean checkStartDateExists() {
        if (startDateDisplay.getText().length() == 0) {
            startDateErrorMessage = "Please put in a start date for this term.";
            return false;
        }
        
        return true;
    }
    private boolean checkEndDateExists() {
        if (endDateDisplay.getText().length() == 0) {
            endDateErrorMessage = "Please put in an end date for this term.";
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
    private boolean checkInputs() {
        boolean bTitle = checkTitleExists() && checkTitleIsUnique();
        titleErrorIcon.setVisibility(bTitle ? View.GONE : View.VISIBLE);
        boolean bStartDate = checkStartDateExists();
        startDateErrorIcon.setVisibility(bStartDate ? View.GONE : View.VISIBLE);
        boolean bEndDate = checkEndDateExists() && checkEndDateIsAfterStartDate();
        endDateErrorIcon.setVisibility(bEndDate ? View.GONE : View.VISIBLE);

        return bTitle && bStartDate && bEndDate;
    }

    public void showTitleErrorMessage(View view) {
        showToast(this, titleErrorMessage);
    }
    public void showStartDateErrorMessage(View view) {
        showToast(this, startDateErrorMessage);
    }
    public void showEndDateErrorMessage(View view) {
        showToast(this, endDateErrorMessage);
    }

    public void saveTerm(View view) {
        if (checkInputs()) {
            if (editing) {
                term.update(title, startDate, endDate);
            } else {
                term = Term.create(title, startDate, endDate);
            }
            Intent intent = new Intent();
            intent.putExtra("term", term);
            setResult(RESULT_SAVED, intent);
            close(view);
        }
    }
    public void deleteTerm(View view) {
        if (term.getCourses().isEmpty()) {
            term.delete();

            setResult(RESULT_DELETED);
            close(view);
        } else {
            deleteTermErrorPopup.setVisibility(View.VISIBLE);
        }
    }
    public void closePopup(View view) {
        deleteTermErrorPopup.setVisibility(View.GONE);
    }
    public void deleteCourses(View view) {
        term.deleteCoursesAndDelete();

        setResult(RESULT_DELETED);
        close(view);
    }
    public void close(View view) {
        finish();
    }
}
