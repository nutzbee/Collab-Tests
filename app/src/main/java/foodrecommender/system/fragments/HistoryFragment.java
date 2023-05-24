package foodrecommender.system.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import foodrecommender.system.R;
import foodrecommender.system.adapters.HistoryAdapter;
import foodrecommender.system.adapters.SampleFoodAdapter;
import foodrecommender.system.classes.History;
import foodrecommender.system.classes.SampleFood;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private View view;
    private LinearProgressIndicator linearProgressIndicator;
    public HistoryFragment() {
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
        view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = view.findViewById(R.id.history_list);
        linearProgressIndicator = requireActivity().findViewById(R.id.profile_progress_indicator);

        if (isAdded()) {
            getHistory();
        }
        return view;
    }

    private void getHistory() {
        if (isAdded()) {
            // Make a POST request to the API endpoint
            // String url = "http://192.168.0.41:5000/sample";
            String url = getString(R.string.history_url);
            RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        linearProgressIndicator.hide();
                        // Parse the JSON response
                        JSONArray selected_foods = response.getJSONArray("selected_foods");
                        ArrayList<History> histories = new ArrayList<>();

                        // Iterate through the JSON array and create SampleFood objects
                        for (int i = 0; i < selected_foods.length(); i++) {
                            JSONObject selected_foodsJSONObject = selected_foods.getJSONObject(i);
                            String shortDesc = selected_foodsJSONObject.getString("descrip");
                            String kcal = selected_foodsJSONObject.getString("energKcal");
                            String foodGroup = selected_foodsJSONObject.getString("foodGroup");
                            History history = new History(shortDesc, foodGroup, kcal);
                            histories.add(history);
                        }

                        // Pass the sampleFoods ArrayList to your RecyclerView adapter
                        HistoryAdapter adapter = new HistoryAdapter(histories);
                        recyclerView.setAdapter(adapter);

                        adapter.setOnItemClickListener(new HistoryAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(String value) {
                                Snackbar.make(view, value, Snackbar.LENGTH_SHORT)
                                        .show();
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
                    getHistory();
                }
            });
            requestQueue.add(jsonObjectRequest);
        }
    }

}