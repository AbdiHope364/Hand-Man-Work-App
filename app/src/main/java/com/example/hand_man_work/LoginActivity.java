package com.example.hand_man_work;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.example.hand_man_work.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.loginButton.setOnClickListener(v -> loginUser());
        binding.signUpText.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        // The rest of the buttons from the old layout are not in the new requirements
        // binding.forgotPasswordText.setOnClickListener(v -> sendPasswordReset());
    }

    @Override
    protected void onStart() {
        super.onStart();
        // If user is already logged in, redirect them
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            redirectUserBasedOnType(currentUser);
        }
    }

    private void loginUser() {
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        if (!isEmailValid(email) || !isPasswordValid(password)) {
            return;
        }

        showProgressBar();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    hideProgressBar();
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        redirectUserBasedOnType(user);
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void redirectUserBasedOnType(FirebaseUser user) {
        if (user == null) return;

        showProgressBar();
        FirestoreHelper.getUserType(user, task -> {
            hideProgressBar();
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String userType = document.getString("type");
                    Intent intent;
                    if ("customer".equals(userType)) {
                        intent = new Intent(LoginActivity.this, CustomerDashboardActivity.class);
                    } else if ("worker".equals(userType)) {
                        intent = new Intent(LoginActivity.this, HandymanDashboardActivity.class);
                    } else {
                        // Handle unknown user type, maybe default to customer or show error
                        intent = new Intent(LoginActivity.this, CustomerDashboardActivity.class);
                        Toast.makeText(this, "Unknown user type, defaulting to customer.", Toast.LENGTH_SHORT).show();
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // This can happen if a user is authenticated but has no Firestore document.
                    // You might want to log them out or send them to the type selection screen.
                    Toast.makeText(this, "User profile not found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isEmailValid(String email) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.setError("Enter a valid email address");
            return false;
        } else {
            binding.emailLayout.setError(null);
            return true;
        }
    }

    private boolean isPasswordValid(String password) {
        if (password.isEmpty() || password.length() < 6) {
            binding.passwordLayout.setError("Password must be at least 6 characters");
            return false;
        } else {
            binding.passwordLayout.setError(null);
            return true;
        }
    }

    private void showProgressBar() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        binding.progressBar.setVisibility(View.GONE);
    }
}
