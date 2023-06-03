package foodrecommender.system.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import foodrecommender.system.R;
import foodrecommender.system.classes.Profile;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {
    private ArrayList<Profile> profiles;
    private Context context;
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public ProfileAdapter(Context context, ArrayList<Profile> profiles) {
        this.profiles = profiles;
        this.context = context;
    }

    @NonNull
    @Override
    public ProfileAdapter.ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item, parent, false);
        return new ProfileAdapter.ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ProfileViewHolder holder, int position) {
        Profile profile = profiles.get(position);
        holder.profileTitle.setText(profile.getProfileTitle());
        holder.profileValue.setText(profile.getProfileValue());
        if (holder.profileTitle.getText().toString().equals("Status")) {
            holder.imageView.setVisibility(View.GONE);
            holder.materialCardView.setClickable(false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(holder.profileTitle.getText().toString(),
                            holder.profileValue.getText().toString(),
                            holder.getAdapterPosition());
                }
            }
        });
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show the dialog
                LayoutInflater inflater = LayoutInflater.from(context);
                View alertDialogView = inflater.inflate(R.layout.profile_dialog_layout, null);
                TextInputEditText inputEditText = alertDialogView.findViewById(R.id.editText);
                TextInputLayout inputLayout = alertDialogView.findViewById(R.id.editTextLayout);

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

                String profileTitle = holder.profileTitle.getText().toString();
                String profileValue = profile.getProfileValue();

                builder.setTitle(profileTitle);
                builder.setMessage("Current: " + profileValue);
                builder.setCancelable(false);
                builder.setView(alertDialogView);

                inputEditText.setText(profileValue);
                if (profileTitle.equals("Age") || profileTitle.equals("Body Mass Index")
                        || profileTitle.equals("Weight")) {
                    inputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else if (profileTitle.equals("Email")){
                    inputEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                }
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
                                            }
                                            ed.apply();

                                            // Update the UI with the new value
                                            profile.setProfileValue(updatedValue);
                                            holder.profileValue.setText(updatedValue);
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
                                params.put("column_name", holder.profileTitle.getText().toString());
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

    public interface OnItemClickListener {
        void onItemClick(String title, String value, int position);
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {
        TextView profileTitle, profileValue;
        ImageView imageView;
        MaterialCardView materialCardView;

        ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            profileTitle = itemView.findViewById(R.id.profile_card_title);
            profileValue = itemView.findViewById(R.id.profile_card_value);
            imageView = itemView.findViewById(R.id.profile_card_icon);
            materialCardView = itemView.findViewById(R.id.profile_material_cardview);

        }
    }
}
