package com.example.hand_man_work_new;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.hand_man_work_new.databinding.ActivityCustomerHomeBinding;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class CustomerHomeActivity extends AppCompatActivity {
    private ActivityCustomerHomeBinding binding;
    private WorkerAdapter adapter;
    private List<Worker> workerList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        setupRecyclerView();
        loadWorkers(null); // Load all initially

        binding.categoryFilterGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = findViewById(checkedId);
            if (chip != null) {
                String category = chip.getText().toString();
                loadWorkers(category.equals("All") ? null : category);
            }
        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { filterByName(query); return true; }
            @Override
            public boolean onQueryTextChange(String newText) { filterByName(newText); return true; }
        });
    }

    private void setupRecyclerView() {
        adapter = new WorkerAdapter(workerList);
        binding.rvWorkers.setLayoutManager(new LinearLayoutManager(this));
        binding.rvWorkers.setAdapter(adapter);
    }

    private void loadWorkers(String category) {
        Query query = db.collection("users").whereEqualTo("userType", "worker").whereEqualTo("isVerified", true);
        if (category != null) query = query.whereEqualTo("category", category);

        query.addSnapshotListener((value, error) -> {
            if (value != null) {
                workerList.clear();
                workerList.addAll(value.toObjects(Worker.class));
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void filterByName(String text) {
        List<Worker> filteredList = new ArrayList<>();
        for (Worker w : workerList) {
            if (w.getName().toLowerCase().contains(text.toLowerCase()) || 
                w.getAddress().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(w);
            }
        }
        adapter.updateList(filteredList);
    }
}
