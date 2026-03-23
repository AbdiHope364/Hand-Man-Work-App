package com.example.hand_man_work;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hand_man_work.databinding.ItemWorkerBinding;

import java.util.List;
import java.util.Locale;

public class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.WorkerViewHolder> {

    private final List<Worker> workerList;
    private final OnWorkerClickListener listener;

    public interface OnWorkerClickListener {
        void onWorkerClick(Worker worker);
    }

    public WorkerAdapter(List<Worker> workerList, OnWorkerClickListener listener) {
        this.workerList = workerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WorkerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWorkerBinding binding = ItemWorkerBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false);
        return new WorkerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkerViewHolder holder, int position) {
        Worker worker = workerList.get(position);
        holder.bind(worker, listener);
    }

    @Override
    public int getItemCount() {
        return workerList.size();
    }

    static class WorkerViewHolder extends RecyclerView.ViewHolder {
        private final ItemWorkerBinding binding;

        public WorkerViewHolder(ItemWorkerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final Worker worker, final OnWorkerClickListener listener) {
            binding.workerName.setText(worker.getName());
            binding.workerSkills.setText("Skills: " + worker.getSkillsString());
            binding.workerRate.setText(String.format(Locale.getDefault(), "$%.2f/hr", worker.getHourlyRate()));
            
            // Set click listener on the entire item
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onWorkerClick(worker);
                }
            });
        }
    }
}
