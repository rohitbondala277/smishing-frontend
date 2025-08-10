package com.example.smishingdetectionapp.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.text.InputType;

//import android.text.method.HideReturnsTransformationMethod;
//import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.databinding.ActivityLoginBinding;
import com.example.smishingdetectionapp.DataBase.LoginResponse;
import com.example.smishingdetectionapp.DataBase.Retrofitinterface;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    // 1) Base URL: emulator -> your local backend
    private static final String BASE_URL = "https://smishing-backend-1004745454775.australia-southeast1.run.app/";

    private ActivityLoginBinding binding;
    private Retrofitinterface api;
    private boolean isPinLogin = false;
    private Retrofit retrofit;
    private Retrofitinterface retrofitinterface;
    //private Object BuildConfig;
    private String BASE_URL = BuildConfig.SERVERIP;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // BLOCKING screenshots and screen recording
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        // Inflate layout
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 2) Retrofit client
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Retrofitinterface.class);

        // If you already persist login somewhere, check here
        if (isUserLoggedIn()) {
            navigateToMainActivity();
            return;
        }

        // View refs (these IDs match your XML)
        final EditText emailEt = binding.email;
        final EditText secretEt = binding.password; // password or PIN depending on mode
        final Button loginBtn = binding.loginButton;
        final Button togglePinBtn = binding.togglePinLogin;
        final Button registerBtn = binding.registerButton;
        final ImageButton togglePwIcon = binding.togglePasswordVisibility;
        final ProgressBar progress = binding.progressbar;

        // Toggle Password/PIN mode
        togglePinBtn.setOnClickListener(v -> {
            secretEt.setText("");

        // ViewModel setup
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        // View bindings
        final EditText usernameEditText = binding.email;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.loginButton;
        final ProgressBar loadingProgressBar = binding.progressbar;
        final SignInButton googleBtn = binding.googleBtn;
        final Button registerButton = binding.registerButton;
        final ImageButton togglePasswordVisibility = binding.togglePasswordVisibility;
        final Button togglePinLogin = binding.togglePinLogin;

        // Toggle functionality for PIN and Password login
        togglePinLogin.setOnClickListener(v -> {
            passwordEditText.setText("");



            if (isPinLogin) {
                // Switch to password
                isPinLogin = false;
                secretEt.setHint(R.string.prompt_password);
                secretEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePinBtn.setText(R.string.login_with_pin);
                loginBtn.setText(R.string.action_login_in);
            } else {
                // Switch to PIN
                isPinLogin = true;
                secretEt.setHint("Enter 6-digit PIN");
                secretEt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                togglePinBtn.setText(R.string.login_with_password);
                loginBtn.setText("Login with PIN");
            }
            secretEt.requestFocus();
        });

        // Toggle password visibility (in password mode only)
        togglePwIcon.setOnClickListener(v -> {
            if (isPinLogin) return; // ignore in PIN mode
            if (isPasswordVisible) {
                secretEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePwIcon.setImageResource(R.drawable.visibilityoff);
                isPasswordVisible = false;
            } else {
                secretEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePwIcon.setImageResource(R.drawable.visibility);
                isPasswordVisible = true;
            }
            secretEt.setSelection(secretEt.getText().length());
        });

        // Login button
        loginBtn.setOnClickListener(v -> {
            String email = emailEt.getText().toString().trim();
            String secret = secretEt.getText().toString().trim();

            if (email.isEmpty()) {
                emailEt.setError("Email required");
                return;
            }
            if (secret.isEmpty()) {
                secretEt.setError(isPinLogin ? "PIN required" : "Password required");
                return;
            }
            if (isPinLogin && secret.length() != 6) {
                secretEt.setError("PIN must be 6 digits");
                return;
            }

            progress.setVisibility(ProgressBar.VISIBLE);
            if (isPinLogin) {
                loginWithPin(email, secret, progress);
            } else {
                loginWithPassword(email, secret, progress);
            }
        });

        // Optional: navigate to register screen
        registerBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Register screen not wired here.", Toast.LENGTH_SHORT).show();
        });
    }

    private void loginWithPassword(String email, String password, ProgressBar progress) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        api.loginPassword(body).enqueue(new Callback<LoginResponse>() {
            @Override public void onResponse(Call<LoginResponse> call, Response<LoginResponse> res) {
                progress.setVisibility(ProgressBar.GONE);
                if (res.isSuccessful() && res.body() != null && res.body().isSuccess()) {
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    navigateToMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();
                }
            }
            @Override public void onFailure(Call<LoginResponse> call, Throwable t) {
                progress.setVisibility(ProgressBar.GONE);
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loginWithPin(String email, String pin, ProgressBar progress) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("pin", pin);

        api.loginPin(body).enqueue(new Callback<LoginResponse>() {
            @Override public void onResponse(Call<LoginResponse> call, Response<LoginResponse> res) {
                progress.setVisibility(ProgressBar.GONE);
                if (res.isSuccessful() && res.body() != null && res.body().isSuccess()) {
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

        }
    }

    private void loginWithPin(String pin) {
        // For testing purposes, simulate a successful PIN login
        Toast.makeText(LoginActivity.this, "PIN verified successfully (bypassed for testing)", Toast.LENGTH_SHORT).show();
        navigateToMainActivity();
    }

    private void loginWithPassword(String email, String password) {
        // For testing purposes, simulate a successful login
        Toast.makeText(LoginActivity.this, "Login successful (bypassed for testing)", Toast.LENGTH_SHORT).show();
        navigateToMainActivity();
    }

    private void handleLoginDialog() {
        final EditText usernameEditText = binding.email;
        final EditText passwordEditText = binding.password;

        HashMap<String, String> map = new HashMap<>();
        map.put("email", usernameEditText.getText().toString());
        map.put("password", passwordEditText.getText().toString());

        Call<DBresult> call = retrofitinterface.executeLogin(map);
        call.enqueue(new Callback<DBresult>() {
            @Override
            public void onResponse(Call<DBresult> call, Response<DBresult> response) {
                if (response.code() == 200) {
                    navigateToMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid email or PIN", Toast.LENGTH_LONG).show();
                }
            }
            @Override public void onFailure(Call<LoginResponse> call, Throwable t) {
                progress.setVisibility(ProgressBar.GONE);
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean isUserLoggedIn() {
        // TODO: read a saved token / shared prefs if you want auto-login.
        return false;
    }

    private void navigateToMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reapply the secure flag when activity resumes
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
    }
}
