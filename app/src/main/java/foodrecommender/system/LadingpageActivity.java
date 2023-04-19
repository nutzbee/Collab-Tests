package foodrecommender.system;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LadingpageActivity extends AppCompatActivity {

    private FragmentTransaction homeFragmentTransaction,
            searchFragmentTransaction, profileFragmentTransaction, settingsFragmentTransaction;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ladingpage);

        // Set up the bottom navigation bar
        bottomNavigationView =  findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.botmenu_home:
                    // Navigate to the LandingpageFragment
                    homeFragmentTransaction = getSupportFragmentManager().beginTransaction();
                    homeFragmentTransaction.replace(R.id.fragment_container, new LandingpageFragment());
                    homeFragmentTransaction.commit();
                    // Handle home action
                    return true;
                case R.id.botmenu_search:
                    // Handle search action
                    // Navigate to the SearchFragment
                    searchFragmentTransaction = getSupportFragmentManager().beginTransaction();
                    searchFragmentTransaction.replace(R.id.fragment_container, new SearchFragment());
                    searchFragmentTransaction.commit();
                    return true;
                case R.id.botmenu_profile:
                    // Handle profile action
                    // Navigate to the ExerciseFragment
                    profileFragmentTransaction = getSupportFragmentManager().beginTransaction();
                    profileFragmentTransaction.replace(R.id.fragment_container, new ExerciseFragment());
                    profileFragmentTransaction.commit();
                    return true;
                case R.id.botmenu_settings:
                    // Handle profile action
                    // Navigate to the SummaryFragment
                    settingsFragmentTransaction = getSupportFragmentManager().beginTransaction();
                    settingsFragmentTransaction.replace(R.id.fragment_container, new SummaryFragment());
                    settingsFragmentTransaction.commit();
                    return true;
                default:
                    return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Find the menu items by their IDs
        MenuItem settingsMenuItem = menu.findItem(R.id.action_settings);

        settingsMenuItem.setOnMenuItemClickListener(item -> {
            // Handle settings menu item click
            openSettingsActivity();
            return true;
        });

        return true;
    }

    private void openSettingsActivity(){
    }
}