package foodrecommender.system.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import foodrecommender.system.R;
import foodrecommender.system.fragments.BaseSignupFragment;

public class SignupActivity extends AppCompatActivity {

    LinearProgressIndicator progressIndicator;
    private String fragmentTag = "First Fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        progressIndicator = findViewById(R.id.progress_indicator);

        getTheFragment();
        userCheck();
    }

    private void userCheck(){
        progressIndicator.show();
        // Check the login status when the app launches or when navigating to a specific page
        boolean isLoggedIn = checkLoginStatus();

        if (isLoggedIn) {
            // User is already logged in, redirect to the landing page
            Intent intent = new Intent(this, LandingpageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else {
            // User is not logged in, allow them to proceed to the current page
            // Continue with the normal flow of the current page
            progressIndicator.hide();
        }
    }

    // Method to check the login status from SharedPreferences or any other storage mechanism
    private boolean checkLoginStatus() {
        // Retrieve the login status from SharedPreferences or other storage
        SharedPreferences sp = getSharedPreferences("user_data", MODE_PRIVATE);
        return sp.getBoolean("is_logged_in", false);
    }

    private void getTheFragment(){
        FragmentTransaction signUpFragmentTransaction = getSupportFragmentManager().beginTransaction();
        signUpFragmentTransaction.replace(R.id.signup_fragment_container, new BaseSignupFragment());
        signUpFragmentTransaction.addToBackStack(fragmentTag);
        signUpFragmentTransaction.commit();
    }

    public void updateActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.signup_fragment_container);
        if (currentFragment instanceof BaseSignupFragment) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();

                // Get the previous fragment from the back stack
                FragmentManager.BackStackEntry backStackEntry = getSupportFragmentManager().getBackStackEntryAt(
                        getSupportFragmentManager().getBackStackEntryCount() - 1);
                String previousFragmentTag = backStackEntry.getName();

                // Update the ActionBar title with the previous fragment's title
                updateActionBarTitle(previousFragmentTag);
            }
        }
        if (progressIndicator.isShown() || progressIndicator.getProgress() > 0){
            progressIndicator.setProgress(0);
            progressIndicator.hide();
        }
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}