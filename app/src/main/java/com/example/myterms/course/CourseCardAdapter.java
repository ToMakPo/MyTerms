package com.example.myterms.course;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myterms.R;
import com.example.myterms.term.Term;
import com.example.myterms.term.TermViewActivity;

import java.util.ArrayList;

import static com.example.myterms.course.Course.Status;
import static com.example.myterms.course.Course.Status.*;

public class CourseCardAdapter extends RecyclerView.Adapter<CourseCardAdapter.ViewHolder> {
    private Term term;
    private ArrayList<Course> courses;
    private TermViewActivity activity;
    private boolean viewActive;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private Course course;

        private TextView titleDisplay;
        private TextView dateDisplay;
        private TextView creditsDisplay;

        private ImageView statusCompleteIcon;
        private ImageView statusInProgressIcon;
        private ImageView statusPlanToTakeIcon;
        private ImageView statusDroppedIcon;

        public ViewHolder(@NonNull View itemView, final TermViewActivity activity) {
            super(itemView);

            titleDisplay = itemView.findViewById(R.id.title_display);
            dateDisplay = itemView.findViewById(R.id.date_display);
            creditsDisplay = itemView.findViewById(R.id.credits_display);

            statusCompleteIcon = itemView.findViewById(R.id.completed_icon);
            statusInProgressIcon = itemView.findViewById(R.id.in_progress_icon);
            statusPlanToTakeIcon = itemView.findViewById(R.id.plan_to_take_icon);
            statusDroppedIcon = itemView.findViewById(R.id.dropped_icon);

            itemView.setOnClickListener(view -> activity.viewCourse(course));

            itemView.setOnLongClickListener(view -> {
                activity.editCourse(course);
                return true;
            });
        }

        public void updateIcon() {
            Status status = course.getStatus();
            statusCompleteIcon.setVisibility(status == COMPLETED ? View.VISIBLE : View.GONE);
            statusInProgressIcon.setVisibility(status == IN_PROGRESS ? View.VISIBLE : View.GONE);
            statusPlanToTakeIcon.setVisibility(status == PLAN_TO_TAKE ? View.VISIBLE : View.GONE);
            statusDroppedIcon.setVisibility(status == DROPPED ? View.VISIBLE : View.GONE);
        }
    }

    public CourseCardAdapter(TermViewActivity activity, Term term) {
        this.term = term;
        this.activity = activity;
        this.viewActive = activity.viewActiveCheckbox.isChecked();
        this.courses = new ArrayList<>();
        refresh();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_course_card, parent, false);
        return new ViewHolder(view, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = courses.get(position);

        holder.course = course;

        holder.titleDisplay.setText(course.getTitle());
        holder.dateDisplay.setText(course.getDateRangeDisplay());
        holder.creditsDisplay.setText(course.getCreditsDisplay());
        holder.updateIcon();
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public void refresh() {
        courses.clear();
        courses.addAll(viewActive ? term.getActiveCourses() : term.getCourses());
        notifyDataSetChanged();
    }

    public void setViewActive(boolean viewActive) {
        this.viewActive = viewActive;
        refresh();
    }
}
