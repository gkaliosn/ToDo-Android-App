package com.example.todo;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private OnTaskDeleteListener deleteListener;
    private OnTaskClickListener clickListener;
    private OnTaskEditListener editListener;

    public interface OnTaskDeleteListener {
        void onTaskDelete(Task task);
    }

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }
    public interface OnTaskEditListener {
        void onTaskEdit(Task task);
    }

    public TaskAdapter(List<Task> tasks, OnTaskDeleteListener deleteListener, OnTaskClickListener clickListener, OnTaskEditListener editListener) {
        this.taskList = tasks;
        this.deleteListener = deleteListener;
        this.clickListener = clickListener;
        this.editListener = editListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.titleTextView.setText(task.getTitle());

        int priority = task.getPriority();
        switch (priority) {
            case 0:
                holder.priorityCircle.setImageResource(R.drawable.circle_green);
                break;
            case 1:
                holder.priorityCircle.setImageResource(R.drawable.circle_yellow);
                break;
            case 2:
                holder.priorityCircle.setImageResource(R.drawable.circle_red);
                break;
        }

        // ΝΕΟ: ανακατεύθυνση όταν πατηθεί item
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onTaskClick(task);
            }
        });

        holder.editbutton.setOnClickListener(v ->{
            if (editListener != null) {
                editListener.onTaskEdit(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public Task getTaskAt(int position) {
        return taskList.get(position);
    }

    public void removeItem(int position) {
        Task task = taskList.get(position);
        taskList.remove(position);
        notifyItemRemoved(position);
        if (deleteListener != null) {
            deleteListener.onTaskDelete(task);
        }
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView priorityCircle;
        ImageView editbutton;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.taskTitle);
            priorityCircle = itemView.findViewById(R.id.priorityCircle);
            editbutton = itemView.findViewById(R.id.editButton);
        }
    }
}
