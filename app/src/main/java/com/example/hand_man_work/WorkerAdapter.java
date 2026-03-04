package com.example.hand_man_work;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.WorkerViewHolder> {

    private List<Worker> workerList;
    private OnWorkerClickListener listener;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_worker, parent, false);
        return new WorkerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkerViewHolder holder, int position) {
        Worker worker = workerList.get(position);
        holder.nameText.setText(worker.getName());
        holder.skillsText.setText("Skills: " + worker.getSkillsString());
        holder.rateText.setText(String.format("$%.2f/hr", worker.getHourlyRate()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onWorkerClick(worker);
            }
        });
    }

    @Override
    public int getItemCount() {
        return workerList.size();
    }

    public static class WorkerViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, skillsText, rateText;

        public WorkerViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.worker_name);
            skillsText = itemView.findViewById(R.id.worker_skills);
            rateText = itemView.findViewById(R.id.worker_rate);
        }
    }
}
