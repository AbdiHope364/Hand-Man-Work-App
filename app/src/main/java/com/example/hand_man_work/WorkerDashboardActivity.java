package com.example.hand_man_work;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hand_man_work.databinding.ActivityWorkerDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;

public class WorkerDashboardActivity extends AppCompatActivity {

    private ActivityWorkerDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize view binding
        binding = ActivityWorkerDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Worker Dashboard");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Setup click listeners
        setupClickListeners();
    }

    private void setupClickListeners() {
        // Profile button
        binding.profileButton.setOnClickListener(v -> {
            startActivity(new Intent(this, WorkerProfileActivity.class));
        });

        // View Bookings button
        binding.viewBookingsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Bookings feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        // Logout button
        binding.logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
