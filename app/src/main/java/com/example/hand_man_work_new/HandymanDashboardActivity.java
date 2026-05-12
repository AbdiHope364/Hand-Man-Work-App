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

        binding.btnProfile.setOnClickListener(v -> {
            // This requires WorkerProfileActivity to be in the same package
            startActivity(new Intent(HandymanDashboardActivity.this, WorkerProfileActivity.class));
        });
    }
}
