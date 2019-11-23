package com.example.myterms.term;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myterms.R;
import com.example.myterms.dashboard.DashboardActivity;

import java.util.ArrayList;

public class TermCardAdapter extends RecyclerView.Adapter<TermCardAdapter.ViewHolder> {
    private ArrayList<Term> terms;
    private DashboardActivity activity;
    private boolean viewActive;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private Term term;

        private TextView titleDisplay;
        private TextView dateDisplay;
        private TextView creditsDisplay;
        private ImageView passedIcon;

        public ViewHolder(@NonNull View itemView, final DashboardActivity activity) {
            super(itemView);

            titleDisplay = itemView.findViewById(R.id.title_display);
            dateDisplay = itemView.findViewById(R.id.date_display);
            creditsDisplay = itemView.findViewById(R.id.credits_display);
            passedIcon = itemView.findViewById(R.id.completed_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.viewTerm(term);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    activity.editTerm(term);
                    return true;
                }
            });
        }
    }

    public TermCardAdapter(DashboardActivity activity, boolean viewActive) {
        this.terms = new ArrayList<>();
        this.activity = activity;
        setViewActive(viewActive);
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
        terms.addAll(!viewActive ? Term.findAll() : Term.findAllActive());
        notifyDataSetChanged();
    }

    public void setViewActive(boolean viewActive) {
        this.viewActive = viewActive;
        refresh();
    }
}
