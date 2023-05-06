package foodrecommender.system;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecommendationFragment extends Fragment {

    private View view;
    private TextInputEditText calorie_req, fod_allergy, nutrient_req;
    private TextInputLayout calorie_layout, fod_allergy_layout, nutrient_layout;
    private RecyclerView recyclerView;
    private String message;
    private LinearProgressIndicator progressIndicator;
    private TextInputLayout[] textInputLayouts;
    private TextInputEditText[] textInputEditTexts;
    private int currentEditTextIndex = 0;
    private Button submitReco;
    private DatabaseHelper databaseHelper;
    private Snackbar snackbar;

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

        recyclerView = view.findViewById(R.id.food_recommendations_recycler_view);
        progressIndicator = requireActivity().findViewById(R.id.progress_indicator);
        progressIndicator.hide();
        //getTextInputEditText();
        //recommend();
        setOnClick();
        addEditTextsActions();

        return view;
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
            if (nextIndex < textInputLayouts.length) {
                textInputEditTexts[currentIndex].setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                            progressIndicator.setProgress(nextIndex);
                            textInputLayouts[currentIndex].setVisibility(View.GONE);
                            textInputLayouts[nextIndex].setVisibility(View.VISIBLE);
                            textInputEditTexts[nextIndex].requestFocus();
                            currentEditTextIndex = nextIndex;
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
                                            nutrient_req.setError(message);
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

    private void getTextInputEditText(){

        calorie_req.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length()>1){
                    //recommend();
                    personalizeContentsNow();
                }
            }
        });

        fod_allergy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length()>2){
                    //recommend();
                    personalizeContentsNow();
                }
            }
        });

        nutrient_req.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length()>2){
                    //recommend();
                    personalizeContentsNow();
                }
            }
        });
    }

    private void personalizeContentsNow(){
        if (calorie_req.getText().toString().isEmpty() ||
                fod_allergy.getText().toString().isEmpty() ||
                nutrient_req.getText().toString().isEmpty()){
            message = "All fields are required";
            snackBar();
        } else {
            SharedPreferences sp = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = sp.edit();
            ed.putInt("calorie_req", Integer.parseInt(calorie_req.getText().toString()));
            ed.putString("fod_allergy", fod_allergy.getText().toString());
            ed.putString("nutrient_req", nutrient_req.getText().toString());
            ed.apply();

            String username, password, foodAllergy, requiredNutrient;
            int pregnancyCount, glucoseCount, bloodPressure, skinThickness, insulin, age, totalCalories;
            float bmi, diabetesPedigree;

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

            long newRowId = databaseHelper.addUser(username, password, pregnancyCount, glucoseCount,
                    bloodPressure, skinThickness, insulin, bmi, diabetesPedigree, age, totalCalories,
                    foodAllergy, requiredNutrient);
            progressIndicator.show();

            if (newRowId != -1) {
                message = "User added successfully!";
                snackBar();
                // Schedule a delayed task to call the methods after the Snackbar duration
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Call the methods after the Snackbar duration
                        showNext();
                        progressIndicator.hide();
                    }
                }, 3000);
            } else {
                message = "'"+ username + "' is already taken";
                snackBar();
                progressIndicator.hide();
            }
        }
    }

    private void showNext(){
        Intent intent = new Intent(getActivity(), LadingpageActivity.class);
        startActivity(intent);

        // Apply a smooth transition animation
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void recommend(){
        if (calorie_req.getText().toString().isEmpty() ||
                fod_allergy.getText().toString().isEmpty() ||
                nutrient_req.getText().toString().isEmpty()){
            message = "All fields are required";
            snackBar();
        } else {
            // String url = "http://192.168.0.41:5000/recommend";
            String url = getString(R.string.recommend_url);
            JSONObject data = new JSONObject();


            try {
                data.put("calorie_req", calorie_req.getText().toString());
                data.put("food_allergy", fod_allergy.getText().toString());
                data.put("nutrient_req", nutrient_req.getText().toString());
                JSONArray foodsToEat = new JSONArray();
                foodsToEat.put("Fruits");
                foodsToEat.put("Beverage");
                foodsToEat.put("Meals");
                data.put("food_groups", foodsToEat);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data, response -> {
                try {
                    JSONArray recommendedFoodsArray = response.getJSONArray("recommended_foods");
                    ArrayList<RecommendedFoods> recommendedFoods1 = new ArrayList<>();

                    // Iterate through the JSON array and create RecommendedFoods objects
                    for (int i = 0; i < recommendedFoodsArray.length(); i++) {
                        JSONObject foodObject = recommendedFoodsArray.getJSONObject(i);
                        String descrip = foodObject.getString("descrip");
                        String energKcal = foodObject.getString("energKcal");
                        String foodGroup = foodObject.getString("foodGroup");
                        //String shortDesc = recommendedFoodsArray.getString(i);
                        RecommendedFoods recommendedFoods = new RecommendedFoods(descrip, foodGroup, energKcal, false);
                        recommendedFoods1.add(recommendedFoods);
                    }

                    // Pass the recommendedFoods ArrayList to your RecyclerView adapter
                    RecommendedFoodsAdapater adapter = new RecommendedFoodsAdapater(recommendedFoods1, getContext());
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                error.printStackTrace();
            });

            Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
        }
    }
}