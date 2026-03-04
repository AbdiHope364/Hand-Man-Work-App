package com.example.hand_man_work;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hand_man_work.databinding.ActivityCustomerHomeBinding;
import com.google.firebase.firestore.DocumentSnapshot;

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

        workerList = new ArrayList<>();
        adapter = new WorkerAdapter(workerList, worker -> {
            Intent intent = new Intent(CustomerHomeActivity.this, BookingActivity.class);
            intent.putExtra("workerName", worker.getName());
            intent.putExtra("workerId", worker.getUid());
            startActivity(intent);
        });

        binding.workerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.workerRecyclerView.setAdapter(adapter);

        fetchWorkers();
    }

    private void fetchWorkers() {
        binding.progressBar.setVisibility(View.VISIBLE);
        FirestoreHelper.getWorkers(task -> {
            binding.progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                workerList.clear();
                for (DocumentSnapshot doc : task.getResult()) {
                    Worker worker = doc.toObject(Worker.class);
                    if (worker != null) {
                        workerList.add(worker);
                    }
                }
                adapter.notifyDataSetChanged();

                if (workerList.isEmpty()) {
                    binding.emptyStateText.setVisibility(View.VISIBLE);
                } else {
                    binding.emptyStateText.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(this, "Error fetching workers", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
