package foodrecommender.system.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import foodrecommender.system.R;
import foodrecommender.system.classes.RecommendedFoods;
import foodrecommender.system.fragments.LandingpageFragment;

public class RecommendedFoodsAdapater extends RecyclerView.Adapter<RecommendedFoodsAdapater.RecommendedFoodsViewHolder> {
    private ArrayList<RecommendedFoods> recommendedFoods;
    private Context context;
    private RecommendedFoodsAdapater.OnItemClickListener listener;

    public void setOnItemClickListener(RecommendedFoodsAdapater.OnItemClickListener listener) {
        this.listener = listener;
    }

    public RecommendedFoodsAdapater(ArrayList<RecommendedFoods> recommendedFoods, Context context) {
        this.recommendedFoods = recommendedFoods;
        this.context = context;
    }

    // Method to add new items to the dataset
    public void addItems(List<RecommendedFoods> newItems, int selectedPosition) {
        int insertPosition = selectedPosition + 1; // Position to insert the additional items
        recommendedFoods.addAll(insertPosition, newItems); // Add the new items to the dataset
        notifyItemRangeInserted(insertPosition, newItems.size()); // Notify the adapter about the new items

    }

    @NonNull
    @Override
    public RecommendedFoodsAdapater.RecommendedFoodsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommended_food_item, parent, false);
        return new RecommendedFoodsAdapater.RecommendedFoodsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendedFoodsAdapater.RecommendedFoodsViewHolder holder, int position) {
        RecommendedFoods recommendedFood = recommendedFoods.get(position);
        holder.shortDescTextView.setText(recommendedFood.getShortDesc());
        holder.foodGroupTextView.setText(recommendedFood.getFoodGroup());
        holder.energyKcalTextView.setText(recommendedFood.getEnergKcal());

        holder.cardView.setChecked(recommendedFood.isPart2());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isItemChecked = holder.cardView.isChecked();
                if (listener != null) {
                    listener.onItemClick(holder.foodGroupTextView.getText().toString(), holder.getAdapterPosition());

                    if (!isItemChecked) {
                        removeAddedItems(holder.getAdapterPosition());
                    }
                }
            }
        });
    }

    private void removeAddedItems(int position) {
        List<RecommendedFoods> removedItems = new ArrayList<>();
        int itemCount = recommendedFoods.size();
        for (int i = position + 1; i < itemCount; i++) {
            RecommendedFoods recommendedFood = recommendedFoods.get(i);
            if (recommendedFood.isPart2()) {
                removedItems.add(recommendedFood);
            }
        }

        recommendedFoods.removeAll(removedItems);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(String position, int position2);
    }

    @Override
    public int getItemCount() {
        return recommendedFoods.size();
    }

    static class RecommendedFoodsViewHolder extends RecyclerView.ViewHolder {
        TextView shortDescTextView, foodGroupTextView, energyKcalTextView;
        MaterialCardView cardView;
        ImageView imageView;

        RecommendedFoodsViewHolder(@NonNull View itemView) {
            super(itemView);
            shortDescTextView = itemView.findViewById(R.id.reco_food_name);
            foodGroupTextView = itemView.findViewById(R.id.reco_food_group);
            energyKcalTextView = itemView.findViewById(R.id.reco_food_calories);
            cardView = itemView.findViewById(R.id.materialRCardView);
            imageView = itemView.findViewById(R.id.reco_food_image);
        }
    }
}
