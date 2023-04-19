package foodrecommender.system;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private TextView recommended_foods;
    private Button recobtn;
    private EditText calorie_req,fod_allergy,nutrient_req;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recommended_foods = findViewById(R.id.recommended_foods);
        recobtn = findViewById(R.id.recommend_button);
        calorie_req = findViewById(R.id.calorie_req);
        fod_allergy = findViewById(R.id.food_allergy);
        nutrient_req = findViewById(R.id.nutrient_req);

        //detectDiabetes();
        recobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //recommend();
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);

                // Apply a smooth transition animation
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }
    private void recommend(){
        String url = "http://192.168.0.41:5000/recommend";
        JSONObject data = new JSONObject();
        String calorie_reqt = "1000";
        String food_allergyt = "MUNG";
        String nutrient_reqt = "Fiber_TD_(g)";
        calorie_req.setText(calorie_reqt);
        fod_allergy.setText(food_allergyt);
        nutrient_req.setText(nutrient_reqt);

        try {
            data.put("calorie_req", calorie_req.getText().toString());
            data.put("food_allergy", fod_allergy.getText().toString());
            data.put("nutrient_req", nutrient_req.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data, response -> {
            try {
                JSONArray recommendedFoods = response.getJSONArray("recommended_foods");
                String food1 = recommendedFoods.getString(0);
                String food2 = recommendedFoods.getString(1);
                String food3 = recommendedFoods.getString(2);
                // Display the recommended foods to the user
                String recommended_foods_combination = "1. " + food1 + "\n2. " + food2 + "\n3. " + food3;
                recommended_foods.setText(recommended_foods_combination);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            error.printStackTrace();
        });

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void detectDiabetes(){
        String url = "http://192.168.0.41:5000/predict";
        JSONObject data = new JSONObject();
        String pregnancies = "7";
        String glucose = "103";
        String blood_pressure = "66";
        String skin_thickness = "32";
        String insulin = "0";
        String bmi = "39.1";
        String diabetes_pedigree_function = "0.344";
        String age = "31";

        try {
            data.put("pregnancies", pregnancies);
            data.put("glucose", glucose);
            data.put("blood_pressure", blood_pressure);
            data.put("skin_thickness", skin_thickness);
            data.put("insulin", insulin);
            data.put("bmi", bmi);
            data.put("diabetes_pedigree_function", diabetes_pedigree_function);
            data.put("age", age);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data, response -> {
            try {
                String diabetesResult = response.getString("diabetes_result");
                //String diabeatResult = diabetesResult.getString(0);
                // Display the recommended foods to the user
                recommended_foods.setText(diabetesResult);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            error.printStackTrace();
        });

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

}