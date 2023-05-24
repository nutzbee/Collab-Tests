package foodrecommender.system.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import foodrecommender.system.R;
import foodrecommender.system.classes.History;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private ArrayList<History> histories;
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public HistoryAdapter(ArrayList<History> histories) {
        this.histories = histories;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        History history = histories.get(position);
        holder.shortDescTextView.setText(history.getShortDesc());
        holder.kcalTextView.setText(history.getKcal());
        holder.foodGroupTextView.setText(history.getFoodGroup());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(holder.shortDescTextView.getText().toString());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return histories.size();
    }

    public interface OnItemClickListener {
        void onItemClick(String value);
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView shortDescTextView, foodGroupTextView;
        TextView kcalTextView;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            foodGroupTextView = itemView.findViewById(R.id.history_food_group);
            shortDescTextView = itemView.findViewById(R.id.history_food_name);
            kcalTextView = itemView.findViewById(R.id.history_food_calories);
        }
    }
}
