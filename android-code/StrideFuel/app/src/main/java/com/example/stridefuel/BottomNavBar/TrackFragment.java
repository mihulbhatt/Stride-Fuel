package com.example.stridefuel.BottomNavBar;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TrackFragment extends Fragment {
    // Views
    TextView textView22, textView24, textView15, textView8, textView6;
    EditText foodEditText;
    CardView cardView11, cardView12;
    ImageView imageView17, imageView14;
    Button Track;
    RecyclerView recyclerView;
    ConstraintLayout constraintLayout;
    ProgressBar progressBar;

    String meal_type;

    private ArrayList<MealModel> mealList;
    private RecyclerMealAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_track, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        constraintLayout = view.findViewById(R.id.constraintLayout);
        Track = view.findViewById(R.id.button);
        textView15 = view.findViewById(R.id.textView15);
        textView24 = view.findViewById(R.id.textView24);
        textView22 = view.findViewById(R.id.textView22);
        textView8 = view.findViewById(R.id.textView8);
        textView6 = view.findViewById(R.id.textView6);
        cardView11 = view.findViewById(R.id.cardView11);
        cardView12 = view.findViewById(R.id.cardView12);
        imageView17 = view.findViewById(R.id.imageView17);
        imageView14 = view.findViewById(R.id.imageView14);
        foodEditText = view.findViewById(R.id.foodEditText);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar4);

        // Set up RecyclerView with a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        // Initialize the meal list and adapter once, so that each query appends to the existing list.
        mealList = new ArrayList<>();
        adapter = new RecyclerMealAdapter(getContext(), mealList);
        recyclerView.setAdapter(adapter);

        // Set up click listener for the Track button.
        Track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = foodEditText.getText().toString().trim();
                if (!query.isEmpty()) {
                    textView6.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    animateLayoutChange();
                    viewAnimations();
                    // Fetch meal data based on the entered query.
                    fetchUserMacros(query);
                } else {
                    Toast.makeText(requireActivity(), "Field is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Animate views to transition into the new layout state.
    private void viewAnimations() {
        // Slide out non-interactive views using XML animation.
        Animation slideout = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out);
        textView15.startAnimation(slideout);
        textView24.startAnimation(slideout);
        textView22.startAnimation(slideout);
        cardView11.startAnimation(slideout);
        cardView12.startAnimation(slideout);
        imageView17.startAnimation(slideout);


        float translation = foodEditText.getHeight() * 9.0f;


        textView8.animate()
                .translationY(-translation)
                .setDuration(800)
                .setInterpolator(new DecelerateInterpolator())
                .start();


        // Ensure the RecyclerView is visible.
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void animateLayoutChange() {
        // Post a runnable to ensure views have been laid out and measured.
        foodEditText.post(new Runnable() {
            @Override
            public void run() {
                // Get the measured height of the EditText
                int editTextHeight = foodEditText.getHeight();
                // Compute dynamic margins based on the EditText's height (adjust factors as needed)
                int topMargin = (int) (editTextHeight * 0.9f);
                int recyclerTopMargin = (int) (editTextHeight * 0.3f);
                int bottomMargin = 0;

                // Clone current constraints from the root layout
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);

                // Update the foodEditText: attach its top to parent's top with a dynamic margin.
                constraintSet.connect(foodEditText.getId(), ConstraintSet.TOP,
                        ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin);

                // Reposition the Track button to align with the EditText (if needed).
                constraintSet.connect(Track.getId(), ConstraintSet.TOP,
                        foodEditText.getId(), ConstraintSet.TOP, 0);

                // Update the RecyclerView so its top is now below the foodEditText.
                constraintSet.connect(recyclerView.getId(), ConstraintSet.TOP,
                        foodEditText.getId(), ConstraintSet.BOTTOM, recyclerTopMargin);

                // Ensure the RecyclerView's bottom is attached to the parent's bottom.
                constraintSet.connect(recyclerView.getId(), ConstraintSet.BOTTOM,
                        ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, bottomMargin);

                // Begin the transition animation and apply the new constraints.
                TransitionManager.beginDelayedTransition(constraintLayout);
                constraintSet.applyTo(constraintLayout);
            }
        });
    }
    // Fetch meal data from the server using Volley and append new meals to the existing list.
    private void fetchUserMacros(final String query)
    {
        if (getArguments() != null) {
            meal_type = getArguments().getString("meal_type", ""); // Provide a default value
        } else {
            meal_type = ""; // Avoid null pointer exception
        }

        if (meal_type.isEmpty())
        {
            StringRequest request = new StringRequest(Request.Method.POST, Constants.URL_USER_MACROS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray foodsArray = jsonObject.getJSONArray("foods");

                                // Temporary list for new meals from this query.
                                ArrayList<MealModel> newMeals = new ArrayList<>();

                                // Loop through the foods array and create MealModel objects.
                                for (int i = 0; i < foodsArray.length(); i++) {
                                    JSONObject food = foodsArray.getJSONObject(i);
                                    String mealName = food.optString("food_name", "Unknown Meal");
                                    String calories = String.valueOf(food.optDouble("nf_calories", 0));
                                    String protein = String.valueOf(food.optDouble("nf_protein", 0));
                                    String fats = String.valueOf(food.optDouble("nf_total_fat", 0));
                                    String carbs = String.valueOf(food.optDouble("nf_total_carbohydrate", 0));

                                    // Get image URL if available.
                                    String imageUrl = "";
                                    if (food.has("photo")) {
                                        JSONObject photo = food.getJSONObject("photo");
                                        imageUrl = photo.optString("thumb", "");
                                    }

                                    MealModel meal = new MealModel(mealName, calories, protein, fats, carbs, imageUrl);
                                    newMeals.add(meal);
                                    progressBar.setVisibility(View.GONE);
                                }

                                // Append the new meals to the persistent mealList once.
                                mealList.addAll(newMeals);
                                // Notify adapter that the data set has changed.
                                adapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), "Please Enter Valid Food Name", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("userID", SharedPrefManager.getInstance(requireActivity()).getUserId());
                    params.put("query", query);
                    return params;
                }
            };

            RequestHandler.getInstance(requireActivity()).addToRequestQueue(request);

        }else{
            StringRequest request1 = new StringRequest(Request.Method.POST, Constants.URL_USER_MACROS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray foodsArray = jsonObject.getJSONArray("foods");

                                // Temporary list for new meals from this query.
                                ArrayList<MealModel> newMeals = new ArrayList<>();

                                // Loop through the foods array and create MealModel objects.
                                for (int i = 0; i < foodsArray.length(); i++) {
                                    JSONObject food = foodsArray.getJSONObject(i);
                                    String mealName = food.optString("food_name", "Unknown Meal");
                                    String calories = String.valueOf(food.optDouble("nf_calories", 0));
                                    String protein = String.valueOf(food.optDouble("nf_protein", 0));
                                    String fats = String.valueOf(food.optDouble("nf_total_fat", 0));
                                    String carbs = String.valueOf(food.optDouble("nf_total_carbohydrate", 0));

                                    // Get image URL if available.
                                    String imageUrl = "";
                                    if (food.has("photo")) {
                                        JSONObject photo = food.getJSONObject("photo");
                                        imageUrl = photo.optString("thumb", "");
                                    }

                                    MealModel meal = new MealModel(mealName, calories, protein, fats, carbs, imageUrl);
                                    newMeals.add(meal);
                                    progressBar.setVisibility(View.GONE);
                                }

                                // Append the new meals to the persistent mealList once.
                                mealList.addAll(newMeals);
                                // Notify adapter that the data set has changed.
                                adapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), "Please Enter Valid Food Name", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("userID", SharedPrefManager.getInstance(requireActivity()).getUserId());
                    params.put("query", query);
                    params.put("meal_type", meal_type);
                    return params;
                }
            };

            RequestHandler.getInstance(requireActivity()).addToRequestQueue(request1);
        }

    }
}
