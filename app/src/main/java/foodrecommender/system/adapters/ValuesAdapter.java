package foodrecommender.system.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import foodrecommender.system.R;
import foodrecommender.system.classes.Values;

public class ValuesAdapter extends RecyclerView.Adapter<ValuesAdapter.ValuesViewHolder> {
    private ArrayList<Values> values;
    private Context context;

    public ValuesAdapter(ArrayList<Values> values) {
        this.values = values;
    }

    @NonNull
    @Override
    public ValuesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.values_item, parent, false);
        return new ValuesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ValuesViewHolder holder, int position) {
        Values values1 = values.get(position);
        holder.valueTitle.setText(values1.getValuesTitle());
        holder.valueValue.setText(values1.getValuesValue());
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    static class ValuesViewHolder extends RecyclerView.ViewHolder{

        MaterialCardView materialCardView;
        TextView valueTitle, valueValue;

        ValuesViewHolder(@NonNull View itemView) {
            super(itemView);
            materialCardView = itemView.findViewById(R.id.values_material_cardview);
            valueTitle = itemView.findViewById(R.id.values_card_title);
            valueValue = itemView.findViewById(R.id.values_card_value);
        }
    }
}
