package com.example.hand_man_work;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hand_man_work.databinding.ActivityBookingBinding;

public class BookingActivity extends AppCompatActivity {

    private ActivityBookingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String workerName = getIntent().getStringExtra("workerName");
        if (workerName != null) {
            binding.workerInfo.setText("Booking with: " + workerName);
        }
    }
}
