package com.orabi.project_planner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class AddPlanActivity extends AppCompatActivity {

    private LinearLayout tasksContainer;
    private List<Task> taskList = new ArrayList<>();
    private int taskCounter = 1;
    DBHelperPlan client_plan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);


        client_plan=new DBHelperPlan(this);
        EditText etPlanName = findViewById(R.id.etPlanName);
        EditText etPlanDescription = findViewById(R.id.etPlanDescription);
//        EditText etTaskName = findViewById(R.id.etTaskName);
//        EditText etTaskStart = findViewById(R.id.etTaskStrat);
//        EditText etTaskDuration = findViewById(R.id.etTaskDuration);
//        Button btnAddPreview = findViewById(R.id.btnAddPreview);
        Button btnSavePlan = findViewById(R.id.btnSavePlan);
//        tasksContainer = findViewById(R.id.tasksContainer);
        ImageButton btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button btnGoToTasks = findViewById(R.id.btnGoToTasks);
        btnGoToTasks.setOnClickListener(v -> {
            Intent intent = new Intent(AddPlanActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });

//        btnAddPreview.setOnClickListener(v -> {
//            String taskName = etTaskName.getText().toString().trim();
//            String startAfter = etTaskStart.getText().toString().trim();
//            String duration = etTaskDuration.getText().toString().trim();
//
//            if (taskName.isEmpty()) {
//                etTaskName.setError("يرجى إدخال اسم التاسك");
//                etTaskName.requestFocus();
//                return;
//            }
//
//            if (duration.isEmpty()) {
//                etTaskDuration.setError("يرجى إدخال المدة");
//                etTaskDuration.requestFocus();
//                return;
//            }
//
//            if (startAfter.isEmpty()) {
//                startAfter = "none";
//            }
//
//            Task newTask = new Task( );
////            newTask.startAfterTask = startAfter;
//            taskList.add(newTask);
//
//            addTaskToView(newTask, taskCounter);
//            taskCounter++;
//
//            etTaskName.getText().clear();
//            etTaskStart.getText().clear();
//            etTaskDuration.getText().clear();
//            etTaskName.requestFocus();
//        });

        btnSavePlan.setOnClickListener(v ->

        {
            try {

            String planName = etPlanName.getText().toString().trim();
            String planDescription = etPlanDescription.getText().toString().trim();

            if (planName.isEmpty()) {
                etPlanName.setError("يرجى إدخال اسم الخطة");
                return;
            }

//            if (taskList.isEmpty()) {
//                Toast.makeText(this, "يرجى إضافة مهمة واحدة على الأقل", Toast.LENGTH_SHORT).show();
//                return;
//            }

            Plan newPlan = new Plan(planName,"", "Waiting", planDescription );
            client_plan.addPlan(newPlan);

            List<Plan> allPlans = client_plan.getPlansDetails();

            Log.d("DEBUG", "Number of plans: " + allPlans.size());
        if (allPlans.isEmpty()) {
            Log.d("DEBUG", "Database is empty!");
        } else {
            for (int i = 0; i < allPlans.size(); i++) {
                Plan p = allPlans.get(i);
                Log.d("DEBUG", "Plan " + i + ": " +
                        "ID=" + p.getId() + ", " +
                        "Title=" + p.getTitle() + ", " +
                        "Start=" + p.getStartDate() + ", " +
                        "Status=" + p.getStatus());
            }
        }
                Toast.makeText(this, "تم حفظ الخطة بنجاح!", Toast.LENGTH_SHORT).show();
                finish();
        }
                 catch (Exception e) {
            // Simple error handling
            Log.e("SAVE_ERROR", "Error: " + e.toString());
//            Toast.makeText(this, "خطأ: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }}

        );
    }

    private void addTaskToView(Task task, int taskNumber) {
//        View taskView = LayoutInflater.from(this).inflate(R.layout.item_task_in_new_plan_page, null);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(20, 8, 20, 8);
//        taskView.setLayoutParams(layoutParams);
//
//        TextView tvTaskNumber = taskView.findViewById(R.id.tvTaskNumber);
//        TextView tvTaskName = taskView.findViewById(R.id.tvTaskName);
//        TextView tvDurationValue = taskView.findViewById(R.id.tvDurationValue);
//        TextView tvStartAfterValue = taskView.findViewById(R.id.tvStartAfterValue);
//
//        tvTaskNumber.setText(String.valueOf(taskNumber));
//        tvTaskName.setText(task.getName());
//        tvDurationValue.setText(task.getExpected_duration().toString());
////        tvStartAfterValue.setText(task.startAfterTask != null ? task.startAfterTask : "none");
//
//        tasksContainer.addView(taskView);
    }
}