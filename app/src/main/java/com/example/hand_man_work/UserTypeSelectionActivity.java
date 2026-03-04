package com.example.hand_man_work;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hand_man_work.databinding.ActivityUserTypeSelectionBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Simplified UserTypeSelectionActivity to handle mandatory role selection.
 */
public class UserTypeSelectionActivity extends AppCompatActivity {

    private ActivityUserTypeSelectionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserTypeSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.customerButton.setOnClickListener(v -> completeRegistration("customer"));
        binding.workerButton.setOnClickListener(v -> completeRegistration("worker"));
    }

    private void completeRegistration(String type) {
        String name = getIntent().getStringExtra("name");
        String phone = getIntent().getStringExtra("phone");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Session expired. Please login.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        FirestoreHelper.saveUser(user, name, phone, type, task -> {
            binding.progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                // Redirect based on type
                Intent intent;
                if ("customer".equals(type)) {
                    intent = new Intent(this, CustomerDashboardActivity.class);
                } else {
                    intent = new Intent(this, HandymanDashboardActivity.class);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to save profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
