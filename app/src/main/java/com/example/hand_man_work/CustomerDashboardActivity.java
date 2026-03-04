package com.example.hand_man_work;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hand_man_work.databinding.ActivityCustomerDashboardBinding;

public class CustomerDashboardActivity extends AppCompatActivity {

    private ActivityCustomerDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnFindWorkers.setOnClickListener(v -> {
            startActivity(new Intent(CustomerDashboardActivity.this, CustomerHomeActivity.class));
        });

        binding.btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(CustomerDashboardActivity.this, CustomerProfileActivity.class));
        });
    }
}
