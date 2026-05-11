package com.example.hand_man_work;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hand_man_work.databinding.ActivityWorkerProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkerProfileActivity extends AppCompatActivity {

    private ActivityWorkerProfileBinding binding;
    private String userId;
    private Uri imageUri;
    
    // Improved Image Picker Launcher for maximum compatibility
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    uploadProfileImage();
                }
            }
    );

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

        // Back button
        binding.backButton.setOnClickListener(v -> finish());
        
        // Setup interactive photo picker
        binding.changePhotoButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            imagePickerLauncher.launch(Intent.createChooser(intent, "Select Profile Picture"));
        });

        loadUserData();

        binding.saveButton.setOnClickListener(v -> saveProfile());
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
                    
                    Object skillsObj = document.get("skills");
                    if (skillsObj instanceof List) {
                        List<String> skills = (List<String>) skillsObj;
                        binding.skillsEditText.setText(TextUtils.join(", ", skills));
                    }
                    
                    Double rate = document.getDouble("hourlyRate");
                    if (rate != null) {
                        binding.rateEditText.setText(String.valueOf(rate));
                    }
                    
                    String photoUrl = document.getString("photoUrl");
                    if (photoUrl != null && !photoUrl.isEmpty()) {
                        Glide.with(this).load(photoUrl).circleCrop().into(binding.profileImage);
                    }
                }
            } else {
                Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadProfileImage() {
        if (imageUri == null) return;

        showLoading(true);
        // Using unique path to bypass cache and ensure instant update
        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("profile_images")
                .child(userId + "_" + System.currentTimeMillis() + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    savePhotoUrlToFirestore(downloadUrl);
                }))
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void savePhotoUrlToFirestore(String url) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("photoUrl", url);

        FirestoreHelper.updateUser(userId, updates, task -> {
            showLoading(false);
            if (task.isSuccessful()) {
                Glide.with(this).load(url).circleCrop().into(binding.profileImage);
                Toast.makeText(this, "Photo updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Sync failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        String name = binding.nameEditText.getText().toString().trim();
        String phone = binding.phoneEditText.getText().toString().trim();
        String skillsStr = binding.skillsEditText.getText().toString().trim();
        String rateStr = binding.rateEditText.getText().toString().trim();

        if (validateInput(name, phone, skillsStr, rateStr)) {
            List<String> skills = new ArrayList<>();
            for (String s : skillsStr.split(",")) {
                if (!s.trim().isEmpty()) {
                    skills.add(s.trim());
                }
            }
            
            double rate = Double.parseDouble(rateStr);

            showLoading(true);
            FirestoreHelper.updateWorkerProfile(userId, name, phone, skills, rate, null, task -> {
                showLoading(false);
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Worker profile saved!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean validateInput(String name, String phone, String skills, String rate) {
        boolean isValid = true;
        if (name.isEmpty()) {
            binding.nameLayout.setError("Name required");
            isValid = false;
        } else {
            binding.nameLayout.setError(null);
        }
        if (phone.isEmpty()) {
            binding.phoneLayout.setError("Phone required");
            isValid = false;
        } else {
            binding.phoneLayout.setError(null);
        }
        if (skills.isEmpty()) {
            binding.skillsLayout.setError("Skills required");
            isValid = false;
        } else {
            binding.skillsLayout.setError(null);
        }
        if (rate.isEmpty()) {
            binding.rateLayout.setError("Rate required");
            isValid = false;
        } else {
            try {
                Double.parseDouble(rate);
                binding.rateLayout.setError(null);
            } catch (NumberFormatException e) {
                binding.rateLayout.setError("Invalid number");
                isValid = false;
            }
        }
        return isValid;
    }

    private void showLoading(boolean isLoading) {
        binding.progressOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.saveButton.setEnabled(!isLoading);
        binding.changePhotoButton.setEnabled(!isLoading);
    }
}
