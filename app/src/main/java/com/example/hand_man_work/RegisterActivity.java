package com.example.hand_man_work;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hand_man_work.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Updated RegisterActivity to support Google Sign-In and standard registration.
 */
public class RegisterActivity extends AppCompatActivity implements GoogleSignInHelper.GoogleSignInListener {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInHelper googleSignInHelper;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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

        // UI Setup
        binding.backButton.setOnClickListener(v -> finish());
        binding.loginText.setOnClickListener(v -> finish());
        binding.registerButton.setOnClickListener(v -> performRegistration());
        binding.googleSignInButton.setOnClickListener(v -> googleSignInHelper.signIn(googleSignInLauncher));
    }

    private void performRegistration() {
        String name = binding.nameEditText.getText().toString().trim();
        String phone = binding.phoneEditText.getText().toString().trim();
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordEditText.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 8) {
            binding.passwordLayout.setError("Minimum 8 characters required");
            return;
        } else {
            binding.passwordLayout.setError(null);
        }

        if (!password.equals(confirmPassword)) {
            binding.confirmPasswordLayout.setError("Passwords do not match");
            return;
        } else {
            binding.confirmPasswordLayout.setError(null);
        }

        if (!binding.termsCheckbox.isChecked()) {
            Toast.makeText(this, "Please agree to the Terms and Conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Standard registration successful, move to role selection
                    Intent i = new Intent(this, UserTypeSelectionActivity.class);
                    i.putExtra("name", name);
                    i.putExtra("phone", phone);
                    startActivity(i);
                    finish();
                } else {
                    showLoading(false);
                    String error = task.getException() != null ? task.getException().getMessage() : "Registration failed";
                    Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                }
            });
    }

    @Override
    public void onGoogleSignInSuccess(FirebaseUser user) {
        if (user == null) return;
        
        // Check if user already exists in Firestore
        db.collection("users").document(user.getUid()).get()
            .addOnCompleteListener(task -> {
                showLoading(false);
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        // User exists, redirect to home
                        String type = document.getString("type");
                        Intent intent = new Intent(this, "worker".equals(type) ? WorkerDashboardActivity.class : CustomerHomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // New Google user, send to role selection
                        Intent intent = new Intent(this, UserTypeSelectionActivity.class);
                        // Google account usually provides name
                        intent.putExtra("name", user.getDisplayName());
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(this, "Database error", Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    public void onGoogleSignInFailure(String error) {
        showLoading(false);
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showLoading(boolean isLoading) {
        binding.progressOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.registerButton.setEnabled(!isLoading);
        binding.googleSignInButton.setEnabled(!isLoading);
    }
}
