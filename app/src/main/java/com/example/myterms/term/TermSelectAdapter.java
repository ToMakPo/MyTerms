package com.example.myterms.term;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myterms.R;

import java.util.ArrayList;

public class TermSelectAdapter extends RecyclerView.Adapter<TermSelectAdapter.ViewHolder> {
    private ArrayList<Term> terms;
    private Term selectedTerm;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private Term term;
        private boolean selected;
        
        private View itemView;
        private TextView titleDisplay;
        private TextView dateDisplay;
        private TextView creditsDisplay;
        private ImageView passedIcon;

        ViewHolder(@NonNull final View itemView, final TermSelectAdapter adapter) {
            super(itemView);
            this.itemView = itemView;
    
            titleDisplay = itemView.findViewById(R.id.title_display);
            dateDisplay = itemView.findViewById(R.id.date_display);
            creditsDisplay = itemView.findViewById(R.id.credits_display);
            passedIcon = itemView.findViewById(R.id.completed_icon);

            itemView.setOnClickListener(view -> {
                if (!selected) {
                    adapter.selectedTerm = term;
                    adapter.refresh();
                }
            });
        }
    }
    
    TermSelectAdapter(Term selectedTerm) {
        this.terms = Term.findAll();
        this.selectedTerm = selectedTerm;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_term_card, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Term term = terms.get(position);

        holder.term = term;
        holder.selected = selectedTerm.getId() == term.getId();
        
        holder.itemView.setSelected(holder.selected);
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
        terms.addAll(Term.findAll());
        notifyDataSetChanged();
    }
    
    Term getSelectedTerm() {
        return selectedTerm;
    }
}
