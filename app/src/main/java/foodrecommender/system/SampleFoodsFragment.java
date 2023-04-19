package foodrecommender.system;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SampleFoodsFragment extends Fragment {

    private View view;
    private Button recommendationButton,predictionButton;
    private RecyclerView recyclerView;
    private LinearLayout linearLayout;
    private ImageView headerImage;
    private TextView welcomeText, suggestionsText;
    private int prevScrollPosition = 0;
    private MaterialCardView welcomeCardView;

    public SampleFoodsFragment() {
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
        view = inflater.inflate(R.layout.fragment_sample_foods, container, false);

        recyclerView = view.findViewById(R.id.suggestion_list);
        suggestionsText = view.findViewById(R.id.suggestions_text);

        sample_foods();

        return view;
    }

    private void sample_foods(){
        // Make a POST request to the API endpoint
        // String url = "http://192.168.0.41:5000/sample";
        String url = getString(R.string.sample_foods_url);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Parse the JSON response
                    JSONArray sampleFoodsArray = response.getJSONArray("sample_foods");
                    ArrayList<SampleFood> sampleFoods = new ArrayList<>();

                    // Iterate through the JSON array and create SampleFood objects
                    for (int i = 0; i < sampleFoodsArray.length(); i++) {
                        JSONObject sampleFoodObject = sampleFoodsArray.getJSONObject(i);
                        String shortDesc = sampleFoodObject.getString("Descrip");
                        int kcal = sampleFoodObject.getInt("Energ_Kcal");
                        String foodGroup = sampleFoodObject.getString("FoodGroup");
                        SampleFood sampleFood = new SampleFood(shortDesc, foodGroup, kcal);
                        sampleFoods.add(sampleFood);
                    }

                    // Pass the sampleFoods ArrayList to your RecyclerView adapter
                    SampleFoodAdapter adapter = new SampleFoodAdapter(sampleFoods);
                    recyclerView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}