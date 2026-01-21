package com.orabi.project_planner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PlanDetailsActivity extends AppCompatActivity {
    DBHelperPlan dbHelper;
    DBHelperTask dbHelperTask;
    private RecyclerView rvTasks;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_details);

        // Initialize database helpers
        dbHelper = new DBHelperPlan(this);
        dbHelperTask = new DBHelperTask(this);

        // Get plan ID from intent
        int planId = getIntent().getIntExtra("PLAN_ID", -1);

        // Initialize views
        ImageButton btnBack = findViewById(R.id.btnBack);
        ImageButton addTaskBtn = findViewById(R.id.addTaskBtn);
        TextView tvPlanName = findViewById(R.id.tvPlanName);
        TextView tvAllDurationValue = findViewById(R.id.tvAllDurationValue);
        TextView tvDescriptionValue = findViewById(R.id.tvDescriptionValue);
        TextView tvStartValue = findViewById(R.id.tvStartValue);
        TextView tvTasksNumberValue = findViewById(R.id.tvTasksNumberValue);
        TextView tvCompletedTasksValue = findViewById(R.id.tvCompletedTasksValue);

        // Initialize RecyclerView
        rvTasks = findViewById(R.id.rvTasks);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));

        // Set up add task button
        addTaskBtn.setOnClickListener(v -> {
            Intent intent = new Intent(PlanDetailsActivity.this, AddTaskActivity.class);
            intent.putExtra("PLAN_ID", planId);
            startActivity(intent);
        });

        if (planId != -1) {
            // Get plan details from database
            Plan plan = dbHelper.getPlanByID(planId);

            if (plan != null) {
                // Set plan details
                tvPlanName.setText(plan.getTitle());
                tvStartValue.setText(plan.getStartDate());

                // Set description
                if (plan.getDescribtion() != null && !plan.getDescribtion().isEmpty()) {
                    tvDescriptionValue.setText(plan.getDescribtion());
                } else {
                    tvDescriptionValue.setText("No description available");
                }

                // Get tasks for this plan from database
                List<Task> tasks = dbHelperTask.getTasksByPlanId(planId);

                // Update task statistics
                tvTasksNumberValue.setText(String.valueOf(tasks.size()));

                // Count completed tasks
                int completedCount = 0;
                int totalDurationDays = 0;
                for (Task task : tasks) {
                    if ("completed".equals(task.getStatus())) {
                        completedCount++;
                    }
                    // Calculate total duration
                    if (task.getExpected_duration() != null) {
                        totalDurationDays += task.getExpected_duration().getDays();
                    }
                }
                tvCompletedTasksValue.setText(String.valueOf(completedCount));

                // Calculate and display total duration
                String durationText;
                if (totalDurationDays > 0) {
                    durationText = totalDurationDays + " days";
                } else {
                    durationText = "Not calculated";
                }
                tvAllDurationValue.setText(durationText);

                // Set up RecyclerView with tasks
                taskAdapter = new TaskAdapter(tasks);
                rvTasks.setAdapter(taskAdapter);

            } else {
                // Plan not found in database
                tvPlanName.setText("Plan not found");
                tvTasksNumberValue.setText("0");
                tvCompletedTasksValue.setText("0");
                tvAllDurationValue.setText("N/A");
            }
        } else {
            // No plan ID provided
            tvPlanName.setText("Invalid plan");
            tvTasksNumberValue.setText("0");
            tvCompletedTasksValue.setText("0");
            tvAllDurationValue.setText("N/A");
        }

        // Back button listener
        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh task list when returning to this activity
        int planId = getIntent().getIntExtra("PLAN_ID", -1);
        if (planId != -1 && dbHelperTask != null) {
            List<Task> tasks = dbHelperTask.getTasksByPlanId(planId);
            if (taskAdapter != null) {
                taskAdapter = new TaskAdapter(tasks);
                rvTasks.setAdapter(taskAdapter);

                // Update task count
                TextView tvTasksNumberValue = findViewById(R.id.tvTasksNumberValue);
                tvTasksNumberValue.setText(String.valueOf(tasks.size()));

                // Update completed count
                int completedCount = 0;
                for (Task task : tasks) {
                    if ("completed".equals(task.getStatus())) {
                        completedCount++;
                    }
                }
                TextView tvCompletedTasksValue = findViewById(R.id.tvCompletedTasksValue);
                tvCompletedTasksValue.setText(String.valueOf(completedCount));
            }
        }
    }
}