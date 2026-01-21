package com.orabi.project_planner;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy, hh:mm a", Locale.getDefault());

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        // Set task number and name
        holder.tvTaskNumber.setText(String.valueOf(position + 1));
        holder.tvTaskName.setText(task.getName());

        // Set duration
        if (task.getExpected_duration() != null) {
            holder.tvDurationValue.setText(task.getExpected_duration().getDays() + "d");
        } else {
            holder.tvDurationValue.setText("N/A");
        }

        // Set start date
        if (task.getStart_date() != null) {
            holder.tvStartDateValue.setText(dateFormat.format(task.getStart_date()));
        } else {
            holder.tvStartDateValue.setText("Not started");
        }

        // Set expected end date (calculate based on start date + duration)
        if (task.getStart_date() != null && task.getExpected_duration() != null) {
            // Calculate expected end date
            long durationMillis = task.getExpected_duration().toMillis();
            long endTime = task.getStart_date().getTime() + durationMillis;
            String expectedEnd = dateFormat.format(new java.util.Date(endTime));
            holder.tvExpectedEndValue.setText(expectedEnd);
        } else {
            holder.tvExpectedEndValue.setText("N/A");
        }

        // Set status
        String status = task.getStatus();
        holder.tvStatusValue.setText(status != null ? status : "Waiting");

        // Set status color
        if ("completed".equals(status)) {
            holder.tvStatusValue.setTextColor(Color.parseColor("#4CAF50")); // Green
            holder.indicator.setBackgroundColor(Color.parseColor("#4CAF50"));
        } else if ("in_progress".equals(status)) {
            holder.tvStatusValue.setTextColor(Color.parseColor("#FFC107")); // Yellow
            holder.indicator.setBackgroundColor(Color.parseColor("#FFC107"));
        } else if ("late".equals(status)) {
            holder.tvStatusValue.setTextColor(Color.parseColor("#F44336")); // Red
            holder.indicator.setBackgroundColor(Color.parseColor("#F44336"));
        } else {
            holder.tvStatusValue.setTextColor(Color.parseColor("#757575")); // Gray
            holder.indicator.setBackgroundColor(Color.parseColor("#757575"));
        }

        // Set button text based on status
        if ("completed".equals(status)) {
            holder.btnAction.setText("Done");
            holder.btnAction.setEnabled(false);
        } else if ("in_progress".equals(status)) {
            holder.btnAction.setText("Finish");
        } else {
            holder.btnAction.setText("Start");
        }

        // Calculate and display time remaining if task is in progress
        if ("in_progress".equals(status) && task.getStart_date() != null && task.getExpected_duration() != null) {
            long currentTime = System.currentTimeMillis();
            long startTime = task.getStart_date().getTime();
            long duration = task.getExpected_duration().toMillis();
            long endTime = startTime + duration;

            if (currentTime < endTime) {
                long remaining = endTime - currentTime;
                // Convert milliseconds to days, hours, minutes
                long days = remaining / (1000 * 60 * 60 * 24);
                long hours = (remaining % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
                long minutes = (remaining % (1000 * 60 * 60)) / (1000 * 60);

                holder.tvTimeRemainingBadge.setText(days + "d, " + hours + "h, " + minutes + "m");
                holder.tvTimeRemainingBadge.setTextColor(Color.parseColor("#4CAF50")); // Green
            } else {
                holder.tvTimeRemainingBadge.setText("Late!");
                holder.tvTimeRemainingBadge.setTextColor(Color.parseColor("#F44336")); // Red
            }
        } else {
            holder.tvTimeRemainingBadge.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskNumber, tvTaskName, tvDurationValue, tvStartDateValue;
        TextView tvExpectedEndValue, tvStatusValue, tvTimeRemainingBadge;
        View indicator;
        TextView btnAction;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find all views from item_task.xml
            tvTaskNumber = itemView.findViewById(R.id.tvTaskNumber);
            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            tvDurationValue = itemView.findViewById(R.id.tvDurationValue);
            tvStartDateValue = itemView.findViewById(R.id.tvStartDateValue);
            tvExpectedEndValue = itemView.findViewById(R.id.tvExpectedEndValue);
            tvStatusValue = itemView.findViewById(R.id.tvStatusValue);
            tvTimeRemainingBadge = itemView.findViewById(R.id.tvTimeRemainingBadge);
            indicator = itemView.findViewById(R.id.taskStatusIndicator);
            btnAction = itemView.findViewById(R.id.btnAction);

            // Optional: Add click listener to the action button
            btnAction.setOnClickListener(v -> {
                // Handle start/finish button click
                // You can implement this later
            });
        }
    }
}