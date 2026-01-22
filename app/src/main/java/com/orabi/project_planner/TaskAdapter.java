package com.orabi.project_planner;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;
    private Context context;
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    private DBHelperTask dbHelperTask;

    private int currentPlanId; // معرف الخطة الحالية
    private OnAllTasksCompletedListener tasksCompletedListener;

    public TaskAdapter(List<Task> taskList, Context context, int planId) {
        this.taskList = taskList;
        this.context = context;
        this.dbHelperTask = new DBHelperTask(context);
        this.currentPlanId = planId; // تخزين معرف الخطة
    }

    public interface OnAllTasksCompletedListener {
        void onAllTasksCompleted(int planId);
    }

    public void setOnAllTasksCompletedListener(OnAllTasksCompletedListener listener) {
        this.tasksCompletedListener = listener;
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

    private String formatDateToYYYYMMDDHHMM(Date date) {
        if (date == null) {
            return "--";
        }
        try {
            return dateTimeFormat.format(date);
        } catch (Exception e) {
            return "--";
        }
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.tvTaskNumber.setText(String.valueOf(position + 1));
        holder.tvTaskName.setText(task.getName());

        if (task.getExpected_duration() != null) {
            holder.tvDurationValue.setText(task.getExpected_duration().toString());
        } else {
            holder.tvDurationValue.setText("N/A");
        }

        if (task.getStart_date() != null) {
            String formattedStartDate = formatDateToYYYYMMDDHHMM(task.getStart_date());
            holder.tvStartDateValue.setText(formattedStartDate);
        } else {
            holder.tvStartDateValue.setText("Not started");
        }

        if (task.getStart_date() != null && task.getExpected_duration() != null) {
            long endTime = task.getStart_date().getTime() + task.getExpected_duration().toMillis();
            Date endDate = new Date(endTime);
            String formattedEndDate = formatDateToYYYYMMDDHHMM(endDate);
            holder.tvExpectedEndValue.setText(formattedEndDate);
        } else {
            holder.tvExpectedEndValue.setText("N/A");
        }

        String status = task.getStatus();
        String statusText = status != null ? status : "Waiting";
        holder.tvStatusValue.setText(statusText);

        if ("completed".equals(status)) {
            holder.btnAction.setText("Completed");
            holder.btnAction.setEnabled(false);
            holder.btnAction.setBackgroundResource(R.drawable.rounded_button_bg_disabled);
            holder.btnAction.setAlpha(0.5f);
            holder.indicator.setBackgroundColor(Color.parseColor("#4CAF50"));
            holder.tvStatusValue.setTextColor(Color.parseColor("#4CAF50"));
            holder.tvTimeRemainingBadge.setText("");

            if (task.getEndDate() != null) {
                String formattedEndDate = formatDateToYYYYMMDDHHMM(task.getEndDate());
                holder.tvStatusValue.setText("Ended: " + formattedEndDate);
            } else {
                holder.tvStatusValue.setText("Completed");
            }

        } else if ("in_progress".equals(status)) {
            holder.btnAction.setText("End");
            holder.btnAction.setEnabled(true);
            holder.btnAction.setBackgroundResource(R.drawable.rounded_button_bg);
            holder.btnAction.setAlpha(1f);
            holder.indicator.setBackgroundColor(Color.parseColor("#FFC107"));
            holder.tvStatusValue.setTextColor(Color.parseColor("#FFC107"));

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
            holder.btnAction.setEnabled(false);
            holder.btnAction.setBackgroundResource(R.drawable.rounded_button_bg_disabled);
            holder.btnAction.setAlpha(0.5f);
            holder.indicator.setBackgroundColor(Color.parseColor("#757575"));
            holder.tvStatusValue.setTextColor(Color.parseColor("#757575"));
            holder.tvTimeRemainingBadge.setText("");
        }

        holder.btnAction.setOnClickListener(v -> {
            if ("End".equals(holder.btnAction.getText().toString())) {
                endTask(task);
            }
        });
    }

    private void endTask(Task task) {
        task.setEndDate(new Date());
        task.setStatus("completed");
        dbHelperTask.updateTaskDetails(task);

        if (task.getNext_task() != null) {
            Task nextTask = dbHelperTask.getTaskById(task.getNext_task().getId());
            if (nextTask != null && "Waiting".equals(nextTask.getStatus())) {
                nextTask.setStart_date(new Date());
                nextTask.setStatus("in_progress");
                dbHelperTask.updateTaskDetails(nextTask);
            }
        }

        checkIfAllTasksCompleted();

        notifyDataSetChanged();
    }

    private void checkIfAllTasksCompleted() {
        List<Task> allPlanTasks = dbHelperTask.getTasksByPlanId(currentPlanId);

        if (allPlanTasks.isEmpty()) {
            return;
        }

        boolean allCompleted = true;
        for (Task task : allPlanTasks) {
            if (!"completed".equals(task.getStatus())) {
                allCompleted = false;
                break;
            }
        }

        if (allCompleted) {
            updatePlanStatusToCompleted();

            if (tasksCompletedListener != null) {
                tasksCompletedListener.onAllTasksCompleted(currentPlanId);
            }

            showCompletionMessage();
        }
    }

    private void updatePlanStatusToCompleted() {
        DBHelperPlan dbHelperPlan = new DBHelperPlan(context);
        Plan plan = dbHelperPlan.getPlanByID(currentPlanId);

        if (plan != null && !"completed".equals(plan.getStatus())) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            plan.setEndDate(sdf.format(new Date()));
            plan.setStatus("completed");
            dbHelperPlan.updateStudentDetails(plan);
        }
    }

    private void showCompletionMessage() {
        Toast.makeText(context, "🎉 تم إكمال جميع مهام الخطة!", Toast.LENGTH_SHORT).show();
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