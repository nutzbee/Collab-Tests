package foodrecommender.system.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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

        showHome();
    }

    private void showHome(){
        // Navigate to the LandingpageFragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof SettingsFragment){
            bottomNavigationView.setSelectedItemId(R.id.botmenu_settings);
        } else if (currentFragment instanceof ProfileFragment){
            bottomNavigationView.setSelectedItemId(R.id.botmenu_profile);
        } else if (currentFragment instanceof SearchFragment){
            bottomNavigationView.setSelectedItemId(R.id.botmenu_search);
        } else {
            bottomNavigationView.setSelectedItemId(R.id.botmenu_home);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Find the menu items by their IDs
        MenuItem iconMenuItem = menu.findItem(R.id.action_logout);
        SharedPreferences sp = getSharedPreferences("user_data", MODE_PRIVATE);
        if (!sp.getBoolean("is_logged_in", false)){
            iconMenuItem.setIcon(R.drawable.baseline_login_24);
            iconMenuItem.setTitle("Login");
        } else {
            iconMenuItem.setIcon(R.drawable.baseline_logout_24);
            iconMenuItem.setTitle("Logout");
        }
        invalidateOptionsMenu();
        iconMenuItem.setOnMenuItemClickListener(item -> {
            // Handle settings menu item click
            iconItemAction();
            return true;
        });

        return true;
    }

    private void iconItemAction(){
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