package com.orabi.project_planner;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;
    private Context context;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy, hh:mm a", Locale.getDefault());
    private DBHelperTask dbHelperTask;

    public TaskAdapter(List<Task> taskList, Context context) {
        this.taskList = taskList;
        this.context = context;
        this.dbHelperTask = new DBHelperTask(context);
    }

    public void updateTasks(List<Task> newList) {
        this.taskList = newList;
        notifyDataSetChanged();
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

        // Set basic info
        holder.tvTaskNumber.setText(String.valueOf(position + 1));
        holder.tvTaskName.setText(task.getName());

        // Set duration
        if (task.getExpected_duration() != null) {
            holder.tvDurationValue.setText(task.getExpected_duration().toString());
        } else {
            holder.tvDurationValue.setText("N/A");
        }

        // Set start date
        if (task.getStart_date() != null) {
            holder.tvStartDateValue.setText(dateFormat.format(task.getStart_date()));
        } else {
            holder.tvStartDateValue.setText("Not started");
        }

        // Calculate expected end date
        if (task.getStart_date() != null && task.getExpected_duration() != null) {
            long endTime = task.getStart_date().getTime() + task.getExpected_duration().toMillis();
            holder.tvExpectedEndValue.setText(dateFormat.format(new Date(endTime)));
        } else {
            holder.tvExpectedEndValue.setText("N/A");
        }

        // Set status
        String status = task.getStatus();
        holder.tvStatusValue.setText(status != null ? status : "Waiting");

        // Task 1: Make button unclickable when status is "Waiting"
        // Task 3: When clicking "End" button, end current task and start next task
        if ("completed".equals(status)) {
            holder.btnAction.setText("Completed");
            holder.btnAction.setEnabled(false);
            holder.btnAction.setBackgroundResource(R.drawable.rounded_button_bg_disabled);
            holder.btnAction.setAlpha(0.5f);
            holder.indicator.setBackgroundColor(Color.parseColor("#4CAF50"));
            holder.tvStatusValue.setTextColor(Color.parseColor("#4CAF50"));
            holder.tvTimeRemainingBadge.setText("");

            // Display end date if available
            if (task.getEndDate() != null) {
                holder.tvStatusValue.setText("Ended: " + dateFormat.format(task.getEndDate()));
            }
        } else if ("in_progress".equals(status)) {
            holder.btnAction.setText("End");
            holder.btnAction.setEnabled(true);
            holder.btnAction.setBackgroundResource(R.drawable.rounded_button_bg);
            holder.btnAction.setAlpha(1f);
            holder.indicator.setBackgroundColor(Color.parseColor("#FFC107"));
            holder.tvStatusValue.setTextColor(Color.parseColor("#FFC107"));

            // Calculate remaining time
            if (task.getStart_date() != null && task.getExpected_duration() != null) {
                long currentTime = System.currentTimeMillis();
                long startTime = task.getStart_date().getTime();
                long duration = task.getExpected_duration().toMillis();
                long endTime = startTime + duration;

                if (currentTime < endTime) {
                    long remaining = endTime - currentTime;
                    long days = remaining / (1000 * 60 * 60 * 24);
                    long hours = (remaining % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
                    long minutes = (remaining % (1000 * 60 * 60)) / (1000 * 60);
                    holder.tvTimeRemainingBadge.setText(days + "d, " + hours + "h, " + minutes + "m");
                    holder.tvTimeRemainingBadge.setTextColor(Color.parseColor("#4CAF50"));
                } else {
                    holder.tvTimeRemainingBadge.setText("Late!");
                    holder.tvTimeRemainingBadge.setTextColor(Color.parseColor("#F44336"));
                }
            }
        } else { // Waiting status
            holder.btnAction.setText("Waiting");
            holder.btnAction.setEnabled(false); // Task 1: Make unclickable
            holder.btnAction.setBackgroundResource(R.drawable.rounded_button_bg_disabled);
            holder.btnAction.setAlpha(0.5f);
            holder.indicator.setBackgroundColor(Color.parseColor("#757575"));
            holder.tvStatusValue.setTextColor(Color.parseColor("#757575"));
            holder.tvTimeRemainingBadge.setText("");
        }

        // Set click listener for action button
        holder.btnAction.setOnClickListener(v -> {
            if ("End".equals(holder.btnAction.getText().toString())) {
                endTask(task);
            }
        });
    }

    private void startTask(Task task) {
        // Task 2: Check if previous task is null or completed
        if (task.getPrevious_task() == null ||
                (task.getPrevious_task() != null && "completed".equals(task.getPrevious_task().getStatus()))) {
            task.setStart_date(new Date());
            task.setStatus("in_progress");
            dbHelperTask.updateTaskDetails(task);
            notifyDataSetChanged();
        }
    }

    private void endTask(Task task) {
        // Task 3: End current task and start next task automatically
        // Set end date (Task 6)
        task.setEndDate(new Date());
        task.setStatus("completed");
        dbHelperTask.updateTaskDetails(task);

        // Start next task if exists
        if (task.getNext_task() != null) {
            Task nextTask = dbHelperTask.getTaskById(task.getNext_task().getId());
            if (nextTask != null && "Waiting".equals(nextTask.getStatus())) {
                nextTask.setStart_date(new Date());
                nextTask.setStatus("in_progress");
                dbHelperTask.updateTaskDetails(nextTask);
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskNumber, tvTaskName, tvDurationValue, tvStartDateValue;
        TextView tvExpectedEndValue, tvStatusValue, tvTimeRemainingBadge;
        View indicator;
        Button btnAction;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskNumber = itemView.findViewById(R.id.tvTaskNumber);
            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            tvDurationValue = itemView.findViewById(R.id.tvDurationValue);
            tvStartDateValue = itemView.findViewById(R.id.tvStartDateValue);
            tvExpectedEndValue = itemView.findViewById(R.id.tvExpectedEndValue);
            tvStatusValue = itemView.findViewById(R.id.tvStatusValue);
            tvTimeRemainingBadge = itemView.findViewById(R.id.tvTimeRemainingBadge);
            indicator = itemView.findViewById(R.id.taskStatusIndicator);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }
}