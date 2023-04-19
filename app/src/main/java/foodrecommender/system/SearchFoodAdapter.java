package foodrecommender.system;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchFoodAdapter extends RecyclerView.Adapter<SearchFoodAdapter.SearchFoodViewHolder> {
    private ArrayList<SearchFood> searchFoods;

    public SearchFoodAdapter(ArrayList<SearchFood> searchFoods) {
        this.searchFoods = searchFoods;
    }

    @NonNull
    @Override
    public SearchFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_food_item, parent, false);
        return new SearchFoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchFoodViewHolder holder, int position) {
        SearchFood searchFood = searchFoods.get(position);
        holder.searchCardName.setText(searchFood.getSShortDesc());
        holder.searchCardCalories.setText(String.valueOf(searchFood.getSKcal()));
        holder.foodGroupTextView.setText(searchFood.getFoodGroup());
    }

    @Override
    public int getItemCount() {
        return searchFoods.size();
    }

    static class SearchFoodViewHolder extends RecyclerView.ViewHolder {
        TextView searchCardName, foodGroupTextView;
        TextView searchCardCalories;

        SearchFoodViewHolder(@NonNull View itemView) {
            super(itemView);
            searchCardName = itemView.findViewById(R.id.search_card_title);
            foodGroupTextView = itemView.findViewById(R.id.search_card_group);
            searchCardCalories = itemView.findViewById(R.id.search_card_value);
        }
    }
}
