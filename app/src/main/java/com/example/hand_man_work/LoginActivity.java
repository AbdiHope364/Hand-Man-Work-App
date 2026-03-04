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

/**
 * Updated LoginActivity to handle the new UI layout.
 */
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

        binding.forgotPasswordText.setOnClickListener(v -> {
            String email = binding.emailEditText.getText().toString().trim();
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailLayout.setError("Enter your registered email to reset password");
                return;
            }
            binding.emailLayout.setError(null);
            sendPasswordReset(email);
        });

        binding.googleSignInButton.setOnClickListener(v -> {
            Toast.makeText(this, "Google Sign-In coming soon", Toast.LENGTH_SHORT).show();
        });
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

        showLoading(true);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        redirectUserBasedOnType(user);
                    } else {
                        showLoading(false);
                        String error = task.getException() != null ? task.getException().getMessage() : "Authentication failed.";
                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void sendPasswordReset(String email) {
        showLoading(true);
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    showLoading(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Password reset email sent.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Failed to send reset email.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void redirectUserBasedOnType(FirebaseUser user) {
        if (user == null) return;

        showLoading(true);
        FirestoreHelper.getUserType(user, task -> {
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
                        intent = new Intent(LoginActivity.this, CustomerDashboardActivity.class);
                        Toast.makeText(this, "Unknown user type, defaulting to customer.", Toast.LENGTH_SHORT).show();
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    showLoading(false);
                    // Handle case where user exists in Auth but not in Firestore
                    Toast.makeText(this, "User profile not found. Please register again.", Toast.LENGTH_LONG).show();
                    mAuth.signOut();
                }
            } else {
                showLoading(false);
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

    private void showLoading(boolean isLoading) {
        binding.progressOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.loginButton.setEnabled(!isLoading);
    }
}
