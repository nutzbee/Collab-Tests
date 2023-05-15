package foodrecommender.system.fragments;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import foodrecommender.system.R;
import foodrecommender.system.activities.SignupActivity;

public class PredictionFragment extends Fragment {

    private View view;
    private TextInputEditText pregnanciesEditText, glucoseEditText, bloodPressureEditText, skinThicknessEditText,
            insulinEditText, bmiEditText, dpfEditText, ageEditText;
    private Button predictButton;
    private Snackbar snackbar;
    private String message;

    private LinearProgressIndicator progressIndicator;
    private TextInputLayout pregnanciesTextInputLayout, glucoseTextInputLayout, bloodPressureTextInputLayout,
            skinThicknessTextInputLayout, insulinTextInputLayout, bmiTextInputLayout, dpfTextInputLayout,
            ageTextInputLayout;
    private int currentEditTextIndex = 0;
    private TextInputLayout[] textInputLayouts;

    private TextInputEditText[] textInputEditTexts;
    private String fragmentTag = "Third Fragment",
    actionBarTitle = "Type-2 Daibetes Records";

    public PredictionFragment() {
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
        view = inflater.inflate(R.layout.fragment_prediction, container, false);

        ((SignupActivity) requireActivity()).updateActionBarTitle(actionBarTitle);

        // TextInputLayouts
        pregnanciesTextInputLayout = view.findViewById(R.id.pregnancies_inputlayout);
        glucoseTextInputLayout = view.findViewById(R.id.glucose_inputlayout);
        bloodPressureTextInputLayout = view.findViewById(R.id.bloodPressure_inputlayout);
        skinThicknessTextInputLayout = view.findViewById(R.id.skinthickness_inputlayout);
        insulinTextInputLayout = view.findViewById(R.id.insulin_inputlayout);
        bmiTextInputLayout = view.findViewById(R.id.bmi_inputlayout);
        dpfTextInputLayout = view.findViewById(R.id.dpf_inputlayout);
        ageTextInputLayout = view.findViewById(R.id.age_inputlayout);

        // TextInputEditTexts
        pregnanciesEditText = view.findViewById(R.id.pregnancies_edittext);
        glucoseEditText = view.findViewById(R.id.glucose_edittext);
        bloodPressureEditText = view.findViewById(R.id.bloodPressure_edittext);
        skinThicknessEditText = view.findViewById(R.id.skinthickness_edittext);
        insulinEditText = view.findViewById(R.id.insulin_edittext);
        bmiEditText = view.findViewById(R.id.bmi_edittext);
        dpfEditText = view.findViewById(R.id.dpf_edittext);
        ageEditText = view.findViewById(R.id.age_edittext);

        predictButton = view.findViewById(R.id.predictme_button);
        progressIndicator = requireActivity().findViewById(R.id.progress_indicator);

        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNextFragment();
                diabetesPredict();
            }
        });

        handleProgressIndicator();
        editTextsActionListener();
        return view;
    }

    private void handleProgressIndicator(){
        if (!progressIndicator.isShown()) {
            progressIndicator.show();
            progressIndicator.setIndeterminate(false);
        }
        progressIndicator.setMax(8);
    }

    private void editTextsActionListener(){

        predictButton.setEnabled(false);

        textInputLayouts = new TextInputLayout[] {
                pregnanciesTextInputLayout,
                glucoseTextInputLayout,
                bloodPressureTextInputLayout,
                skinThicknessTextInputLayout,
                insulinTextInputLayout,
                bmiTextInputLayout,
                dpfTextInputLayout,
                ageTextInputLayout
        };

        textInputEditTexts = new TextInputEditText[] {
                pregnanciesEditText,
                glucoseEditText,
                bloodPressureEditText,
                skinThicknessEditText,
                insulinEditText,
                bmiEditText,
                dpfEditText,
                ageEditText
        };

        for (int i = 0; i < textInputLayouts.length; i++) {
            final int currentIndex = i;
            final int nextIndex = i + 1;
            if (nextIndex < textInputLayouts.length) {
                textInputEditTexts[currentIndex].setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            if (!textInputEditTexts[currentIndex].getText().toString().isEmpty()) {
                                textInputLayouts[currentIndex].setVisibility(View.GONE);
                                textInputLayouts[nextIndex].setVisibility(View.VISIBLE);
                                textInputEditTexts[nextIndex].requestFocus();
                                progressIndicator.setProgress(nextIndex);
                                currentEditTextIndex = nextIndex;
                            } else {
                                message = "Do not leave empty fields";
                                snackBarStrings();
                            }
                            if (currentEditTextIndex == 7){
                                textInputEditTexts[nextIndex].addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                    }

                                    @Override
                                    public void afterTextChanged(Editable editable) {
                                        if (editable.length() > 0) {
                                            try {
                                                int age = Integer.parseInt(editable.toString());
                                                if (age > 90 || age < 0) {
                                                    predictButton.setEnabled(false);
                                                    ageEditText.setError("Invalid age");
                                                } else {
                                                    predictButton.setEnabled(true);
                                                    ageEditText.setError(null);
                                                }
                                            } catch (NumberFormatException e) {
                                                predictButton.setEnabled(false);
                                                ageEditText.setError("Invalid input");
                                            }
                                        } else {
                                            predictButton.setEnabled(false);
                                            ageEditText.setError("Age is required");
                                        }
                                    }
                                });
                            }
                            return true;
                        }
                        return false;
                    }
                });
            }
        }
    }

    private void nextSet(){
        FragmentTransaction signUpFragmentTransaction = getParentFragmentManager().beginTransaction();
        signUpFragmentTransaction.replace(R.id.signup_fragment_container, new RecommendationFragment());
        signUpFragmentTransaction.addToBackStack(fragmentTag);
        signUpFragmentTransaction.commit();
    }

    private void snackBarStrings(){
        int duration = Snackbar.LENGTH_SHORT;
        snackbar = Snackbar.make(view, message, duration);

        snackbar.setText(message);
        snackbar.show();
    }

    private void showNextFragment(){
        if (isAdded()) {
            // Get input values from TextInputEditTexts
            int pregnancies = Integer.parseInt(pregnanciesEditText.getText().toString());
            int glucose = Integer.parseInt(glucoseEditText.getText().toString());
            int bloodPressure = Integer.parseInt(bloodPressureEditText.getText().toString());
            int skinThickness = Integer.parseInt(skinThicknessEditText.getText().toString());
            int insulin = Integer.parseInt(insulinEditText.getText().toString());
            float bmi = Float.parseFloat(bmiEditText.getText().toString());
            float dpf = Float.parseFloat(dpfEditText.getText().toString());
            int age = Integer.parseInt(ageEditText.getText().toString());

            SharedPreferences sp = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = sp.edit();
            ed.putInt("pregnancies", pregnancies);
            ed.putInt("glucose", glucose);
            ed.putInt("bloodPressure", bloodPressure);
            ed.putInt("skinThickness", skinThickness);
            ed.putInt("insulin", insulin);
            ed.putFloat("bmi", bmi);
            ed.putFloat("dpf", dpf);
            ed.putInt("age", age);
            ed.apply();

            // Proceed to last page of Registration
            nextSet();

        }
    }

    private void diabetesPredict(){
        if (isAdded()) {
            // String url = "http://192.168.0.41:5000/predict";
            String url = getString(R.string.predict_url);
            JSONObject data = new JSONObject();
            // Get input values from TextInputEditTexts
            int pregnancies = Integer.parseInt(pregnanciesEditText.getText().toString());
            int glucose = Integer.parseInt(glucoseEditText.getText().toString());
            int bloodPressure = Integer.parseInt(bloodPressureEditText.getText().toString());
            int skinThickness = Integer.parseInt(skinThicknessEditText.getText().toString());
            int insulin = Integer.parseInt(insulinEditText.getText().toString());
            float bmi = Float.parseFloat(bmiEditText.getText().toString());
            float dpf = Float.parseFloat(dpfEditText.getText().toString());
            int age = Integer.parseInt(ageEditText.getText().toString());

            SharedPreferences sp = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = sp.edit();
            ed.putInt("pregnancies", pregnancies);
            ed.putInt("glucose", glucose);
            ed.putInt("bloodPressure", bloodPressure);
            ed.putInt("skinThickness", skinThickness);
            ed.putInt("insulin", insulin);
            ed.putFloat("bmi", bmi);
            ed.putFloat("dpf", dpf);
            ed.putInt("age", age);
            ed.apply();

            try {
                data.put("pregnancies", pregnancies);
                data.put("glucose", glucose);
                data.put("blood_pressure", bloodPressure);
                data.put("skin_thickness", skinThickness);
                data.put("insulin", insulin);
                data.put("bmi", bmi);
                data.put("diabetes_pedigree_function", dpf);
                data.put("age", age);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data, response -> {
                try {
                    message = response.getString("diabetes_result");
                    snackBarStrings();

                    ed.putString("status", message);
                    ed.apply();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                error.printStackTrace();
            });

            Volley.newRequestQueue(requireContext()).add(jsonObjectRequest);
        }
    }

}