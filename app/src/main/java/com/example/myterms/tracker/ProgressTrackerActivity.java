package com.example.myterms.tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myterms.R;
import com.example.myterms.assessment.Assessment;
import com.example.myterms.course.Course;
import com.example.myterms.course.CourseViewActivity;
import com.example.myterms.term.Term;
import com.example.myterms.term.TermViewActivity;

import static com.example.myterms.application.Codes.REQUEST_VIEW_COURSE;
import static com.example.myterms.application.Codes.REQUEST_VIEW_TERM;
import static com.example.myterms.course.Course.Status.COMPLETED;
import static com.example.myterms.course.Course.Status.DROPPED;
import static com.example.myterms.course.Course.Status.IN_PROGRESS;
import static com.example.myterms.course.Course.Status.PLAN_TO_TAKE;

public class ProgressTrackerActivity extends AppCompatActivity {
    
    TextView courseInProgressDisplay;
    TextView coursePlanToTakeDisplay;
    TextView courseCompletedDisplay;
    TextView courseDroppedDisplay;
    TextView courseCompletionDisplay;
    TextView assessmentIncompleteDisplay;
    TextView assessmentCompleteDisplay;
    TextView assessmentCompletionDisplay;
    
    TextView upcomingTermLabel;
    RecyclerView upcomingTermRecycler;
    TermListAdapter termListAdapter;
    TextView upcomingCourseLabel;
    RecyclerView upcomingCourseRecycler;
    CourseListAdapter courseListAdapter;
    TextView upcomingAssessmentLabel;
    RecyclerView upcomingAssessmentRecycler;
    AssessmentListAdapter assessmentListAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_tracker);
        
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
    
    public void buildActivity() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Progress Tracker");
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        ///   COURSE IN PROGRESS   ///
        courseInProgressDisplay = findViewById(R.id.course_in_progress_display);
        setCourseInProgressDisplay();
        
        ///   COURSE PLAN TO TAKE   ///
        coursePlanToTakeDisplay = findViewById(R.id.course_plan_to_take_display);
        setCoursePlanToTakeDisplay();
    
        ///   COURSE COMPLETED   ///
        courseCompletedDisplay = findViewById(R.id.course_completed_display);
        setCourseCompletedDisplay();
    
        ///   COURSE DROPPED   ///
        courseDroppedDisplay = findViewById(R.id.course_dropped_display);
        setCourseDroppedDisplay();
    
        ///   COURSE COMPLETION   ///
        courseCompletionDisplay = findViewById(R.id.course_completion_display);
        setCourseCompletionDisplay();
    
        ///   ASSESSMENT INCOMPLETE   ///
        assessmentIncompleteDisplay = findViewById(R.id.assessment_incomplete_display);
        setAssessmentIncompleteDisplay();
    
        ///   ASSESSMENT COMPLETE   ///
        assessmentCompleteDisplay = findViewById(R.id.assessment_complete_display);
        setAssessmentCompleteDisplay();
    
        ///   ASSESSMENT COMPLETION   ///
        assessmentCompletionDisplay = findViewById(R.id.assessment_completion_display);
        setAssessmentCompletionDisplay();
    
        ///   UPCOMING TERMS   ///
        upcomingTermLabel = findViewById(R.id.upcoming_terms_label);
        upcomingTermRecycler = findViewById(R.id.upcoming_terms_recycler);
        termListAdapter = new TermListAdapter(this);
    
        ///   UPCOMING COURSES   ///
        upcomingCourseLabel = findViewById(R.id.upcoming_courses_label);
        upcomingCourseRecycler = findViewById(R.id.upcoming_courses_recycler);
        courseListAdapter = new CourseListAdapter(this);
    
        ///   UPCOMING ASSESSMENTS   ///
        upcomingAssessmentLabel = findViewById(R.id.upcoming_assessments_label);
        upcomingAssessmentRecycler = findViewById(R.id.upcoming_assessments_recycler);
        assessmentListAdapter = new AssessmentListAdapter(this);
    }
    
    public void setCourseInProgressDisplay() {
        int n = Course.findAllByStatus(IN_PROGRESS).size();
        courseInProgressDisplay.setText(String.valueOf(n));
    }
    
    public void setCoursePlanToTakeDisplay() {
        int n = Course.findAllByStatus(PLAN_TO_TAKE).size();
        coursePlanToTakeDisplay.setText(String.valueOf(n));
    }
    
    public void setCourseCompletedDisplay() {
        int n = Course.findAllByStatus(COMPLETED).size();
        courseCompletedDisplay.setText(String.valueOf(n));
    }
    
    public void setCourseDroppedDisplay() {
        int n = Course.findAllByStatus(DROPPED).size();
        courseDroppedDisplay.setText(String.valueOf(n));
    }
    
    static final String courseCompletionFormat = "You have completed %3.2f%% of your courses.";
    public void setCourseCompletionDisplay() {
        double t = Course.findAll("WHERE status >= 0").size();
        double c = Course.findAll("WHERE status = 0").size();
        
        double p = c / t * 100;
        String output = String.format(courseCompletionFormat, t > 0 ? p : 100.0);
        
        courseCompletionDisplay.setText(output);
    }
    
    public void setAssessmentIncompleteDisplay() {
        int n = Assessment.findAll("WHERE complete = 0").size();
        assessmentIncompleteDisplay.setText(String.valueOf(n));
    }
    
    public void setAssessmentCompleteDisplay() {
        int n = Assessment.findAll("WHERE complete = 1").size();
        assessmentCompleteDisplay.setText(String.valueOf(n));
    }
    
    static final String assessmentCompletionFormat = "You have completed %3.2f%% of your assessment.";
    public void setAssessmentCompletionDisplay() {
        double t = Assessment.findAll().size();
        double c = Assessment.findAll("WHERE complete = 1").size();
    
        double p = c / t * 100;
        String output = String.format(assessmentCompletionFormat, t > 0 ? p : 100.0);
    
        assessmentCompletionDisplay.setText(output);
    }
    
    public void viewTerm(Term term) {
        Intent intent = new Intent(this, TermViewActivity.class);
        intent.putExtra("term", term);
        startActivityForResult(intent, REQUEST_VIEW_TERM);
    }
    
    public void viewCourse(Course course) {
        Intent intent = new Intent(this, CourseViewActivity.class);
        intent.putExtra("course", course);
        startActivityForResult(intent, REQUEST_VIEW_COURSE);
    }
    
    public void viewAssessment(Assessment assessment) {
        Intent intent = new Intent(this, CourseViewActivity.class);
        intent.putExtra("course", assessment.getCourse());
        startActivityForResult(intent, REQUEST_VIEW_COURSE);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        setCourseInProgressDisplay();
        setCoursePlanToTakeDisplay();
        setCourseCompletedDisplay();
        setCourseDroppedDisplay();
        setCourseCompletionDisplay();
        setAssessmentIncompleteDisplay();
        setAssessmentCompleteDisplay();
        setAssessmentCompletionDisplay();
        
        termListAdapter.refresh();
        courseListAdapter.refresh();
        assessmentListAdapter.refresh();
    }
}
