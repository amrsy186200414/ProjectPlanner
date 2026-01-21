package com.orabi.project_planner;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_details);

        String planTitle = getIntent().getStringExtra("PLAN_TITLE");

        ImageButton btnBack = findViewById(R.id.btnBack);
        TextView tvPlanName = findViewById(R.id.tvPlanName);
        RecyclerView rvTasks = findViewById(R.id.rvTasks);

        if (planTitle != null) {
            tvPlanName.setText(planTitle);
        }

        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task(1, "BUILD CLASSES", "2d", "completed"));
        tasks.add(new Task(1, "PAINT WALLS", "1d", "in_progress"));
        tasks.add(new Task(1, "INSTALL WINDOWS", "3d", "pending"));
//
//        TaskAdapter adapter = new TaskAdapter(tasks);
//        rvTasks.setLayoutManager(new LinearLayoutManager(this));
//        rvTasks.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
    }
}