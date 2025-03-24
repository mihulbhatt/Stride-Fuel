package com.example.stridefuel.BottomNavBar;

import android.content.Context;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.stridefuel.Constants;
import com.example.stridefuel.RequestHandler;
import com.example.stridefuel.SharedPrefManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodayMealFetcher {

    // API endpoint URL
    public static final String URL_GET_TODAY_MEALS = Constants.URL_GET_TODAY_MEALS;

    private Context mContext;

    // Constructor that accepts a Context.
    public TodayMealFetcher(Context context) {
        // Using the application context to avoid leaking Activity context
        this.mContext = context.getApplicationContext();
    }

    public void fetchTodayMeals(final String userId,
                                final Response.Listener<List<MealModel>> listener,
                                final Response.ErrorListener errorListener) {

        StringRequest request = new StringRequest(Request.Method.POST, URL_GET_TODAY_MEALS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Parse the raw JSON response
                            JSONArray jsonArray = new JSONArray(response);
                            List<MealModel> mealList = new ArrayList<>();

                            // Loop through each JSON object and build the MealModel list
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject mealObject = jsonArray.getJSONObject(i);

                                // Extract fields from the JSON object
                                String mealName = mealObject.optString("meal_name", "Unknown Meal");
                                String calories = mealObject.optString("calories", "0");
                                String protein = mealObject.optString("protein", "0");
                                String fats = mealObject.optString("fats", "0");
                                String carbs = mealObject.optString("carbs", "0");
                                String mealDate = mealObject.optString("meal_date", ""); // timestamp if needed

                                // Create a new MealModel instance.
                                MealModel meal = new MealModel(mealName, calories, protein, fats, carbs, mealDate);
                                mealList.add(meal);
                            }

                            // Save the raw JSON response and current timestamp in SharedPreferences.
                            SharedPrefManager.getInstance(mContext).saveMealList(response);

                            // Pass the meal list back to the caller.
                            listener.onResponse(mealList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            errorListener.onErrorResponse(new VolleyError(e));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorListener.onErrorResponse(error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Prepare POST parameters.
                Map<String, String> params = new HashMap<>();
                params.put("userID", userId);
                return params;
            }
        };

        // Add the request to the Volley request queue using the provided context.
        RequestHandler.getInstance(mContext).addToRequestQueue(request);
    }
}
