package foodrecommender.system;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

public class BaseSignupFragment extends Fragment {
    private View view;
    private Button signUpButton, signInButton;

    private LinearProgressIndicator progressIndicator;
    private TextInputEditText username, email, password, conpass;

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
        username = view.findViewById(R.id.usernameEditText);
        email = view.findViewById(R.id.emailEditText);
        password = view.findViewById(R.id.passwordEditText);
        conpass = view.findViewById(R.id.confirmPasswordEditText);

        signUpButton = view.findViewById(R.id.signUpButton);
        signInButton = view.findViewById(R.id.signInButton);
        progressIndicator = requireActivity().findViewById(R.id.progress_indicator);
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
        return view;
    }

    private void signUp(){
        progressIndicator.show();
        SharedPreferences sp = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("username", username.getText().toString());
        ed.putString("password", password.getText().toString());
        ed.apply();
        FragmentTransaction signUpFragmentTransaction = getParentFragmentManager().beginTransaction();
        signUpFragmentTransaction.replace(R.id.signup_fragment_container, new PredictionFragment());
        signUpFragmentTransaction.addToBackStack(null);
        signUpFragmentTransaction.commit();
    }

    private void signIn(){
        // Start the next activity
        Intent intent = new Intent(getContext(), SigninActivity.class);
        startActivity(intent);

        // Apply a smooth transition animation
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}