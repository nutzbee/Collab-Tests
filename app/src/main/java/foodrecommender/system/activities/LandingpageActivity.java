package foodrecommender.system.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import foodrecommender.system.R;
import foodrecommender.system.fragments.ProfileFragment;
import foodrecommender.system.fragments.LandingpageFragment;
import foodrecommender.system.fragments.SearchFragment;
import foodrecommender.system.fragments.SettingsFragment;
import foodrecommender.system.global.NetworkConnectivityCallback;

public class LandingpageActivity extends AppCompatActivity {

    private FragmentTransaction homeFragmentTransaction,
            searchFragmentTransaction, profileFragmentTransaction, settingsFragmentTransaction;

    private BottomNavigationView bottomNavigationView;
    private ConnectivityManager.NetworkCallback networkCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landingpage);

        FrameLayout rootView = findViewById(R.id.fragment_container);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkCallback = new NetworkConnectivityCallback(rootView);
        connectivityManager.registerDefaultNetworkCallback(networkCallback);

        // Set up the bottom navigation bar
        bottomNavigationView =  findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.botmenu_home) {
                // Navigate to the LandingpageFragment
                homeFragmentTransaction = getSupportFragmentManager().beginTransaction();
                homeFragmentTransaction.replace(R.id.fragment_container, new LandingpageFragment());
                homeFragmentTransaction.commitNow();
                // Handle home action
                return true;
            } else if (itemId == R.id.botmenu_search) {
                // Handle search action
                // Navigate to the SearchFragment
                searchFragmentTransaction = getSupportFragmentManager().beginTransaction();
                searchFragmentTransaction.replace(R.id.fragment_container, new SearchFragment());
                searchFragmentTransaction.commitNow();
                return true;
            } else if (itemId == R.id.botmenu_profile) {
                // Handle profile action
                // Navigate to the ExerciseFragment
                profileFragmentTransaction = getSupportFragmentManager().beginTransaction();
                profileFragmentTransaction.replace(R.id.fragment_container, new ProfileFragment());
                profileFragmentTransaction.commitNow();
                return true;
            } else if (itemId == R.id.botmenu_settings) {
                // Handle profile action
                // Navigate to the SummaryFragment
                settingsFragmentTransaction = getSupportFragmentManager().beginTransaction();
                settingsFragmentTransaction.replace(R.id.fragment_container, new SettingsFragment());
                settingsFragmentTransaction.commitNow();
                return true;
            } else {
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Find the menu items by their IDs
        MenuItem logoutMenuItem = menu.findItem(R.id.action_logout);
        SharedPreferences sp = getSharedPreferences("user_data", MODE_PRIVATE);
        if (!sp.getBoolean("is_logged_in", false)){
            logoutMenuItem.setIcon(R.drawable.baseline_login_24);
            logoutMenuItem.setTitle("Login");
        } else {
            logoutMenuItem.setIcon(R.drawable.baseline_logout_24);
            logoutMenuItem.setTitle("Logout");
        }
        invalidateOptionsMenu();
        logoutMenuItem.setOnMenuItemClickListener(item -> {
            // Handle settings menu item click
            logoutIconAction();
            return true;
        });

        return true;
    }

    private void logoutIconAction(){
        SharedPreferences sp = getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();

        Intent intent = new Intent (this, SigninActivity.class);
        if (sp.getBoolean("is_logged_in", false)) intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        ed.clear();
        ed.apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}