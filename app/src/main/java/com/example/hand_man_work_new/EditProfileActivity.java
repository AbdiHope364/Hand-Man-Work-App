package com.example.hand_man_work_new;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.hand_man_work_new.databinding.ActivityEditProfileBinding;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private Uri imageUri;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("profile_pics");
        userId = FirebaseAuth.getInstance().getUid();

        ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        binding.imgProfile.setImageURI(imageUri);
                    }
                }
        );

        binding.btnChangeImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            pickImage.launch(intent);
        });

        binding.btnSaveProfile.setOnClickListener(v -> uploadData());
    }

    private void uploadData() {
        if (imageUri != null) {
            StorageReference fileRef = storageRef.child(userId + ".jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> saveToFirestore(uri.toString()));
            }).addOnFailureListener(e -> Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show());
        } else {
            saveToFirestore(null);
        }
    }

    private void saveToFirestore(String imageUrl) {
        List<String> selectedSkills = new ArrayList<>();
        for (int i = 0; i < binding.skillChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) binding.skillChipGroup.getChildAt(i);
            if (chip.isChecked()) selectedSkills.add(chip.getText().toString());
        }

        db.collection("users").document(userId)
                .update("bio", binding.etBio.getText().toString(),
                        "skills", selectedSkills,
                        "imageUrl", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}
