package com.example.myterms.tracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myterms.R;
import com.example.myterms.assessment.Assessment;

import java.util.ArrayList;

public class AssessmentListAdapter extends RecyclerView.Adapter<AssessmentListAdapter.ViewHolder> {
    private ArrayList<Assessment> assessments;
    private TrackerActivity activity;
    
    AssessmentListAdapter(TrackerActivity activity) {
        this.assessments = Assessment.findAllUpcoming();
        this.activity = activity;
        
        activity.upcomingAssessmentRecycler.setLayoutManager(new LinearLayoutManager(activity));
        activity.upcomingAssessmentRecycler.setAdapter(this);
        setVisibility();
    }
    
    public void refresh() {
        assessments.clear();
        assessments.addAll(Assessment.findAllUpcoming());
        setVisibility();
        
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_assessment_card, parent, false);
        return new ViewHolder(view, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Assessment assessment = assessments.get(position);
    
        holder.assessment = assessment;
    
        holder.nameDisplay.setText(assessment.getName());
        holder.dateDisplay.setText(assessment.getCompletionDate().getLongDateDisplay());
        holder.descriptionDisplay.setText(assessment.getDescription());
    }

    @Override
    public int getItemCount() {
        return assessments.size();
    }
    
    public void setVisibility() {
        if (assessments.isEmpty()) {
            activity.noUpcomingAssessmentMessage.setVisibility(View.VISIBLE);
            activity.upcomingAssessmentRecycler.setVisibility(View.GONE);
        } else {
            activity.noUpcomingAssessmentMessage.setVisibility(View.GONE);
            activity.upcomingAssessmentRecycler.setVisibility(View.VISIBLE);
        }
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        private Assessment assessment;
        
        private TextView nameDisplay;
        private TextView dateDisplay;
        private TextView descriptionDisplay;
        
        ViewHolder(@NonNull View itemView, final TrackerActivity activity) {
            super(itemView);
            
            ///   ROOT   ///
            itemView.setOnClickListener(view -> activity.viewAssessment(assessment));
            
            ///   NAME DISPLAY   ///
            nameDisplay = itemView.findViewById(R.id.name_display);
            
            ///   DATE DISPLAY   ///
            dateDisplay = itemView.findViewById(R.id.completion_date_display);
            
            ///   DESCRIPTION DISPLAY   ///
            descriptionDisplay = itemView.findViewById(R.id.description_display);
            
            ///   COMPLETED ICON   ///
            itemView.findViewById(R.id.icon_group).setVisibility(View.GONE);
        }
    }
}
