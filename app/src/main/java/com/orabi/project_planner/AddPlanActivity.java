package com.orabi.project_planner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class AddPlanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);

        // تأكد من أن الـ ID صحيح في XML
        Button btnGoToTasks = findViewById(R.id.btnGoToTasks);
        ImageButton btnBack = findViewById(R.id.btnBack);

        // زر الرجوع (بسيط)
        btnBack.setOnClickListener(v -> {
            finish();
        });

        // زر الانتقال إلى إضافة المهام
        btnGoToTasks.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTaskActivity.class);
            startActivity(intent);
        });
    }
}