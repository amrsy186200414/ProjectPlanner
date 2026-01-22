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
    int idplan;
    private LinearLayout tasksContainer;
    private List<Task> taskList = new ArrayList<>();
    private int taskCounter = 1;
    DBHelperPlan client_plan;
    DBHelperTask client_task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);


        client_plan=new DBHelperPlan(this);
        EditText etPlanName = findViewById(R.id.etPlanName);
        EditText etPlanDescription = findViewById(R.id.etPlanDescription);

        Button btnSavePlan = findViewById(R.id.btnSavePlan);
        ImageButton btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        androidx.cardview.widget.CardView btnGoToTasks = findViewById(R.id.btnGoToTasks);


        btnGoToTasks.setOnClickListener(v -> {

            String planName = etPlanName.getText().toString().trim();
            String planDescription = etPlanDescription.getText().toString().trim();
            if (planName.isEmpty()) {
                etPlanName.setError("يرجى إدخال اسم الخطة");
                return;
            }
            Plan newPlan = new Plan(planName,"", "Waiting", planDescription );
            idplan= (int)client_plan.addPlan(newPlan);

            Intent intent = new Intent(AddPlanActivity.this, AddTaskActivity.class);
            intent.putExtra("PLAN_ID", idplan);

            startActivity(intent);
        });

        btnSavePlan.setOnClickListener(v ->

        {
            try {

                client_task=new DBHelperTask(this);
                taskList= client_task.getTasksByPlanId(idplan);
            if (taskList.isEmpty()) {
                Toast.makeText(this, "يرجى إضافة مهمة واحدة على الأقل", Toast.LENGTH_SHORT).show();
                return;
            }


                Toast.makeText(this, "تم حفظ الخطة بنجاح!", Toast.LENGTH_SHORT).show();
                finish();
        }
                 catch (Exception e) {
            Log.e("SAVE_ERROR", "Error: " + e.toString());
//            Toast.makeText(this, "خطأ: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }}

        );
    }


}