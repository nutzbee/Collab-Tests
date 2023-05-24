package foodrecommender.system.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import foodrecommender.system.R;
public class ExerciseFragment extends Fragment {
    private View view;
    private TextView burnedCaloriesTextView, userWeight, userActivity;
    private AutoCompleteTextView actvActivity;
    private TextInputEditText weightInput;
    private MaterialCardView runningCard, walkingCard, bikingCard;
    private LinearProgressIndicator linearProgressIndicator;

    public ExerciseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_exercise, container, false);

        burnedCaloriesTextView = view.findViewById(R.id.content_calories);
        userWeight = view.findViewById(R.id.content_distance);
        userActivity = view.findViewById(R.id.content_text);
        actvActivity = view.findViewById(R.id.actvActivity);
        weightInput = view.findViewById(R.id.weight_input);
        walkingCard = view.findViewById(R.id.walking_card);
        bikingCard = view.findViewById(R.id.biking_card);
        runningCard = view.findViewById(R.id.running_card);
        linearProgressIndicator = requireActivity().findViewById(R.id.profile_progress_indicator);

        addCardActions();
        fetchTheActivites();
        setReadMode();
        return view;
    }

    private void addCardActions(){
        walkingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Walking", Snackbar.LENGTH_SHORT).show();
            }
        });
        bikingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Cycling", Snackbar.LENGTH_SHORT).show();
            }
        });
        runningCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Running", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTheActivites(){
        String url = getString(R.string.exercise_url_get);
        //String url = "http://nutzbee.pythonanywhere.com/get_activities";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    linearProgressIndicator.hide();
                    JSONArray activitiesArray = response.getJSONArray("activities");
                    List<String> activitiesList = new ArrayList<>();
                    for (int i = 0; i < activitiesArray.length(); i++) {
                        activitiesList.add(activitiesArray.getString(i));
                    }

                    if (isAdded()){
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, activitiesList);
                        actvActivity.setAdapter(adapter);
                        actvActivity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                predictReferences();
                                Snackbar.make(requireView(), "You selected "+actvActivity.getText().toString(), Snackbar.LENGTH_SHORT).show();
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
                fetchTheActivites();
            }
        });

        Volley.newRequestQueue(requireActivity()).add(jsonObjectRequest);
    }

    private void predictReferences(){
        String url = getString(R.string.exercise_url);
        //String url = "http://nutzbee.pythonanywhere.com/get_calories_burned";
        SharedPreferences sp = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        JSONObject data = new JSONObject();
        double user_weight_lb = sp.getFloat("weight", 0.0f);

        try {
            data.put("user_weight_lb", user_weight_lb);
            data.put("selected_activity", actvActivity.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data, response -> {
            try {
                String burnedCaloriesResult = response.getString("total_calories_burned");
                String userWeightString = response.getString("user_weight_lb");
                String userActivityString = response.getString("activity");
                burnedCaloriesTextView.setVisibility(View.VISIBLE);
                burnedCaloriesTextView.setText(burnedCaloriesResult);
                userWeight.setVisibility(View.VISIBLE);
                userWeight.setText(userWeightString);
                userActivity.setVisibility(View.VISIBLE);
                userActivity.setText(userActivityString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            error.printStackTrace();
        });

        Volley.newRequestQueue(requireActivity()).add(jsonObjectRequest);
    }

    private void setReadMode(){
        SharedPreferences sp = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        weightInput.setFocusable(false);
        weightInput.setClickable(false);
        weightInput.setLongClickable(false);
        weightInput.setInputType(InputType.TYPE_NULL);
        weightInput.setText(String.format("%s is your weight in lbs", sp.getFloat("weight", 0.0f)));
    }
}