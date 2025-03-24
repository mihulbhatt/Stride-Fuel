package com.example.stridefuel.BottomNavBar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.stridefuel.R;
import com.example.stridefuel.SharedPrefManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Adjust insets: remove top and bottom padding to avoid extra blank space
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Remove both top and bottom insets
            v.setPadding(systemBars.left, 0, systemBars.right, 0);
            return insets;
        });

        mBottomBar = findViewById(R.id.bottomNavigationView);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        // the NavController from NavHostFragment to handle navigation
        NavController navController = navHostFragment.getNavController();

        String currentUsername = SharedPrefManager.getInstance(this).getUserName();
        String onboardedUsername = SharedPrefManager.getInstance(this).getOnboardedUsernameForUser(currentUsername);
        boolean isOnboarded = SharedPrefManager.getInstance(this).isOnboardingCompletedForUser(currentUsername);

        if (currentUsername.equals(onboardedUsername) && isOnboarded) {
            mBottomBar.setVisibility(View.VISIBLE);
            navController.popBackStack(R.id.onboardingFragment, true);
            navController.navigate(R.id.dashboardFragment);
        } else {
            mBottomBar.setVisibility(View.GONE);
            navController.navigate(R.id.onboardingFragment);
        }


        mBottomBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.navdashboard)
                    navController.navigate(R.id.dashboardFragment);
                else if (itemId == R.id.navtrack)
                    navController.navigate(R.id.trackFragment);
                else
                    navController.navigate(R.id.profileFragment);

                return true;
            }
        });
    }
}
