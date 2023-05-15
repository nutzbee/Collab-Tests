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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import foodrecommender.system.R;
import foodrecommender.system.activities.SigninActivity;
import foodrecommender.system.activities.SignupActivity;
import foodrecommender.system.adapters.ProfileAdapter;
import foodrecommender.system.adapters.UserAdapter;
import foodrecommender.system.classes.Profile;
import foodrecommender.system.classes.SampleFood;
import foodrecommender.system.classes.UserDAO;

public class ProfileFragment extends Fragment {
    private View view;
    private UserDAO userDAO;
    private String message;
    private Snackbar snackbar;
    private RecyclerView profileRecyclerView;
    private UserAdapter userAdapter;
    private MaterialCardView materialCardView;

    private ArrayAdapter<String> adapter;
    private Chip exerciseChip, summaryChip;
    private FragmentTransaction exerciseFragmentTransaction;
    private FrameLayout frameLayout;
    private Button loginButton, signupButton;

    public ProfileFragment() {
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
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        profileRecyclerView = view.findViewById(R.id.profile_recycler_view);
        materialCardView = view.findViewById(R.id.profile_picture_card);
        exerciseChip = view.findViewById(R.id.exercise_chip);
        summaryChip = view.findViewById(R.id.summary_chip);
        frameLayout = view.findViewById(R.id.fragment_container_summary);
        loginButton = view.findViewById(R.id.buttonLogin);
        signupButton = view.findViewById(R.id.buttonSignup);

        handleButtonActions();
        handleChipActions();
        showProfileInformation();
        return view;
    }

    private void handleButtonActions(){
        if (isAdded()) {
            SharedPreferences sp = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
            if (!sp.getBoolean("is_logged_in", false)) {
                loginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(requireActivity(), SigninActivity.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });

                signupButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(requireActivity(), SignupActivity.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });

                if (!loginButton.isShown() && !signupButton.isShown() || profileRecyclerView.isShown()) {
                    loginButton.setVisibility(View.VISIBLE);
                    signupButton.setVisibility(View.VISIBLE);
                    profileRecyclerView.setVisibility(View.GONE);
                }
            } else {
                if (loginButton.isShown() && signupButton.isShown() || !profileRecyclerView.isShown()) {
                    loginButton.setVisibility(View.GONE);
                    signupButton.setVisibility(View.GONE);
                    profileRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void handleChipActions() {

        if (isAdded()) {
            exerciseChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        summaryChip.setChecked(false);
                    }
                    changeFragment();
                }
            });

            summaryChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        exerciseChip.setChecked(false);
                    }
                    changeFragment();
                }
            });
        }
    }

    private void changeFragment() {
        if (isAdded()) {
            SharedPreferences sp = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
            if (profileRecyclerView.isShown() && sp.getBoolean("is_logged_in", false)) {
                profileRecyclerView.setVisibility(View.GONE);
            }
            frameLayout.setVisibility(View.VISIBLE);
            if (exerciseChip.isChecked()) {
                exerciseFragmentTransaction = getChildFragmentManager().beginTransaction();
                exerciseFragmentTransaction.replace(R.id.fragment_container_summary, new ExerciseFragment());
                exerciseFragmentTransaction.commitNow();
            } else if (summaryChip.isChecked()) {
                exerciseFragmentTransaction = getChildFragmentManager().beginTransaction();
                exerciseFragmentTransaction.replace(R.id.fragment_container_summary, new SummaryFragment());
                exerciseFragmentTransaction.commitNow();
            } else {
                if (frameLayout.isShown()) {
                    frameLayout.setVisibility(View.GONE);
                }
                if (!profileRecyclerView.isShown() && sp.getBoolean("is_logged_in", false)) {
                    profileRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void showProfileInformation() {
        if (isAdded()) {
            SharedPreferences sp = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
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
        }
    }
}