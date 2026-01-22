package com.orabi.project_planner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserName, tvAllPlansValue, tvInProgressValue, tvCompletedValue;
    private EditText etUserName;
    private DBHelperPlan dbHelperPlan;

    private static final String USER_PREFS = "user_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelperPlan = new DBHelperPlan(this);

        ImageButton btnBack = findViewById(R.id.btnBack);
        etUserName = findViewById(R.id.etUserName);
        ImageButton btnSaveName = findViewById(R.id.btnSaveName);
        tvUserName = findViewById(R.id.tvUserName);
        tvAllPlansValue = findViewById(R.id.tvAllPlansValue);
        tvInProgressValue = findViewById(R.id.tvInProgressValue);
        tvCompletedValue = findViewById(R.id.tvCompletedValue);

        loadUserName();

        updatePlanStats();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSaveName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserName();
            }
        });
    }

    private void loadUserName() {
        SharedPreferences prefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        String savedName = prefs.getString("user_name", "USER NAME");
        tvUserName.setText(savedName.toUpperCase());
    }

    private void saveUserName() {
        String newName = etUserName.getText().toString().trim();

        if (newName.isEmpty()) {
            Toast.makeText(ProfileActivity.this, "يرجى إدخال اسم", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = getSharedPreferences(USER_PREFS, MODE_PRIVATE).edit();
        editor.putString("user_name", newName);
        editor.apply();

        tvUserName.setText(newName.toUpperCase());
        etUserName.setText("");
        Toast.makeText(ProfileActivity.this, "تم تحديث الاسم", Toast.LENGTH_SHORT).show();
    }

    private void updatePlanStats() {
        List<Plan> allPlans = dbHelperPlan.getPlansDetails();

        int totalPlans = allPlans.size();
        int inProgressCount = 0;
        int completedCount = 0;

        for (Plan plan : allPlans) {
            String status = plan.getStatus();
            if (status != null) {
                if (status.equals("in_progress")) {
                    inProgressCount++;
                } else if (status.equals("completed")) {
                    completedCount++;
                }
            }
        }

        tvAllPlansValue.setText(String.valueOf(totalPlans));
        tvInProgressValue.setText(String.valueOf(inProgressCount));
        tvCompletedValue.setText(String.valueOf(completedCount));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePlanStats();
    }
}