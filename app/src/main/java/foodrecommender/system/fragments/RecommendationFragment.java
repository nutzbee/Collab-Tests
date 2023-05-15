package foodrecommender.system.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import foodrecommender.system.activities.SignupActivity;
import foodrecommender.system.classes.DatabaseHelper;
import foodrecommender.system.activities.LandingpageActivity;
import foodrecommender.system.R;

public class RecommendationFragment extends Fragment {

    private View view;
    private TextInputEditText calorie_req, fod_allergy, nutrient_req;
    private TextInputLayout calorie_layout, fod_allergy_layout, nutrient_layout;
    private String message;
    private LinearProgressIndicator progressIndicator;
    private TextInputLayout[] textInputLayouts;
    private TextInputEditText[] textInputEditTexts;
    private int currentEditTextIndex = 0;
    private Button submitReco;
    private DatabaseHelper databaseHelper;
    private Snackbar snackbar;
    private String actionBarTitle = "Food Preferences";
    private String name, email, username, password, foodAllergy, requiredNutrient;
    private int pregnancyCount, glucoseCount, bloodPressure, skinThickness, insulin, age, totalCalories;
    private float bmi, diabetesPedigree, weight;
    private SharedPreferences sp;
    private SharedPreferences.Editor ed;

    public RecommendationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_recommendation, container, false);

        ((SignupActivity) requireActivity()).updateActionBarTitle(actionBarTitle);

        // Define database
        databaseHelper = new DatabaseHelper(requireContext());

        // TextInputEditTexts
        calorie_req = view.findViewById(R.id.calorie_requirement_edit_text);
        fod_allergy = view.findViewById(R.id.food_allergies_edit_text);
        nutrient_req = view.findViewById(R.id.required_nutrient_edit_text);

        // TextInputLayouts
        calorie_layout = view.findViewById(R.id.calorie_requirement_text_input);
        fod_allergy_layout = view.findViewById(R.id.food_allergies_text_input);
        nutrient_layout = view.findViewById(R.id.required_nutrient_text_input);

        // Button
        submitReco = view.findViewById(R.id.submitReco);
        progressIndicator = requireActivity().findViewById(R.id.progress_indicator);

        handleProgressIndicator();
        setOnClick();
        addEditTextsActions();

        return view;
    }

    private void handleProgressIndicator(){
        progressIndicator.setMax(11);
    }
    private void setOnClick(){
        submitReco.setEnabled(false);
        submitReco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                personalizeContentsNow();
            }
        });
    }

    private void addEditTextsActions(){
        textInputLayouts = new TextInputLayout[] {
                calorie_layout,
                fod_allergy_layout,
                nutrient_layout
        };

        textInputEditTexts = new TextInputEditText[] {
                calorie_req,
                fod_allergy,
                nutrient_req
        };

        for (int i = 0; i < textInputLayouts.length; i++) {
            final int currentIndex = i;
            final int nextIndex = i + 1;
            final int progressIndex = nextIndex + 8;
            if (nextIndex < textInputLayouts.length) {
                textInputEditTexts[currentIndex].setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                            if (!textInputEditTexts[currentIndex].getText().toString().isEmpty()) {
                                textInputLayouts[currentIndex].setVisibility(View.GONE);
                                textInputLayouts[nextIndex].setVisibility(View.VISIBLE);
                                textInputEditTexts[nextIndex].requestFocus();
                                progressIndicator.setProgress(progressIndex);
                                currentEditTextIndex = nextIndex;
                            } else {
                                message = "Do not leave empty fields";
                                snackBar();
                            }
                            if (currentEditTextIndex == 2){
                                textInputEditTexts[nextIndex].addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                    }

                                    @Override
                                    public void afterTextChanged(Editable editable) {
                                        if (editable.length() > 3) {
                                            if (!submitReco.isEnabled()) {
                                                submitReco.setEnabled(true);
                                            }
                                        } else {
                                            if (submitReco.isEnabled()){
                                                submitReco.setEnabled(false);
                                            }
                                            message = "Required";
                                            if (nutrient_req.getError() != message) {
                                                nutrient_req.setError(message);
                                            }
                                        }
                                    }
                                });
                            }
                            return true;
                        }
                        return false;
                    }
                });
            }
        }
    }

    private void snackBar(){
        int duration = Snackbar.LENGTH_SHORT;
        snackbar = Snackbar.make(view, message, duration);

        snackbar.setText(message);
        snackbar.show();
    }

    private void personalizeContentsNow(){
        if (isAdded()) {
            if (calorie_req.getText().toString().isEmpty() ||
                    fod_allergy.getText().toString().isEmpty() ||
                    nutrient_req.getText().toString().isEmpty()) {
                message = "Do not leave empty fields";
                snackBar();
            } else {
                sp = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
                ed = sp.edit();
                ed.putInt("calorie_req", Integer.parseInt(calorie_req.getText().toString()));
                ed.putString("fod_allergy", fod_allergy.getText().toString());
                ed.putString("nutrient_req", nutrient_req.getText().toString());
                ed.putBoolean("is_logged_in", true);
                ed.apply();

                name = sp.getString("name", "");
                weight = sp.getFloat("weight", 0.0f);
                email = sp.getString("email", "");
                username = sp.getString("username", "");
                password = sp.getString("password", "");
                pregnancyCount = sp.getInt("pregnancies", 0);
                glucoseCount = sp.getInt("glucose", 0);
                bloodPressure = sp.getInt("bloodPressure", 0);
                skinThickness = sp.getInt("skinThickness", 0);
                insulin = sp.getInt("insulin", 0);
                bmi = sp.getFloat("bmi", 0.0f);
                diabetesPedigree = sp.getFloat("dpf", 0.0f);
                age = sp.getInt("age", 0);
                totalCalories = sp.getInt("calorie_req", 0);
                foodAllergy = sp.getString("fod_allergy", "");
                requiredNutrient = sp.getString("nutrient_req", "");

                sendRegistrationRequest();
            }
        }
    }

    private void sendRegistrationRequest(){
        if (isAdded()) {
            // Registration example
            String registerUrl = "https://uxoricidal-image.000webhostapp.com/register.php";

            StringRequest registerRequest = new StringRequest(Request.Method.POST, registerUrl,
                    response -> {
                        // Handle the registration response
                        try {
                            progressIndicator.setIndeterminate(true);
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            message = jsonResponse.getString("message");
                            int id = jsonResponse.getInt("user_id");

                            snackBar();
                            if (success) {
                                // Registration successful
                                // Handle success scenario (e.g., navigate to the login screen)
                                ed.putInt("id", id);
                                ed.apply();
                                progressIndicator.hide();
                                showNext();
                            } else {
                                // Registration failed
                                // Show an error message to the user
                                progressIndicator.setIndeterminate(false);
                                progressIndicator.setProgress(progressIndicator.getProgress());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        // Handle the registration error
                        error.printStackTrace();
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    // Set the parameters for the registration request (username and password)
                    Map<String, String> params = new HashMap<>();
                    params.put("name", name);
                    params.put("weight", String.valueOf(weight));
                    params.put("email", email);
                    params.put("username", username);
                    params.put("password", password);
                    params.put("pregnancy_count", String.valueOf(pregnancyCount));
                    params.put("glucose_count", String.valueOf(glucoseCount));
                    params.put("diastolic_bp", String.valueOf(bloodPressure));
                    params.put("skin_thickness", String.valueOf(skinThickness));
                    params.put("insulin_level", String.valueOf(insulin));
                    params.put("bmi", String.valueOf(bmi));
                    params.put("dpf", String.valueOf(diabetesPedigree));
                    params.put("age", String.valueOf(age));
                    params.put("required_calories", String.valueOf(totalCalories));
                    params.put("food_allergies", foodAllergy);
                    params.put("required_nutrient", requiredNutrient);
                    return params;
                }
            };

            // Add the registration request to the request queue
            RequestQueue queue = Volley.newRequestQueue(requireContext());
            queue.add(registerRequest);
        }
    }

    private void showNext(){
        Intent intent = new Intent(getActivity(), LandingpageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        // Apply a smooth transition animation
        requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out_two);
    }
}