package foodrecommender.system.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import foodrecommender.system.R;
import foodrecommender.system.adapters.RecommendedFoodsAdapater;
import foodrecommender.system.adapters.ValuesAdapter;
import foodrecommender.system.classes.RecommendedFoods;
import foodrecommender.system.classes.Values;

public class LandingpageFragment extends Fragment {
    private View view;
    private TextView welcomeText;
    private CardView welcomeCardView, recoCardview;
    private TabLayout tabLayout;
    private FrameLayout frameLayout;
    private TabLayout.Tab sampleFoodsTab, recommendationTab, diabeticCheckTab;
    private Snackbar snackbar;
    private FragmentTransaction sampleFoodsFragmentTransaction,
            recommendationFragmentTransaction, diaCheckFragmentTransaction, settingsFragmentTransaction;
    private String message;
    private RecyclerView recoRecyclerView;
    private RecommendedFoodsAdapater adapter;
    private MaterialSwitch foodPreference;
    private boolean isLoggedIn;
    private LinearProgressIndicator linearProgressIndicator;

    public LandingpageFragment() {
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
        view = inflater.inflate(R.layout.fragment_landingpage, container, false);

        welcomeText = view.findViewById(R.id.welcome_text);
        welcomeCardView = view.findViewById(R.id.material_landing_cv3);
        frameLayout = view.findViewById(R.id.landingFrameLayout);
        recoRecyclerView = view.findViewById(R.id.recommendations_recycler_view);
        recoCardview = view.findViewById(R.id.recommendations_cv);
        foodPreference = view.findViewById(R.id.switchLocalGlobal);
        linearProgressIndicator = view.findViewById(R.id.recommendF_progress_indicator);

        switchActions();
        return view;
    }

    private void switchActions() {
        if (isAdded()) {
            linearProgressIndicator.show();
            SharedPreferences sp = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = sp.edit();
            boolean isLocal = sp.getBoolean("isLocal", false);
            isLoggedIn = sp.getBoolean("is_logged_in", false);
            if (isLocal){
                recommendLocal();
            } else {
                recommend();
            }
            foodPreference.setChecked(isLocal);
            foodPreference.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        recommendLocal();
                    } else {
                        recommend();
                    }
                    ed.putBoolean("isLocal", b);
                    ed.apply();
                    linearProgressIndicator.show();
                }
            });
            if (isLoggedIn){
                recoCardview.setVisibility(View.VISIBLE);
            }
        }
    }

    private void recommendLocal() {
        if (isAdded()) {
            SharedPreferences sp = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
            String fod_allergy = sp.getString("fod_allergy", ""),
                    nutrient_req = sp.getString("nutrient_req", "");
            int calorie_req = sp.getInt("calorie_req", 0);

            if (calorie_req < 1 ||
                    fod_allergy.isEmpty() ||
                    nutrient_req.isEmpty()) {

                if (isLoggedIn) {
                    message = "Your preferences is not set";
                } else {
                    message = "Login for full features";
                }
                snackBarStrings();
            } else {
                // String url = "http://192.168.0.41:5000/recommend";
                String url = getString(R.string.recommend_local_url);
                JSONObject data = new JSONObject();

                try {
                    data.put("calorie_req", calorie_req);
                    data.put("food_allergy", fod_allergy);
                    data.put("nutrient_req", nutrient_req);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            linearProgressIndicator.hide();
                            JSONArray recommendedFoodsArray = response.getJSONArray("recommended_foods");
                            ArrayList<RecommendedFoods> recommendedFoods1 = new ArrayList<>();

                            String energKcal;

                            // Iterate through the JSON array and create RecommendedFoods objects
                            for (int i = 0; i < recommendedFoodsArray.length(); i++) {
                                JSONObject foodObject = recommendedFoodsArray.getJSONObject(i);
                                String descrip = foodObject.getString("descrip");
                                energKcal = foodObject.getString("energKcal");
                                //String shortDesc = recommendedFoodsArray.getString(i);
                                RecommendedFoods recommendedFoods = new RecommendedFoods(descrip, "", energKcal, false);
                                recommendedFoods1.add(recommendedFoods);
                            }

                            // Pass the recommendedFoods ArrayList to your RecyclerView adapter
                            if (isAdded()) {
                                adapter = new RecommendedFoodsAdapater(recommendedFoods1, requireContext());
                                recoRecyclerView.setAdapter(adapter);
                                adapter.setOnImageClickListener(new RecommendedFoodsAdapater.OnImageClickListener() {
                                    @Override
                                    public void onImageClick(String position, String foodName, int position2, String url) {
                                        url = getString(R.string.recommend_local_again_url);
                                        foodName = "Protein (g)";
                                        fetchRecommendedFoodsAgain(position, foodName, position2, url);
                                    }
                                });

                                adapter.setOnItemClickListener(new RecommendedFoodsAdapater.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(String foodName, int position, String url) {
                                        url = getString(R.string.get_local_values_url);
                                        fetchFoodValues(foodName, position, url);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        recommendLocal();
                    }
                });

                Volley.newRequestQueue(requireContext()).add(jsonObjectRequest);

            }
        }
    }

    private void recommend() {
        if (isAdded()) {
            SharedPreferences sp = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
            String fod_allergy = sp.getString("fod_allergy", ""),
                    nutrient_req = sp.getString("nutrient_req", "");
            int calorie_req = sp.getInt("calorie_req", 0);
            boolean isLoggedIn = sp.getBoolean("is_logged_in", false);

            if (calorie_req < 1 ||
                    fod_allergy.isEmpty() ||
                    nutrient_req.isEmpty()) {

                if (isLoggedIn) {
                    message = "Your preferences is not set";
                } else {
                    message = "Login for full features";
                }
                snackBarStrings();
            } else {
                // String url = "http://192.168.0.41:5000/recommend";
                String url = getString(R.string.recommend_url);
                JSONObject data = new JSONObject();

                try {
                    data.put("calorie_req", calorie_req);
                    data.put("food_allergy", fod_allergy);
                    data.put("nutrient_req", nutrient_req);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            linearProgressIndicator.hide();
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
                            if (isAdded()) {
                                adapter = new RecommendedFoodsAdapater(recommendedFoods1, requireContext());
                                recoRecyclerView.setAdapter(adapter);
                                adapter.setOnImageClickListener(new RecommendedFoodsAdapater.OnImageClickListener() {
                                    @Override
                                    public void onImageClick(String position, String foodName, int position2, String url) {
                                        url = getString(R.string.recommend_again_url);
                                        fetchRecommendedFoodsAgain(position, foodName, position2, url);
                                    }
                                });

                                adapter.setOnItemClickListener(new RecommendedFoodsAdapater.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(String foodName, int position, String url) {
                                        url = getString(R.string.get_values_url);
                                        fetchFoodValues(foodName, position, url);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        recommend();
                    }
                });

                Volley.newRequestQueue(requireContext()).add(jsonObjectRequest);

            }
        }
    }

    private void fetchFoodValues(String foodName, int position, String url) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_values, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
        JSONObject data = new JSONObject();

        try {
            data.put("selected_food_name", foodName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data, response -> {
            try {
                JSONObject values = response.getJSONObject("values");
                ArrayList<Values> mainValues = new ArrayList<>();
                ArrayList<Values> valueValues = new ArrayList<>();
                Iterator<String> keys = values.keys();

                if (url.equals(getString(R.string.get_values_url))) {
                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = values.getString(key);
                        String newMainKey = key.replace("_", " ");

                        if (key.equals("Shrt_Desc") || key.equals("Descrip") ||
                                key.equals("FoodGroup") || key.equals("NDB_No") ||
                                key.equals("Energ_Kcal")) {
                            Values mainValues1 = new Values(newMainKey, value);
                            mainValues.add(mainValues1);
                        } else {
                            Values valueValues1 = new Values(newMainKey, value);
                            valueValues.add(valueValues1);
                        }
                    }
                } else {
                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = values.getString(key);

                        if (key.equals("Alternate/Common name(s)") || key.equals("Food name and Description") ||
                                key.equals("Energy, calculated (kcal)") || key.equals("Food_ID")) {
                            Values mainValues1 = new Values(key, value);
                            mainValues.add(mainValues1);
                        } else {
                            Values valueValues1 = new Values(key, value);
                            valueValues.add(valueValues1);
                        }
                    }
                }

                ValuesAdapter valuesAdapter = new ValuesAdapter(mainValues);
                ValuesAdapter nutriAdapter = new ValuesAdapter(valueValues);
                RecyclerView valuesRecyclerView = bottomSheetView.findViewById(R.id.values_list);
                RecyclerView nutvaluesRecyclerView = bottomSheetView.findViewById(R.id.nutritional_list);
                valuesRecyclerView.setAdapter(valuesAdapter);
                nutvaluesRecyclerView.setAdapter(nutriAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            error.printStackTrace();
        });

        Volley.newRequestQueue(requireContext()).add(jsonObjectRequest);
    }

    private void fetchRecommendedFoodsAgain(String foodId, String foodName, int foodId2, String url) {
        if (isAdded()) {
            // String url = "http://192.168.0.41:5000/recommend_again";
            JSONObject data = new JSONObject();

            try {
                data.put("selected_food", foodId);
                data.put("selected_food_name", foodName);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data, response -> {
                try {
                    JSONArray recommendedFoodsArray = response.getJSONArray("recommended_foods_again");
                    ArrayList<RecommendedFoods> additionalItems = new ArrayList<>();

                    // Iterate through the JSON array and create RecommendedFoods objects
                    for (int i = 0; i < recommendedFoodsArray.length(); i++) {
                        JSONObject foodObject = recommendedFoodsArray.getJSONObject(i);
                        String descrip = foodObject.getString("descrip");
                        String energKcal = foodObject.getString("energKcal");
                        String foodGroup = foodObject.getString("foodGroup");
                        RecommendedFoods recommendedFoods = new RecommendedFoods(descrip, foodGroup, energKcal, true);
                        additionalItems.add(recommendedFoods);
                    }

                    if (adapter != null) {
                        adapter.addItems(additionalItems, foodId2); // Add the additional items to the RecyclerView
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                error.printStackTrace();
            });

            Volley.newRequestQueue(requireContext()).add(jsonObjectRequest);
        }
    }

    private void snackBarStrings() {
        //message = "Sample Foods";
        if (isAdded()) {
            int duration = Snackbar.LENGTH_SHORT;
            snackbar = Snackbar.make(frameLayout, message, duration);

            snackbar.setText(message);
            snackbar.show();
        }
    }
}