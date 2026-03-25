package com.example.hand_man_work;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hand_man_work.databinding.ActivityCustomerDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;

public class CustomerDashboardActivity extends AppCompatActivity {

    private ActivityCustomerDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Card Find Workers click
        binding.cardFindWorkers.setOnClickListener(v -> {
            startActivity(new Intent(CustomerDashboardActivity.this, CustomerHomeActivity.class));
        });

        // Card Profile click
        binding.cardProfile.setOnClickListener(v -> {
            startActivity(new Intent(CustomerDashboardActivity.this, CustomerProfileActivity.class));
        });

        // Logout button
        binding.btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(CustomerDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        // Prevent accidental exit, show toast or just finish if it's the right flow
        super.onBackPressed();
    }
}
