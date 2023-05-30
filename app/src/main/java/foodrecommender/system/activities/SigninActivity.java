package foodrecommender.system.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import foodrecommender.system.R;

public class SigninActivity extends AppCompatActivity {

    private TextInputEditText mEmailInput;
    private TextInputEditText mPasswordInput;
    private Button mSignInButton;
    private SharedPreferences sp;
    private RelativeLayout relativeLayout;
    private CircularProgressIndicator circularProgressIndicator;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        mEmailInput = findViewById(R.id.email_input);
        mPasswordInput = findViewById(R.id.password_input);
        mSignInButton = findViewById(R.id.sign_in_button);
        relativeLayout = findViewById(R.id.signin_RL);
        circularProgressIndicator = findViewById(R.id.signin_progress_indicator);

        sp = getSharedPreferences("user_data", MODE_PRIVATE);

        handleButtonClicks();
        checkForSavedData();
    }

    private void sendLoginRequest() {
        circularProgressIndicator.show();
        // Login example
        String loginUrl = "https://uxoricidal-image.000webhostapp.com/login.php";

        StringRequest loginRequest = new StringRequest(Request.Method.POST, loginUrl,
                response -> {
                    // Handle the login response
                    try {
                        if (snackbar != null && snackbar.isShown()) {
                            snackbar.dismiss();
                        }
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        String message = jsonResponse.getString("message");

                        if (success) {
                            // Login successful
                            // Handle success scenario (e.g., navigate to the home screen)
                            JSONObject userObject = jsonResponse.getJSONObject("user");

                            Log.d("TAG", "sendLoginRequest: "+ userObject);

                            SharedPreferences.Editor ed = sp.edit();
                            ed.putInt("id", userObject.getInt("id"));
                            ed.putString("name", userObject.getString("name"));
                            ed.putFloat("weight", Float.parseFloat(userObject.getString("weight")));
                            ed.putString("email", userObject.getString("email"));
                            ed.putString("username", userObject.getString("username"));
                            ed.putString("password", userObject.getString("password"));

                            ed.putInt("pregnancies", Integer.parseInt(userObject.getString("pregnancy_count")));
                            ed.putInt("glucose", Integer.parseInt(userObject.getString("glucose_count")));
                            ed.putInt("bloodPressure", Integer.parseInt(userObject.getString("diastolic_bp")));
                            ed.putInt("skinThickness", Integer.parseInt(userObject.getString("skin_thickness")));
                            ed.putInt("insulin", Integer.parseInt(userObject.getString("insulin_level")));
                            ed.putFloat("bmi", Float.parseFloat(userObject.getString("bmi")));
                            ed.putFloat("dpf", Float.parseFloat(userObject.getString("dpf")));
                            ed.putInt("age", Integer.parseInt(userObject.getString("age")));

                            ed.putInt("calorie_req", Integer.parseInt(userObject.getString("required_calories")));
                            ed.putString("fod_allergy", userObject.getString("food_allergies"));
                            ed.putString("nutrient_req", userObject.getString("required_nutrient"));
                            ed.putBoolean("is_logged_in", true);
                            ed.apply();

                            Log.d("TAG", "sendLoginRequest: "+ sp.getString("fod_allergy", ""));

                            calculateStatus();
                            showNext();
                        } else {
                            // Login failed
                            // Show an error message to the user
                            Snackbar.make(relativeLayout, message, Snackbar.LENGTH_SHORT).show();
                            circularProgressIndicator.hide();
                            circularProgressIndicator.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Handle the login error
                    error.printStackTrace();

                    snackbar = Snackbar.make(relativeLayout, "No connection", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Refresh", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sendLoginRequest();
                        }
                    });
                    snackbar.show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                // Set the parameters for the login request (username and password)
                Map<String, String> params = new HashMap<>();
                String email = mEmailInput.getText().toString(),
                        password = mPasswordInput.getText().toString();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        // Add the registration request to the request queue
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(loginRequest);
    }

    private void calculateStatus(){
        // String url = "http://192.168.0.41:5000/predict";
        String url = getString(R.string.predict_url);
        JSONObject data = new JSONObject();

        SharedPreferences sp = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        // Get input values from TextInputEditTexts
        int pregnancies = sp.getInt("pregnancies", 0);
        int glucose = sp.getInt("glucose", 0);
        int bloodPressure = sp.getInt("bloodPressure", 0);
        int skinThickness = sp.getInt("skinThickness", 0);
        int insulin = sp.getInt("insulin", 0);
        float bmi = sp.getFloat("bmi", 0.0f);
        float dpf = sp.getFloat("dpf", 0.0f);
        int age = sp.getInt("age", 0);

        try {
            data.put("pregnancies", pregnancies);
            data.put("glucose", glucose);
            data.put("blood_pressure", bloodPressure);
            data.put("skin_thickness", skinThickness);
            data.put("insulin", insulin);
            data.put("bmi", bmi);
            data.put("diabetes_pedigree_function", dpf);
            data.put("age", age);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data, response -> {
            try {
                String message = response.getString("diabetes_result");
                SharedPreferences.Editor ed = sp.edit();
                ed.putString("status", message);
                ed.apply();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            error.printStackTrace();
        });

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void checkForSavedData() {
        String email = sp.getString("email", ""),
                password = sp.getString("password", "");

        if (!email.isEmpty()) {
            mEmailInput.setText(email);
            mPasswordInput.setText(password);
        }
    }

    private void handleButtonClicks() {
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendLoginRequest();
            }
        });
    }

    private void showNext() {
        // Start the next activity
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("is_logged_in", true);
        ed.apply();
        Intent intent = new Intent(SigninActivity.this, LandingpageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        // Apply a smooth transition animation
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out_two);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}