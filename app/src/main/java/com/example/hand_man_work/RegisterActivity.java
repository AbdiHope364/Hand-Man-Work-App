package com.example.hand_man_work;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hand_man_work.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = binding.nameEditText.getText().toString().trim();
        String phone = binding.phoneEditText.getText().toString().trim();
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        if (!isNameValid(name) | !isPhoneValid(phone) | !isEmailValid(email) | !isPasswordValid(password)) {
            return;
        }

        showProgressBar();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    hideProgressBar();
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        // On successful registration, go to UserTypeSelectionActivity
                        Intent intent = new Intent(RegisterActivity.this, UserTypeSelectionActivity.class);
                        intent.putExtra("userName", name);
                        intent.putExtra("userPhone", phone);
                        startActivity(intent);
                        finish(); // Finish this activity so user can't come back
                    } else {
                        // If sign up fails, display a message to the user.
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean isNameValid(String name) {
        if (name.isEmpty()) {
            binding.nameLayout.setError("Name is required");
            return false;
        } else {
            binding.nameLayout.setError(null);
            return true;
        }
    }

    private boolean isPhoneValid(String phone) {
        if (phone.isEmpty()) {
            binding.phoneLayout.setError("Phone number is required");
            return false;
        } else {
            binding.phoneLayout.setError(null);
            return true;
        }
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
