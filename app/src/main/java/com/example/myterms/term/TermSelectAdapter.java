package com.example.myterms.term;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myterms.R;

import java.util.ArrayList;

public class TermSelectAdapter extends RecyclerView.Adapter<TermSelectAdapter.ViewHolder> {
    private ArrayList<Term> terms;
    private Term selectedTerm;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TermSelectAdapter adapter;
        private Term term;
        private boolean selected;
        
//        private ImageView selectedBG;
        private View itemView;
        private CheckBox selectedState;
        private TextView titleDisplay;
        private TextView dateDisplay;
        private TextView creditsDisplay;
        private ImageView passedIcon;

        public ViewHolder(@NonNull final View itemView, final TermSelectAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            this.itemView = itemView;
    
            selectedState = itemView.findViewById(R.id.selected_state);
            titleDisplay = itemView.findViewById(R.id.title_display);
            dateDisplay = itemView.findViewById(R.id.date_display);
            creditsDisplay = itemView.findViewById(R.id.credits_display);
            passedIcon = itemView.findViewById(R.id.completed_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!selected) {
                        adapter.selectedTerm = term;
                        adapter.refresh();
                    }
                }
            });
        }
    }
    
    public TermSelectAdapter(Term selectedTerm) {
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
        
        holder.selectedState.setChecked(holder.selected);
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
    
    public Term getSelectedTerm() {
        return selectedTerm;
    }
}
