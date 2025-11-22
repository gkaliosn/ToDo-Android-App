package com.example.todo;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CompletedTaskAdapter extends RecyclerView.Adapter<CompletedTaskAdapter.CompletedTaskViewHolder> {

    private List<Task> completedTaskList;

    public CompletedTaskAdapter(List<Task> completedTasks) {
        this.completedTaskList = completedTasks;
    }

    @NonNull
    @Override
    public CompletedTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.completed_task_item, parent, false);
        return new CompletedTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedTaskViewHolder holder, int position) {
        Task task = completedTaskList.get(position);
        holder.titleTextView.setText(task.getTitle() + " (Completed)");
        // Το background έχει ήδη στο xml το πράσινο χρώμα
    }

    @Override
    public int getItemCount() {
        return completedTaskList.size();
    }

    static class CompletedTaskViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;

        CompletedTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.taskTitle);
        }
    }
}
