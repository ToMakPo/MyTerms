package com.example.myterms.course;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myterms.R;
import com.example.myterms.application.Codes;
import com.example.myterms.assessment.Assessment;
import com.example.myterms.assessment.AssessmentCardAdapter;
import com.example.myterms.assessment.AssessmentEditActivity;
import com.example.myterms.mentor.Mentor;
import com.example.myterms.note.Note;
import com.example.myterms.note.NoteCardAdapter;
import com.example.myterms.note.NoteEditActivity;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

import static com.example.myterms.assessment.Assessment.TYPE_OBJECTIVE;
import static com.example.myterms.assessment.Assessment.TYPE_PERFORMANCE;

public class CourseViewActivity extends AppCompatActivity implements Codes {
    private static final String TAG = "app: CVActivity";
    
    private Course course;
    
    public static final int ACTION_CREATE = 0, ACTION_EDIT = 1;

    private TextView titleDisplay;
    private TextView dateDisplay;
    private FlexboxLayout mentorList;
    private TextView creditsDisplay;
    private TextView statusDisplay;

    private ImageView statusCompleteIcon;
    private ImageView statusInProgressIcon;
    private ImageView statusPlanToTakeIcon;
    private ImageView statusDroppedIcon;

    private AssessmentCardAdapter objectiveCardAdapter;
    private AssessmentCardAdapter performanceCardAdapter;
    private NoteCardAdapter noteCardAdapter;

    private RelativeLayout viewMentorPopup;
    private TextView mentorNameDisplay;
    private TextView mentorPhoneDisplay;
    private TextView mentorEmailDisplay;

    private RelativeLayout changeStatusPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_view);
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
        course = received.getParcelableExtra("course");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Course View");
        actionBar.setDisplayHomeAsUpEnabled(true);

        titleDisplay = findViewById(R.id.title_display);
        titleDisplay.setText(course.getTitle());
        dateDisplay = findViewById(R.id.date_display);
        dateDisplay.setText(course.getDateRangeDisplay());
        mentorList = findViewById(R.id.mentor_list);
        updateMentorList();
        creditsDisplay = findViewById(R.id.credits_display);
        creditsDisplay.setText(course.getCreditsDisplay());
        statusDisplay = findViewById(R.id.status_display);
        statusDisplay.setText(course.getStatus().toString());
        statusDisplay.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                changeStatus(view);
                return false;
            }
        });
        statusCompleteIcon = findViewById(R.id.completed_icon);
        statusInProgressIcon = findViewById(R.id.in_progress_icon);
        statusPlanToTakeIcon = findViewById(R.id.plan_to_take_icon);
        statusDroppedIcon = findViewById(R.id.dropped_icon);
        updateIcon();

        RecyclerView.LayoutManager objectiveLayoutManager = new LinearLayoutManager(this);
        objectiveCardAdapter = new AssessmentCardAdapter(this, course, TYPE_OBJECTIVE);
        RecyclerView objectiveRecycler = findViewById(R.id.objective_recycler);
        objectiveRecycler.setLayoutManager(objectiveLayoutManager);
        objectiveRecycler.setAdapter(objectiveCardAdapter);

        RecyclerView.LayoutManager performanceLayoutManager = new LinearLayoutManager(this);
        performanceCardAdapter = new AssessmentCardAdapter(this, course, TYPE_PERFORMANCE);
        RecyclerView performanceRecycler = findViewById(R.id.performance_recycler);
        performanceRecycler.setLayoutManager(performanceLayoutManager);
        performanceRecycler.setAdapter(performanceCardAdapter);

        RecyclerView.LayoutManager noteLayoutManager = new LinearLayoutManager(this);
        noteCardAdapter = new NoteCardAdapter(this, course);
        RecyclerView noteRecycler = findViewById(R.id.note_recycler);
        noteRecycler.setLayoutManager(noteLayoutManager);
        noteRecycler.setAdapter(noteCardAdapter);

        viewMentorPopup = findViewById(R.id.view_mentor_popup);
        mentorNameDisplay = findViewById(R.id.mentor_name_display);
        mentorPhoneDisplay = findViewById(R.id.mentor_phone_display);
        mentorEmailDisplay = findViewById(R.id.mentor_email_display);

        changeStatusPopup = findViewById(R.id.change_status_popup);
    }

    public void updateInfo() {
        course = Course.findByID(course.getId());
        titleDisplay.setText(course.getTitle());
        dateDisplay.setText(course.getDateRangeDisplay());
        updateMentorList();
        creditsDisplay.setText(course.getCreditsDisplay());
        statusDisplay.setText(course.getStatus().toString());
        updateIcon();
    }
    public void updateIcon() {
        statusCompleteIcon.setVisibility(course.getStatus() == Course.Status.COMPLETE ? View.VISIBLE : View.GONE);
        statusInProgressIcon.setVisibility(course.getStatus() == Course.Status.IN_PROGRESS ? View.VISIBLE : View.GONE);
        statusPlanToTakeIcon.setVisibility(course.getStatus() == Course.Status.PLAN_TO_TAKE ? View.VISIBLE : View.GONE);
        statusDroppedIcon.setVisibility(course.getStatus() == Course.Status.DROPPED ? View.VISIBLE : View.GONE);
    }

    public void editCourse(View view) {
        Intent intent = new Intent(this, CourseEditActivity.class);
        intent.putExtra("course", course);
        startActivityForResult(intent, REQUEST_EDIT_COURSE);
    }

    private void updateMentorList() {
        mentorList.removeAllViewsInLayout();

        ArrayList<Mentor> mentors = course.getMentors();

        TextView label = new TextView(this);
        label.setText(mentors.size() == 1 ? R.string.mentor_label : R.string.mentors_label);
        mentorList.addView(label);

        for (int i = 0; i < mentors.size(); i++) {
            TextView delimiter = new TextView(this);
            delimiter.setText(i > 0 ? ", " : " ");
            mentorList.addView(delimiter);

            Mentor mentor = mentors.get(i);
            TextView mentorName = new TextView(this);
            mentorName.setText(mentor.getName());
            mentorName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mentorName = ((TextView) v).getText().toString();
                    Mentor mentor = Mentor.findByName(mentorName);
                    viewMentorPopup(mentor);
                }
            });
            mentorList.addView(mentorName);
        }
    }
    private void viewMentorPopup(Mentor mentor) {
        if (mentor != null) {
            viewMentorPopup.setVisibility(View.VISIBLE);
            mentorNameDisplay.setText(mentor.getName());
            mentorPhoneDisplay.setText(mentor.getPhoneDisplay());
            mentorEmailDisplay.setText(mentor.getEmailAddress());
        }
    }
    public void closeMentorView(View view) {
        viewMentorPopup.setVisibility(View.GONE);
    }

    public void createNewObjective(View view) {
        Intent intent = new Intent(this, AssessmentEditActivity.class);
        intent.putExtra("course", course);
        intent.putExtra("type", TYPE_OBJECTIVE);
        intent.putExtra("action", ACTION_CREATE);
        startActivityForResult(intent, REQUEST_CREATE_OBJECTIVE);
    }
    public void createNewPerformance(View view) {
        Intent intent = new Intent(this, AssessmentEditActivity.class);
        intent.putExtra("course", course);
        intent.putExtra("type", Assessment.TYPE_PERFORMANCE);
        intent.putExtra("action", ACTION_CREATE);
        startActivityForResult(intent, REQUEST_CREATE_PERFORMANCE);
    }
    public void editAssessment(Assessment assessment) {
        Intent intent = new Intent(this, AssessmentEditActivity.class);
        intent.putExtra("course", course);
        intent.putExtra("assessment", assessment);
        intent.putExtra("type", assessment.getType());
        intent.putExtra("action", ACTION_EDIT);
        startActivityForResult(intent, REQUEST_EDIT_OBJECTIVE);
    }

    public void createNewNote(View view) {
        Intent intent = new Intent(this, NoteEditActivity.class);
        intent.putExtra("course", course);
        startActivityForResult(intent, REQUEST_CREATE_NOTE);
    }
    public void editNote(Note note) {
        Intent intent = new Intent(this, NoteEditActivity.class);
        intent.putExtra("note", note);
        intent.putExtra("course", course);
        startActivityForResult(intent, REQUEST_EDIT_NOTE);
    }

    public void changeStatus(View view) {
        changeStatusPopup.setVisibility(View.VISIBLE);
    }
    public void confirmStatus(View view) {
        changeStatusPopup.setVisibility(View.GONE);

        Course.Status status = Course.Status.get(Integer.parseInt((String)view.getTag()));

        course.updateStatus(status);

        updateInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDIT_COURSE) {
            if (resultCode == RESULT_SAVED) {
                updateInfo();
            }
            if (resultCode == RESULT_DELETED) {
                setResult(RESULT_DELETED);
                finish();
            }
            if (resultCode == RESULT_DROPPED) {
                setResult(RESULT_DROPPED);
                finish();
            }
            if (resultCode == REQUEST_CREATE_OBJECTIVE || resultCode == REQUEST_CREATE_PERFORMANCE || resultCode == REQUEST_CREATE_NOTE) {
                updateInfo();
            }
        }

        if (requestCode == REQUEST_CREATE_OBJECTIVE || requestCode == REQUEST_EDIT_OBJECTIVE) {
            if (resultCode == RESULT_SAVED || resultCode == RESULT_DELETED) {
                objectiveCardAdapter.refresh();
            }
        }

        if (requestCode == REQUEST_CREATE_PERFORMANCE || requestCode == REQUEST_EDIT_PERFORMANCE) {
            if (resultCode == RESULT_SAVED || resultCode == RESULT_DELETED) {
                performanceCardAdapter.refresh();
            }
        }

        if (requestCode == REQUEST_CREATE_NOTE || requestCode == REQUEST_EDIT_NOTE) {
            if (resultCode == RESULT_SAVED || resultCode == RESULT_DELETED) {
                noteCardAdapter.refresh();
            }
        }
    }
    
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra("requestCode", requestCode);
        super.startActivityForResult(intent, requestCode);
    }
    
    public void clearAllOptioned() {
        objectiveCardAdapter.clearOptioned();
        performanceCardAdapter.clearOptioned();
        noteCardAdapter.clearOptioned();
    }
}
