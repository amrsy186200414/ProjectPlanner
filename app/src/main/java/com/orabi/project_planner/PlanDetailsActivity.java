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
    private int planId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_details);

        dbHelper = new DBHelperPlan(this);
        dbHelperTask = new DBHelperTask(this);

        planId = getIntent().getIntExtra("PLAN_ID", -1);

        ImageButton btnBack = findViewById(R.id.btnBack);
        ImageButton addTaskBtn = findViewById(R.id.addTaskBtn);
        TextView tvPlanName = findViewById(R.id.tvPlanName);
        TextView tvAllDurationValue = findViewById(R.id.tvAllDurationValue);
        TextView tvDescriptionValue = findViewById(R.id.tvDescriptionValue);
        TextView tvStartValue = findViewById(R.id.tvStartValue);
        TextView tvTasksNumberValue = findViewById(R.id.tvTasksNumberValue);
        TextView tvCompletedTasksValue = findViewById(R.id.tvCompletedTasksValue);

        rvTasks = findViewById(R.id.rvTasks);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));

        addTaskBtn.setOnClickListener(v -> {
            Intent intent = new Intent(PlanDetailsActivity.this, AddTaskActivity.class);
            intent.putExtra("PLAN_ID", planId);
            startActivity(intent);
        });

        if (planId != -1) {
            Plan plan = dbHelper.getPlanByID(planId);

            if (plan != null) {
                tvPlanName.setText(plan.getTitle());
                tvStartValue.setText(plan.getStartDate());

                if (plan.getDescribtion() != null && !plan.getDescribtion().isEmpty()) {
                    tvDescriptionValue.setText(plan.getDescribtion());
                } else {
                    tvDescriptionValue.setText("No description available");
                }

                updateTaskDisplay();
            } else {
                tvPlanName.setText("Plan not found");
            }
        } else {
            tvPlanName.setText("Invalid plan");
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private void updateTaskDisplay() {
        List<Task> tasks = dbHelperTask.getTasksByPlanId(planId);

        TextView tvTasksNumberValue = findViewById(R.id.tvTasksNumberValue);
        TextView tvCompletedTasksValue = findViewById(R.id.tvCompletedTasksValue);
        TextView tvAllDurationValue = findViewById(R.id.tvAllDurationValue);

        tvTasksNumberValue.setText(String.valueOf(tasks.size()));

        int completedCount = 0;
        int totalDurationDays = 0;
        for (Task task : tasks) {
            if ("completed".equals(task.getStatus())) {
                completedCount++;
            }
            if (task.getExpected_duration() != null) {
                totalDurationDays += task.getExpected_duration().getDays();
            }
        }
        tvCompletedTasksValue.setText(String.valueOf(completedCount));

        if (totalDurationDays > 0) {
            tvAllDurationValue.setText(totalDurationDays + " days");
        } else {
            tvAllDurationValue.setText("Not calculated");
        }

        taskAdapter = new TaskAdapter(tasks, this);
        rvTasks.setAdapter(taskAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (planId != -1) {
            updateTaskDisplay();
        }
    }
}