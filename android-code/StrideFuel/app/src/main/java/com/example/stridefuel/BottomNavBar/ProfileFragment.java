package com.example.stridefuel.BottomNavBar;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.stridefuel.R;
import com.example.stridefuel.SharedPrefManager;
import com.example.stridefuel.SignUp;

public class ProfileFragment extends Fragment {
    TextView usernameProfileTextView, emailProfileTextView, bmrProfileTextView, genderProfileTextView, heightProfileTextView, weightProfileTextView;
    Button logoutButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        usernameProfileTextView = view.findViewById(R.id.usernameProfileTextView);
        emailProfileTextView = view.findViewById(R.id.emailProfileTextView);
        bmrProfileTextView = view.findViewById(R.id.bmrProfileTextView);
        genderProfileTextView = view.findViewById(R.id.genderProfileTextView);
        heightProfileTextView = view.findViewById(R.id.heightProfileTextView);
        weightProfileTextView = view.findViewById(R.id.weightProfileTextView);
        logoutButton = view.findViewById(R.id.logoutButton);

        setProfileData();
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(requireActivity()).LogoutUser();
                Intent iSignUp = new Intent(requireActivity(), SignUp.class);
                startActivity(iSignUp);
            }
        });
    }
    private void setProfileData()
    {
        String userName = SharedPrefManager.getInstance(requireActivity()).getUserName();
        String userEmail = SharedPrefManager.getInstance(requireActivity()).getUserEmail();
        float bmr = SharedPrefManager.getInstance(requireActivity()).getBmrForUser(userName);
        String gender = SharedPrefManager.getInstance(requireActivity()).getGenderForUser(userName);
        float height = SharedPrefManager.getInstance(requireActivity()).getHeightForUser(userName);
        float weight = SharedPrefManager.getInstance(requireActivity()).getWeightForUser(userName);

        usernameProfileTextView.setText(userName);
        emailProfileTextView.setText(userEmail);
        genderProfileTextView.setText(gender);
        bmrProfileTextView.setText(String.valueOf((int) bmr)+ " Kcal");
        heightProfileTextView.setText(String.valueOf((int) height) + " cm");
        weightProfileTextView.setText(String.valueOf((int) weight) + " Kg");

    }
}