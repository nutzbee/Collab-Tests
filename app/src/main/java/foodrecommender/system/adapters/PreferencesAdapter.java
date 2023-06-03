package foodrecommender.system.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.InputType;
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
import foodrecommender.system.classes.Preferences;
import foodrecommender.system.classes.Profile;

public class PreferencesAdapter extends RecyclerView.Adapter<PreferencesAdapter.PreferencesViewHolder> {
    private ArrayList<Preferences> preferences;
    private Context context;

    public PreferencesAdapter(Context context, ArrayList<Preferences> preferences) {
        this.preferences = preferences;
        this.context = context;
    }

    @NonNull
    @Override
    public PreferencesAdapter.PreferencesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.preferences, parent, false);
        return new PreferencesAdapter.PreferencesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferencesAdapter.PreferencesViewHolder holder, int position) {
        Preferences preferences1 = preferences.get(position);
        holder.preferenceTitle.setText(preferences1.getPreferencesTitle());
        holder.preferenceValue.setText(preferences1.getPreferencesValue());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show the dialog
                LayoutInflater inflater = LayoutInflater.from(context);
                View alertDialogView = inflater.inflate(R.layout.profile_dialog_layout, null);
                TextInputEditText inputEditText = alertDialogView.findViewById(R.id.editText);
                TextInputLayout inputLayout = alertDialogView.findViewById(R.id.editTextLayout);

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

                String profileTitle = holder.preferenceTitle.getText().toString();
                String profileValue = preferences1.getPreferencesValue();

                builder.setTitle(profileTitle);
                builder.setMessage("Current: " + profileValue);
                builder.setCancelable(false);
                builder.setView(alertDialogView);

                inputEditText.setText(profileValue);
                if (!(profileTitle.equals("Allergies")
                        || profileTitle.equals("Nutrient requirement"))) inputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                inputLayout.setHint("Enter new value");

                // Set the positive button
                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String updatedValue = inputEditText.getText().toString();

                        // Send Volley request to update.php with the updated value
                        // Use Volley library or your preferred method to make the HTTP request
                        String loginUrl = "https://uxoricidal-image.000webhostapp.com/update.php";

                        StringRequest loginRequest = new StringRequest(Request.Method.POST, loginUrl,
                                response -> {
                                    // Handle the login response
                                    try {
                                        JSONObject jsonResponse = new JSONObject(response);
                                        boolean success = jsonResponse.getBoolean("success");
                                        String message = jsonResponse.getString("message");

                                        if (success) {
                                            // Login successful
                                            // Handle success scenario (e.g., navigate to the home screen)
                                            Log.d("TAG", "sendLoginRequest: SUCCESS");
                                            String column = jsonResponse.getString("column");
                                            String value = jsonResponse.getString("value");
                                            SharedPreferences sp = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor ed = sp.edit();
                                            switch (column) {
                                                case "name":
                                                    ed.putString("name", value);
                                                    break;
                                                case "email":
                                                    ed.putString("email", value);
                                                    break;
                                                case "username":
                                                    ed.putString("username", value);
                                                    break;
                                                case "password":
                                                    ed.putString("password", value);
                                                    break;
                                                case "weight":
                                                    ed.putFloat("weight", Float.parseFloat(value));
                                                    break;
                                                case "age":
                                                    ed.putInt("age", Integer.parseInt(value));
                                                    break;
                                                case "bmi":
                                                    ed.putFloat("bmi", Float.parseFloat(value));
                                                    break;
                                                case "required_calories":
                                                    ed.putInt("calorie_req", Integer.parseInt(value));
                                                    break;
                                                case "dpf":
                                                    ed.putFloat("dpf", Float.parseFloat(value));
                                                    break;
                                                case "food_allergies":
                                                    ed.putString("fod_allergy", value);
                                                    break;
                                                case "glucose_count":
                                                    ed.putInt("glucose", Integer.parseInt(value));
                                                    break;
                                                case "insulin_level":
                                                    ed.putInt("insulin", Integer.parseInt(value));
                                                    break;
                                                case "pregnancy_count":
                                                    ed.putInt("pregnancies", Integer.parseInt(value));
                                                    break;
                                                case "skin_thickness":
                                                    ed.putInt("skinThickness", Integer.parseInt(value));
                                                    break;
                                                case "diastolic_bp":
                                                    ed.putInt("bloodPressure", Integer.parseInt(value));
                                                    break;
                                                case "required_nutrient":
                                                    ed.putString("nutrient_req", value);
                                                    break;
                                            }
                                            ed.apply();

                                            // Update the UI with the new value
                                            preferences1.setPreferencesValue(updatedValue);
                                            holder.preferenceValue.setText(updatedValue);
                                            notifyItemChanged(holder.getAdapterPosition());
                                        } else {
                                            // Login failed
                                            // Show an error message to the user
                                            Log.d("TAG", "sendLoginRequest: FAILED " + message);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                },
                                error -> {
                                    // Handle the login error
                                    error.printStackTrace();
                                }
                        ) {
                            @Override
                            protected Map<String, String> getParams() {
                                // Set the parameters for the login request (username and password)
                                Map<String, String> params = new HashMap<>();
                                SharedPreferences sp = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
                                String newValue = inputEditText.getText().toString();
                                params.put("column_name", holder.preferenceTitle.getText().toString());
                                params.put("new_data", newValue);
                                params.put("user_id", String.valueOf(sp.getInt("id", 0)));
                                return params;
                            }
                        };

                        // Add the registration request to the request queue
                        RequestQueue queue = Volley.newRequestQueue(context);
                        queue.add(loginRequest);
                    }
                });

                // Set the negative button
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                // Show the dialog
                builder.create();
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return preferences.size();
    }

    static class PreferencesViewHolder extends RecyclerView.ViewHolder{

        MaterialCardView materialCardView;
        TextView preferenceTitle, preferenceValue;

        PreferencesViewHolder(@NonNull View itemView) {
            super(itemView);
            materialCardView = itemView.findViewById(R.id.preferences_material_cardview);
            preferenceTitle = itemView.findViewById(R.id.preferences_card_title);
            preferenceValue = itemView.findViewById(R.id.preferences_card_value);
        }
    }
}
