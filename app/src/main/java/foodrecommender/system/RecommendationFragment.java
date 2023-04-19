package foodrecommender.system;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecommendationFragment extends Fragment {

    private View view;
    private TextInputEditText calorie_req, fod_allergy, nutrient_req;
    private RecyclerView recyclerView;

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
        calorie_req = view.findViewById(R.id.calorie_requirement_edit_text);
        fod_allergy = view.findViewById(R.id.food_allergies_edit_text);
        nutrient_req = view.findViewById(R.id.required_nutrient_edit_text);
        recyclerView = view.findViewById(R.id.food_recommendations_recycler_view);

        snackBar();
        getTextInputEditText();
        recommend();

        return view;
    }

    private void snackBar(){
        int duration = Snackbar.LENGTH_SHORT;
        String message = "Recommendations";
        Snackbar snackbar = Snackbar.make(view, message, duration);

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
                    recommend();
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
                    recommend();
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
                    recommend();
                }
            }
        });
    }

    private void recommend(){
        if (calorie_req.getText().toString().isEmpty() ||
                fod_allergy.getText().toString().isEmpty() ||
                nutrient_req.getText().toString().isEmpty()){

        } else {
            // String url = "http://192.168.0.41:5000/recommend";
            String url = getString(R.string.recommend_url);
            JSONObject data = new JSONObject();
            String calorie_reqt = "30";
            String food_allergyt = "na";
            String nutrient_reqt = "Fiber_TD_(g)";


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
                        int energKcal = foodObject.getInt("energKcal");
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