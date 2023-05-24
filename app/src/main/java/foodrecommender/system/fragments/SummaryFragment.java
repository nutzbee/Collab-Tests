package foodrecommender.system.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import foodrecommender.system.R;

public class SummaryFragment extends Fragment {
    private View view;
    private TextView summaryContent, healthReport, recommendations;
    private LinearProgressIndicator linearProgressIndicator;

    public SummaryFragment() {
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
        view = inflater.inflate(R.layout.fragment_summary, container, false);
        summaryContent = view.findViewById(R.id.summary_content);
        healthReport = view.findViewById(R.id.health_card_value);
        recommendations = view.findViewById(R.id.recommendations_card_value);
        linearProgressIndicator = requireActivity().findViewById(R.id.profile_progress_indicator);

        summary_report();
        return view;
    }

    private void summary_report(){
        // Make a POST request to the API endpoint
        // String url = "http://192.168.0.41:5000/summary";
        String url = getString(R.string.summary_url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    linearProgressIndicator.hide();
                    String summary_report = response.getString("summary_report");
                    String health_report = response.getString("health_monitor");
                    String recommendation = response.getString("recommendations");
                    summaryContent.setText(summary_report);
                    healthReport.setText(health_report);
                    recommendations.setText(recommendation);
                    if (isAdded()) {
                        if (health_report.equals("Unidentified")) {
                            healthReport.setTextColor(getResources().getColor(com.google.android.material.R.color.design_default_color_error, requireContext().getTheme()));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                summary_report();
            }
        });
        Volley.newRequestQueue(requireActivity()).add(jsonObjectRequest);
    }
}