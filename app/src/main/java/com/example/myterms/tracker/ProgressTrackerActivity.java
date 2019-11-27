package com.example.myterms.tracker;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myterms.R;
import com.example.myterms.assessment.Assessment;
import com.example.myterms.course.Course;
import com.example.myterms.term.Term;

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
    TextView upcomingAssessmentLabel;
    RecyclerView upcomingAssessmentRecycler;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_tracker);
        
        buildActivity();
    }
    
    public void buildActivity() {
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
    
        ///   UPCOMING ASSESSMENTS   ///
        upcomingAssessmentLabel = findViewById(R.id.upcoming_assessments_label);
        upcomingAssessmentRecycler = findViewById(R.id.upcoming_assessments_recycler);
    }
    
    public void setCourseInProgressDisplay() {
        int n = Course.findAllByStatus(IN_PROGRESS).size();
        courseInProgressDisplay.setText(n);
    }
    
    public void setCoursePlanToTakeDisplay() {
        int n = Course.findAllByStatus(PLAN_TO_TAKE).size();
        coursePlanToTakeDisplay.setText(n);
    }
    
    public void setCourseCompletedDisplay() {
        int n = Course.findAllByStatus(COMPLETED).size();
        courseCompletedDisplay.setText(n);
    }
    
    public void setCourseDroppedDisplay() {
        int n = Course.findAllByStatus(DROPPED).size();
        courseDroppedDisplay.setText(n);
    }
    
    static final String courseCompletionFormat = "You have completed %6.2f%% of your courses.";
    public void setCourseCompletionDisplay() {
        int t = Course.findAll("WHERE status >= 0").size();
        int c = Course.findAll("WHERE status = 0").size();
        
        double p = c / t;
        
        courseCompletionDisplay.setText(String.format(courseCompletionFormat, p));
    }
    
    public void setAssessmentIncompleteDisplay() {
        int n = Assessment.findAll("WHERE complete = 0").size();
        courseDroppedDisplay.setText(n);
    }
    
    public void setAssessmentCompleteDisplay() {
        int n = Assessment.findAll("WHERE complete = 1").size();
        courseDroppedDisplay.setText(n);
    }
    
    static final String assessmentCompletionFormat = "You have completed %6.2f%% of your assessment.";
    public void setAssessmentCompletionDisplay() {
        int t = Assessment.findAll().size();
        int c = Assessment.findAll("WHERE complete = 1").size();
    
        double p = c / t;
    
        assessmentCompletionDisplay.setText(String.format(assessmentCompletionFormat, p));
    }
    
    public void viewTerm(Term term) {
    
    }
    
    public void viewCourse(Course course) {
    
    }
    
    public void viewAssessment(Assessment assessment) {
    
    }
}
