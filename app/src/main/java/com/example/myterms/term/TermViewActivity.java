package com.example.myterms.term;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myterms.R;
import com.example.myterms.application.Codes;
import com.example.myterms.course.Course;
import com.example.myterms.course.CourseCardAdapter;
import com.example.myterms.course.CourseEditActivity;
import com.example.myterms.course.CourseViewActivity;

public class TermViewActivity extends AppCompatActivity implements Codes {
    private Term term;

    private TextView titleDisplay;
    private TextView dateDisplay;
    private TextView creditsDisplay;
    private CheckBox listToggle;

    private CourseCardAdapter courseCardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_view);

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
        term = received.getParcelableExtra("term");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Term View");
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        

        titleDisplay = findViewById(R.id.title_display);
        titleDisplay.setText(term.getTitle());
        dateDisplay = findViewById(R.id.date_display);
        dateDisplay.setText(term.getDateRangeDisplay());
        creditsDisplay = findViewById(R.id.credits_display);
        creditsDisplay.setText(term.getCreditsDisplay());
        listToggle = findViewById(R.id.view_active_check_box);

        RecyclerView.LayoutManager courseLayoutManager = new LinearLayoutManager(this);
        courseCardAdapter = new CourseCardAdapter(this, term);
        RecyclerView courseRecycler = findViewById(R.id.course_recycler);
        courseRecycler.setLayoutManager(courseLayoutManager);
        courseRecycler.setAdapter(courseCardAdapter);
    }

    public void updateInfo() {
        term = Term.findByID(term.getId());
        if (term != null) {
            titleDisplay.setText(term.getTitle());
            dateDisplay.setText(term.getDateRangeDisplay());
            creditsDisplay.setText(term.getCreditsDisplay());
        }
    }

    public void editTerm(View view) {
        Intent intent = new Intent(this, TermEditActivity.class);
        intent.putExtra("term", term);
        startActivityForResult(intent, REQUEST_EDIT_TERM);
    }
    public void createCourse(View view) {
        Intent intent = new Intent(this, CourseEditActivity.class);
        intent.putExtra("term", term);
        startActivityForResult(intent, REQUEST_CREATE_COURSE);
    }

    public void editCourse(Course course) {
        Intent intent = new Intent(this, CourseEditActivity.class);
        intent.putExtra("course", course);
        startActivityForResult(intent, REQUEST_EDIT_COURSE);
    }

    public void viewCourse(Course course) {
        Intent intent = new Intent(this, CourseViewActivity.class);
        intent.putExtra("course", course);
        startActivityForResult(intent, REQUEST_VIEW_COURSE);
    }

    public void toggleList(View view) {
        courseCardAdapter.setViewAll(listToggle.isChecked());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDIT_TERM) {
            if (resultCode == RESULT_DELETED) {
                setResult(RESULT_DELETED);
                finish();
            }
        }

        if (requestCode == REQUEST_CREATE_COURSE) {
            if (resultCode == RESULT_SAVED) {
                Course course = data.getParcelableExtra("course");
                viewCourse(course);
            }
        }

        courseCardAdapter.refresh();
        updateInfo();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra("requestCode", requestCode);
        super.startActivityForResult(intent, requestCode);
    }
}
