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
        private ViewHolder self;

        private TextView nameDisplay;
        private TextView dateDisplay;
        private TextView descriptionDisplay;
        private ImageView completeIcon;
        private ImageView notCompleteIcon;
        private ImageButton optionsButton;
        private LinearLayout optionsBox;
        private RelativeLayout confirmDeleteMessageBox;
    
        ViewHolder(@NonNull View itemView, final CourseViewActivity activity, final AssessmentCardAdapter adapter) {
            super(itemView);
            self = this;
    
            ///   ROOT   ///
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    activity.editAssessment(assessment);
                    return true;
                }
            });
            itemView.findViewById(R.id.root).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        optionsButton.setVisibility(View.VISIBLE);
                    } else {
                        optionsButton.setVisibility(View.GONE);
                    }
                }
            });
    
            ///   NAME DISPLAY   ///
            nameDisplay = itemView.findViewById(R.id.name_display);
//            nameDisplay.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (assessment.isComplete())
//                        markAsNotComplete(activity);
//                    else
//                        markAsComplete(activity);
//                }
//            });
            
            ///   DATE DISPLAY   ///
            dateDisplay = itemView.findViewById(R.id.completion_date_display);
            descriptionDisplay = itemView.findViewById(R.id.description_display);
            
            ///   COMPLETE ICON   ///
            completeIcon = itemView.findViewById(R.id.completed_icon);
            completeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    markAsNotComplete(activity);
                }
            });
            notCompleteIcon = itemView.findViewById(R.id.not_complete_icon);
            notCompleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    markAsComplete(activity);
                }
            });

            ///   OPTIONS BOX   ///
            optionsBox = itemView.findViewById(R.id.options_box);
            optionsBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        optionsBox.setVisibility(View.GONE);
                        confirmDeleteMessageBox.setVisibility(View.GONE);
                    }
                }
            });
            
            ///   OPTIONS BUTTON   ///
            optionsButton = itemView.findViewById(R.id.options_button);
            optionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.setOptioned(self);
                }
            });
    
            ///   EDIT BUTTON   ///
            ImageButton editButton = itemView.findViewById(R.id.edit_button);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.editAssessment(assessment);
                    adapter.clearOptioned();
                }
            });
    
            ///   DELETE BUTTON   ///
            ImageButton deleteButton = itemView.findViewById(R.id.delete_button);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmDeleteMessageBox.setVisibility(View.VISIBLE);
                }
            });
    
            ///   CONFIRM DELETE BUTTON   ///
            confirmDeleteMessageBox = itemView.findViewById(R.id.confirm_delete_message_box);
            TextView confirmDeleteButton = itemView.findViewById(R.id.confirm_delete_button);
            confirmDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    assessment.delete();
                    activity.clearAllOptioned();
                    adapter.refresh();
                }
            });
    
            ///   CANCEL DELETE BUTTON   ///
            TextView cancelDeleteButton = itemView.findViewById(R.id.cancel_delete_button);
            cancelDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmDeleteMessageBox.setVisibility(View.GONE);
                }
            });
        }
    
        private void markAsNotComplete(final CourseViewActivity activity) {
            assessment.markAsNotComplete();
            completeIcon.setVisibility(View.GONE);
            notCompleteIcon.setVisibility(View.VISIBLE);
            activity.updateInfo();
        }
        private void markAsComplete(final CourseViewActivity activity) {
            assessment.markAsComplete();
            completeIcon.setVisibility(View.VISIBLE);
            notCompleteIcon.setVisibility(View.GONE);
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
        holder.notCompleteIcon.setVisibility(complete ? View.GONE : View.VISIBLE);
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
        optioned.optionsButton.setVisibility(View.GONE);
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
