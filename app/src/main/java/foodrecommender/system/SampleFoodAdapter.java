package foodrecommender.system;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SampleFoodAdapter extends RecyclerView.Adapter<SampleFoodAdapter.SampleFoodViewHolder> {
    private ArrayList<SampleFood> sampleFoods;
    private int maxItemsToShow = 3;
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public SampleFoodAdapter(ArrayList<SampleFood> sampleFoods) {
        this.sampleFoods = sampleFoods;
    }

    @NonNull
    @Override
    public SampleFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
        return new SampleFoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SampleFoodViewHolder holder, int position) {
        SampleFood sampleFood = sampleFoods.get(position);
        holder.shortDescTextView.setText(sampleFood.getShortDesc());
        holder.kcalTextView.setText(sampleFood.getKcal());
        holder.foodGroupTextView.setText(sampleFood.getFoodGroup());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return Math.min(maxItemsToShow, sampleFoods.size());
    }

    public void setMaxItemsToShow(int maxItemsToShow) {
        this.maxItemsToShow = maxItemsToShow;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public SampleFood getSampleFood(int position) {
        return sampleFoods.get(position);
    }

    static class SampleFoodViewHolder extends RecyclerView.ViewHolder {
        TextView shortDescTextView, foodGroupTextView;
        TextView kcalTextView;

        SampleFoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodGroupTextView = itemView.findViewById(R.id.food_group);
            shortDescTextView = itemView.findViewById(R.id.food_name);
            kcalTextView = itemView.findViewById(R.id.food_calories);
        }
    }
}
