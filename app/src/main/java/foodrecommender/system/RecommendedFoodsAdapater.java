package foodrecommender.system;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class RecommendedFoodsAdapater extends RecyclerView.Adapter<foodrecommender.system.RecommendedFoodsAdapater.RecommendedFoodsViewHolder> {
    private ArrayList<RecommendedFoods> recommendedFoods;
    private Context context;

    public RecommendedFoodsAdapater(ArrayList<RecommendedFoods> recommendedFoods, Context context) {
        this.recommendedFoods = recommendedFoods;
        this.context = context;
    }

    @NonNull
    @Override
    public foodrecommender.system.RecommendedFoodsAdapater.RecommendedFoodsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommended_food_item, parent, false);
        return new foodrecommender.system.RecommendedFoodsAdapater.RecommendedFoodsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull foodrecommender.system.RecommendedFoodsAdapater.RecommendedFoodsViewHolder holder, int position) {
        RecommendedFoods recommendedFood = recommendedFoods.get(position);
        holder.shortDescTextView.setText(recommendedFood.getShortDesc());
        holder.foodGroupTextView.setText(recommendedFood.getFoodGroup());
        holder.energyKcalTextView.setText(String.valueOf(recommendedFood.getEnergKcal()));

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                holder.cardView.setChecked(!holder.cardView.isChecked());
                return true;
            }
        });
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
