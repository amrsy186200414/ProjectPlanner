package com.orabi.project_planner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PlanDetailsActivity extends AppCompatActivity {
    DBHelperPlan dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_details);

        dbHelper = new DBHelperPlan(this);

        int planId = getIntent().getIntExtra("PLAN_ID", -1);

        ImageButton btnBack = findViewById(R.id.btnBack);
        TextView tvPlanName = findViewById(R.id.tvPlanName);
        TextView tvAllDurationValue = findViewById(R.id.tvAllDurationValue);
        TextView tvDescriptionValue = findViewById(R.id.tvDescriptionValue);
        TextView tvStartValue = findViewById(R.id.tvStartValue);
        TextView tvTasksNumberValue = findViewById(R.id.tvTasksNumberValue);
        TextView tvCompletedTasksValue = findViewById(R.id.tvCompletedTasksValue);

        if (planId != -1) {
            // Get plan details from database
            Plan plan = dbHelper.getPlanByID(planId);

            if (plan != null) {
                // Set plan details
                tvPlanName.setText(plan.getTitle());
                tvStartValue.setText(plan.getStartDate());

                // Set description (if your Plan class has it)
                if (plan.getDescribtion() != null && !plan.getDescribtion().isEmpty()) {
                    tvDescriptionValue.setText(plan.getDescribtion());
                } else {
                    tvDescriptionValue.setText("No description available");
                }

                // For now, set placeholder values for task-related fields
                // These would come from a Task table if you had one
                tvTasksNumberValue.setText("0");
                tvCompletedTasksValue.setText("0");
                tvAllDurationValue.setText("Not calculated");

                // Optional: If your Plan class has end date, show it
                if (plan.getExpectedEndDate() != null && !plan.getExpectedEndDate().isEmpty()) {
                    // You could add this to your layout if needed
                }

                // Optional: Show status
                if (plan.getStatus() != null && !plan.getStatus().isEmpty()) {
                    // You could add a status indicator to your layout
                }
            } else {
                // Plan not found in database
                tvPlanName.setText("Plan not found");
            }
        } else {
            // No plan ID provided
            tvPlanName.setText("Invalid plan");
        }

        List<Task> tasks = new ArrayList<>();
//        tasks.add(new Task(1, "BUILD CLASSES", "2d", "completed"));
//        tasks.add(new Task(1, "PAINT WALLS", "1d", "in_progress"));
//        tasks.add(new Task(1, "INSTALL WINDOWS", "3d", "pending"));
//
//        TaskAdapter adapter = new TaskAdapter(tasks);
//        rvTasks.setLayoutManager(new LinearLayoutManager(this));
//        rvTasks.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
    }
}