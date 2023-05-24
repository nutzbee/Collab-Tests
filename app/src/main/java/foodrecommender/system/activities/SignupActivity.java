package foodrecommender.system.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import foodrecommender.system.BuildConfig;
import foodrecommender.system.R;
import foodrecommender.system.fragments.BaseSignupFragment;

public class SignupActivity extends AppCompatActivity {

    LinearProgressIndicator progressIndicator;
    private String fragmentTag = "Sign Up";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        progressIndicator = findViewById(R.id.progress_indicator);

        getTheFragment();
        setupNotification();
        updateCheck();
    }

    private void updateCheck() {
        String url = getString(R.string.get_updates_url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                String message = response.getString("message");
                String ver_code = response.getString("ver_code");

                if (BuildConfig.VERSION_CODE != Integer.parseInt(ver_code)) {
                    Log.d("TAG", "updateCheck: " + message);
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                    builder.setTitle(message);
                    builder.setMessage("Your version is " + BuildConfig.VERSION_CODE + " and the new version is " + ver_code);
                    builder.setCancelable(false);
                    builder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            sendDownloadRequest();
                            finish();
                        }
                    });
                    builder.create();
                    builder.show();
                } else {
                    Log.d("TAG", "updateCheck: App up to date");
                    userCheck();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace);

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void sendDownloadRequest() {
        String url = getString(R.string.send_download_request_url);

        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());

        // Create a file to save the APK
        File file = new File(directory, "Diabeates - Beat-Cream.apk");

        // Initialize a new DownloadManager instance
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        // Create a download request
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                .setDestinationUri(Uri.fromFile(file))
                .setTitle("Diabeates")
                .setDescription("Downloading")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true);

        // Enqueue the download request
        long downloadId = downloadManager.enqueue(request);

        Toast.makeText(this, "Download started", Toast.LENGTH_SHORT).show();
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

        if (enabledNotifs){
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

    private void userCheck(){
        progressIndicator.show();
        // Check the login status when the app launches or when navigating to a specific page
        boolean isLoggedIn = checkLoginStatus();
        boolean isDarkMode = checkTheme();

        if (isDarkMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

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

    private boolean checkTheme(){
        SharedPreferences sp = getSharedPreferences("theme_mode", MODE_PRIVATE);
        return sp.getBoolean("isDarkMode", false);
    }

    private boolean checkReminders(){
        SharedPreferences sp = getSharedPreferences("theme_mode", MODE_PRIVATE);
        return sp.getBoolean("isExerciseReminderEnabled", false);
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