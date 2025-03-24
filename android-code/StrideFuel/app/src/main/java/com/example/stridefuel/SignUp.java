package com.example.stridefuel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    private EditText username, emailadd, pass1, pass2;
    private Button signup;
    private TextView login;
    private ProgressBar progressBar;
    private CheckBox checkBox;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        username = findViewById(R.id.usernameedittext);
        emailadd = findViewById(R.id.emailaddedittext);
        pass1 = findViewById(R.id.pass1edittext);
        pass2 = findViewById(R.id.pass2edittext);
        checkBox = findViewById(R.id.checkBox);
        signup = findViewById(R.id.signup);
        login = findViewById(R.id.login);

        progressBar = findViewById(R.id.progressBar);

        Intent iLogin = new Intent(SignUp.this, LoginPage.class);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = username.getText().toString().trim();
                String emailAdd = emailadd.getText().toString().trim();
                String pwd1 = pass1.getText().toString().trim();
                String pwd2 = pass2.getText().toString().trim();


                if ((TextUtils.isEmpty(userName) || TextUtils.isEmpty(emailAdd) || TextUtils.isEmpty(pwd1) || TextUtils.isEmpty(pwd2)))
                {
                    Toast.makeText(SignUp.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }
                 else
                 {
                     if (checkBox.isChecked())
                    {
                        if (pwd1.equals(pwd2))
                        {
                            Log.d("SignUp", "Registering user...");
                            registerUser();
                        } else
                            Toast.makeText(SignUp.this, "Password mismatch", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(SignUp.this, "Please accept the Terms & Conditions", Toast.LENGTH_SHORT).show();
                 }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(iLogin);
            }
        });

    }

    public void registerUser() {
        final String usernameInput = username.getText().toString().trim();
        final String email = emailadd.getText().toString().trim();
        final String password1 = pass1.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);
        signup.setEnabled(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_REGISTER,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            progressBar.setVisibility(View.GONE);
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            signup.setEnabled(true);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    // This happens when the server is offline (no response received)
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Server is offline! Please try again later.", Toast.LENGTH_LONG).show();
                    signup.setEnabled(true);
                } else {
                    String errorMessage = "Error: " + error.getMessage();
                    Log.d("VolleyError", errorMessage);
                    Toast.makeText(SignUp.this, "We're having trouble connecting right now. Please check your connection and try again shortly.", Toast.LENGTH_SHORT).show();
                    signup.setEnabled(true);
                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", usernameInput);
                params.put("email", email);
                params.put("password", password1);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}