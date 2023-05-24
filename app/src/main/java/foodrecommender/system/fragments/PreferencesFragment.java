package foodrecommender.system.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;

import foodrecommender.system.R;
import foodrecommender.system.adapters.PreferencesAdapter;
import foodrecommender.system.adapters.ProfileAdapter;
import foodrecommender.system.classes.Preferences;
import foodrecommender.system.classes.Profile;

public class PreferencesFragment extends Fragment {
    private View view;
    private RecyclerView preferencesRecyclerView;
    private LinearProgressIndicator linearProgressIndicator;

    public PreferencesFragment() {
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
        view = inflater.inflate(R.layout.fragment_preferences, container, false);
        initializeViews();
        showPreferences();
        return view;
    }

    private void showPreferences() {
        SharedPreferences sp = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);

        // Retrieve the necessary data from SharedPreferences
        int calorie_req = sp.getInt("calorie_req", 0);
        String nutrient_req = sp.getString("nutrient_req", "");
        float dpf = sp.getFloat("dpf", 0);
        String fod_allergy = sp.getString("fod_allergy", "");
        int glucose = sp.getInt("glucose", 0);
        int insulin = sp.getInt("insulin", 0);
        int pregnancies = sp.getInt("pregnancies", 0);
        int skinThickness = sp.getInt("skinThickness", 0);
        int bloodPressure = sp.getInt("bloodPressure", 0);

        // Create a list of data items
        ArrayList<Preferences> preferences = new ArrayList<>();
        preferences.add(new Preferences("Calorie requirement", String.valueOf(calorie_req)));
        preferences.add(new Preferences("Nutrient requirement", nutrient_req));
        preferences.add(new Preferences("Diabetes Pedigree Function", String.valueOf(dpf)));
        preferences.add(new Preferences("Allergies", fod_allergy));
        preferences.add(new Preferences("Glucose count", String.valueOf(glucose)));
        preferences.add(new Preferences("Insulin Level", String.valueOf(insulin)));
        preferences.add(new Preferences("Pregnancy count", String.valueOf(pregnancies)));
        preferences.add(new Preferences("Skin Thickness", String.valueOf(skinThickness)));
        preferences.add(new Preferences("Diastolic BP", String.valueOf(bloodPressure)));

        PreferencesAdapter preferencesAdapter = new PreferencesAdapter(requireContext(), preferences);
        preferencesRecyclerView.setAdapter(preferencesAdapter);
        linearProgressIndicator.hide();
    }

    private void initializeViews() {
        preferencesRecyclerView = view.findViewById(R.id.preferences_recycler_view);
        linearProgressIndicator = requireActivity().findViewById(R.id.profile_progress_indicator);
    }
}