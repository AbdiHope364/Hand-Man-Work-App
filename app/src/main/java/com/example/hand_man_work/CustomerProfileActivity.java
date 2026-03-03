package com.example.hand_man_work;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hand_man_work.databinding.ActivityCustomerProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

public class CustomerProfileActivity extends AppCompatActivity {

    private ActivityCustomerProfileBinding binding;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            finish();
            return;
        }

        loadUserData();

        binding.saveButton.setOnClickListener(v -> saveProfile());
    }

    private void loadUserData() {
        showProgressBar();
        FirestoreHelper.getUserData(userId, task -> {
            hideProgressBar();
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    binding.nameEditText.setText(document.getString("name"));
                    binding.phoneEditText.setText(document.getString("phone"));
                    binding.addressEditText.setText(document.getString("address"));
                }
            } else {
                Toast.makeText(this, "Failed to load profile data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        String name = binding.nameEditText.getText().toString().trim();
        String phone = binding.phoneEditText.getText().toString().trim();
        String address = binding.addressEditText.getText().toString().trim();

        if (validateInput(name, phone, address)) {
            showProgressBar();
            FirestoreHelper.updateCustomerProfile(userId, name, phone, address, task -> {
                hideProgressBar();
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean validateInput(String name, String phone, String address) {
        boolean isValid = true;
        if (name.isEmpty()) {
            binding.nameLayout.setError("Name is required");
            isValid = false;
        } else {
            binding.nameLayout.setError(null);
        }

        if (phone.isEmpty()) {
            binding.phoneLayout.setError("Phone is required");
            isValid = false;
        } else {
            binding.phoneLayout.setError(null);
        }

        if (address.isEmpty()) {
            binding.addressLayout.setError("Address is required");
            isValid = false;
        } else {
            binding.addressLayout.setError(null);
        }
        return isValid;
    }

    private void showProgressBar() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        binding.progressBar.setVisibility(View.GONE);
    }
}
