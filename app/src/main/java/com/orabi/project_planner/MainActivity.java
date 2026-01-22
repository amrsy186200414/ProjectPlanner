package com.orabi.project_planner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
    private List<Plan> allPlans;
    private PlanAdapter adapter;
    private String currentFilter = "All";
    private TextView tvUserName;

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

        client_plan = new DBHelperPlan(this);

        tvUserName = findViewById(R.id.tvUserName);

        loadUserName();

        LinearLayout header = findViewById(R.id.headerLayout);
        header.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        TextView filterAll = findViewById(R.id.filterAll);
        TextView filterInProgress = findViewById(R.id.filterInProgress);
        TextView filterWaiting = findViewById(R.id.filterWaiting);

        filterAll.setOnClickListener(v -> {
            currentFilter = "All";
            updateFilterUI();
            applyFilter();
        });

        filterInProgress.setOnClickListener(v -> {
            currentFilter = "in_progress";
            updateFilterUI();
            applyFilter();
        });

        filterWaiting.setOnClickListener(v -> {
            currentFilter = "Waiting";
            updateFilterUI();
            applyFilter();
        });

        allPlans = client_plan.getPlansDetails();

        RecyclerView rvPlans = findViewById(R.id.rvPlans);
        rvPlans.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PlanAdapter(allPlans, this);
        rvPlans.setAdapter(adapter);

        updateFilterUI();

        ImageView btnAdd = findViewById(R.id.navAdd);
        ImageView btnProfile = findViewById(R.id.navProfile);
        ImageView home = findViewById(R.id.navHome);

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddPlanActivity.class);
            startActivity(intent);
        });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void loadUserName() {
        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        String userName = prefs.getString("user_name", "User");

        if (userName == null || userName.trim().isEmpty() || userName.equals("USER NAME")) {
            userName = "User";
        }

        tvUserName.setText("HEY, " + userName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.stopTimer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adapter != null) {
            adapter.stopTimer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserName();

        allPlans = client_plan.getPlansDetails();
        applyFilter();
    }

    private void updateFilterUI() {
        TextView filterAll = findViewById(R.id.filterAll);
        TextView filterInProgress = findViewById(R.id.filterInProgress);
        TextView filterWaiting = findViewById(R.id.filterWaiting);

        filterAll.setBackgroundResource(R.drawable.rounded_time_bg);
        filterAll.setTextColor(getResources().getColor(R.color.text_like_background));

        filterInProgress.setBackgroundResource(R.drawable.rounded_time_bg);
        filterInProgress.setTextColor(getResources().getColor(R.color.text_like_background));

        filterWaiting.setBackgroundResource(R.drawable.rounded_time_bg);
        filterWaiting.setTextColor(getResources().getColor(R.color.text_like_background));

        switch (currentFilter) {
            case "All":
                filterAll.setBackgroundResource(R.drawable.rounded_button_bg);
                filterAll.setTextColor(getResources().getColor(R.color.white));
                break;
            case "in_progress":
                filterInProgress.setBackgroundResource(R.drawable.rounded_button_bg);
                filterInProgress.setTextColor(getResources().getColor(R.color.white));
                break;
            case "Waiting":
                filterWaiting.setBackgroundResource(R.drawable.rounded_button_bg);
                filterWaiting.setTextColor(getResources().getColor(R.color.white));
                break;
        }
    }

    private void applyFilter() {
        if (allPlans == null || adapter == null) return;

        List<Plan> filteredPlans;
        if ("All".equals(currentFilter)) {
            filteredPlans = new ArrayList<>(allPlans);
        } else {
            filteredPlans = new ArrayList<>();
            for (Plan plan : allPlans) {
                if (currentFilter.equals(plan.getStatus())) {
                    filteredPlans.add(plan);
                }
            }
        }
        adapter.updatePlans(filteredPlans);
    }
}