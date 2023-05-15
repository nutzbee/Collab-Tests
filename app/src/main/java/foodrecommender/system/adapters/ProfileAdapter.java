package foodrecommender.system.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(holder.profileTitle.getText().toString());

                // Create the EditText view
                final EditText editText = new EditText(context);
                editText.setPaddingRelative(64, 32, 64, 32);
                editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                editText.setSingleLine();
                editText.setText(profile.getProfileValue());
                builder.setView(editText);

                // Set the positive button
                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String updatedValue = editText.getText().toString();

                        // Update the UI with the new value
                        profile.setProfileValue(updatedValue);
                        holder.profileValue.setText(updatedValue);
                        notifyItemChanged(holder.getAdapterPosition());

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
                                        String column = jsonResponse.getString("column");
                                        String value = jsonResponse.getString("value");

                                        if (success) {
                                            // Login successful
                                            // Handle success scenario (e.g., navigate to the home screen)
                                            Log.d("TAG", "sendLoginRequest: SUCCESS");
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
                                                    ed.putString("age", value);
                                                    break;
                                                case "bmi":
                                                    ed.putString("bmi", value);
                                                    break;
                                            }
                                            ed.apply();
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
                                String newValue = editText.getText().toString();
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
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {
        TextView profileTitle, profileValue;
        TextInputLayout textInputLayout;
        TextInputEditText textInputEditText;
        ImageView imageView;

        ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            profileTitle = itemView.findViewById(R.id.profile_card_title);
            profileValue = itemView.findViewById(R.id.profile_card_value);
            textInputLayout = itemView.findViewById(R.id.profile_card_valueLayout);
            textInputEditText = itemView.findViewById(R.id.profile_card_value_edittext);
            imageView = itemView.findViewById(R.id.profile_card_icon);

        }
    }
}
