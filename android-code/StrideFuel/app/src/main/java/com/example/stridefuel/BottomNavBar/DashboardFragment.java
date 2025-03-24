package com.example.stridefuel.BottomNavBar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.stridefuel.CircularProgressBar;
import com.example.stridefuel.Constants;
import com.example.stridefuel.R;
import com.example.stridefuel.RequestHandler;
import com.example.stridefuel.SharedPrefManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DashboardFragment extends Fragment {

    TextView calorieTextView, proteinTextView, fatTextView, carbsTextView,
            dateTextView, breakfastTextView, lunchTextView, dinnerTextView,
            snackTextView, eatenTextView, remainingTextView, totalEatenCarbsTextView,
            totalEatenFatTextView, totalEatenProteinTextView, breakfastLeftTextView,
            lunchLeftTextView, dinnerLeftTextView, snacksLeftTextView;
    ProgressBar carbsProgressBar, fatsProgressBar, proteinProgressBar;
    ImageView bfAddImage, lunchAddImage, dinnerAddImage, snacksAddImage;
    ScrollView scrollView;
    private CircularProgressBar circularProgressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    // Using floats for calculations
    float breakfastCalories, lunchCalories, dinnerCalories, snackCalories;
    float userCalorieGoal, proteinGoals, fatGoals, carbsGoals;
    float totalCaloriesEaten = 0f, totalFat = 0f, totalCarbs = 0f, totalProtein = 0f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calorieTextView = view.findViewById(R.id.calorieTextView);
        proteinTextView = view.findViewById(R.id.proteinTextView);
        fatTextView = view.findViewById(R.id.fatTextView);
        carbsTextView = view.findViewById(R.id.carbsTextView);
        dateTextView = view.findViewById(R.id.dateTextView);
        breakfastTextView = view.findViewById(R.id.breakfastTextView);
        lunchTextView = view.findViewById(R.id.lunchTextView);
        dinnerTextView = view.findViewById(R.id.dinnerTextView);
        snackTextView = view.findViewById(R.id.snacksTextView);
        eatenTextView = view.findViewById(R.id.eatenTextView);
        remainingTextView = view.findViewById(R.id.remainingTextView);
        circularProgressBar = view.findViewById(R.id.circularProgressBar);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        totalEatenCarbsTextView = view.findViewById(R.id.totalEatenCarbsTextView);
        totalEatenFatTextView = view.findViewById(R.id.totalEatenFatTextView);
        totalEatenProteinTextView = view.findViewById(R.id.totalEatenProteinTextView);
        carbsProgressBar = view.findViewById(R.id.carbsProgressBar);
        fatsProgressBar = view.findViewById(R.id.fatsProgressBar);
        proteinProgressBar = view.findViewById(R.id.proteinProgressBar);
        bfAddImage = view.findViewById(R.id.bfAddImage);
        lunchAddImage = view.findViewById(R.id.lunchAddImage);
        dinnerAddImage = view.findViewById(R.id.dinnerAddImage);
        snacksAddImage = view.findViewById(R.id.snacksAddImage);
        breakfastLeftTextView = view.findViewById(R.id.breakfastLeftTextView);
        lunchLeftTextView = view.findViewById(R.id.lunchLeftTextView);
        dinnerLeftTextView = view.findViewById(R.id.dinnerLeftTextView);
        snacksLeftTextView = view.findViewById(R.id.snacksLeftTextView);
        scrollView = view.findViewById(R.id.scrollView);


        loadUserData();
        updateCalorieDisplay();
        updateMacros();
        updateDate();
        fetchTodayMeals(); // Initial fetch


        bfAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("meal_type", "Breakfast");
                dashboardToTrack(v,bundle);
            }
        });
        lunchAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("meal_type", "Lunch");
                dashboardToTrack(v,bundle);
            }
        });
        dinnerAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("meal_type", "Dinner");
                dashboardToTrack(v,bundle);
            }
        });
        snacksAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("meal_type", "Snacks");
                dashboardToTrack(v,bundle);
            }
        });


        // Swipe down to refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchTodayMeals(); // Example refresh action
            swipeRefreshLayout.setRefreshing(false);
        });

        // larger distance set to require more forceful swipe
        swipeRefreshLayout.setDistanceToTriggerSync(400); // it Adjusts the value as needed

        // it ensures triggers only if scrolled to the top
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setProgressViewOffset(true, 100, 300);

        // Prevent swipe when not at the top
        swipeRefreshLayout.setOnChildScrollUpCallback((parent, child) -> {
            return scrollView.getScrollY() > 0; // Prevent if not at the top
        });
    }

    private void loadUserData() {
        String existedUser = SharedPrefManager.getInstance(requireActivity()).getUserName();
        // Convert to float for precision, even if values are whole numbers
        userCalorieGoal = (float) SharedPrefManager.getInstance(requireActivity()).getBmrForUser(existedUser);
        proteinGoals = (float) SharedPrefManager.getInstance(requireActivity()).getProteinGramsForUser(existedUser);
        fatGoals = (float) SharedPrefManager.getInstance(requireActivity()).getFatGramsForUser(existedUser);
        carbsGoals = (float) SharedPrefManager.getInstance(requireActivity()).getCarbsGramsForUser(existedUser);
    }

    private void updateCalorieDisplay() {
        if (userCalorieGoal > 0) {
            // Allocate 30% each to breakfast, lunch, and dinner;
            // remaining calories will be assign to snacks so the total matches.
            breakfastCalories = (userCalorieGoal * 30f) / 100f;
            lunchCalories = (userCalorieGoal * 30f) / 100f;
            dinnerCalories = (userCalorieGoal * 30f) / 100f;
            snackCalories = userCalorieGoal - (breakfastCalories + lunchCalories + dinnerCalories);

            // Convert to integer values for display
            calorieTextView.setText(String.valueOf(Math.round(userCalorieGoal)));
            breakfastTextView.setText(Math.round(breakfastCalories) + " kcal");
            lunchTextView.setText(Math.round(lunchCalories) + " kcal");
            dinnerTextView.setText(Math.round(dinnerCalories) + " kcal");
            snackTextView.setText(Math.round(snackCalories) + " kcal");
        } else {
            calorieTextView.setText("Please complete onboarding");
        }
    }

    private void fetchTodayMeals() {
        StringRequest getTodayMeals = new StringRequest(Request.Method.POST, Constants.URL_GET_TODAY_MEALS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Expecting a JSON object with key "meals"
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray mealsArray = jsonResponse.getJSONArray("meals");

                            // Reset totals
                            totalCaloriesEaten = 0f;
                            totalProtein = 0f;
                            totalFat = 0f;
                            totalCarbs = 0f;

                            float totalBreakfastCalories = 0f;
                            float totalLunchCalories = 0f;
                            float totalDinnerCalories = 0f;
                            float totalSnacksCalories = 0f;

                            for (int i = 0; i < mealsArray.length(); i++) {
                                JSONObject meal = mealsArray.getJSONObject(i);
                                totalCaloriesEaten += (float) meal.getDouble("calories");
                                totalProtein += (float) meal.getDouble("protein");
                                totalFat += (float) meal.getDouble("fats");
                                totalCarbs += (float) meal.getDouble("carbs");

                                String currentMealType = meal.getString("meal_type");
                                // Aggregate calories based on meal type
                                if (currentMealType.equals("Breakfast")) {
                                    totalBreakfastCalories += (float) meal.getDouble("calories");
                                    breakfastLeftTextView.setText(Math.round(totalBreakfastCalories) + " / ");
                                } else if (currentMealType.equals("Lunch")) {
                                    totalLunchCalories += (float) meal.getDouble("calories");
                                    lunchLeftTextView.setText(Math.round(totalLunchCalories) + " / ");
                                } else if (currentMealType.equals("Dinner")) {
                                    totalDinnerCalories += (float) meal.getDouble("calories");
                                    dinnerLeftTextView.setText(Math.round(totalDinnerCalories) + " / ");
                                } else if(currentMealType.equals("Snacks")){
                                    totalSnacksCalories += (float) meal.getDouble("calories");
                                    snacksLeftTextView.setText(Math.round(totalSnacksCalories) + " / ");
                                }
                            }

                            float remainingCalories = userCalorieGoal - totalCaloriesEaten;


                            // Update UI on main thread, converting values to integers for display
                            new Handler(Looper.getMainLooper()).post(() -> {
                                eatenTextView.setText(String.valueOf(Math.round(totalCaloriesEaten)));
                                remainingTextView.setText(String.valueOf(Math.round(remainingCalories)));
                                int calorieProgress = (int) ((totalCaloriesEaten * 100f) / userCalorieGoal);
                                circularProgressBar.setProgress(calorieProgress);

                                // Display remaining macros as integer values
                                totalEatenProteinTextView.setText(Math.round(totalProtein) + " / ");
                                totalEatenFatTextView.setText(Math.round(totalFat) + " / ");
                                totalEatenCarbsTextView.setText(Math.round(totalCarbs) + " / ");

                                // Update the progress bars (percentage of each goal achieved)
                                int proteinProgress = (int) ((totalProtein * 100f) / proteinGoals);
                                proteinProgressBar.setProgress(proteinProgress);

                                int fatProgress = (int) ((totalFat * 100f) / fatGoals);
                                fatsProgressBar.setProgress(fatProgress);

                                int carbsProgress = (int) ((totalCarbs * 100f) / carbsGoals);
                                carbsProgressBar.setProgress(carbsProgress);

                                swipeRefreshLayout.setRefreshing(false);
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userId", SharedPrefManager.getInstance(requireActivity()).getUserId());
                return params;
            }
        };

        RequestHandler.getInstance(requireActivity()).addToRequestQueue(getTodayMeals);
    }

    private void updateMacros() {
        if (proteinGoals > 0 && fatGoals > 0 && carbsGoals > 0) {
            proteinTextView.setText(Math.round(proteinGoals) + " g");
            fatTextView.setText(Math.round(fatGoals) + " g");
            carbsTextView.setText(Math.round(carbsGoals) + " g");
        } else {
            calorieTextView.setText("Please complete onboarding");
        }
    }

    private void updateDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        dateTextView.setText(currentDate);
    }

    private void dashboardToTrack(View v, Bundle bundle)
    {
        NavController navController = Navigation.findNavController(v);
        navController.navigate(R.id.action_dashboardFragment_to_trackFragment, bundle);
    }
}
