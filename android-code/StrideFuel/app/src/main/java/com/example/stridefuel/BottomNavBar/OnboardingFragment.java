package com.example.stridefuel.BottomNavBar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.stridefuel.Constants;
import com.example.stridefuel.R;
import com.example.stridefuel.RequestHandler;
import com.example.stridefuel.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OnboardingFragment extends Fragment {
    private CardView maleCard, femaleCard;
    private TextView heightTextView, weightTextView,ageTextView;
    private SeekBar heightSeekBar;
    private ImageView weightAddButton, weightMinusButton,ageAddButton,ageMinusButton;
    private Button calculateButton;

    private String selectedGender = "male";
    private int height = 170;
    private int weight = 60;
    private int age = 18;

    private static final int MIN_HEIGHT = 100;
    private static final int MAX_HEIGHT = 250;
    private static final int MIN_WEIGHT = 30;
    private static final int MIN_AGE = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_onboarding, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        intializeViews(view);

        setIntialValues();

        setUpEventListeners();

    }
    private void intializeViews(View view)
    {
        maleCard = view.findViewById(R.id.malecard);
        femaleCard = view.findViewById(R.id.femalecard);

        heightTextView = view.findViewById(R.id.heightTextView);
        heightSeekBar = view.findViewById(R.id.heightSeekBar);

        weightTextView = view.findViewById(R.id.weightTextView);
        weightAddButton = view.findViewById(R.id.weightAddButton);
        weightMinusButton = view.findViewById(R.id.weightMinusButton);

        ageTextView = view.findViewById(R.id.ageTextView);
        ageAddButton = view.findViewById(R.id.ageAddButton);
        ageMinusButton = view.findViewById(R.id.ageMinusButton);

        calculateButton = view.findViewById(R.id.calculateButton);
    }

    private void setIntialValues()
    {
        // Set initial height
        heightTextView.setText(String.valueOf(height));

        // Configuring seek bar
        heightSeekBar.setMax(MAX_HEIGHT - MIN_HEIGHT);
        heightSeekBar.setProgress(height - MIN_HEIGHT);

        // Set initial weight
        weightTextView.setText(String.valueOf(weight));

        // Set initial Age
        ageTextView.setText(String.valueOf(age));

        // Set initial gender selection
        maleCard.setSelected(true);
        femaleCard.setSelected(false);
    }

    private void setUpEventListeners()
    {
        maleCard.setOnClickListener(v -> {
                maleCard.setSelected(true);
                femaleCard.setSelected(false);
                selectedGender = "male";
        });

        femaleCard.setOnClickListener(v ->
        {
            femaleCard.setSelected(true);
            maleCard.setSelected(false);
            selectedGender = "female";
        });

        heightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                height = progress + MIN_HEIGHT;
                heightTextView.setText(String.valueOf(height));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // not needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // not needed
            }
        });

        weightAddButton.setOnClickListener(v -> {
            weight++;
            weightTextView.setText(String.valueOf(weight));
        });

        weightMinusButton.setOnClickListener(v -> {
            if (weight > MIN_WEIGHT)
            {
                weight--;
                weightTextView.setText(String.valueOf(weight));
            }
        });

        ageAddButton.setOnClickListener(v -> {
            age++;
            ageTextView.setText(String.valueOf(age));
        });

        ageMinusButton.setOnClickListener(v -> {
            if (age > MIN_AGE)
            {
                age--;
                ageTextView.setText(String.valueOf(age));
            }
        });

        calculateButton.setOnClickListener(v -> {
            calculateBMR();
        });
    }
    private void calculateBMR() {
        double bmr;
        // Mifflin-St Jeor Equation for BMR calculation
        if (selectedGender.equals("male"))
            bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5;
        else
            bmr = (10 * weight) + (6.25 * height) - (5 * age) - 161;

        // Round BMR to 2 decimal places
        double bmrResult = Math.round(bmr * 100.0) / 100.0;

        // Calculate daily calorie goal using an activity factor
        int dailyCalorieGoal = (int) Math.round(bmrResult * 1.55);

        // Calculate macro calories using percentages (as doubles)
        double proteinCalories = dailyCalorieGoal * 0.15; // 15% protein
        double fatCalories = dailyCalorieGoal * 0.30;     // 30% fat
        double carbsCalories = dailyCalorieGoal * 0.55;     // 55% carbs

        // Convert calories to grams using the known kcal per gram values,
        // and round the result to the nearest integer.
        int proteinGrams = (int) Math.round(proteinCalories / 4.0); // 4 kcal per gram of protein
        int fatGrams = (int) Math.round(fatCalories / 9.0);         // 9 kcal per gram of fat
        int carbsGrams = (int) Math.round(carbsCalories / 4.0);       // 4 kcal per gram of carbs

        // Calculate total calories based on the macro grams obtained
        int calculatedMacroCalories = (proteinGrams * 4) + (fatGrams * 9) + (carbsGrams * 4);

        // If there is a discrepancy due to rounding, adjusting carbs (or any macro) to match the total calorie goal
        if (calculatedMacroCalories < dailyCalorieGoal) {
            int diff = dailyCalorieGoal - calculatedMacroCalories;
            // Adding extra grams to carbs (4 kcal per gram)
            int extraGrams = (int) Math.round(diff / 4.0);
            carbsGrams += extraGrams;
        } else if (calculatedMacroCalories > dailyCalorieGoal) {
            int diff = calculatedMacroCalories - dailyCalorieGoal;
            // Remove excess grams from carbs
            int reduceGrams = (int) Math.round(diff / 4.0);
            carbsGrams = Math.max(0, carbsGrams - reduceGrams);
        }


        String username = SharedPrefManager.getInstance(requireActivity()).getUserName();
        // Sending calorie goal to the php
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_UPDATE_CALORIE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            Log.d("Successfully updated calorie: ", message);
                        } catch (JSONException e) {
                            Log.e("VolleyResponse", "JSON parse error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (!isAdded() || getActivity() == null) return;
                        Log.e("VolleyError", error.getMessage() != null ? error.getMessage() : "Unknown error");
                        Toast.makeText(requireActivity(), "Error updating calorie goal", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("calorie_goal", String.valueOf(dailyCalorieGoal));
                return params;
            }
        };
        RequestHandler.getInstance(requireActivity()).addToRequestQueue(stringRequest);

        // Saving the data locally and navigating to the next screen
        savedDataAndNavigate(dailyCalorieGoal, proteinGrams, fatGrams, carbsGrams, selectedGender, weight, age, height, username);
    }


    private void savedDataAndNavigate(int dailyCalorieGoal, int proteinGrams, int fatGrams, int carbsGrams, String selectedGender, int weight, int age, int height, String username) {
        SharedPrefManager.getInstance(requireActivity()).saveOnboardingData(dailyCalorieGoal, proteinGrams, fatGrams, carbsGrams, selectedGender, weight, age, height, username);

        try {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_onboardingFragment_to_dashboardFragment);
            requireActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);
        } catch (Exception e) {
            String error = e.getMessage();
            if (error != null) {
                Log.d("Navigation Error:", error);
            }
            Toast.makeText(getContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
        }
        SharedPrefManager.getInstance(requireActivity()).setOnboardingCompleted(true);
    }
}
