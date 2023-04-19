package foodrecommender.system;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONException;
import org.json.JSONObject;

public class PredictionFragment extends Fragment {

    private View view;
    private TextInputEditText pregnanciesEditText, glucoseEditText, bloodPressureEditText, skinThicknessEditText,
            insulinEditText, bmiEditText, dpfEditText, ageEditText;
    private MaterialTextView resultTextView;
    private Button predictButton;
    private Snackbar snackbar;
    private String message;

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
        pregnanciesEditText = view.findViewById(R.id.pregnancies_edittext);
        glucoseEditText = view.findViewById(R.id.glucose_edittext);
        bloodPressureEditText = view.findViewById(R.id.bloodPressure_edittext);
        skinThicknessEditText = view.findViewById(R.id.skinthickness_edittext);
        insulinEditText = view.findViewById(R.id.insulin_edittext);
        bmiEditText = view.findViewById(R.id.bmi_edittext);
        dpfEditText = view.findViewById(R.id.dpf_edittext);
        ageEditText = view.findViewById(R.id.age_edittext);
        resultTextView = view.findViewById(R.id.result_textview);
        predictButton = view.findViewById(R.id.predictme_button);

        snackBarStrings();

        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diabetesPredict();
            }
        });
        return view;
    }

    private void snackBarStrings(){
        // Replace with your desired message
        message = "Diabetes Check";
        int duration = Snackbar.LENGTH_SHORT; // Specify the duration for the Snackbar, either LENGTH_SHORT or LENGTH_LONG
        snackbar = Snackbar.make(view, message, duration);

        snackbar.setText(message);
        snackbar.show();
    }

    private void diabetesPredict(){
        // Check if any of the fields are empty
        if (pregnanciesEditText.getText().toString().isEmpty() || glucoseEditText.getText().toString().isEmpty() ||
                bloodPressureEditText.getText().toString().isEmpty() || skinThicknessEditText.getText().toString().isEmpty() ||
                insulinEditText.getText().toString().isEmpty() || bmiEditText.getText().toString().isEmpty() ||
                dpfEditText.getText().toString().isEmpty() || ageEditText.getText().toString().isEmpty()) {
            // Display an error message or perform appropriate action
            String message = "Please fill all the blanks";
            resultTextView.setText(message);
            resultTextView.setTextColor(getResources().getColor(R.color.text_color_red, getContext().getTheme()));
            resultTextView.setAlpha(1.0f);
            resultTextView.requestFocus();
        } else {
            // String url = "http://192.168.0.41:5000/predict"; // Replace with your server IP address
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
                    String diabetesResult = response.getString("diabetes_result");
                    //Display the result in the textview

                    resultTextView.setText(diabetesResult);
                    if (resultTextView.getAlpha() == 0f) {
                        resultTextView.setAlpha(1.0f);
                    }
                    resultTextView.setTextColor(getResources().getColor(R.color.primary, getContext().getTheme()));
                    resultTextView.requestFocus();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                error.printStackTrace();
            });

            Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
        }
    }
}