package foodrecommender.system.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import foodrecommender.system.R;
import foodrecommender.system.activities.LandingpageActivity;
import foodrecommender.system.activities.SigninActivity;
import foodrecommender.system.activities.SignupActivity;

public class BaseSignupFragment extends Fragment {
    private View view;
    private Button signUpButton, signInButton, exitButton;

    private LinearProgressIndicator progressIndicator;
    private TextInputEditText name, email, weight, username, password, conpass;
    private String fragmentTag = "Second Fragment",
    actionBarTitle = "Sign Up", message;

    public BaseSignupFragment() {
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
        view = inflater.inflate(R.layout.fragment_base_signup, container, false);

        ((SignupActivity) requireActivity()).updateActionBarTitle(actionBarTitle);

        username = view.findViewById(R.id.usernameEditText);
        name = view.findViewById(R.id.nameEditText);
        email = view.findViewById(R.id.emailEditText);
        weight = view.findViewById(R.id.weightEditText);
        password = view.findViewById(R.id.passwordEditText);
        conpass = view.findViewById(R.id.confirmPasswordEditText);

        signUpButton = view.findViewById(R.id.signUpButton);
        signInButton = view.findViewById(R.id.signInButton);
        exitButton = view.findViewById(R.id.exitButton);
        progressIndicator = requireActivity().findViewById(R.id.progress_indicator);

        setButtonActions();
        checkForSavedData();
        return view;
    }

    private void setButtonActions(){
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                continueToLandingPage();
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void continueToLandingPage(){
        if (isAdded()) {
            SharedPreferences sp = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = sp.edit();
            ed.putBoolean("is_logged_in", false);
            ed.apply();

            Intent intent = new Intent(requireActivity(), LandingpageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            // Apply a smooth transition animation
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out_two);
        }
    }

    private void checkForSavedData(){
        if (isAdded()) {
            SharedPreferences sp = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);

            String nameInput = sp.getString("name", ""),
                    emailInput = sp.getString("email", ""),
                    usernameInput = sp.getString("username", ""),
                    passwordInput = sp.getString("password", "");
            float weightInput = sp.getFloat("weight", 0);

            if (!nameInput.isEmpty() || !emailInput.isEmpty() ||
                    weightInput>0 || !usernameInput.isEmpty() || !passwordInput.isEmpty()) {
                name.setText(nameInput);
                email.setText(emailInput);
                weight.setText(String.valueOf(weightInput));
                username.setText(usernameInput);
                password.setText(passwordInput);

                message = "Data restored";
                Log.d("checkForSavedData", "checkForSavedData: " + message);
            }
        }
    }

    private void showSnackbar(){
        // Create the snackbar
        int duration = Snackbar.LENGTH_SHORT;
        Snackbar snackbar = Snackbar.make(view, message, duration);

        snackbar.setText(message);
        snackbar.show();
    }

    private void signUp(){
        String nameInput = name.getText().toString(),
                emailInput = email.getText().toString(),
                weightInput = weight.getText().toString(),
                usernameInput = username.getText().toString(),
                passwordInput = password.getText().toString(),
                conpassInput = conpass.getText().toString();

        if (nameInput.isEmpty() || weightInput.isEmpty() || emailInput.isEmpty() ||
                usernameInput.isEmpty() || passwordInput.isEmpty() || conpassInput.isEmpty()){
            // Show message for empty fields
            message = "Do not leave empty fields";
            showSnackbar();

        } else {
            if (!passwordInput.equals(conpassInput)) {
                message = "Password not match";
                showSnackbar();
            } else {
                // Store the user's data
                SharedPreferences sp = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
                SharedPreferences.Editor ed = sp.edit();
                ed.putString("name", nameInput);
                ed.putString("email", emailInput);
                ed.putFloat("weight", 0.0f);
                ed.putString("username", usernameInput);
                ed.putString("password", passwordInput);
                ed.apply();

                // Proceed to page 2 of Registration
                FragmentTransaction signUpFragmentTransaction = getParentFragmentManager().beginTransaction();
                signUpFragmentTransaction.replace(R.id.signup_fragment_container, new PredictionFragment());
                signUpFragmentTransaction.addToBackStack(fragmentTag);
                signUpFragmentTransaction.commit();
            }
        }
    }

    private void signIn(){
        // Start the next activity
        Intent intent = new Intent(getContext(), SigninActivity.class);
        startActivity(intent);

        // Apply a smooth transition animation
        requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out_two);
    }
}