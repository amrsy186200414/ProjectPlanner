package com.orabi.project_planner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTaskActivity extends AppCompatActivity {

    private DBHelperTask client_task;
    private RecyclerView rvTasks;
    private ShortTaskAdapter shortTaskAdapter;
    private List<Task> taskList = new ArrayList<>();
    private Map<Integer, Task> taskMap = new HashMap<>(); // For quick lookup by ID
    private int taskCounter = 1;
    private Spinner spinnerTaskStart;
    private ArrayAdapter<String> spinnerAdapter;
    private List<String> startOptions = new ArrayList<>();
    private int planId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Get plan ID from intent
        planId = getIntent().getIntExtra("PLAN_ID", -1);
        if (planId == -1) {
            Toast.makeText(this, "خطأ: لم يتم تحديد الخطة", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        client_task = new DBHelperTask(this);

        // ربط العناصر
        ImageButton btnBack = findViewById(R.id.btnBack);
        Button btnSavePlan = findViewById(R.id.btnSavePlan);
        Button btnAddPreview = findViewById(R.id.btnAddPreview);
        EditText etTaskName = findViewById(R.id.etTaskName);
        EditText etTaskDuration = findViewById(R.id.etTaskDuration);

        // Spinner
        spinnerTaskStart = findViewById(R.id.etTaskStrat);

        // RecyclerView
        rvTasks = findViewById(R.id.rvTasks);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        shortTaskAdapter = new ShortTaskAdapter(taskList);
        rvTasks.setAdapter(shortTaskAdapter);

        // إعداد Spinner
        setupSpinner();

        // ===== زر ADD (إضافة مهمة جديدة) =====
        btnAddPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskName = etTaskName.getText().toString().trim();
                String durationStr = etTaskDuration.getText().toString().trim();

                if (taskName.isEmpty()) {
                    etTaskName.setError("يرجى إدخال اسم المهمة");
                    etTaskName.requestFocus();
                    return;
                }

                if (durationStr.isEmpty()) {
                    etTaskDuration.setError("يرجى إدخال المدة");
                    etTaskDuration.requestFocus();
                    return;
                }

                // Parse duration (expecting format like "5d" or just "5")
                int days = 0;
                try {
                    // Remove non-digits
                    String daysStr = durationStr.replaceAll("[^0-9]", "");
                    days = Integer.parseInt(daysStr);
                } catch (NumberFormatException e) {
                    etTaskDuration.setError("يرجى إدخال مدة صحيحة");
                    etTaskDuration.requestFocus();
                    return;
                }

                // Get selected previous task from spinner
                String selectedOption = spinnerTaskStart.getSelectedItem().toString();
                Task previousTask = null;

                if (selectedOption.startsWith("After Task")) {
                    try {
                        // Extract task ID from option like "After Task 1: Task Name"
                        String numStr = selectedOption.replaceAll("[^0-9]", "");
                        int previousTaskId = Integer.parseInt(numStr);
                        previousTask = taskMap.get(previousTaskId);
                    } catch (NumberFormatException e) {
                        // If parsing fails, keep as null
                    }
                }

                // Create new task
                Task newTask = new Task();
                newTask.setId(taskCounter);
                newTask.setName(taskName);
                newTask.setExpected_duration(new Duration(0, days, 0, 0));
                newTask.setPrevious_task(previousTask); // Set previous task
                newTask.setPlanid(planId); // Set the plan ID
                newTask.setStart_date(new Date()); // Set current date as start

                // If there's a previous task, update its next_task
                if (previousTask != null) {
                    previousTask.setNext_task(newTask);
                }

                // Add task to lists
                taskList.add(newTask);
                taskMap.put(taskCounter, newTask);
                shortTaskAdapter.notifyDataSetChanged();

                // Update spinner options
                updateSpinnerOptions();

                // Scroll to new task
                rvTasks.scrollToPosition(taskList.size() - 1);

                // Clear fields
                etTaskName.setText("");
                etTaskDuration.setText("");
                spinnerTaskStart.setSelection(0);

                // Focus on task name
                etTaskName.requestFocus();

                Toast.makeText(AddTaskActivity.this,
                        "✓ تمت إضافة المهمة رقم " + taskCounter,
                        Toast.LENGTH_SHORT).show();

                taskCounter++;
            }
        });

        // ===== زر Save Changes (حفظ والخروج) =====
        btnSavePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTasks();
            }
        });

        // ===== زر Back (رجوع دون حفظ) =====
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!taskList.isEmpty()) {
                    Toast.makeText(AddTaskActivity.this,
                            "⚠️ سيتم فقدان " + taskList.size() + " مهمة غير محفوظة",
                            Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    private void setupSpinner() {
        // Get tasks without next_task for the same plan from database
        List<Task> availableTasks = client_task.getTasksWithoutNextTask(planId);

        startOptions.clear();
        startOptions.add("Start immediately"); // Start without previous task

        // Add tasks from database that don't have next_task
        for (Task task : availableTasks) {
            String option = "After Task " + task.getId() + ": " + task.getName();
            startOptions.add(option);
            taskMap.put(task.getId(), task);
        }

        spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                startOptions
        );

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTaskStart.setAdapter(spinnerAdapter);

        spinnerTaskStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Optional: Add any selection logic here
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void updateSpinnerOptions() {
        // Update with tasks from current session that don't have next_task
        startOptions.clear();
        startOptions.add("Start immediately");

        for (Task task : taskList) {
            if (task.getNext_task() == null) {
                String option = "After Task " + task.getId() + ": " + task.getName();
                if (!startOptions.contains(option)) {
                    startOptions.add(option);
                }
            }
        }

        spinnerAdapter.notifyDataSetChanged();
    }

    private void saveTasks() {
        if (taskList.isEmpty()) {
            Toast.makeText(this, "❗ يرجى إضافة مهمة واحدة على الأقل", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save all tasks to database
        for (Task task : taskList) {
            client_task.addTask(task);
        }

        Toast.makeText(this, "✅ تم حفظ " + taskList.size() + " مهمة بنجاح", Toast.LENGTH_SHORT).show();
        finish();
    }
}