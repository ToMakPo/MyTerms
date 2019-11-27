package com.example.myterms.tracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myterms.R;
import com.example.myterms.term.Term;

import java.util.ArrayList;

public class TermListAdapter extends RecyclerView.Adapter<TermListAdapter.ViewHolder> {
    private ArrayList<Term> terms;
    private ProgressTrackerActivity activity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private Term term;

        private TextView titleDisplay;
        private TextView dateDisplay;
        private TextView creditsDisplay;
        private ImageView passedIcon;

        ViewHolder(@NonNull View itemView, final ProgressTrackerActivity activity) {
            super(itemView);

            titleDisplay = itemView.findViewById(R.id.title_display);
            dateDisplay = itemView.findViewById(R.id.date_display);
            creditsDisplay = itemView.findViewById(R.id.credits_display);
            passedIcon = itemView.findViewById(R.id.completed_icon);

            itemView.setOnClickListener(view -> activity.viewTerm(term));
        }
    }

    TermListAdapter(ProgressTrackerActivity activity) {
        this.terms = Term.findAllUpcoming();
        this.activity = activity;
        
        activity.upcomingTermRecycler.setAdapter(this);
        
        if (terms.isEmpty()) {
            activity.upcomingTermLabel.setVisibility(View.GONE);
            activity.upcomingTermRecycler.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_term_card, parent, false);
        return new ViewHolder(view, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Term term = terms.get(position);

        holder.term = term;

        holder.titleDisplay.setText(term.getTitle());
        holder.dateDisplay.setText(term.getDateRangeDisplay());
        holder.creditsDisplay.setText(term.getCreditsDisplay());
        holder.passedIcon.setVisibility(term.hasPassed() ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return terms.size();
    }

    public void refresh() {
        terms.clear();
        terms.addAll(Term.findAllUpcoming());
        
        if (terms.isEmpty()) {
            activity.upcomingTermLabel.setVisibility(View.GONE);
            activity.upcomingTermRecycler.setVisibility(View.GONE);
        } else {
            activity.upcomingTermLabel.setVisibility(View.VISIBLE);
            activity.upcomingTermRecycler.setVisibility(View.VISIBLE);
        }
        
        notifyDataSetChanged();
    }
}
