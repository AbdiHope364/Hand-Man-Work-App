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

        // Setup toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Available Workers");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize
        workerList = new ArrayList<>();
        
        // Set up the adapter with click listener
        adapter = new WorkerAdapter(workerList, worker -> {
            // This is where navigation to BookingActivity happens
            Intent intent = new Intent(CustomerHomeActivity.this, BookingActivity.class);
            intent.putExtra("workerId", worker.getUid());
            intent.putExtra("workerName", worker.getName());
            startActivity(intent);
        });

        // Setup RecyclerView
        binding.workerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.workerRecyclerView.setAdapter(adapter);

        // Load workers from Firestore
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
                } else {
                    Toast.makeText(this, "Found " + workerList.size() + " workers", 
                        Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(e -> {
                binding.progressBar.setVisibility(View.GONE);
                binding.emptyStateText.setVisibility(View.VISIBLE);
                binding.emptyStateText.setText("Error: " + e.getMessage());
                Toast.makeText(this, "Failed to load workers", Toast.LENGTH_SHORT).show();
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
