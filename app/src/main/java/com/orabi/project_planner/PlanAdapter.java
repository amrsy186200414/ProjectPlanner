package com.orabi.project_planner;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {
    private List<Plan> planList;
    private Context context;
    private Timer timer;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());

    public PlanAdapter(List<Plan> planList, Context context) {
        this.planList = planList;
        this.context = context;
        startTimer();
    }

    public void updatePlans(List<Plan> newList) {
        this.planList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        Plan plan = planList.get(position);

        // Set basic info
        holder.tvTitle.setText(plan.getTitle());
        holder.tvStartValue.setText(plan.getStartDate());
        holder.tvExpectedEndValue.setText(plan.getExpectedEndDate());

        // Set duration
        if (plan.getDuration() != null) {
            holder.tvDurationValue.setText(plan.getDuration().toString());
        } else {
            holder.tvDurationValue.setText("Not set");
        }

        // Set button text based on status
        if ("in_progress".equals(plan.getStatus())) {
            holder.btnStart.setText("In Progress");
            holder.btnStart.setEnabled(false);
        } else if ("completed".equals(plan.getStatus())) {
            holder.btnStart.setText("Completed");
            holder.btnStart.setEnabled(false);
        } else {
            holder.btnStart.setText("Start");
            holder.btnStart.setEnabled(true);
        }

        // Calculate and display time remaining for in_progress plans
        if ("in_progress".equals(plan.getStatus()) && plan.getStartDate() != null && !plan.getStartDate().isEmpty()) {
            try {
                Date startDate = dateFormat.parse(plan.getStartDate());
                if (startDate != null && plan.getDuration() != null) {
                    long totalMillis = plan.getDuration().toMillis();
                    long currentTime = System.currentTimeMillis();
                    long startTime = startDate.getTime();
                    long elapsedTime = currentTime - startTime;
                    long remainingMillis = totalMillis - elapsedTime;

                    if (remainingMillis > 0) {
                        Duration remaining = Duration.fromMillis((int) remainingMillis);
                        holder.tvTimeRemaining.setText(remaining.toString());
                        holder.tvTimeRemaining.setTextColor(ContextCompat.getColor(context, R.color.status_green));
                    } else {
                        holder.tvTimeRemaining.setText("Overdue");
                        holder.tvTimeRemaining.setTextColor(ContextCompat.getColor(context, R.color.status_red));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            holder.tvTimeRemaining.setText(plan.getDuration() != null ? plan.getDuration().toString() : "Not set");
        }

        // Set status color
        int colorRes;
        switch (plan.getStatus()) {
            case "completed":
                colorRes = R.color.status_green;
                break;
            case "late":
                colorRes = R.color.status_red;
                break;
            case "in_progress":
                colorRes = R.color.status_yellow;
                break;
            default:
                colorRes = R.color.status_yellow;
                break;
        }
        holder.statusIndicator.setBackgroundColor(ContextCompat.getColor(context, colorRes));

        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlanDetailsActivity.class);
            intent.putExtra("PLAN_ID", plan.getId());
            intent.putExtra("PLAN_TITLE", plan.getTitle());
            context.startActivity(intent);
        });

        holder.btnStart.setOnClickListener(v -> {
            startPlan(plan.getId());
        });
    }

    private void startPlan(int planId) {
        DBHelperPlan dbHelper = new DBHelperPlan(context);
        DBHelperTask dbHelperTask = new DBHelperTask(context);

        // Update plan start date
        Plan plan = dbHelper.getPlanByID(planId);
        if (plan != null) {
            plan.setStartDate(dateFormat.format(new Date()));
            plan.setStatus("in_progress");
            dbHelper.updateStudentDetails(plan);

            // Start tasks without previous tasks
            List<Task> tasks = dbHelperTask.getTasksByPlanId(planId);
            for (Task task : tasks) {
                if (task.getPrevious_task() == null) {
                    task.setStart_date(new Date());
                    task.setStatus("in_progress");
                    dbHelperTask.updateTaskDetails(task);
                }
            }

            // Refresh the list
            if (context instanceof MainActivity) {
                ((MainActivity) context).onResume();
            }
        }
    }

    @Override
    public int getItemCount() {
        return planList.size();
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Update time remaining every minute
                if (context instanceof MainActivity) {
                    ((MainActivity) context).runOnUiThread(() -> notifyDataSetChanged());
                }
            }
        }, 0, 60000); // Update every minute
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDurationValue, tvStartValue, tvExpectedEndValue, tvTimeRemaining;
        View statusIndicator;
        Button btnStart;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvPlanTitle);
            tvDurationValue = itemView.findViewById(R.id.tvDurationValue);
            tvStartValue = itemView.findViewById(R.id.tvStartValue);
            tvExpectedEndValue = itemView.findViewById(R.id.tvExpectedEndValue);
            tvTimeRemaining = itemView.findViewById(R.id.tvTimeRemaining);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);
            btnStart = itemView.findViewById(R.id.btnStart);
        }
    }
}