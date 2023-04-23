package foodrecommender.system;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class BaseSignupFragment extends Fragment {
    private View view;
    private Button signUpButton, signInButton;

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
        signUpButton = view.findViewById(R.id.signUpButton);
        signInButton = view.findViewById(R.id.signInButton);

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
        FragmentTransaction signUpFragmentTransaction = getChildFragmentManager().beginTransaction();
        signUpFragmentTransaction.replace(R.id.signup_fragment_container_fragment, new BmiFragment());
        signUpFragmentTransaction.commitNow();
    }

    private void signIn(){
        // Start the next activity
        Intent intent = new Intent(getContext(), SigninActivity.class);
        startActivity(intent);

        // Apply a smooth transition animation
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}