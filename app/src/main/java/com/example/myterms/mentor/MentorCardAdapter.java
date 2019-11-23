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

public class MentorCardAdapter extends RecyclerView.Adapter<MentorCardAdapter.ViewHolder> {
    private final ArrayList<Mentor> mentors;
    private final ArrayList<Mentor> selectedMentors;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private Mentor mentor;

        private CheckBox nameDisplay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameDisplay = itemView.findViewById(R.id.name_display);
        }
    }

    public MentorCardAdapter(ArrayList<Mentor> selectedMentors) {
        this.mentors = Mentor.findAll();
        this.selectedMentors = selectedMentors;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_mentor_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Mentor mentor = mentors.get(position);

        holder.mentor = mentor;

        holder.nameDisplay.setText(mentor.getName());
        holder.nameDisplay.setChecked(selectedMentors.contains(mentor));
        holder.nameDisplay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedMentors.add(mentor);
                } else {
                    selectedMentors.remove(mentor);
                }
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
}
