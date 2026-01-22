package com.orabi.project_planner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PlanDetailsActivity extends AppCompatActivity implements TaskAdapter.OnAllTasksCompletedListener {
    DBHelperPlan dbHelper;
    DBHelperTask dbHelperTask;
    private RecyclerView rvTasks;
    private TaskAdapter taskAdapter;
    private int planId;

    private ImageButton addTaskBtn;

    private static final String TAG = "PlanDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_details);

        dbHelper = new DBHelperPlan(this);
        dbHelperTask = new DBHelperTask(this);

        planId = getIntent().getIntExtra("PLAN_ID", -1);
        Log.d(TAG, "Plan ID: " + planId);

        ImageButton btnBack = findViewById(R.id.btnBack);
        addTaskBtn = findViewById(R.id.addTaskBtn);
        TextView tvPlanName = findViewById(R.id.tvPlanName);
        TextView tvAllDurationValue = findViewById(R.id.tvAllDurationValue);
        TextView tvDescriptionValue = findViewById(R.id.tvDescriptionValue);
        TextView tvStartValue = findViewById(R.id.tvStartValue);
        TextView tvTasksNumberValue = findViewById(R.id.tvTasksNumberValue);
        TextView tvCompletedTasksValue = findViewById(R.id.tvCompletedTasksValue);

        rvTasks = findViewById(R.id.rvTasks);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));

        updateAddTaskButton();

        addTaskBtn.setOnClickListener(v -> {
            if (dbHelper.canAddTasksToPlan(planId)) {
                Intent intent = new Intent(PlanDetailsActivity.this, AddTaskActivity.class);
                intent.putExtra("PLAN_ID", planId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "❌ الخصة مكتملة - لا يمكن إضافة مهام جديدة", Toast.LENGTH_SHORT).show();
            }
        });

        if (planId != -1) {
            Plan plan = dbHelper.getPlanByID(planId);

            if (plan != null) {
                logPlanAttributes(plan);

                tvPlanName.setText(plan.getTitle());

                String formattedStartDate = formatDateToYYYYMMDDHHMM(plan.getStartDate());
                tvStartValue.setText(formattedStartDate);

                if (plan.getDescribtion() != null && !plan.getDescribtion().isEmpty()) {
                    tvDescriptionValue.setText(plan.getDescribtion());
                } else {
                    tvDescriptionValue.setText("No description available");
                }

                updateAddTaskButton();

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

    private void updateAddTaskButton() {
        if (planId == -1) return;

        Plan plan = dbHelper.getPlanByID(planId);
        if (plan == null) return;

        String status = plan.getStatus();

        if ("completed".equals(status) || "done".equals(status)) {
            // حالة مكتملة
            addTaskBtn.setBackgroundResource(R.drawable.rounded_time_bg);
            addTaskBtn.setImageResource(R.drawable.ic_right);
            addTaskBtn.setEnabled(false);
            addTaskBtn.setImageTintList(getResources().getColorStateList(R.color.status_green));
            addTaskBtn.setAlpha(0.7f);

            // يمكنك إضافة Tooltip
            addTaskBtn.setContentDescription("الخطة مكتملة");

        } else if ("in_progress".equals(status)) {
            addTaskBtn.setEnabled(true);
            addTaskBtn.setAlpha(1.0f);
            addTaskBtn.setContentDescription("قيد التنفيذ - إضافة مهمة");

        } else if ("late".equals(status)) {
            addTaskBtn.setEnabled(true);
            addTaskBtn.setAlpha(1.0f);
            addTaskBtn.setContentDescription("متأخرة - إضافة مهمة");

        } else {
            addTaskBtn.setImageResource(R.drawable.ic_addtask);
            addTaskBtn.setEnabled(true);
            addTaskBtn.setAlpha(1.0f);
            addTaskBtn.setContentDescription("في الانتظار - إضافة مهمة");
        }
    }

    private void updateAddTaskButtonSimple() {
        if (planId == -1) return;

        Plan plan = dbHelper.getPlanByID(planId);
        if (plan == null) return;

        String status = plan.getStatus();

        if ("completed".equals(status) || "done".equals(status)) {
            addTaskBtn.setBackgroundResource(R.drawable.rounded_button_bg_disabled);
            addTaskBtn.setEnabled(false);
            addTaskBtn.setAlpha(0.5f);

        } else if ("in_progress".equals(status)) {
            addTaskBtn.setBackgroundResource(R.drawable.rounded_button_bg_yellow);
            addTaskBtn.setEnabled(true);
            addTaskBtn.setAlpha(1.0f);

        } else if ("late".equals(status)) {
            addTaskBtn.setBackgroundResource(R.drawable.rounded_button_bg_red);
            addTaskBtn.setEnabled(true);
            addTaskBtn.setAlpha(1.0f);

        } else {
            addTaskBtn.setBackgroundResource(R.drawable.rounded_button_bg);
            addTaskBtn.setEnabled(true);
            addTaskBtn.setAlpha(1.0f);
        }
    }

    private void updateAddTaskButtonTint() {
        if (planId == -1) return;

        Plan plan = dbHelper.getPlanByID(planId);
        if (plan == null) return;

        String status = plan.getStatus();

        if ("completed".equals(status) || "done".equals(status)) {
            addTaskBtn.setImageTintList(getResources().getColorStateList(R.color.status_green));

        } else if ("in_progress".equals(status)) {
            addTaskBtn.setImageTintList(getResources().getColorStateList(R.color.status_yellow));

        } else if ("late".equals(status)) {
            addTaskBtn.setImageTintList(getResources().getColorStateList(R.color.status_red));

        } else {
            addTaskBtn.setImageTintList(getResources().getColorStateList(R.color.white));
        }
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

    private String formatDateToYYYYMMDDHHMM(String dateStr) {
        if (dateStr == null || dateStr.isEmpty() || dateStr.equals("--")) {
            return "--";
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            SimpleDateFormat inputFormat;

            if (dateStr.contains("/")) {
                if (dateStr.contains(":")) {
                    inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                } else {
                    inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                }
            } else if (dateStr.contains("-") && dateStr.contains(":")) {
                return dateStr;
            } else if (dateStr.contains("-")) {
                inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            } else {
                return dateStr;
            }

            Date date = inputFormat.parse(dateStr);
            if (date != null) {
                return sdf.format(date);
            }
            return dateStr;

        } catch (Exception e) {
            return dateStr;
        }
    }

    private void updatePlanDurationDisplay(Plan plan) {
        TextView tvAllDurationValue = findViewById(R.id.tvAllDurationValue);

        if (plan.getDuration() != null) {
            String durationText = formatDurationForDisplay(plan.getDuration());
            tvAllDurationValue.setText(durationText);
            Log.d(TAG, "Displaying duration: " + durationText);
        } else {
            Duration calculatedDuration = dbHelper.calculatePlanDuration(planId, dbHelperTask);
            if (calculatedDuration != null && calculatedDuration.getTotalDays() > 0) {
                String durationText = formatDurationForDisplay(calculatedDuration);
                tvAllDurationValue.setText(durationText);

                plan.setDuration(calculatedDuration);
                dbHelper.updateStudentDetails(plan);
                Log.d(TAG, "Calculated and saved duration: " + durationText);
            } else {
                tvAllDurationValue.setText("Not calculated");
                Log.d(TAG, "Duration not calculated");
            }
        }
    }

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

    private void updateTaskDisplay() {
        List<Task> tasks = dbHelperTask.getTasksByPlanId(planId);
        Log.d(TAG, "Tasks count: " + tasks.size());

        TextView tvTasksNumberValue = findViewById(R.id.tvTasksNumberValue);
        TextView tvCompletedTasksValue = findViewById(R.id.tvCompletedTasksValue);
        TextView tvAllDurationValue = findViewById(R.id.tvAllDurationValue);

        tvTasksNumberValue.setText(String.valueOf(tasks.size()));

        int completedCount = 0;
        for (Task task : tasks) {
            if ("completed".equals(task.getStatus())) {
                completedCount++;
            }
        }
        tvCompletedTasksValue.setText(String.valueOf(completedCount));

        Plan plan = dbHelper.getPlanByID(planId);
        if (plan != null) {
            // 1. Check if all tasks are completed
            boolean allTasksCompleted = tasks.size() > 0;
            for (Task task : tasks) {
                if (!"completed".equals(task.getStatus())) {
                    allTasksCompleted = false;
                    break;
                }
            }

            if (allTasksCompleted && tasks.size() > 0 && !"completed".equals(plan.getStatus())) {
                // تحديث حالة الخطة
                plan.setStatus("completed");

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                String now = sdf.format(new Date());
                plan.setEndDate(now);

                dbHelper.updateStudentDetails(plan);
                Log.d(TAG, "All tasks completed. Plan marked as 'completed'");

                updateAddTaskButton();

                Toast.makeText(this, "🎉 تم إكمال الخطة بنجاح!", Toast.LENGTH_SHORT).show();
            }

            Duration planDuration = calculateMaxChainDuration(tasks);
            String durationText = formatDurationForDisplay(planDuration);
            tvAllDurationValue.setText(durationText);

            plan.setDuration(planDuration);

            if (plan.getStartDate() != null && !plan.getStartDate().isEmpty() && planDuration != null) {
                try {
                    String formattedStartDate = formatDateToYYYYMMDDHHMM(plan.getStartDate());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    Date startDate = sdf.parse(formattedStartDate);

                    if (startDate != null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(startDate);

                        calendar.add(Calendar.MONTH, planDuration.getMonths());
                        calendar.add(Calendar.DAY_OF_MONTH, planDuration.getDays());
                        calendar.add(Calendar.HOUR_OF_DAY, planDuration.getHours());
                        calendar.add(Calendar.MINUTE, planDuration.getMinutes());

                        String expectedEndDate = sdf.format(calendar.getTime());
                        plan.setExpectedEndDate(expectedEndDate);
                        Log.d(TAG, "Expected end date calculated: " + expectedEndDate);
                    }
                } catch (ParseException e) {
                    Log.e(TAG, "Error parsing start date: " + e.getMessage());
                } catch (Exception e) {
                    Log.e(TAG, "Error calculating expected end date: " + e.getMessage());
                }
            }

            dbHelper.updateStudentDetails(plan);
            Log.d(TAG, "Plan updated with new duration and expected end date");

            updateAddTaskButton();
        }

        logTaskSummary(tasks, completedCount);

        taskAdapter = new TaskAdapter(tasks, this, planId);
        taskAdapter.setOnAllTasksCompletedListener(this);
        rvTasks.setAdapter(taskAdapter);
    }

    private void logTaskSummary(List<Task> tasks, int completedCount) {
        Log.d(TAG, "=== TASK SUMMARY ===");
        Log.d(TAG, "Total tasks: " + tasks.size());
        Log.d(TAG, "Completed: " + completedCount);
        Log.d(TAG, "In progress/Waiting: " + (tasks.size() - completedCount));

        for (int i = 0; i < Math.min(tasks.size(), 5); i++) {
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
    public void onAllTasksCompleted(int completedPlanId) {
        if (completedPlanId == planId) {
            runOnUiThread(() -> {
                updateAddTaskButton();

                Toast.makeText(this, "✅ تم إكمال جميع مهام الخطة", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void refreshPlanData() {
        Plan plan = dbHelper.getPlanByID(planId);
        if (plan != null) {
            TextView tvPlanName = findViewById(R.id.tvPlanName);
            TextView tvStartValue = findViewById(R.id.tvStartValue);
            TextView tvDescriptionValue = findViewById(R.id.tvDescriptionValue);

            tvPlanName.setText(plan.getTitle());

            String formattedStartDate = formatDateToYYYYMMDDHHMM(plan.getStartDate());
            tvStartValue.setText(formattedStartDate);

            if (plan.getDescribtion() != null && !plan.getDescribtion().isEmpty()) {
                tvDescriptionValue.setText(plan.getDescribtion());
            } else {
                tvDescriptionValue.setText("No description available");
            }

            updateAddTaskButton();

            updatePlanDurationDisplay(plan);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Activity resumed");
        if (planId != -1) {
            refreshPlanData();
            updateTaskDisplay();
        }
    }
}