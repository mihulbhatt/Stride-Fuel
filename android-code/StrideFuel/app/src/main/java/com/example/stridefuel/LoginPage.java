package com.example.stridefuel;

import static com.example.stridefuel.R.*;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.stridefuel.BottomNavBar.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginPage extends AppCompatActivity
{
    private EditText realusername,pwd;
    private Button button;
    private TextView tv5;
    private ProgressBar progressBar2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tv5 = findViewById(R.id.tv5);
        button = findViewById(R.id.loginButton);
        realusername = findViewById(R.id.editTextTextUserName);
        pwd = findViewById(id.editTextTextPassword);
        progressBar2 = findViewById(id.loginProgressBar);


        Intent iSignUp = new Intent(LoginPage.this,SignUp.class);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(realusername.getText().toString().isEmpty() || pwd.getText().toString().isEmpty())
                    Toast.makeText(LoginPage.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                else userLogIn();
            }
        });

        tv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(iSignUp);
            }
        });

    }
    private void userLogIn()
    {
        final String username = realusername.getText().toString().trim();
        final String password = pwd.getText().toString().trim();

        progressBar2.setVisibility(View.VISIBLE);
        button.setEnabled(false);

        StringRequest stringRequest1 = new StringRequest(Request.Method.POST, Constants.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressBar2.setVisibility(View.GONE);
                            JSONObject jsonObject = new JSONObject(response);
                            if(!jsonObject.getBoolean("error"))
                            {
                                SharedPrefManager.getInstance(getApplicationContext()).
                                        userLogIn(
                                        jsonObject.getInt("id"),
                                        jsonObject.getString("username"),
                                        jsonObject.getString("email")
                                );

                                Intent iHome = new Intent(LoginPage.this, MainActivity.class);
                                startActivity(iHome);
                                finish();
                                Toast.makeText(LoginPage.this, "Logged In successfully", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),jsonObject.getString("message") ,Toast.LENGTH_LONG).show();
                            }
                            button.setEnabled(true);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar2.setVisibility(View.GONE);
                        button.setEnabled(true);
                        // If the error is related to network issues like a timeout or no connection
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(), "Network timeout. Please try again.", Toast.LENGTH_LONG).show();
                        }
                        // If the error has a network response, extract status code and response data
                        else if (error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            String responseData = new String(error.networkResponse.data);
                            Log.e("VOLLEY_ERROR", "Status code: " + statusCode);
                            Log.e("VOLLEY_ERROR", "Response data: " + responseData);

                            if (statusCode == 404) {
                                Toast.makeText(getApplicationContext(), "Resource not found. Please try again.", Toast.LENGTH_LONG).show();
                            } else if (statusCode == 500) {
                                Toast.makeText(getApplicationContext(), "Server error. Please try later.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error " + statusCode, Toast.LENGTH_LONG).show();
                            }
                        }
                        // Fallback for any other error cases
                        else {
                            String errorMessage = (error.getMessage() != null) ? error.getMessage() : "An unexpected error occurred.";
                            Log.e("VOLLEY_ERROR", "Error: " + errorMessage);
                            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest1);


        StringRequest userBmrValuefetching = new StringRequest(Request.Method.POST, Constants.URL_USER_BMR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(!jsonObject.getBoolean("error"))
                            {
                                int bmrValue = jsonObject.getInt("bmr");
                                Log.d("BMR Response", "Fetched BMR: " + bmrValue);
                                SharedPrefManager.getInstance(getApplicationContext()).saveBmr(bmrValue);

                                // Now that the BMR is stored, launch MainActivity.
                                Intent iHome = new Intent(LoginPage.this, MainActivity.class);
                                startActivity(iHome);
                            } else {
                                Toast.makeText(LoginPage.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("VolleyResponse", "JSON parse error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("VolleyError", "Error occurred: " + error.toString());
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> params = new HashMap<>();
                params.put("username", realusername.getText().toString().trim());
                return params;
            }
        };
        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(userBmrValuefetching);
    }
}