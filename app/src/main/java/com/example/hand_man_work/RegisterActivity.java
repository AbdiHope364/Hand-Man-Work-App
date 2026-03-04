package com.example.hand_man_work;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hand_man_work.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Updated RegisterActivity to handle the new UI layout.
 */
public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        // Back button to close activity
        binding.backButton.setOnClickListener(v -> finish());

        // Login text to go back to LoginActivity
        binding.loginText.setOnClickListener(v -> finish());

        binding.registerButton.setOnClickListener(v -> performRegistration());
        
        // Google Sign-In button (Placeholder for now as per Phase 1 requirements)
        binding.googleSignInButton.setOnClickListener(v -> {
            Toast.makeText(this, "Google Sign-In coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void performRegistration() {
        String name = binding.nameEditText.getText().toString().trim();
        String phone = binding.phoneEditText.getText().toString().trim();
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordEditText.getText().toString().trim();

        // Validation
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
                showLoading(false);
                if (task.isSuccessful()) {
                    // Pass name and phone to the role selection screen
                    Intent i = new Intent(this, UserTypeSelectionActivity.class);
                    i.putExtra("name", name);
                    i.putExtra("phone", phone);
                    startActivity(i);
                    finish();
                } else {
                    String error = task.getException() != null ? task.getException().getMessage() : "Registration failed";
                    Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                }
            });
    }

    private void showLoading(boolean isLoading) {
        binding.progressOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.registerButton.setEnabled(!isLoading);
    }
}
