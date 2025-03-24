package com.example.stridefuel.BottomNavBar;

public class MealModel {
    private String mealName, calories, protein, fats, carbs, imageURL;

    public MealModel(String mealName, String calories, String protein, String fats, String carbs, String imageURL) {
        this.mealName = mealName;
        this.calories = calories;
        this.protein = protein;
        this.fats = fats;
        this.carbs = carbs;
        this.imageURL = imageURL;
    }

    public String getMealName() {
        return mealName;
    }

    public String getCalories() {
        return calories;
    }

    public String getProtein() {
        return protein;
    }

    public String getFats() {
        return fats;
    }

    public String getCarbs() {
        return carbs;
    }

    public String getImageURL() {
        return imageURL;
    }
}
