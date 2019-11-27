package com.example.myterms.assessment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myterms.R;
import com.example.myterms.course.Course;
import com.example.myterms.course.CourseViewActivity;

import java.util.ArrayList;

public class AssessmentCardAdapter extends RecyclerView.Adapter<AssessmentCardAdapter.ViewHolder> {
    
    private Course course;
    private ArrayList<Assessment> assessments;
    private CourseViewActivity activity;
    private int type;
    private ViewHolder optioned;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private Assessment assessment;

        private TextView nameDisplay;
        private TextView dateDisplay;
        private TextView descriptionDisplay;
        private ImageView completeIcon;
        private ImageView incompleteIcon;
        private LinearLayout optionsBox;
        private RelativeLayout confirmDeleteMessageBox;
    
        ViewHolder(@NonNull View itemView, final CourseViewActivity activity, final AssessmentCardAdapter adapter) {
            super(itemView);
    
            ///   ROOT   ///
            itemView.setOnLongClickListener(view -> {
                activity.editAssessment(assessment);
                return true;
            });
            itemView.findViewById(R.id.root).setOnFocusChangeListener((view, hasFocus) -> {
                if (hasFocus) {
                    adapter.setOptioned(this);
                } else {
                    adapter.clearOptioned();
                }
            });
    
            ///   NAME DISPLAY   ///
            nameDisplay = itemView.findViewById(R.id.name_display);
            
            ///   DATE DISPLAY   ///
            dateDisplay = itemView.findViewById(R.id.completion_date_display);
            
            ///   DESCRIPTION DISPLAY   ///
            descriptionDisplay = itemView.findViewById(R.id.description_display);
            
            ///   COMPLETED ICON   ///
            completeIcon = itemView.findViewById(R.id.completed_icon);
            completeIcon.setOnClickListener(v -> markAsIncomplete(activity));
            incompleteIcon = itemView.findViewById(R.id.incomplete_icon);
            incompleteIcon.setOnClickListener(v -> markAsComplete(activity));

            ///   OPTIONS BOX   ///
            optionsBox = itemView.findViewById(R.id.options_group);
            optionsBox.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    optionsBox.setVisibility(View.GONE);
                    confirmDeleteMessageBox.setVisibility(View.GONE);
                }
            });
    
            ///   EDIT BUTTON   ///
            ImageButton editButton = itemView.findViewById(R.id.edit_button);
            editButton.setOnClickListener(view -> {
                activity.editAssessment(assessment);
                adapter.clearOptioned();
            });
    
            ///   DELETE BUTTON   ///
            ImageButton deleteButton = itemView.findViewById(R.id.delete_button);
            deleteButton.setOnClickListener(view -> confirmDeleteMessageBox.setVisibility(View.VISIBLE));
    
            ///   CONFIRM DELETE BUTTON   ///
            confirmDeleteMessageBox = itemView.findViewById(R.id.confirm_delete_message_popup);
            TextView confirmDeleteButton = itemView.findViewById(R.id.confirm_delete_button);
            confirmDeleteButton.setOnClickListener(view -> {
                assessment.delete();
                activity.clearAllOptioned();
                adapter.refresh();
            });
    
            ///   CANCEL DELETE BUTTON   ///
            TextView cancelDeleteButton = itemView.findViewById(R.id.cancel_delete_button);
            cancelDeleteButton.setOnClickListener(view -> confirmDeleteMessageBox.setVisibility(View.GONE));
        }
    
        private void markAsIncomplete(final CourseViewActivity activity) {
            assessment.markAsIncomplete();
            completeIcon.setVisibility(View.GONE);
            incompleteIcon.setVisibility(View.VISIBLE);
            activity.updateInfo();
        }
        private void markAsComplete(final CourseViewActivity activity) {
            assessment.markAsComplete();
            completeIcon.setVisibility(View.VISIBLE);
            incompleteIcon.setVisibility(View.GONE);
            activity.updateInfo();
        }
    }
    
    public AssessmentCardAdapter(CourseViewActivity activity, Course course, int assessmentType) {
        this.course = course;
        this.type = assessmentType;
        this.assessments = course.getAssessments(type);
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_assessment_card, parent, false);
        return new ViewHolder(view, activity, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Assessment assessment = assessments.get(position);

        holder.assessment = assessment;
        boolean complete = assessment.isComplete();
    
        holder.nameDisplay.setText(assessment.getName());
        holder.dateDisplay.setText(assessment.getCompletionDate().getDateDisplay());
        holder.descriptionDisplay.setText(assessment.getDescription());
        holder.completeIcon.setVisibility(complete ? View.VISIBLE : View.GONE);
        holder.incompleteIcon.setVisibility(complete ? View.GONE : View.VISIBLE);
    }
    
    public void clearOptioned() {
        if (optioned != null) {
            optioned.optionsBox.setVisibility(View.GONE);
            optioned.confirmDeleteMessageBox.setVisibility(View.GONE);
            optioned = null;
        }
    }
    private void setOptioned(ViewHolder card) {
        activity.clearAllOptioned();
        
        optioned = card;
        optioned.optionsBox.setVisibility(View.VISIBLE);
        optioned.optionsBox.requestFocus();
    }

    @Override
    public int getItemCount() {
        return assessments.size();
    }

    public void refresh() {
        assessments.clear();
        assessments.addAll(course.getAssessments(type));
        notifyDataSetChanged();
    }
}
