package com.orabi.project_planner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Date;
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

//        List<Plan>plans=client_plan.getPlansDetails();
//        for(Plan p:plans)
//        { client_plan.deletePlan(p);}

//        Plan myplan=new Plan("jjjj SCHOOL", "1/10/24", "in_progress","mydescribtion");
//        myplan.setExpectedEndDate("1/10/24");
//        myplan.setDuration(new Duration(1,1,1,1));
//        Plan myplan2=new Plan("LEARNing ANDROID", "5/10/24", "completed","mydescribtion");

//        client_plan.addPlan(myplan);
//        client_plan.addPlan(myplan2);


        List<Plan> allPlans = client_plan.getPlansDetails();
//        Log.d("DEBUG", "Number of plans: " + allPlans.size());
//        if (allPlans.isEmpty()) {
//            Log.d("DEBUG", "Database is empty!");
//        } else {
//            for (int i = 0; i < allPlans.size(); i++) {
//                Plan p = allPlans.get(i);
//                Log.d("DEBUG", "Plan " + i + ": " +
//                        "ID=" + p.getId() + ", " +
//                        "Title=" + p.getTitle() + ", " +
//                        "Start=" + p.getStartDate() + ", " +
//                        "Status=" + p.getStatus());
//            }
//        }

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

