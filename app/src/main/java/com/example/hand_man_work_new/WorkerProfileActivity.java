package com.example.hand_man_work_new;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hand_man_work_new.databinding.ActivityWorkerProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.UUID;

public class WorkerProfileActivity extends AppCompatActivity {
    private ActivityWorkerProfileBinding binding;
    private FirebaseFirestore db;
    private String workerId; // Passed from Intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWorkerProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        workerId = getIntent().getStringExtra("workerId");

        binding.btnBookNow.setOnClickListener(v -> createBookingRequest());
    }

    private void createBookingRequest() {
        String customerId = FirebaseAuth.getInstance().getUid();
        String customerName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String bookingId = UUID.randomUUID().toString();

        Booking newBooking = new Booking(bookingId, customerId, customerName, workerId);

        db.collection("bookings").document(bookingId)
                .set(newBooking)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Booking Request Sent!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
