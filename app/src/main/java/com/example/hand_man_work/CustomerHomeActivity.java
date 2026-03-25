package com.example.hand_man_work;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hand_man_work.databinding.ActivityCustomerHomeBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CustomerHomeActivity extends AppCompatActivity {

    private ActivityCustomerHomeBinding binding;
    private WorkerAdapter adapter;
    private List<Worker> workerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Change back button to navigate to Customer Profile Activity
        binding.backButton.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerHomeActivity.this, CustomerProfileActivity.class);
            startActivity(intent);
        });

        // Initialize
        workerList = new ArrayList<>();
        
        adapter = new WorkerAdapter(workerList, worker -> {
            Intent intent = new Intent(CustomerHomeActivity.this, BookingActivity.class);
            intent.putExtra("workerId", worker.getUid());
            intent.putExtra("workerName", worker.getName());
            startActivity(intent);
        });

        binding.workerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.workerRecyclerView.setAdapter(adapter);

        loadWorkers();
    }

    private void loadWorkers() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.emptyStateText.setVisibility(View.GONE);

        FirebaseFirestore.getInstance()
            .collection("users")
            .whereEqualTo("type", "worker")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                binding.progressBar.setVisibility(View.GONE);
                workerList.clear();
                
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Worker worker = document.toObject(Worker.class);
                    worker.setUid(document.getId());
                    workerList.add(worker);
                }
                
                adapter.notifyDataSetChanged();
                
                if (workerList.isEmpty()) {
                    binding.emptyStateText.setVisibility(View.VISIBLE);
                }
            })
            .addOnFailureListener(e -> {
                binding.progressBar.setVisibility(View.GONE);
                binding.emptyStateText.setVisibility(View.VISIBLE);
                binding.emptyStateText.setText("Error: " + e.getMessage());
            });
    }
}
