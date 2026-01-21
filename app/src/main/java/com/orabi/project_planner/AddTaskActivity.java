package com.orabi.project_planner;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AddTaskActivity extends AppCompatActivity {

    private RecyclerView rvTasks;
    private ShortTaskAdapter shortTaskAdapter;
    private List<Task> taskList = new ArrayList<>();
    private int taskCounter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // ربط العناصر
        ImageButton btnBack = findViewById(R.id.btnBack);
        Button btnSavePlan = findViewById(R.id.btnSavePlan);
        Button btnAddPreview = findViewById(R.id.btnAddPreview);
        EditText etTaskName = findViewById(R.id.etTaskName);
        EditText etTaskStart = findViewById(R.id.etTaskStrat);
        EditText etTaskDuration = findViewById(R.id.etTaskDuration);

        // RecyclerView
        rvTasks = findViewById(R.id.rvTasks);

        // إعداد RecyclerView
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        shortTaskAdapter = new ShortTaskAdapter(taskList);
        rvTasks.setAdapter(shortTaskAdapter);

        // إضافة فواصل بين العناصر
        addItemDecoration();

        // ===== زر ADD (إضافة مهمة جديدة) =====
        btnAddPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // جلب البيانات من الحقول
                String taskName = etTaskName.getText().toString().trim();
                String startAfter = etTaskStart.getText().toString().trim();
                String duration = etTaskDuration.getText().toString().trim();

                // التحقق من البيانات
                if (taskName.isEmpty()) {
                    etTaskName.setError("يرجى إدخال اسم المهمة");
                    etTaskName.requestFocus();
                    return;
                }

                if (duration.isEmpty()) {
                    etTaskDuration.setError("يرجى إدخال المدة");
                    etTaskDuration.requestFocus();
                    return;
                }

                // تنسيق المدة (إضافة 'd' إذا لم تكن موجودة)
                if (!duration.toLowerCase().endsWith("d")) {
                    duration = duration + "d";
                }

                // معالجة حقل Start After
                if (startAfter.isEmpty()) {
                    startAfter = "none";
                } else {
                    // إذا كان رقماً، تحقق من وجود المهمة
                    try {
                        int afterNum = Integer.parseInt(startAfter);
                        if (afterNum < 1 || afterNum > taskList.size()) {
                            etTaskStart.setError("يجب أن تكون المهمة موجودة");
                            etTaskStart.requestFocus();
                            return;
                        }
                    } catch (NumberFormatException e) {
                        // إذا لم يكن رقماً، يجب أن يكون "none"
                        if (!startAfter.equalsIgnoreCase("none")) {
                            etTaskStart.setError("أدخل 'none' أو رقم مهمة");
                            etTaskStart.requestFocus();
                            return;
                        }
                    }
                }

                // إنشاء كائن Task
                Task newTask = new Task();
//                newTask.startAfterTask = startAfter;
                newTask.setId(taskCounter)  ;

                // إضافة المهمة للقائمة
                shortTaskAdapter.addTask(newTask);

                // التمرير لأسفل لرؤية المهمة الجديدة
                rvTasks.scrollToPosition(taskList.size() - 1);

                // مسح الحقول
                etTaskName.setText("");
                etTaskStart.setText("");
                etTaskDuration.setText("");

                // التركيز على حقل اسم المهمة
                etTaskName.requestFocus();

                // عرض رسالة نجاح
                Toast.makeText(AddTaskActivity.this,
                        "✓ تمت إضافة المهمة رقم " + taskCounter,
                        Toast.LENGTH_SHORT).show();

                taskCounter++;

                // تحديث العداد في الـ hint
                etTaskStart.setHint("Start after task (1-" + (taskCounter-1) + " or none)");
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

        // إعداد حدث النقر على المهام
        setupTaskClickListener();

        // تعيين hint أولي
        etTaskStart.setHint("Start after task (none للبدء مباشرة)");
    }

    private void saveTasks() {
        if (taskList.isEmpty()) {
            Toast.makeText(this,
                    "❗ يرجى إضافة مهمة واحدة على الأقل",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // التحقق من الاعتمادية بين المهام
        boolean valid = validateTaskDependencies();
        if (!valid) {
            Toast.makeText(this,
                    "❌ هناك أخطاء في اعتماديات المهام",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // حفظ المهام (هنا تكتب كود الحفظ الفعلي)
        saveToDatabase();

        // رسالة النجاح
        Toast.makeText(this,
                "✅ تم حفظ " + taskList.size() + " مهمة بنجاح",
                Toast.LENGTH_SHORT).show();

        // إنهاء النشاط
        finish();
    }

    private boolean validateTaskDependencies() {
        for (int i = 0; i < taskList.size(); i++) {
            Task task = taskList.get(i);
//            String after = task.startAfterTask;
            String after="1";
            if (after != null && !after.equalsIgnoreCase("none")) {
                try {
                    int afterNum = Integer.parseInt(after);

                    // التحقق من أن الرقم صحيح
                    if (afterNum < 1 || afterNum > taskList.size()) {
                        Toast.makeText(this,
                                "المهمة " + (i+1) + " تعتمد على مهمة غير موجودة",
                                Toast.LENGTH_LONG).show();
                        return false;
                    }

                    // التحقق من عدم وجود اعتماديات دائرية
                    if (hasCircularDependency(i, afterNum - 1)) {
                        Toast.makeText(this,
                                "❌ يوجد اعتماد دائري بين المهام",
                                Toast.LENGTH_LONG).show();
                        return false;
                    }

                    // لا يمكن أن تعتمد المهمة على نفسها
                    if (afterNum == (i + 1)) {
                        Toast.makeText(this,
                                "❌ المهمة لا يمكن أن تعتمد على نفسها",
                                Toast.LENGTH_LONG).show();
                        return false;
                    }

                } catch (NumberFormatException e) {
                    Toast.makeText(this,
                            "❌ قيمة غير صالحة في 'Start after' للمهمة " + (i+1),
                            Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean hasCircularDependency(int taskIndex, int dependsOnIndex) {
        // كشف الاعتمادات الدائرية البسيطة
        Task dependsOnTask = taskList.get(dependsOnIndex);
//        String dependsOnValue = dependsOnTask.startAfterTask;
        String dependsOnValue ="2";
        if (dependsOnValue != null && !dependsOnValue.equalsIgnoreCase("none")) {
            try {
                int nextIndex = Integer.parseInt(dependsOnValue) - 1;
                if (nextIndex == taskIndex) {
                    return true; // اعتماد دائري
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    private void saveToDatabase() {
        // هنا تكتب كود الحفظ الفعلي
        // مثال:
        /*
        AppDatabase db = AppDatabase.getInstance(this);
        for (Task task : taskList) {
            db.taskDao().insertTask(task);
        }
        */

        // مؤقتاً: حفظ في SharedPreferences
        android.content.SharedPreferences prefs = getSharedPreferences("temp_tasks", MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("task_count", taskList.size());
        for (int i = 0; i < taskList.size(); i++) {
            Task task = taskList.get(i);
            editor.putString("task_" + i + "_name", task.getName());
            editor.putString("task_" + i + "_duration", task.getExpected_duration().toString());
            editor.putString("task_" + i + "_after", "1");
        }
        editor.apply();
    }

    private void setupTaskClickListener() {
        shortTaskAdapter.setOnTaskClickListener(new ShortTaskAdapter.OnTaskClickListener() {
            @Override
            public void onTaskClick(int position, Task task) {
                // عرض خيارات للمهمة (تحرير/حذف)
                showTaskOptions(position, task);
            }
        });
    }

    private void showTaskOptions(int position, Task task) {
        // يمكنك استخدام AlertDialog هنا
//        Toast.makeText(this,
//                "المهمة: " + task.taskName + "\n" +
//                        "المدة: " + task.duration + "\n" +
//                        "تبدأ بعد: " + task.startAfterTask,
//                Toast.LENGTH_SHORT).show();
    }

    private void addItemDecoration() {
        // إضافة مسافة بين العناصر
        rvTasks.addItemDecoration(new androidx.recyclerview.widget.DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
        ));
    }
}