package com.example.hand_man_work_new;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.hand_man_work_new.databinding.ActivityWorkerDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class WorkerDashboardActivity extends AppCompatActivity {
    private ActivityWorkerDashboardBinding binding;
    private BookingAdapter adapter;
    private List<Booking> bookingList = new ArrayList<>();
    private FirebaseFirestore db;
    private String workerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWorkerDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        workerId = FirebaseAuth.getInstance().getUid();

        adapter = new BookingAdapter(bookingList);
        binding.rvWorkerBookings.setLayoutManager(new LinearLayoutManager(this));
        binding.rvWorkerBookings.setAdapter(adapter);

        binding.statusChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipPending) loadBookings("pending");
            else if (checkedId == R.id.chipActive) loadBookings("accepted");
            else if (checkedId == R.id.chipHistory) loadBookings("completed");
        });

        loadBookings("pending"); // Default view
    }

    private void loadBookings(String status) {
        db.collection("bookings")
            .whereEqualTo("workerId", workerId)
            .whereEqualTo("status", status)
            .addSnapshotListener((value, error) -> {
                if (value != null) {
                    bookingList.clear();
                    bookingList.addAll(value.toObjects(Booking.class));
                    adapter.notifyDataSetChanged();
                }
            });
    }
}
