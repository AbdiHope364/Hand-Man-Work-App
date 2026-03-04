package com.example.hand_man_work;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hand_man_work.databinding.ActivityCustomerProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

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

        binding.backButton.setOnClickListener(v -> finish());
        
        setupTextWatchers();
        loadUserData();

        binding.saveButton.setOnClickListener(v -> saveProfile());
        
        binding.changePhotoButton.setOnClickListener(v -> {
            Toast.makeText(this, "Photo upload coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupTextWatchers() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                updateProgress();
            }
        };

        binding.nameEditText.addTextChangedListener(watcher);
        binding.phoneEditText.addTextChangedListener(watcher);
        binding.addressEditText.addTextChangedListener(watcher);
    }

    private void updateProgress() {
        int progress = 0;
        if (!binding.nameEditText.getText().toString().trim().isEmpty()) progress += 33;
        if (!binding.phoneEditText.getText().toString().trim().isEmpty()) progress += 33;
        if (!binding.addressEditText.getText().toString().trim().isEmpty()) progress += 34;
        
        binding.completionProgress.setProgress(progress);
        binding.completionText.setText("Profile Completion: " + progress + "%");
    }

    private void loadUserData() {
        showLoading(true);
        FirestoreHelper.getUserData(userId, task -> {
            showLoading(false);
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    binding.nameEditText.setText(document.getString("name"));
                    binding.phoneEditText.setText(document.getString("phone"));
                    binding.addressEditText.setText(document.getString("address"));
                    binding.emailEditText.setText(document.getString("email"));
                    
                    updateProgress();
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
            showLoading(true);
            
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", name);
            updates.put("phone", phone);
            updates.put("address", address);

            FirestoreHelper.updateCustomerProfile(userId, updates, task -> {
                showLoading(false);
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

    private void showLoading(boolean isLoading) {
        binding.progressOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.saveButton.setEnabled(!isLoading);
    }
}
