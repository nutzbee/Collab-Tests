package foodrecommender.system.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import foodrecommender.system.R;
import foodrecommender.system.adapters.RecommendedFoodsAdapater;
import foodrecommender.system.classes.RecommendedFoods;

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

        recommend();

        return view;
    }

    private void recommend() {
        if (isAdded()) {
            SharedPreferences sp = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
            String fod_allergy, nutrient_req;
            int calorie_req;
            calorie_req = sp.getInt("calorie_req", 0);
            fod_allergy = sp.getString("fod_allergy", "");
            nutrient_req = sp.getString("nutrient_req", "");

            if (calorie_req < 1 ||
                    fod_allergy.isEmpty() ||
                    nutrient_req.isEmpty()) {

                if (sp.getBoolean("is_logged_in", false)) {
                    message = "Manage food preferences in profile";
                } else {
                    message = "Login to view full features";
                }
                snackBarStrings();
            } else {
                recoCardview.setVisibility(View.VISIBLE);
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
                        if (isAdded()) {
                            adapter = new RecommendedFoodsAdapater(recommendedFoods1, requireContext());
                            recoRecyclerView.setAdapter(adapter);

                            adapter.setOnItemClickListener(new RecommendedFoodsAdapater.OnItemClickListener() {
                                @Override
                                public void onItemClick(String position, int position2) {
                                    Snackbar.make(frameLayout, "Check for "+position, Snackbar.LENGTH_SHORT).show();
                                    fetchRecommendedFoodsAgain(position, position2);
                                }
                            });
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
    }

    private void fetchRecommendedFoodsAgain(String foodId, int foodId2) {
        if (isAdded()) {
            // String url = "http://192.168.0.41:5000/recommend_again";
            String url = getString(R.string.recommend_again_url);
            JSONObject data = new JSONObject();

            try {
                data.put("selected_food", foodId);
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