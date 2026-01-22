package com.orabi.project_planner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PlanDetailsActivity extends AppCompatActivity {
    DBHelperPlan dbHelper;
    DBHelperTask dbHelperTask;
    private RecyclerView rvTasks;
    private TaskAdapter taskAdapter;
    private int planId;

    private static final String TAG = "PlanDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_details);

        // Initialize database helpers
        dbHelper = new DBHelperPlan(this);
        dbHelperTask = new DBHelperTask(this);

        planId = getIntent().getIntExtra("PLAN_ID", -1);
        Log.d(TAG, "Plan ID: " + planId);

        // Initialize views
        ImageButton btnBack = findViewById(R.id.btnBack);
        ImageButton addTaskBtn = findViewById(R.id.addTaskBtn);
        TextView tvPlanName = findViewById(R.id.tvPlanName);
        TextView tvAllDurationValue = findViewById(R.id.tvAllDurationValue);
        TextView tvDescriptionValue = findViewById(R.id.tvDescriptionValue);
        TextView tvStartValue = findViewById(R.id.tvStartValue);
        TextView tvTasksNumberValue = findViewById(R.id.tvTasksNumberValue);
        TextView tvCompletedTasksValue = findViewById(R.id.tvCompletedTasksValue);

        rvTasks = findViewById(R.id.rvTasks);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));

        addTaskBtn.setOnClickListener(v -> {
            Intent intent = new Intent(PlanDetailsActivity.this, AddTaskActivity.class);
            intent.putExtra("PLAN_ID", planId);
            startActivity(intent);
        });

        if (planId != -1) {
            Plan plan = dbHelper.getPlanByID(planId);

            if (plan != null) {
                // Log plan attributes
                logPlanAttributes(plan);

                tvPlanName.setText(plan.getTitle());
                tvStartValue.setText(plan.getStartDate());

                if (plan.getDescribtion() != null && !plan.getDescribtion().isEmpty()) {
                    tvDescriptionValue.setText(plan.getDescribtion());
                } else {
                    tvDescriptionValue.setText("No description available");
                }

                updatePlanDurationDisplay(plan);
                updateTaskDisplay();
            } else {
                Log.w(TAG, "Plan not found");
                tvPlanName.setText("Plan not found");
                tvTasksNumberValue.setText("0");
                tvCompletedTasksValue.setText("0");
                tvAllDurationValue.setText("N/A");
            }
        } else {
            Log.e(TAG, "Invalid plan ID");
            tvPlanName.setText("Invalid plan");
            tvTasksNumberValue.setText("0");
            tvCompletedTasksValue.setText("0");
            tvAllDurationValue.setText("N/A");
        }

        btnBack.setOnClickListener(v -> finish());
    }

    /**
     * Simple method to log all plan attributes
     */
    private void logPlanAttributes(Plan plan) {
        Log.d(TAG, "=== PLAN ATTRIBUTES ===");
        Log.d(TAG, "ID: " + plan.getId());
        Log.d(TAG, "Title: " + plan.getTitle());
        Log.d(TAG, "Status: " + plan.getStatus());
        Log.d(TAG, "Start Date: " + plan.getStartDate());
        Log.d(TAG, "Expected End Date: " + plan.getExpectedEndDate());
        Log.d(TAG, "End Date: " + (plan.getEndDate() != null ? plan.getEndDate() : "Not set"));
        Log.d(TAG, "Description: " + (plan.getDescribtion() != null ? plan.getDescribtion() : "No description"));

        if (plan.getDuration() != null) {
            Log.d(TAG, "Duration: " + plan.getDuration().toString() +
                    " (Days: " + plan.getDuration().getDays() +
                    ", Hours: " + plan.getDuration().getHours() +
                    ", Minutes: " + plan.getDuration().getMinutes() + ")");
        } else {
            Log.d(TAG, "Duration: Not set");
        }
        Log.d(TAG, "=======================");
    }

    private void updatePlanDurationDisplay(Plan plan) {
        TextView tvAllDurationValue = findViewById(R.id.tvAllDurationValue);

        if (plan.getDuration() != null) {
            String durationText = formatDurationForDisplay(plan.getDuration());
            tvAllDurationValue.setText(durationText);
            Log.d(TAG, "Displaying duration: " + durationText);
        } else {
            // Calculate duration if not set
            Duration calculatedDuration = dbHelper.calculatePlanDuration(planId, dbHelperTask);
            if (calculatedDuration != null && calculatedDuration.getTotalDays() > 0) {
                String durationText = formatDurationForDisplay(calculatedDuration);
                tvAllDurationValue.setText(durationText);

                // Update plan in database
                plan.setDuration(calculatedDuration);
                dbHelper.updateStudentDetails(plan);
                Log.d(TAG, "Calculated and saved duration: " + durationText);
            } else {
                tvAllDurationValue.setText("Not calculated");
                Log.d(TAG, "Duration not calculated");
            }
        }
    }

    // Helper method to format duration for display
    private String formatDurationForDisplay(Duration duration) {
        if (duration == null) return "Not set";

        StringBuilder sb = new StringBuilder();
        if (duration.getDays() > 0) {
            sb.append(duration.getDays()).append("d");
            if (duration.getHours() > 0) {
                sb.append(", ").append(duration.getHours()).append("h");
            }
        } else if (duration.getHours() > 0) {
            sb.append(duration.getHours()).append("h");
        } else if (duration.getMinutes() > 0) {
            sb.append(duration.getMinutes()).append("m");
        } else {
            sb.append("0d");
        }

        return sb.toString();
    }

// In PlanDetailsActivity.java, update the updateTaskDisplay() method:

    private void updateTaskDisplay() {
        List<Task> tasks = dbHelperTask.getTasksByPlanId(planId);
        Log.d(TAG, "Tasks count: " + tasks.size());

        TextView tvTasksNumberValue = findViewById(R.id.tvTasksNumberValue);
        TextView tvCompletedTasksValue = findViewById(R.id.tvCompletedTasksValue);
        TextView tvAllDurationValue = findViewById(R.id.tvAllDurationValue);
        ImageButton addTaskBtn = findViewById(R.id.addTaskBtn);

        tvTasksNumberValue.setText(String.valueOf(tasks.size()));

        int completedCount = 0;
        for (Task task : tasks) {
            if ("completed".equals(task.getStatus())) {
                completedCount++;
            }
        }
        tvCompletedTasksValue.setText(String.valueOf(completedCount));

        // Check if all tasks are completed
        Plan plan = dbHelper.getPlanByID(planId);
        if (plan != null) {
            // 1. Check if all tasks are completed
            boolean allTasksCompleted = true;
            for (Task task : tasks) {
                if (!"completed".equals(task.getStatus())) {
                    allTasksCompleted = false;
                    break;
                }
            }

            if (allTasksCompleted && tasks.size() > 0) {
                // Set plan to "done" and set end date to today
                plan.setStatus("done");

                // Set end date to today
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String today = sdf.format(new Date());
                plan.setEndDate(today);

                dbHelper.updateStudentDetails(plan);
                Log.d(TAG, "All tasks completed. Plan marked as 'done' with end date: " + today);
            }

            // 2. Calculate and set expected end date
            Duration planDuration = calculateMaxChainDuration(tasks);
            String durationText = formatDurationForDisplay(planDuration);
            tvAllDurationValue.setText(durationText);

            // Update plan duration
            plan.setDuration(planDuration);

            // Calculate and set expected end date if start date is set
            if (plan.getStartDate() != null && !plan.getStartDate().isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    Date startDate = sdf.parse(plan.getStartDate());

                    if (startDate != null && planDuration != null) {
                        // Calculate expected end date
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(startDate);

                        // Add months
                        calendar.add(Calendar.MONTH, planDuration.getMonths());
                        // Add days
                        calendar.add(Calendar.DAY_OF_MONTH, planDuration.getDays());
                        // Note: We're not adding hours/minutes as they don't affect the date

                        String expectedEndDate = sdf.format(calendar.getTime());
                        plan.setExpectedEndDate(expectedEndDate);
                        Log.d(TAG, "Expected end date calculated: " + expectedEndDate);
                    }
                } catch (ParseException e) {
                    Log.e(TAG, "Error parsing start date: " + e.getMessage());
                }
            }

            // Save updated plan
            dbHelper.updateStudentDetails(plan);
            Log.d(TAG, "Plan updated with new duration and expected end date");

            // 3. Disable add task button if plan is "done"
            if ("done".equals(plan.getStatus())) {
                addTaskBtn.setEnabled(false);
                addTaskBtn.setAlpha(0.5f);
                Log.d(TAG, "Add task button disabled - plan is done");
            } else {
                addTaskBtn.setEnabled(true);
                addTaskBtn.setAlpha(1.0f);
            }
        }

        // Log task summary
        logTaskSummary(tasks, completedCount);

        taskAdapter = new TaskAdapter(tasks, this);
        rvTasks.setAdapter(taskAdapter);
    }
    /**
     * Simple method to log task summary
     */
    private void logTaskSummary(List<Task> tasks, int completedCount) {
        Log.d(TAG, "=== TASK SUMMARY ===");
        Log.d(TAG, "Total tasks: " + tasks.size());
        Log.d(TAG, "Completed: " + completedCount);
        Log.d(TAG, "In progress/Waiting: " + (tasks.size() - completedCount));

        // Log each task briefly
        for (int i = 0; i < Math.min(tasks.size(), 5); i++) { // Show first 5 tasks max
            Task task = tasks.get(i);
            Log.d(TAG, "Task " + (i+1) + ": ID=" + task.getId() +
                    ", Name=" + task.getName() +
                    ", Status=" + task.getStatus() +
                    ", Duration=" + (task.getExpected_duration() != null ?
                    task.getExpected_duration().toString() + "d" : "N/A"));
        }
        if (tasks.size() > 5) {
            Log.d(TAG, "... and " + (tasks.size() - 5) + " more tasks");
        }
        Log.d(TAG, "===================");
    }

    // Helper method to calculate max chain duration
    private Duration calculateMaxChainDuration(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return new Duration(0, 0, 0, 0);
        }

        java.util.Map<Integer, Task> taskMap = new java.util.HashMap<>();
        for (Task task : tasks) {
            taskMap.put(task.getId(), task);
        }

        List<Task> startingTasks = new java.util.ArrayList<>();
        for (Task task : tasks) {
            if (task.getPrevious_task() == null ||
                    task.getPrevious_task().getId() <= 0 ||
                    !taskMap.containsKey(task.getPrevious_task().getId())) {
                startingTasks.add(task);
            }
        }

        if (startingTasks.isEmpty()) {
            startingTasks.addAll(tasks);
        }

        Duration maxDuration = new Duration(0, 0, 0, 0);

        for (Task startTask : startingTasks) {
            Duration chainDuration = new Duration(0, 0, 0, 0);
            Task current = startTask;

            while (current != null) {
                if (current.getExpected_duration() != null) {
                    chainDuration.add(current.getExpected_duration());
                }

                if (current.getNext_task() != null &&
                        current.getNext_task().getId() > 0 &&
                        taskMap.containsKey(current.getNext_task().getId())) {
                    current = taskMap.get(current.getNext_task().getId());
                } else {
                    current = null;
                }
            }

            if (chainDuration.getTotalDays() > maxDuration.getTotalDays()) {
                maxDuration = chainDuration;
            }
        }

        return maxDuration;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Activity resumed");
        if (planId != -1) {
            updateTaskDisplay();
        }
    }
}