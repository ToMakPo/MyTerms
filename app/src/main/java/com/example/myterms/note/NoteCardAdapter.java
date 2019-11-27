package com.example.myterms.note;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myterms.R;
import com.example.myterms.course.Course;
import com.example.myterms.course.CourseViewActivity;

import java.util.ArrayList;

public class NoteCardAdapter extends RecyclerView.Adapter<NoteCardAdapter.ViewHolder> {
    private Course course;
    private ArrayList<Note> notes;
    private CourseViewActivity activity;
    private ViewHolder optioned;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private Note note;
        private ViewHolder self;

        private TextView messageDisplay;
        private ImageButton optionsButton;
        private LinearLayout optionsBox;
        private RelativeLayout confirmDeleteMessageBox;
    
        ViewHolder(@NonNull View itemView, final CourseViewActivity activity, final NoteCardAdapter adapter) {
            super(itemView);
            
            ///   ROOT   ///
            itemView.setOnLongClickListener(view -> {
                activity.editNote(note);
                return true;
            });
            itemView.findViewById(R.id.root).setOnFocusChangeListener((view, hasFocus) -> {
                if (hasFocus) {
                    adapter.setOptioned(this);
                } else {
                    adapter.clearOptioned();
                }
            });
    
            ///   MESSAGE   ///
            messageDisplay = itemView.findViewById(R.id.message_display);
    
            ///   OPTIONS BOX   ///
            optionsBox = itemView.findViewById(R.id.options_group);
            optionsBox.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    optionsBox.setVisibility(View.GONE);
                    confirmDeleteMessageBox.setVisibility(View.GONE);
                }
            });
    
            ///   SHARE BUTTON   ///
            ImageButton emailButton = itemView.findViewById(R.id.share_button);
            emailButton.setOnClickListener(view -> {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain")
                        .putExtra(Intent.EXTRA_SUBJECT, "Note for course '" + note.getCourse().getTitle() + "'")
                        .putExtra(Intent.EXTRA_TEXT, note.getMessage());
                activity.startActivity(Intent.createChooser(emailIntent, "Share via:"));
            });
    
            ///   EDIT BUTTON   ///
            ImageButton editButton = itemView.findViewById(R.id.edit_button);
            editButton.setOnClickListener(view -> {
                activity.editNote(note);
                adapter.clearOptioned();
            });
    
            ///   DELETE BUTTON   ///
            ImageButton deleteButton = itemView.findViewById(R.id.delete_button);
            deleteButton.setOnClickListener(view -> confirmDeleteMessageBox.setVisibility(View.VISIBLE));
    
            ///   CONFIRM DELETE BUTTON   ///
            confirmDeleteMessageBox = itemView.findViewById(R.id.confirm_delete_message_popup);
            TextView confirmDeleteButton = itemView.findViewById(R.id.confirm_delete_button);
            confirmDeleteButton.setOnClickListener(view -> {
                note.delete();
                activity.clearAllOptioned();
                adapter.refresh();
            });
            
            ///   CANCEL DELETE BUTTON   ///
            TextView cancelDeleteButton = itemView.findViewById(R.id.cancel_delete_button);
            cancelDeleteButton.setOnClickListener(view -> confirmDeleteMessageBox.setVisibility(View.GONE));
        }
    }

    public NoteCardAdapter(CourseViewActivity activity, Course course) {
        this.course = course;
        this.notes = course.getNotes();
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_note_card, parent, false);
        return new ViewHolder(view, activity, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = notes.get(position);

        holder.note = note;
        holder.messageDisplay.setText(note.getMessage());
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
        return notes.size();
    }

    public void refresh() {
        notes.clear();
        notes.addAll(course.getNotes());
        notifyDataSetChanged();
    }
}
