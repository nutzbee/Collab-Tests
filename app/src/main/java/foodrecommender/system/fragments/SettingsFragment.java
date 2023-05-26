package foodrecommender.system.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textview.MaterialTextView;

import foodrecommender.system.R;

public class SettingsFragment extends Fragment {
    private View view;
    private MaterialSwitch themeSwitch, exerciseSwitch;
    private Button contactUs, viewProfile, updatePreferences, home, search, tools;
    private static final String EMAIL_ADDRESS = "diabeates@gmail.com";
    private static final String EMAIL_SUBJECT = "Feedback and Suggestions";
    private BottomNavigationView bottomNavigationView;
    private TextView deviceInformation;
    private MaterialTextView resultTv, heightTv, weightTv;
    private NumberPicker heightPicker, weightPicker;
    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        SharedPreferences sp = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        boolean isLoggedIn = sp.getBoolean("is_logged_in", false);

        themeSwitch = view.findViewById(R.id.switchTheme);
        contactUs = view.findViewById(R.id.buttonContactUs);
        updatePreferences = view.findViewById(R.id.buttonUpdatePreferences);
        viewProfile = view.findViewById(R.id.buttonViewProfile);
        home = view.findViewById(R.id.buttonHome);
        search = view.findViewById(R.id.buttonSearch);
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        deviceInformation = view.findViewById(R.id.deviceInformationTextView);
        tools = view.findViewById(R.id.buttonBMICalculator);
        exerciseSwitch = view.findViewById(R.id.switchExerciseReminders);

        if (!isLoggedIn){
            home.setEnabled(false);
            search.setEnabled(false);
            viewProfile.setEnabled(false);
            updatePreferences.setEnabled(false);
        } else {
            home.setEnabled(true);
            search.setEnabled(true);
            viewProfile.setEnabled(true);
            updatePreferences.setEnabled(true);
        }

        tools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCalculator();
            }
        });

        deviceInformation.setText(String.format(
                "%s %s Android %s", Build.MANUFACTURER, Build.MODEL, Build.VERSION.RELEASE));

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToSearch();
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToHome();
            }
        });

        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToProfile();
            }
        });

        updatePreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToPreferences();
            }
        });

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                composeEmail(EMAIL_ADDRESS, EMAIL_SUBJECT);
            }
        });
        switchActions();
        return view;
    }

    private void showCalculator() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.fragment_bmi, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        resultTv = bottomSheetView.findViewById(R.id.bmi_result_textview);
        heightTv = bottomSheetView.findViewById(R.id.height_input_textview);
        weightTv = bottomSheetView.findViewById(R.id.weight_input_textview);
        heightPicker = bottomSheetView.findViewById(R.id.height_picker);
        weightPicker = bottomSheetView.findViewById(R.id.weight_picker);

        numberPicker();
        calculateBMI();
    }

    private void numberPicker(){
        if (isAdded()) {
            SharedPreferences sp = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
            float weight = sp.getFloat("weight", 0.0f);

            heightPicker.setMinValue(100);
            heightPicker.setMaxValue(250);
            heightPicker.setValue(130);

            weightPicker.setMinValue(20);
            weightPicker.setMaxValue(200);
            weightPicker.setValue((int) weight);

            heightPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                    calculateBMI();
                }
            });

            weightPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                    calculateBMI();
                }
            });
        }
    }

    private void calculateBMI(){
        int height, weight;
        height = heightPicker.getValue();
        weight = weightPicker.getValue();

        double heightMeters = height / 100.0;
        double bmi = weight / (heightMeters * heightMeters);

        resultTv.setText(String.format("BMI: %.2f", bmi));
    }

    private void navigateToHome() {
        bottomNavigationView.setSelectedItemId(R.id.botmenu_home);
    }

    private void navigateToSearch() {
        bottomNavigationView.setSelectedItemId(R.id.botmenu_search);
    }

    private void navigateToProfile() {
        bottomNavigationView.setSelectedItemId(R.id.botmenu_profile);
    }

    private void navigateToPreferences(){
        if (isAdded()) {
            SharedPreferences sp = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = sp.edit();
            ed.putBoolean("isPreferences", true);
            ed.apply();

            bottomNavigationView.setSelectedItemId(R.id.botmenu_profile);
        }
    }

    private void composeEmail(String emailAddress, String emailSubject) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
        intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    private void switchActions(){
        if (isAdded()) {
            SharedPreferences sp = requireActivity().getSharedPreferences("theme_mode", Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = sp.edit();
            boolean enabled = sp.getBoolean("isDarkMode", false);
            boolean exerciseReminderEnabled = sp.getBoolean("isExerciseReminderEnabled", false);
            themeSwitch.setChecked(enabled);
            exerciseSwitch.setChecked(exerciseReminderEnabled);

            exerciseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    ed.putBoolean("isExerciseReminderEnabled", b);
                    ed.apply();
                }
            });
            themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    // Handle switch state change
                    if (b) {
                        // Enable dark mode
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        // Enable light mode
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                    ed.putBoolean("isDarkMode", b);
                    ed.apply();
                }
            });
        }
    }
}