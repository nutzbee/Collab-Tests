package foodrecommender.system;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExerciseFragment extends Fragment {
    private View view;
    private TextView burnedCaloriesTextView, userWeight, userActivity;
    private AutoCompleteTextView actvActivity;
    private MaterialCardView card;
    private UserDAO userDAO;
    private String message;
    private Snackbar snackbar;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;

    public ExerciseFragment() {
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
        view = inflater.inflate(R.layout.fragment_exercise, container, false);
        recyclerView = view.findViewById(R.id.profile_recycler_view);
        SharedPreferences sp = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);

        userDAO = new UserDAO(getActivity());
        if (!sp.getString("username", "").equals("")){
            String inputUsername = sp.getString("username", "");

            // Retrieve values from the database
            ArrayList<User> userList = userDAO.getUsers(inputUsername);

            // Retrieve lists of usernames and passwords from the database
            ArrayList<String> usernameList = userDAO.getUsernames(inputUsername);
            ArrayList<String> passwordList = userDAO.getPasswords(inputUsername);

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            // Create an instance of the UserAdapter and set it to the RecyclerView
            userAdapter = new UserAdapter(usernameList, passwordList);
            recyclerView.setAdapter(userAdapter);

            // Process the retrieved values as needed
            for (User user : userList) {
                String username = user.getUsername();
                String password = user.getPassword();
                int pregnancyCount = user.getPregnancyCount();
                // Retrieve other columns as needed

                // Process the retrieved values here
                message = "Username: " + username + ", Password: " + password + ", Pregnancy count: " + pregnancyCount;
                snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
                snackbar.show();
                // Process other columns as needed
            }
        }

        burnedCaloriesTextView = view.findViewById(R.id.content_calories);
        userWeight = view.findViewById(R.id.content_distance);
        userActivity = view.findViewById(R.id.content_text);
        actvActivity = view.findViewById(R.id.actvActivity);
        card = view.findViewById(R.id.running_card);
        card();
        fetchTheActivites();
        return view;
    }
    private void card(){
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                predictReferences();
            }
        });
    }

    private void fetchTheActivites(){
        String url = getString(R.string.exercise_url_get);
        //String url = "http://nutzbee.pythonanywhere.com/get_activities";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONArray activitiesArray = response.getJSONArray("activities");
                List<String> activitiesList = new ArrayList<>();
                for (int i = 0; i < activitiesArray.length(); i++) {
                    activitiesList.add(activitiesArray.getString(i));
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, activitiesList);
                actvActivity.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            error.printStackTrace();
        });

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
    }

    private void predictReferences(){
        String url = getString(R.string.exercise_url);
        //String url = "http://nutzbee.pythonanywhere.com/get_calories_burned";
        JSONObject data = new JSONObject();
        double user_weight_lb = 134.482;

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
                burnedCaloriesTextView.setText(burnedCaloriesResult);
                userWeight.setText(userWeightString);
                userActivity.setText(userActivityString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            error.printStackTrace();
        });

        Volley.newRequestQueue(getActivity()).add(jsonObjectRequest);
    }
}