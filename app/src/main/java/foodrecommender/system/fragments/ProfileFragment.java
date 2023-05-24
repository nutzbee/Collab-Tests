package foodrecommender.system.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import foodrecommender.system.R;
import foodrecommender.system.activities.SigninActivity;
import foodrecommender.system.activities.SignupActivity;
import foodrecommender.system.adapters.ProfileAdapter;
import foodrecommender.system.classes.Profile;

public class ProfileFragment extends Fragment {
    private View view;
    private RecyclerView profileRecyclerView;
    private MaterialCardView materialCardView;
    private Chip exerciseChip, summaryChip, preferencesChip, historyChip;
    private FragmentTransaction exerciseFragmentTransaction;
    private FrameLayout frameLayout;
    private Button loginButton, signupButton;
    private FrameLayout parentView;
    private boolean isLoggedIn;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        initializeViews();
        handleButtonActions();
        handleChipActions();
        showProfileInformation();
        return view;
    }

    private void initializeViews() {
        profileRecyclerView = view.findViewById(R.id.profile_recycler_view);
        materialCardView = view.findViewById(R.id.profile_picture_card);
        exerciseChip = view.findViewById(R.id.exercise_chip);
        summaryChip = view.findViewById(R.id.summary_chip);
        frameLayout = view.findViewById(R.id.fragment_container_summary);
        parentView = view.findViewById(R.id.parentProfileFrame);
        loginButton = view.findViewById(R.id.buttonLogin);
        signupButton = view.findViewById(R.id.buttonSignup);
        preferencesChip = view.findViewById(R.id.preferences_chip);
        historyChip = view.findViewById(R.id.history_chip);
    }

    private void handleButtonActions() {
        SharedPreferences sp = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        boolean isLoggedIn = sp.getBoolean("is_logged_in", false);
        loginButton.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), SigninActivity.class);
            startActivity(intent);
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        signupButton.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), SignupActivity.class);
            startActivity(intent);
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        if (!isLoggedIn) {
            loginButton.setVisibility(View.VISIBLE);
            signupButton.setVisibility(View.VISIBLE);
            profileRecyclerView.setVisibility(View.GONE);
        }
    }

    private void handleChipActions() {
        exerciseChip.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                summaryChip.setChecked(false);
                preferencesChip.setChecked(false);
                historyChip.setChecked(false);
            }
            changeFragment();
        });

        summaryChip.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                exerciseChip.setChecked(false);
                preferencesChip.setChecked(false);
                historyChip.setChecked(false);
            }
            changeFragment();
        });

        preferencesChip.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                exerciseChip.setChecked(false);
                summaryChip.setChecked(false);
                historyChip.setChecked(false);
            }
            changeFragment();
        });

        historyChip.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                exerciseChip.setChecked(false);
                summaryChip.setChecked(false);
                preferencesChip.setChecked(false);
            }
            changeFragment();
        });
    }

    private void changeFragment() {
        if (profileRecyclerView.isShown() && isLoggedIn) {
            profileRecyclerView.setVisibility(View.GONE);
        }

        frameLayout.setVisibility(View.VISIBLE);
        exerciseFragmentTransaction = getChildFragmentManager().beginTransaction();
        if (exerciseChip.isChecked()) {
            exerciseFragmentTransaction.replace(R.id.fragment_container_summary, new ExerciseFragment());
            exerciseFragmentTransaction.commitNow();
        } else if (summaryChip.isChecked()) {
            exerciseFragmentTransaction.replace(R.id.fragment_container_summary, new SummaryFragment());
            exerciseFragmentTransaction.commitNow();
        } else if (preferencesChip.isChecked()) {
            exerciseFragmentTransaction.replace(R.id.fragment_container_summary, new PreferencesFragment());
            exerciseFragmentTransaction.commitNow();
        } else if (historyChip.isChecked()) {
            exerciseFragmentTransaction.replace(R.id.fragment_container_summary, new HistoryFragment());
            exerciseFragmentTransaction.commitNow();
        } else {
            if (frameLayout.isShown()) {
                frameLayout.setVisibility(View.GONE);
            }
            if (!profileRecyclerView.isShown() && isLoggedIn) {
                profileRecyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showProfileInformation() {
        SharedPreferences sp = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        isLoggedIn = sp.getBoolean("is_logged_in", false);
        boolean isPreferences = sp.getBoolean("isPreferences", false);

        if (isPreferences){
            profileRecyclerView.setVisibility(View.GONE);
            preferencesChip.setChecked(true);
            ed.putBoolean("isPreferences", false);
            ed.apply();
        }

        if (!isLoggedIn){
            exerciseChip.setEnabled(false);
            historyChip.setEnabled(false);
            summaryChip.setEnabled(false);
            preferencesChip.setEnabled(false);
        } else {
            exerciseChip.setEnabled(true);
            historyChip.setEnabled(true);
            summaryChip.setEnabled(true);
            preferencesChip.setEnabled(true);
        }

        RecyclerView recyclerView = view.findViewById(R.id.profile_recycler_view);

        // Retrieve the necessary data from SharedPreferences
        String name = sp.getString("name", "");
        String email = sp.getString("email", "");
        int age = sp.getInt("age", 0);
        String username = sp.getString("username", "");
        String password = sp.getString("password", "");
        float weight = sp.getFloat("weight", 0.0f);
        float bmi = sp.getFloat("bmi", 0.0f);
        String status = sp.getString("status", "");

        // Create a list of data items
        ArrayList<Profile> profiles = new ArrayList<>();
        profiles.add(new Profile("Full Name", name));
        profiles.add(new Profile("Email", email));
        profiles.add(new Profile("Age", String.valueOf(age)));
        profiles.add(new Profile("Username", username));
        profiles.add(new Profile("Password", password));
        profiles.add(new Profile("Weight", String.valueOf(weight)));
        profiles.add(new Profile("Body Mass Index", String.valueOf(bmi)));
        profiles.add(new Profile("Status", status));

        ProfileAdapter profileAdapter = new ProfileAdapter(requireContext(), profiles);
        recyclerView.setAdapter(profileAdapter);
        profileAdapter.setOnItemClickListener((title, value, position) -> showSnackbar(title, value));
    }

    private void showSnackbar(String profileTitle, String profileValue) {
        if (profileTitle.equals("Status")) {
            Snackbar.make(parentView, profileValue, Snackbar.LENGTH_SHORT).show();
        }
    }
}