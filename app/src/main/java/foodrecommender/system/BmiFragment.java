package foodrecommender.system;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import com.google.android.material.textview.MaterialTextView;

public class BmiFragment extends Fragment {
    private View view;
    private MaterialTextView resultTv, heightTv, weightTv;
    private NumberPicker heightPicker, weightPicker;
    private Button calculateBt;
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
        calculateBt = view.findViewById(R.id.confirmBmiButton);

        heightPicker.setMinValue(100);
        heightPicker.setMaxValue(250);
        heightPicker.setValue(130);
        weightPicker.setMinValue(20);
        weightPicker.setMaxValue(200);
        weightPicker.setValue(40);

        calculateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateBMI();
            }
        });

        return view;
    }

    private void calculateBMI(){
        height = heightPicker.getValue();
        weight = weightPicker.getValue();

        double heightMeters = height / 100.0;
        double bmi = weight / (heightMeters * heightMeters);

        resultTv.setText(String.format("Your BMI is %.2f", bmi));
    }
}