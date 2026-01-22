package com.orabi.project_planner;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
    private DBHelperPlan dbHelperPlan;
    private TextView tvDurationExample;

    private static final String TAG = "AddTaskActivity";
    private static final Pattern DURATION_PATTERN = Pattern.compile("^\\s*(?:(\\d+)\\s*m(?:o(?:nth(?:s)?)?)?)?\\s*(?:(\\d+)\\s*d(?:ay(?:s)?)?)?\\s*(?:(\\d+)\\s*h(?:our(?:s)?)?)?\\s*(?:(\\d+)\\s*m(?:in(?:ute(?:s)?)?)?)?\\s*$", Pattern.CASE_INSENSITIVE);

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
        dbHelperPlan = new DBHelperPlan(this);

        Plan plan = dbHelperPlan.getPlanByID(planId);
        if (plan != null && "done".equals(plan.getStatus())) {
            Toast.makeText(this, "❌ لا يمكن إضافة مهام لخطة مكتملة", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initializeTaskCounter();


        ImageButton btnBack = findViewById(R.id.btnBack);
        Button btnSavePlan = findViewById(R.id.btnSavePlan);
        Button btnAddPreview = findViewById(R.id.btnAddPreview);
        EditText etTaskName = findViewById(R.id.etTaskName);
        EditText etTaskDuration = findViewById(R.id.etTaskDuration);
        tvDurationExample = findViewById(R.id.tvDurationExample);

        spinnerTaskStart = findViewById(R.id.etTaskStrat);

        // RecyclerView
        rvTasks = findViewById(R.id.rvTasks);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        shortTaskAdapter = new ShortTaskAdapter(taskList);
        rvTasks.setAdapter(shortTaskAdapter);

        setupSpinner();

        setupDurationInput(etTaskDuration);

        btnAddPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Plan currentPlan = dbHelperPlan.getPlanByID(planId);
                if (currentPlan != null && "done".equals(currentPlan.getStatus())) {
                    Toast.makeText(AddTaskActivity.this,
                            "❌ لا يمكن إضافة مهام لخطة مكتملة",
                            Toast.LENGTH_LONG).show();
                    return;
                }

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

                Duration duration = parseDuration(durationStr);
                if (duration == null) {
                    etTaskDuration.setError("مدة غير صحيحة. مثال: 2d أو 3h 30m أو 1mo 5d");
                    etTaskDuration.requestFocus();
                    return;
                }

                if (duration.getMonths() == 0 && duration.getDays() == 0 &&
                        duration.getHours() == 0 && duration.getMinutes() == 0) {
                    etTaskDuration.setError("المدة يجب أن تكون على الأقل دقيقة واحدة");
                    etTaskDuration.requestFocus();
                    return;
                }

                String selectedOption = spinnerTaskStart.getSelectedItem().toString();
                Task previousTask = null;
                int previousTaskId = -1;

                if (!selectedOption.equals("Start immediately")) {
                    try {
                        String[] parts = selectedOption.split(":");
                        if (parts.length > 0) {
                            String firstPart = parts[0].trim(); // "After Task 1"
                            String numStr = firstPart.replaceAll("[^0-9]", "");
                            previousTaskId = Integer.parseInt(numStr);

                            if (previousTaskId > 0) {
                                for (Task task : taskList) {
                                    if (task.getId() == previousTaskId) {
                                        previousTask = task;
                                        break;
                                    }
                                }

                                if (previousTask == null) {
                                    previousTask = client_task.getTaskById(previousTaskId);
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(AddTaskActivity.this,
                                "خطأ في تحرقم المهمة السابقة", Toast.LENGTH_SHORT).show();
                    }
                }

                // Create new task
                Task newTask = new Task();
                newTask.setId(taskCounter);
                newTask.setName(taskName);
                newTask.setExpected_duration(duration);
                newTask.setPlanid(planId);
                newTask.setStart_date(null);
                newTask.setStatus("Waiting");

                if (previousTask != null) {
                    newTask.setPrevious_task(previousTask);

                    previousTask.setNext_task(newTask);

                    for (int i = 0; i < taskList.size(); i++) {
                        if (taskList.get(i).getId() == previousTask.getId()) {
                            taskList.set(i, previousTask);
                            break;
                        }
                    }

                    Toast.makeText(AddTaskActivity.this,
                            "✓ ستبدأ هذه المهمة بعد: " + previousTask.getName(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    newTask.setPrevious_task(null);
                    Toast.makeText(AddTaskActivity.this,
                            "✓ ستبدأ هذه المهمة فوراً",
                            Toast.LENGTH_SHORT).show();
                }

                taskList.add(newTask);
                taskMap.put(taskCounter, newTask);
                shortTaskAdapter.notifyDataSetChanged();

                calculateAndUpdatePlanDuration();

                updateSpinnerOptions();

                rvTasks.scrollToPosition(taskList.size() - 1);

                etTaskName.setText("");
                etTaskDuration.setText("");
                spinnerTaskStart.setSelection(0);

                String formattedDuration = formatDurationForDisplay(duration);
                Toast.makeText(AddTaskActivity.this,
                        "✓ تمت إضافة المهمة رقم " + taskCounter + " (المدة: " + formattedDuration + ")",
                        Toast.LENGTH_LONG).show();

                etTaskName.requestFocus();

                taskCounter++;
            }
        });

        btnSavePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTasks();
            }
        });

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

    private void setupDurationInput(EditText etTaskDuration) {
        etTaskDuration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().trim();
                if (input.isEmpty()) {
                    tvDurationExample.setText("أمثلة: 2d, 3h 30m, 1mo 5d, 45m");
                    tvDurationExample.setTextColor(getResources().getColor(R.color.secondary_text));
                    return;
                }

                Duration duration = parseDuration(input);
                if (duration != null) {
                    String formatted = formatDurationForDisplay(duration);
                    tvDurationExample.setText("✓ المدة: " + formatted);
                    tvDurationExample.setTextColor(getResources().getColor(R.color.status_green));
                } else {
                    tvDurationExample.setText("❌ تنسيق غير صحيح. أمثلة: 2d, 3h 30m, 1mo 5d");
                    tvDurationExample.setTextColor(getResources().getColor(R.color.status_red));
                }
            }
        });

        tvDurationExample.setText("أمثلة: 2d, 3h 30m, 1mo 5d, 45m");
        tvDurationExample.setTextColor(getResources().getColor(R.color.secondary_text));
    }

    /**
     * Parse duration string with flexible format
     * Supports: 2d, 3h, 30m, 1mo, 1mo 5d, 2d 3h, 3h 30m, 1mo 5d 3h 30m
     */
    private Duration parseDuration(String durationStr) {
        if (durationStr == null || durationStr.trim().isEmpty()) {
            return null;
        }

        String input = durationStr.trim().toLowerCase();

        java.util.regex.Matcher matcher = DURATION_PATTERN.matcher(input);
        if (matcher.matches()) {
            try {
                int months = 0, days = 0, hours = 0, minutes = 0;

                // Check each group
                String monthGroup = matcher.group(1);
                String dayGroup = matcher.group(2);
                String hourGroup = matcher.group(3);
                String minuteGroup = matcher.group(4);

                if (monthGroup != null) months = Integer.parseInt(monthGroup);
                if (dayGroup != null) days = Integer.parseInt(dayGroup);
                if (hourGroup != null) hours = Integer.parseInt(hourGroup);
                if (minuteGroup != null) minutes = Integer.parseInt(minuteGroup);

                // Validate values
                if (months >= 0 && days >= 0 && hours >= 0 && minutes >= 0 &&
                        (months > 0 || days > 0 || hours > 0 || minutes > 0)) {
                    return new Duration(months, days, hours, minutes);
                }
            } catch (NumberFormatException e) {
            }
        }

        try {
            int months = 0, days = 0, hours = 0, minutes = 0;

            String[] parts = input.split("\\s+");

            for (String part : parts) {
                if (part.isEmpty()) continue;

                String numStr = part.replaceAll("[^0-9]", "");
                String unit = part.replaceAll("[0-9]", "").toLowerCase();

                if (numStr.isEmpty()) continue;

                int value = Integer.parseInt(numStr);

                if (unit.contains("mo") || unit.contains("month")) {
                    months = value;
                } else if (unit.contains("d") || unit.contains("day")) {
                    days = value;
                } else if (unit.contains("h") || unit.contains("hour")) {
                    hours = value;
                } else if (unit.contains("m") && !unit.contains("mo")) {
                    minutes = value;
                } else if (unit.isEmpty()) {
                    days = value;
                }
            }

            if (months > 0 || days > 0 || hours > 0 || minutes > 0) {
                return new Duration(months, days, hours, minutes);
            }
        } catch (NumberFormatException e) {
            return null;
        }

        return null;
    }

    private void initializeTaskCounter() {
        List<Task> existingTasks = client_task.getTasksByPlanId(planId);
        if (!existingTasks.isEmpty()) {

            int maxId = 0;
            for (Task task : existingTasks) {
                if (task.getId() > maxId) {
                    maxId = task.getId();
                }
            }
            taskCounter = maxId + 1;
        } else {
            taskCounter = 1;
        }
        Log.d(TAG, "Initialized taskCounter to: " + taskCounter);
    }

    private void setupSpinner() {
        // Clear previous options
        startOptions.clear();
        startOptions.add("Start immediately");

        List<Task> availableTasks = client_task.getTasksWithoutNextTask(planId);

        for (Task task : availableTasks) {
            String option = "After Task " + task.getId() + ": " + task.getName();
            startOptions.add(option);
            taskMap.put(task.getId(), task);
        }

        for (Task task : taskList) {
            if (task.getNext_task() == null) {
                String option = "After Task " + task.getId() + ": " + task.getName();
                if (!startOptions.contains(option)) {
                    startOptions.add(option);
                }
            }
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
                String selectedOption = startOptions.get(position);
                if (!selectedOption.equals("Start immediately")) {
                    Toast.makeText(AddTaskActivity.this,
                            "المهمة الجديدة ستبدأ بعد: " + selectedOption,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateSpinnerOptions() {
        List<String> newOptions = new ArrayList<>();
        newOptions.add("Start immediately");

        for (Task task : taskList) {
            if (task.getNext_task() == null) {
                String option = "After Task " + task.getId() + ": " + task.getName();
                if (!newOptions.contains(option)) {
                    newOptions.add(option);
                }
            }
        }

        List<Task> dbTasks = client_task.getTasksWithoutNextTask(planId);
        for (Task task : dbTasks) {
            String option = "After Task " + task.getId() + ": " + task.getName();
            if (!newOptions.contains(option)) {
                newOptions.add(option);
            }
        }

        startOptions.clear();
        startOptions.addAll(newOptions);
        spinnerAdapter.notifyDataSetChanged();
    }

    /**
     * Calculate plan duration as the maximum sum of task chains
     * and update it in the database
     */
    private void calculateAndUpdatePlanDuration() {
        try {
            List<Task> allTasks = new ArrayList<>();

            List<Task> dbTasks = client_task.getTasksByPlanId(planId);
            allTasks.addAll(dbTasks);

            for (Task localTask : taskList) {

                boolean existsInDb = false;
                for (Task dbTask : dbTasks) {
                    if (dbTask.getId() == localTask.getId()) {
                        existsInDb = true;
                        break;
                    }
                }
                if (!existsInDb) {
                    allTasks.add(localTask);
                }
            }

            Log.d(TAG, "Calculating plan duration from " + allTasks.size() + " tasks");

            Duration planDuration = calculateMaxChainDuration(allTasks);
            String durationText = formatDurationForDisplay(planDuration);

            Log.d(TAG, "Calculated plan duration: " + durationText);

            updatePlanDurationOnly(planDuration);

            Toast.makeText(this, "✓ مدة الخطة: " + durationText, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e(TAG, "Error calculating plan duration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updatePlanDurationOnly(Duration newDuration) {
        SQLiteDatabase db = dbHelperPlan.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            String durationStr = newDuration.getMonths() + ":" +
                    newDuration.getDays() + ":" +
                    newDuration.getHours() + ":" +
                    newDuration.getMinutes();
            values.put(DBHelperPlan.P_DURATION, durationStr);

            int result = db.update("`" + DBHelperPlan.TABLE + "`",
                    values,
                    DBHelperPlan.P_ID + " = ?",
                    new String[]{String.valueOf(planId)});

            Log.d(TAG, "Updated only plan duration, rows affected: " + result);
        } catch (Exception e) {
            Log.e(TAG, "Error updating plan duration: " + e.getMessage());
        } finally {
            db.close();
        }
    }

    /**
     * Calculate the maximum chain duration from a list of tasks
     */
    private Duration calculateMaxChainDuration(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return new Duration(0, 0, 0, 0);
        }

        Map<Integer, Task> taskMap = new HashMap<>();
        for (Task task : tasks) {
            taskMap.put(task.getId(), task);
        }

        List<Task> startingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getPrevious_task() == null ||
                    task.getPrevious_task().getId() <= 0 ||
                    !taskMap.containsKey(task.getPrevious_task().getId())) {
                startingTasks.add(task);
                Log.d(TAG, "Starting task found: ID=" + task.getId() + ", Name=" + task.getName());
            }
        }

        if (startingTasks.isEmpty()) {
            startingTasks.addAll(tasks);
            Log.d(TAG, "No starting tasks found, using all tasks as independent");
        }

        Log.d(TAG, "Found " + startingTasks.size() + " starting tasks");

        Duration maxDuration = new Duration(0, 0, 0, 0);

        for (Task startTask : startingTasks) {
            Duration chainDuration = new Duration(0, 0, 0, 0);
            Task current = startTask;
            int chainLength = 0;

            Log.d(TAG, "Calculating chain starting from task ID=" + startTask.getId());

            // Follow the chain
            while (current != null) {
                chainLength++;
                if (current.getExpected_duration() != null) {
                    chainDuration.add(current.getExpected_duration());
                    Log.d(TAG, "  Adding task " + current.getId() + " duration: " +
                            current.getExpected_duration().getDays() + "d, " +
                            "Chain total so far: " + chainDuration.getDays() + "d");
                }

                if (current.getNext_task() != null &&
                        current.getNext_task().getId() > 0 &&
                        taskMap.containsKey(current.getNext_task().getId())) {
                    current = taskMap.get(current.getNext_task().getId());
                } else {
                    current = null;
                }
            }

            Log.d(TAG, "Chain length: " + chainLength + " tasks, Total duration: " +
                    chainDuration.getDays() + "d");

            // Compare and keep the maximum
            if (chainDuration.getTotalDays() > maxDuration.getTotalDays()) {
                maxDuration = chainDuration;
                Log.d(TAG, "New max duration found: " + maxDuration.getDays() + "d");
            }
        }

        Log.d(TAG, "Final max chain duration: " + maxDuration.getDays() + "d");
        return maxDuration;
    }

    /**
     * Format duration for display in UI
     */
    private String formatDurationForDisplay(Duration duration) {
        if (duration == null) return "0d";

        StringBuilder sb = new StringBuilder();
        if (duration.getMonths() > 0) {
            sb.append(duration.getMonths()).append("mo");
            if (duration.getDays() > 0 || duration.getHours() > 0 || duration.getMinutes() > 0) {
                sb.append(" ");
            }
        }
        if (duration.getDays() > 0) {
            sb.append(duration.getDays()).append("d");
            if (duration.getHours() > 0 || duration.getMinutes() > 0) {
                sb.append(" ");
            }
        }
        if (duration.getHours() > 0) {
            sb.append(duration.getHours()).append("h");
            if (duration.getMinutes() > 0) {
                sb.append(" ");
            }
        }
        if (duration.getMinutes() > 0) {
            sb.append(duration.getMinutes()).append("m");
        }

        if (sb.length() == 0) {
            sb.append("0m");
        }

        return sb.toString();
    }

    private void saveTasks() {
        Plan planCheck = dbHelperPlan.getPlanByID(planId);
        if (planCheck != null && "done".equals(planCheck.getStatus())) {
            Toast.makeText(this, "❌ لا يمكن إضافة مهام لخطة مكتملة", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (taskList.isEmpty()) {
            Toast.makeText(this, "❗ يرجى إضافة مهمة واحدة على الأقل", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Saving " + taskList.size() + " tasks to database");

        for (Task task : taskList) {
            Log.d(TAG, "Saving task: ID=" + task.getId() + ", Name=" + task.getName() +
                    ", PreviousTask=" + (task.getPrevious_task() != null ? task.getPrevious_task().getId() : "null") +
                    ", NextTask=" + (task.getNext_task() != null ? task.getNext_task().getId() : "null"));

            long taskId = client_task.addTask(task);
            if (taskId != -1) {
                task.setId((int) taskId);
                Log.d(TAG, "Task saved with database ID: " + taskId);

                if (task.getPrevious_task() != null && task.getPrevious_task().getId() > 0) {
                    Task previousTaskInDb = client_task.getTaskById(task.getPrevious_task().getId());
                    if (previousTaskInDb != null) {
                        previousTaskInDb.setNext_task(task);
                        client_task.updateTaskDetails(previousTaskInDb);
                        Log.d(TAG, "Updated relationship: Task " + previousTaskInDb.getId() +
                                " -> Task " + task.getId());
                    }
                }
            } else {
                Log.e(TAG, "Failed to save task: " + task.getName());
            }
        }

        Toast.makeText(this, "✅ تم حفظ " + taskList.size() + " مهمة بنجاح", Toast.LENGTH_SHORT).show();

        try {
            Duration planDuration = dbHelperPlan.calculatePlanDuration(planId, client_task);
            Plan plan = dbHelperPlan.getPlanByID(planId);
            if (plan != null && planDuration != null) {
                plan.setDuration(planDuration);
                dbHelperPlan.updateStudentDetails(plan);
                Log.d(TAG, "Final plan duration updated: " + planDuration.toString());
                Toast.makeText(this, "✓ مدة الخطة النهائية: " + formatDurationForDisplay(planDuration),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error calculating final plan duration: " + e.getMessage());
        }

        finish();
    }
}