package com.example.hand_man_work;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hand_man_work.databinding.ActivityBookingBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Phase 4: Implementation of booking request functionality.
 */
public class BookingActivity extends AppCompatActivity {

    private ActivityBookingBinding binding;
    private String workerId;
    private String workerName;
    private Calendar selectedDateTime;
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        workerId = getIntent().getStringExtra("workerId");
        workerName = getIntent().getStringExtra("workerName");

        if (workerId == null || workerName == null) {
            Toast.makeText(this, "Error: Worker data missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.backButton.setOnClickListener(v -> finish());
        binding.workerInfoText.setText("Booking with: " + workerName);
        
        selectedDateTime = Calendar.getInstance();
        updateDateTimeUI();

        binding.datePickerButton.setOnClickListener(v -> showDatePicker());
        binding.timePickerButton.setOnClickListener(v -> showTimePicker());
        binding.submitBookingButton.setOnClickListener(v -> submitBooking());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateTimeUI();
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minute);
                    updateDateTimeUI();
                },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                true);
        timePickerDialog.show();
    }

    private void updateDateTimeUI() {
        binding.dateTimeText.setText(dateTimeFormat.format(selectedDateTime.getTime()));
    }

    private void submitBooking() {
        String description = binding.descriptionEditText.getText().toString().trim();
        String location = binding.locationEditText.getText().toString().trim();
        String customerId = FirebaseAuth.getInstance().getUid();

        if (description.isEmpty()) {
            binding.descriptionLayout.setError("Job description is required");
            return;
        } else {
            binding.descriptionLayout.setError(null);
        }

        if (location.isEmpty()) {
            binding.locationLayout.setError("Location is required");
            return;
        } else {
            binding.locationLayout.setError(null);
        }

        if (customerId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);
        FirestoreHelper.createBooking(
                customerId,
                workerId,
                workerName,
                selectedDateTime.getTime(),
                description,
                location,
                task -> {
                    showLoading(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Booking request sent!", Toast.LENGTH_LONG).show();
                        // Return to home screen
                        Intent intent = new Intent(this, CustomerHomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to send booking request", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void showLoading(boolean isLoading) {
        binding.progressOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.submitBookingButton.setEnabled(!isLoading);
    }
}
