package com.example.hand_man_work;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hand_man_work.databinding.ActivityWorkerProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorkerProfileActivity extends AppCompatActivity {

    private ActivityWorkerProfileBinding binding;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWorkerProfileBinding.inflate(getLayoutInflater());
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
                    
                    List<String> skills = (List<String>) document.get("skills");
                    if (skills != null) {
                        binding.skillsEditText.setText(TextUtils.join(", ", skills));
                    }
                    
                    Double rate = document.getDouble("hourlyRate");
                    if (rate != null) {
                        binding.rateEditText.setText(String.valueOf(rate));
                    }
                    
                    binding.photoUrlEditText.setText(document.getString("photoUrl"));
                }
            } else {
                Toast.makeText(this, "Failed to load profile data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        String name = binding.nameEditText.getText().toString().trim();
        String phone = binding.phoneEditText.getText().toString().trim();
        String skillsStr = binding.skillsEditText.getText().toString().trim();
        String rateStr = binding.rateEditText.getText().toString().trim();
        String photoUrl = binding.photoUrlEditText.getText().toString().trim();

        if (validateInput(name, phone, skillsStr, rateStr)) {
            List<String> skills = new ArrayList<>();
            for (String s : skillsStr.split(",")) {
                if (!s.trim().isEmpty()) {
                    skills.add(s.trim());
                }
            }
            
            double rate = Double.parseDouble(rateStr);

            showProgressBar();
            FirestoreHelper.updateWorkerProfile(userId, name, phone, skills, rate, photoUrl, task -> {
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

    private boolean validateInput(String name, String phone, String skills, String rate) {
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

        if (skills.isEmpty()) {
            binding.skillsLayout.setError("Skills are required");
            isValid = false;
        } else {
            binding.skillsLayout.setError(null);
        }

        if (rate.isEmpty()) {
            binding.rateLayout.setError("Hourly rate is required");
            isValid = false;
        } else {
            try {
                Double.parseDouble(rate);
                binding.rateLayout.setError(null);
            } catch (NumberFormatException e) {
                binding.rateLayout.setError("Invalid rate");
                isValid = false;
            }
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
