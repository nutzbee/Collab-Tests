package foodrecommender.system;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LandingpageFragment extends Fragment {
    private View view;
    private TextView welcomeText;
    private CardView welcomeCardView;
    private TabLayout tabLayout;
    private FrameLayout frameLayout;
    private TabLayout.Tab sampleFoodsTab, recommendationTab, diabeticCheckTab;
    private Snackbar snackbar;
    private FragmentTransaction sampleFoodsFragmentTransaction,
            recommendationFragmentTransaction, diaCheckFragmentTransaction, settingsFragmentTransaction;
    private String message;

    public LandingpageFragment() {
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
        view = inflater.inflate(R.layout.fragment_landingpage, container, false);

        welcomeText = view.findViewById(R.id.welcome_text);
        welcomeCardView = view.findViewById(R.id.material_landing_cv3);
        frameLayout = view.findViewById(R.id.fragment_container_home);
        loadFrags();

        return view;
    }

    private void loadFrags(){

    }

    private void snackBarStrings(){
        //message = "Sample Foods";
        int duration = Snackbar.LENGTH_SHORT;
        snackbar = Snackbar.make(view, message, duration);

        snackbar.setText(message);
        snackbar.show();
    }

    private void setTabLayoutItems(){
        sampleFoodsTab = tabLayout.newTab();

        sampleFoodsTab = tabLayout.newTab();
        sampleFoodsTab.setContentDescription("Sample Foods");
        sampleFoodsTab.setIcon(R.drawable.baseline_home_24);
        tabLayout.addTab(sampleFoodsTab);
    }

    private void setTabItemsListener(){

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        // Tab 1 is selected
                        sampleFoodsFragmentTransaction = getChildFragmentManager().beginTransaction();
                        sampleFoodsFragmentTransaction.replace(R.id.fragment_container_home, new SampleFoodsFragment());
                        sampleFoodsFragmentTransaction.commit();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}