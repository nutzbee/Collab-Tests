package foodrecommender.system.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import com.google.android.material.textview.MaterialTextView;

import foodrecommender.system.R;

public class BmiFragment extends Fragment {
    private View view;
    private MaterialTextView resultTv, heightTv, weightTv;
    private NumberPicker heightPicker, weightPicker;
    private int height, weight;

    public BmiFragment() {
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
        view = inflater.inflate(R.layout.fragment_bmi, container, false);

        resultTv = view.findViewById(R.id.bmi_result_textview);
        heightTv = view.findViewById(R.id.height_input_textview);
        weightTv = view.findViewById(R.id.weight_input_textview);
        heightPicker = view.findViewById(R.id.height_picker);
        weightPicker = view.findViewById(R.id.weight_picker);

        numberPicker();
        calculateBMI();

        return view;
    }

    private void numberPicker(){
        heightPicker.setMinValue(100);
        heightPicker.setMaxValue(250);
        heightPicker.setValue(130);

        weightPicker.setMinValue(20);
        weightPicker.setMaxValue(200);
        weightPicker.setValue(40);

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

    private void calculateBMI(){
        height = heightPicker.getValue();
        weight = weightPicker.getValue();

        double heightMeters = height / 100.0;
        double bmi = weight / (heightMeters * heightMeters);

        resultTv.setText(String.format("BMI: %.2f", bmi));
    }
}