package com.example.hand_man_work_new;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hand_man_work_new.databinding.ActivityHandymanDashboardBinding;

public class HandymanDashboardActivity extends AppCompatActivity {

    private ActivityHandymanDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHandymanDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Launch the Profile Editing screen
        binding.btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(HandymanDashboardActivity.this, EditProfileActivity.class));
        });
    }
}
