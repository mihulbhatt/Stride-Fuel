package com.example.stridefuel;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.stridefuel.BottomNavBar.MainActivity;
import com.example.stridefuel.BottomNavBar.OnboardingFragment;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Apply edge-to-edge display without unnecessary EdgeToEdge.enable()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Delay for splash and check user login state
        new Handler().postDelayed(() -> {
            if (SharedPrefManager.getInstance(this).isLoggedIn())
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            else startActivity(new Intent(SplashActivity.this, LoginPage.class));
            },1500); // 1.5-second splash delay
    }
}
