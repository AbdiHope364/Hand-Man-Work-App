package com.example.hand_man_work_new;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hand_man_work_new.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity implements GoogleSignInHelper.GoogleSignInListener {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInHelper googleSignInHelper;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            binding = ActivityLoginBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            // ONE-TIME USE SESSION: Force sign out every time the login screen is created (app start)
            if (mAuth.getCurrentUser() != null) {
                mAuth.signOut();
            }

            // Initialize Google Sign-In Helper
            googleSignInHelper = new GoogleSignInHelper(this, this);
            googleSignInLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            googleSignInHelper.handleSignInResult(result.getData());
                        }
                    }
            );

            setupClickListeners();
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Initialization Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupClickListeners() {
        if (binding.backButton != null) {
            binding.backButton.setOnClickListener(v -> finish());
        }
        binding.loginButton.setOnClickListener(v -> loginUser());
        binding.signUpText.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        
        binding.forgotPasswordText.setOnClickListener(v -> {
            String email = binding.emailEditText.getText().toString().trim();
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailLayout.setError("Enter your registered email");
                return;
            }
            binding.emailLayout.setError(null);
            sendPasswordReset(email);
        });

        binding.googleSignInButton.setOnClickListener(v -> googleSignInHelper.signIn(googleSignInLauncher));
    }

    private void loginUser() {
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        if (!validateInputs(email, password)) return;

        showLoading(true);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        redirectUserBasedOnType(mAuth.getCurrentUser());
                    } else {
                        showLoading(false);
                        String error = task.getException() != null ? task.getException().getMessage() : "Login failed";
                        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validateInputs(String email, String password) {
        boolean isValid = true;
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.setError("Valid email required");
            isValid = false;
        } else {
            binding.emailLayout.setError(null);
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            binding.passwordLayout.setError("Password too short");
            isValid = false;
        } else {
            binding.passwordLayout.setError(null);
        }
        return isValid;
    }

    private void sendPasswordReset(String email) {
        showLoading(true);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            showLoading(false);
            if (task.isSuccessful()) {
                Toast.makeText(this, "Reset email sent!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void redirectUserBasedOnType(FirebaseUser user) {
        if (user == null) return;
        showLoading(true);
        db.collection("users").document(user.getUid()).get()
            .addOnCompleteListener(task -> {
                showLoading(false);
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc != null && doc.exists()) {
                        String type = doc.getString("type");
                        Intent intent = new Intent(this, "worker".equals(type) ? WorkerDashboardActivity.class : CustomerHomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // User exists in Auth but not in Firestore - likely a new Google sign-in
                        startActivity(new Intent(this, UserTypeSelectionActivity.class));
                        finish();
                    }
                } else {
                    Toast.makeText(this, "Database error", Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    public void onGoogleSignInSuccess(FirebaseUser user) {
        redirectUserBasedOnType(user);
    }

    @Override
    public void onGoogleSignInFailure(String error) {
        showLoading(false);
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showLoading(boolean isLoading) {
        if (binding != null) {
            binding.progressOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.loginButton.setEnabled(!isLoading);
            binding.googleSignInButton.setEnabled(!isLoading);
        }
    }
}
