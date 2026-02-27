package com.example.hand_man_work;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hand_man_work.databinding.ActivityUserTypeSelectionBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserTypeSelectionActivity extends AppCompatActivity {

    private ActivityUserTypeSelectionBinding binding;
    private String name;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserTypeSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get user details passed from RegisterActivity
        name = getIntent().getStringExtra("userName");
        phone = getIntent().getStringExtra("userPhone");

        binding.customerButton.setOnClickListener(v -> saveUserProfile("customer"));
        binding.workerButton.setOnClickListener(v -> saveUserProfile("worker"));
    }

    private void saveUserProfile(String userType) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            // Should not happen if registration was successful
            Toast.makeText(this, "Error: No authenticated user found.", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressBar();

        FirestoreHelper.createUserProfile(firebaseUser, name, phone, userType, task -> {
            hideProgressBar();
            if (task.isSuccessful()) {
                // Based on user type, redirect to the correct dashboard
                Intent intent;
                if ("customer".equals(userType)) {
                    intent = new Intent(UserTypeSelectionActivity.this, CustomerDashboardActivity.class);
                } else {
                    intent = new Intent(UserTypeSelectionActivity.this, HandymanDashboardActivity.class);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(UserTypeSelectionActivity.this, "Failed to save user profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgressBar() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        binding.progressBar.setVisibility(View.GONE);
    }
}
