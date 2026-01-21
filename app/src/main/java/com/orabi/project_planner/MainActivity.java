package com.orabi.project_planner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    DBHelperPlan client_plan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        client_plan=new DBHelperPlan(this);
        LinearLayout header = findViewById(R.id.headerLayout);
        header.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddPlanActivity.class);
            startActivity(intent);
        });

//        List<Plan> allPlans = new ArrayList<>();
//        allPlans.add(new Plan("BUILD SCHOOL", "1/10/24", "15/10/24", "in_progress"));
//        allPlans.add(new Plan("LEARN ANDROID", "5/10/24", "20/10/24", "completed"));
//        allPlans.add(new Plan("FITNESS PLAN", "10/10/24", "30/10/24", "in_progress"));

        Plan myplan=new Plan(1,"jjjj SCHOOL", "1/10/24", "in_progress");
        Plan myplan2=new Plan(0,"LEARNing ANDROID", "5/10/24", "completed");



        List<Plan> allPlans = client_plan.getPlansDetails();


        RecyclerView rvPlans = findViewById(R.id.rvPlans);
        rvPlans.setLayoutManager(new LinearLayoutManager(this));

        PlanAdapter adapter = new PlanAdapter(allPlans);
        rvPlans.setAdapter(adapter);

        ImageView btnAdd = findViewById(R.id.navAdd);
        ImageView btnProfile = findViewById(R.id.navProfile);

        ImageView home =findViewById(R.id.navHome);



        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddPlanActivity.class);
            startActivity(intent);
        });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

    }
}

