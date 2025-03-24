package com.example.stridefuel;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager
{
    private static SharedPrefManager instance;
    private static Context ctx;
    private static final String SHARED_PREF_NAME = "mysharedpref12";
    private static final String  KEY_USERNAME = "username";
    private static final String  KEY_USER_EMAIL = "useremail";
    private static final String  KEY_USER_ID = "userid";
    private static final String KEY_ONBOARDING_COMPLETE = "onboardingComplete";

    private static final String KEY_ONBOARDED_USERNAME = "onboardedUsername";

    private static final String KEY_BMR = "dailyCalorieGoal";
    private static final String KEY_PROTEINGRAMS = "proteinGrams";
    private static final String KEY_FATGRAMS = "fatGrams";
    private static final String KEY_CARBSGRAMS = "carbsGrams";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_AGE = "age";
    private static final String KEY_HEIGHT = "height";

    private static final String KEY_MEAL_LIST = "mealList";
    private static final String KEY_MEAL_LIST_TIMESTAMP = "mealListTimestamp";


    private SharedPrefManager(Context context) {
        ctx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
        return instance;
    }
    public boolean userLogIn(int id, String username, String email)
    {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(KEY_USER_ID,id);
        editor.putString(KEY_USER_EMAIL,email);
        editor.putString(KEY_USERNAME,username);


        editor.apply();
        return true;
    }
    public String getUserName() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME,"Please onboard First");
    }
    public String getUserEmail() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_EMAIL,"");
    }
    public String getUserId() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return String.valueOf(sharedPreferences.getInt(KEY_USER_ID, -1));
    }


    public boolean isLoggedIn()
    {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.contains(KEY_USERNAME);
    }
    public boolean LogoutUser()
    {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }
    private String getNamespacedKey(String username, String key) {
        return username + "_" + key;
    }
    public boolean saveOnboardingData(int dailyCalorieGoal, int proteinGrams, int fatGrams, int carbsGrams,
                                      String gender, int weight, int age, int height, String username) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getNamespacedKey(username, KEY_BMR), dailyCalorieGoal);
        editor.putInt(getNamespacedKey(username, KEY_PROTEINGRAMS), proteinGrams);
        editor.putInt(getNamespacedKey(username, KEY_FATGRAMS), fatGrams);
        editor.putInt(getNamespacedKey(username, KEY_CARBSGRAMS), carbsGrams);
        editor.putString(getNamespacedKey(username, KEY_GENDER), gender);
        editor.putInt(getNamespacedKey(username, KEY_WEIGHT), weight);
        editor.putInt(getNamespacedKey(username, KEY_AGE), age);
        editor.putInt(getNamespacedKey(username, KEY_HEIGHT), height);
        editor.putBoolean(getNamespacedKey(username, KEY_ONBOARDING_COMPLETE), true);
        editor.putString(getNamespacedKey(username, KEY_ONBOARDED_USERNAME), username);
        editor.apply();
        return true;
    }



    public boolean saveBmr(int bmr) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_BMR, bmr);
        editor.apply();
        return true;
    }

    public int getBmrForUser(String username) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(getNamespacedKey(username, KEY_BMR), 0);
    }

    public int getProteinGramsForUser(String username) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(getNamespacedKey(username, KEY_PROTEINGRAMS), 0);
    }

    public int getFatGramsForUser(String username) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(getNamespacedKey(username, KEY_FATGRAMS), 0);
    }

    public int getCarbsGramsForUser(String username) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(getNamespacedKey(username, KEY_CARBSGRAMS), 0);
    }

    public boolean setOnboardingCompleted(boolean completed) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_ONBOARDING_COMPLETE, completed);
        editor.apply();
        return true;
    }

    public boolean isOnboardingCompletedForUser(String username) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(getNamespacedKey(username, KEY_ONBOARDING_COMPLETE), false);
    }
    public String getOnboardedUsernameForUser(String username) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(getNamespacedKey(username, KEY_ONBOARDED_USERNAME), "");
    }

    // Save the meal list (as a JSON string) along with the current time.
    public boolean saveMealList(String mealListJson) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_MEAL_LIST, mealListJson);
        editor.putLong(KEY_MEAL_LIST_TIMESTAMP, System.currentTimeMillis());
        editor.apply();
        return true;
    }

    // Retrieve the saved meal list JSON.
    public String getMealList() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_MEAL_LIST, null);
    }

    // Retrieve the timestamp of when the meal list was last saved.
    public long getMealListTimestamp() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(KEY_MEAL_LIST_TIMESTAMP, 0);
    }

    // Optionally, clear the saved meal list.
    public boolean clearMealList() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_MEAL_LIST);
        editor.remove(KEY_MEAL_LIST_TIMESTAMP);
        editor.apply();
        return true;
    }

    public String getGenderForUser(String userName) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(getNamespacedKey(userName, KEY_GENDER), "");
    }

    public int getHeightForUser(String userName) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(getNamespacedKey(userName, KEY_HEIGHT), 0);
    }

    public int getWeightForUser(String userName) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(getNamespacedKey(userName, KEY_WEIGHT), 0);
    }

}