package foodrecommender.system.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import foodrecommender.system.R;
import foodrecommender.system.classes.SampleFood;
import foodrecommender.system.adapters.SampleFoodAdapter;

public class SampleFoodsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView moreTextView;
    private View view;
    private LinearProgressIndicator linearProgressIndicator;

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
        moreTextView = view.findViewById(R.id.more_ticker_textview);
        linearProgressIndicator = view.findViewById(R.id.sampleF_progress_indicator);

        if (isAdded()) {
            sample_foods();
        }

        return view;
    }

    private void sample_foods(){
        // Make a POST request to the API endpoint
        // String url = "http://192.168.0.41:5000/sample";
        linearProgressIndicator.show();
        String url = getString(R.string.sample_foods_url);
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    linearProgressIndicator.hide();
                    // Parse the JSON response
                    JSONArray sampleFoodsArray = response.getJSONArray("sample_foods");
                    ArrayList<SampleFood> sampleFoods = new ArrayList<>();

                    // Iterate through the JSON array and create SampleFood objects
                    for (int i = 0; i < sampleFoodsArray.length(); i++) {
                        JSONObject sampleFoodObject = sampleFoodsArray.getJSONObject(i);
                        String shortDesc = sampleFoodObject.getString("Descrip");
                        String kcal = sampleFoodObject.getString("Energ_Kcal");
                        String foodGroup = sampleFoodObject.getString("FoodGroup");
                        SampleFood sampleFood = new SampleFood(shortDesc, foodGroup, kcal);
                        sampleFoods.add(sampleFood);
                    }

                    // Pass the sampleFoods ArrayList to your RecyclerView adapter
                    SampleFoodAdapter adapter = new SampleFoodAdapter(sampleFoods);
                    adapter.setMaxItemsToShow(3);
                    recyclerView.setAdapter(adapter);

                    adapter.setOnItemClickListener(new SampleFoodAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String value) {
                            Snackbar.make(view, value, Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                    });

                    moreTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
                            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
                            bottomSheetDialog.setContentView(bottomSheetView);
                            /// Create a new instance of the adapter with max items to show set to 10
                            SampleFoodAdapter bottomSheetAdapter = new SampleFoodAdapter(sampleFoods);
                            bottomSheetAdapter.setMaxItemsToShow(10);

                            RecyclerView sheetList = bottomSheetView.findViewById(R.id.sheet_list);
                            sheetList.setAdapter(bottomSheetAdapter);

                            // Call setMaxItemsToShow and notifyDataSetChanged on the adapter to update the data
                            bottomSheetAdapter.setOnItemClickListener(new SampleFoodAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(String value) {
                                    Snackbar.make(bottomSheetView, value,
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            });

                            bottomSheetDialog.show();
                            //moreTextView.setVisibility(View.GONE);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                sample_foods();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}