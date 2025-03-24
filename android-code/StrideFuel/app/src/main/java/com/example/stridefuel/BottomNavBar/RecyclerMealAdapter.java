package com.example.stridefuel.BottomNavBar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.stridefuel.R;
import java.util.List;

public class RecyclerMealAdapter extends RecyclerView.Adapter<RecyclerMealAdapter.ViewHolder> {

    private Context context;
    private List<MealModel> mealList;

    public RecyclerMealAdapter(Context context, List<MealModel> mealList) {
        this.context = context;
        this.mealList = mealList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.meal_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MealModel meal = mealList.get(position);
        holder.mealName.setText(meal.getMealName().toUpperCase());
        holder.calories.setText( meal.getCalories() + " kcal");
        holder.protein.setText(meal.getProtein() + "g");
        holder.fats.setText(" " + meal.getFats() + "g ");
        holder.carbs.setText(meal.getCarbs() + "g");

        // Load the image from the URL using Glide
        Glide.with(context)
                .load(meal.getImageURL())
                .placeholder(R.drawable.placeholder) // optional placeholder image
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mealName, calories, protein, fats, carbs;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mealName = itemView.findViewById(R.id.mealName);
            calories = itemView.findViewById(R.id.caloriesTextViewRecycler);
            protein = itemView.findViewById(R.id.proteinTextViewRecycler);
            fats = itemView.findViewById(R.id.fatsTextViewRecycler);
            carbs = itemView.findViewById(R.id.CarbsTextViewRecycler);
            imageView = itemView.findViewById(R.id.imageView15);
        }
    }
}
