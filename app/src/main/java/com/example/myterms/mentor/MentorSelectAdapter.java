package com.example.myterms.mentor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myterms.R;

import java.util.ArrayList;

public class MentorSelectAdapter extends RecyclerView.Adapter<MentorSelectAdapter.ViewHolder> {
    private ArrayList<Mentor> mentors;
    private ArrayList<Mentor> selectedMentors;
    private MentorSelectActivity activity;
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private Mentor mentor;
        
        private CheckBox nameCheckbox;
        private boolean setting_up;

        public ViewHolder(@NonNull View itemView, final MentorSelectAdapter adapter) {
            super(itemView);
    
            nameCheckbox = itemView.findViewById(R.id.name_display);

            nameCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!setting_up) {
                        if (isChecked) {
                            adapter.addMentorToSelected(mentor);
                        } else {
                            adapter.removeMentorFromSelected(mentor);
                        }
                    }
                }
            });
        }
    
        public Mentor getMentor() {
            return mentor;
        }
    }
    
    public MentorSelectAdapter(MentorSelectActivity activity, long[] selectedMentors) {
        this.mentors = Mentor.findAll();
        this.selectedMentors = Mentor.findAllByIDs(selectedMentors);
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_mentor_card, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.setting_up = true;
        final Mentor mentor = this.mentors.get(position);
        
        holder.mentor = mentor;

        holder.nameCheckbox.setText(mentor.getName());
        holder.nameCheckbox.setChecked(selectedMentors.contains(mentor));
        holder.setting_up = false;
        holder.nameCheckbox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                activity.editMentor(mentor);
                return false;
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return mentors.size();
    }

    public void refresh() {
        mentors.clear();
        mentors.addAll(Mentor.findAll());
        notifyDataSetChanged();
    }
    
    public long[] getSelectedMentors() {
        long[] passedIDs = new long[selectedMentors.size()];
    
        for (int i = 0; i < selectedMentors.size(); i++) {
            passedIDs[i] = selectedMentors.get(i).getId();
        }
        
        return passedIDs;
    }
    
    public void addMentorToSelected(Mentor mentor) {
        if (!selectedMentors.contains(mentor))
            selectedMentors.add(mentor);
    }
    
    public void removeMentorFromSelected(Mentor mentor) {
        for (Mentor selectedMentor : selectedMentors) {
            if (mentor.equals(selectedMentor)) {
                selectedMentors.remove(selectedMentor);
                break;
            }
        }
    }
}
