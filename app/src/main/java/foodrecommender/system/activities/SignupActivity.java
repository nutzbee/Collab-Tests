package foodrecommender.system.activities;

import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.io.File;

import foodrecommender.system.BuildConfig;
import foodrecommender.system.R;
import foodrecommender.system.fragments.BaseSignupFragment;

public class SignupActivity extends AppCompatActivity {

    private LinearProgressIndicator progressIndicator;
    private String fragmentTag = "Sign Up";
    private RelativeLayout relativeLayout;
    private boolean isSnackbarShown = false;
    private FragmentManager fragmentManager;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        themeCheck();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        progressIndicator = findViewById(R.id.progress_indicator);
        relativeLayout = findViewById(R.id.signup_rel);
        fragmentManager = getSupportFragmentManager();
        currentFragment = fragmentManager.findFragmentById(R.id.signup_fragment_container);

        setupNotification();
        updateCheck();
    }

    private void updateCheck() {
        progressIndicator.show();
        updateActionBarTitle("Checking for updates..");

        String url = getString(R.string.get_updates_url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                if (isSnackbarShown) {
                    Snackbar.make(relativeLayout, "Connected", Snackbar.LENGTH_SHORT).show();
                }
                String title = response.getString("title");
                String message = response.getString("message");
                String verCode = response.getString("ver_code");
                String verName = response.getString("ver_name");

                if (response.has("maintenance")) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(SignupActivity.this);
                    builder.setTitle(title);
                    builder.setMessage(message);
                    builder.setOnCancelListener(dialogInterface -> finish());
                    builder.create().show();
                } else if (!String.valueOf(BuildConfig.VERSION_CODE).equals(verCode)) {
                    Log.d("TAG", "updateCheck: " + message);
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(SignupActivity.this);
                    builder.setTitle(title);
                    builder.setMessage("Current: " + BuildConfig.VERSION_NAME
                            + "\nNew: " + verName + "\n\n" + message);
                    builder.setOnCancelListener(dialogInterface -> finish());
                    builder.setPositiveButton("Download", (dialogInterface, i) -> {
                        sendDownloadRequest();
                        finish();
                    });
                    builder.create().show();
                } else {
                    Log.d("TAG", "updateCheck: " + message);
                    updateActionBarTitle("Signing you in..");
                    userCheck();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // Handle error here
            error.printStackTrace();

            Snackbar snackbar = Snackbar.make(relativeLayout, "Disconnected", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Refresh", view -> updateCheck());
            snackbar.show();
            isSnackbarShown = true;
        });

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void sendDownloadRequest() {
        String url = getString(R.string.send_download_request_url);

        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());

        // Create a file to save the APK
        File file = new File(directory, getString(R.string.downloaded_file_name));

        // Initialize a new DownloadManager instance
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        // Create a download request
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                .setDestinationUri(Uri.fromFile(file))
                .setTitle("Diabetes")
                .setDescription("Downloading")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true);

        // Enqueue the download request
        long downloadId = downloadManager.enqueue(request);

        Toast.makeText(getApplicationContext(), "Download started", Toast.LENGTH_SHORT).show();
    }

    private void setupNotification() {
        // Create a notification channel (required for Android 8.0 Oreo and above)
        boolean enabledNotifs = checkReminders();
        createNotificationChannel();

        // Build the notification
        NotificationCompat.Builder exercise = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.red_app_icon_main) // Replace with your own notification icon
                .setContentTitle("Exercise Reminder")
                .setContentText("Exercise helps us to maintain our healthiness so don't forget to stretch your body and be active today.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationCompat.Builder medicine = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.red_app_icon_main) // Replace with your own notification icon
                .setContentTitle("Medicine Reminder")
                .setContentText("A friendly reminder to take your medicines today.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (enabledNotifs) {
            // Display the notification
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, exercise.build());
            notificationManager.notify(2, medicine.build());
        }
    }

    private void createNotificationChannel() {
        CharSequence name = "My Channel";
        String description = "Channel Description";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("channel_id", name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void themeCheck() {
        boolean isDarkMode = checkTheme();

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void userCheck() {
        // Check the login status when the app launches or when navigating to a specific page
        boolean isLoggedIn = checkLoginStatus();

        if (isLoggedIn) {
            // User is already logged in, redirect to the landing page
            Intent intent = new Intent(SignupActivity.this, LandingpageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else {
            // User is not logged in, allow them to proceed to the current page
            // Continue with the normal flow of the current page
            progressIndicator.hide();
            relativeLayout.setVisibility(View.VISIBLE);
            updateActionBarTitle(getString(R.string.signUpLabel));
            getTheFragment();
        }
    }

    // Method to check the login status from SharedPreferences or any other storage mechanism
    private boolean checkLoginStatus() {
        // Retrieve the login status from SharedPreferences or other storage
        SharedPreferences sp = getSharedPreferences("user_data", MODE_PRIVATE);
        return sp.getBoolean("is_logged_in", false);
    }

    private boolean checkTheme() {
        SharedPreferences sp = getSharedPreferences("theme_mode", MODE_PRIVATE);
        return sp.getBoolean("isDarkMode", false);
    }

    private boolean checkReminders() {
        SharedPreferences sp = getSharedPreferences("theme_mode", MODE_PRIVATE);
        return sp.getBoolean("isExerciseReminderEnabled", false);
    }

    private void getTheFragment() {
        FragmentTransaction signUpFragmentTransaction = getSupportFragmentManager().beginTransaction();
        signUpFragmentTransaction.replace(R.id.signup_fragment_container, new BaseSignupFragment());
        signUpFragmentTransaction.addToBackStack(fragmentTag);
        signUpFragmentTransaction.commit();
    }

    public void updateActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    public void onBackPressed() {
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
        } else {
            super.onBackPressed();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        if (progressIndicator.isShown() || progressIndicator.getProgress() > 0) {
            progressIndicator.setProgress(0);
            progressIndicator.hide();
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();

                // Get the previous fragment from the back stack
                FragmentManager.BackStackEntry backStackEntry = getSupportFragmentManager().getBackStackEntryAt(
                        getSupportFragmentManager().getBackStackEntryCount() - 1);
                String previousFragmentTag = backStackEntry.getName();

                // Update the ActionBar title with the previous fragment's title
                updateActionBarTitle(previousFragmentTag);
            } else {
                super.onBackPressed();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }

        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
