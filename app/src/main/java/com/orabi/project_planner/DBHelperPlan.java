package com.orabi.project_planner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DBHelperPlan extends SQLiteOpenHelper {
    // Database Name
    static final String DATABASE = "PROJECT_PLANNER.db";
    // Database Version
    static final int DB_VERSION = 3;
    // Table Name
    static final String TABLE = "Plan";
    // Table Field Name
    static final String P_ID = "id";
    static final String P_TITLE = "title";
    static final String P_STATUS = "status";
    static final String P_STARTDATE = "startDate";
    static final String P_EXPECTED_END_DATE = "expectedEndDate";
    static final String P_END_DATE = "endDate";
    static final String P_DURATION = "duration";
    static final String P_DESCRIBTION = "describtion";

    private static final String TAG = "DBHelperPlan";

    Context context;

    public DBHelperPlan(Context context) {
        super(context, DATABASE, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuery = "CREATE TABLE `" + TABLE + "` ( "
                + P_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + P_TITLE + " TEXT,"
                + P_STATUS + " TEXT,"
                + P_STARTDATE + " TEXT,"
                + P_EXPECTED_END_DATE + " TEXT,"
                + P_END_DATE + " TEXT,"
                + P_DURATION + " TEXT,"
                + P_DESCRIBTION + " TEXT"
                + ")";
        Log.d(TAG, "Creating table with query: " + createQuery);
        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            Log.d(TAG, "Upgrading from version " + oldVersion + " to " + newVersion);
            db.execSQL("ALTER TABLE `" + TABLE + "` ADD COLUMN " + P_END_DATE + " TEXT");
        } else {
            db.execSQL("DROP TABLE IF EXISTS `" + TABLE + "`");
            onCreate(db);
        }
    }

    public long addPlan(Plan plan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(P_TITLE, plan.getTitle());

        // Convert duration to proper string format
        String durationStr = "";
        if (plan.getDuration() != null) {
            durationStr = plan.getDuration().getMonths() + ":" +
                    plan.getDuration().getDays() + ":" +
                    plan.getDuration().getHours() + ":" +
                    plan.getDuration().getMinutes();
        } else {
            durationStr = "0:0:0:0";
        }
        values.put(P_DURATION, durationStr);

        values.put(P_STATUS, plan.getStatus());
        values.put(P_STARTDATE, plan.getStartDate());
        values.put(P_EXPECTED_END_DATE, plan.getExpectedEndDate());
        values.put(P_DESCRIBTION, plan.getDescribtion());
        values.put(P_END_DATE, plan.getEndDate());

        Log.d(TAG, "Adding plan: " + plan.getTitle() +
                ", Duration: " + durationStr +
                ", Description: " + plan.getDescribtion());

        long planId = db.insert("`" + TABLE + "`", null, values);
        db.close();
        return planId;
    }

    public int updateStudentDetails(Plan plan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(P_TITLE, plan.getTitle());

        // Convert duration to proper string format
        String durationStr = "";
        if (plan.getDuration() != null) {
            durationStr = plan.getDuration().getMonths() + ":" +
                    plan.getDuration().getDays() + ":" +
                    plan.getDuration().getHours() + ":" +
                    plan.getDuration().getMinutes();
        } else {
            durationStr = "0:0:0:0";
        }
        values.put(P_DURATION, durationStr);

        values.put(P_STATUS, plan.getStatus());
        values.put(P_STARTDATE, plan.getStartDate());
        values.put(P_EXPECTED_END_DATE, plan.getExpectedEndDate());
        values.put(P_DESCRIBTION, plan.getDescribtion());
        values.put(P_END_DATE, plan.getEndDate());

        Log.d(TAG, "Updating plan ID " + plan.getId() +
                ", Duration: " + durationStr +
                ", Description: " + plan.getDescribtion());

        int result = db.update("`" + TABLE + "`", values, P_ID + " = ?",
                new String[]{String.valueOf(plan.getId())});
        db.close();
        return result;
    }
    // Add this method to DBHelperPlan.java or a utility class
    public boolean canAddTasksToPlan(int planId) {
        Plan plan = getPlanByID(planId);
        return plan != null && !"done".equals(plan.getStatus());
    }

    public List<Plan> getPlansDetails() {
        List<Plan> plans = new ArrayList<>();
        String selectQuery = "SELECT * FROM `" + TABLE + "`";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Plan plan = new Plan();

                // Use column names instead of indices
                plan.setId(cursor.getInt(cursor.getColumnIndexOrThrow(P_ID)));
                plan.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(P_TITLE)));
                plan.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(P_STATUS)));
                plan.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow(P_STARTDATE)));
                plan.setExpectedEndDate(cursor.getString(cursor.getColumnIndexOrThrow(P_EXPECTED_END_DATE)));
                plan.setEndDate(cursor.getString(cursor.getColumnIndexOrThrow(P_END_DATE)));

                // Get duration and description by column name
                String durationStr = cursor.getString(cursor.getColumnIndexOrThrow(P_DURATION));
                String descriptionStr = cursor.getString(cursor.getColumnIndexOrThrow(P_DESCRIBTION));

                Log.d(TAG, "Retrieved Plan ID " + plan.getId() +
                        " - Duration: '" + durationStr +
                        "', Description: '" + descriptionStr + "'");

                plan.setDuration(Duration.fromString(durationStr));
                plan.setDescribtion(descriptionStr);

                plans.add(plan);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return plans;
    }
    public Plan getPlanByID(int planid) {
        String selectQuery = "SELECT * FROM `" + TABLE + "` WHERE " + P_ID + " = ?";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(planid)});

        if (cursor.moveToFirst()) {
            Plan plan = new Plan();

            // Use column names instead of indices
            plan.setId(cursor.getInt(cursor.getColumnIndexOrThrow(P_ID)));
            plan.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(P_TITLE)));
            plan.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(P_STATUS)));
            plan.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow(P_STARTDATE)));
            plan.setExpectedEndDate(cursor.getString(cursor.getColumnIndexOrThrow(P_EXPECTED_END_DATE)));
            plan.setEndDate(cursor.getString(cursor.getColumnIndexOrThrow(P_END_DATE)));

            // Get duration and description by column name
            String durationStr = cursor.getString(cursor.getColumnIndexOrThrow(P_DURATION));
            String descriptionStr = cursor.getString(cursor.getColumnIndexOrThrow(P_DESCRIBTION));

            Log.d(TAG, "Retrieved single Plan ID " + planid +
                    " - Duration: '" + durationStr +
                    "', Description: '" + descriptionStr + "'");

            plan.setDuration(Duration.fromString(durationStr));
            plan.setDescribtion(descriptionStr);

            cursor.close();
            db.close();
            return plan;
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return null;
    }
    public int deletePlan(Plan plan) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("`" + TABLE + "`", P_ID + " = ?",
                new String[]{String.valueOf(plan.getId())});
        db.close();
        return result;
    }

    // Rest of your methods remain the same...
    public void updatePlanEndDateIfAllTasksCompleted(int planId, DBHelperTask dbHelperTask) {
        List<Task> tasks = dbHelperTask.getTasksByPlanId(planId);
        boolean allCompleted = true;

        for (Task task : tasks) {
            if (!"completed".equals(task.getStatus())) {
                allCompleted = false;
                break;
            }
        }

        if (allCompleted && !tasks.isEmpty()) {
            Plan plan = getPlanByID(planId);
            if (plan != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                plan.setEndDate(sdf.format(new Date()));
                plan.setStatus("completed");
                updateStudentDetails(plan);
            }
        }
    }

    public Duration calculatePlanDuration(int planId, DBHelperTask dbHelperTask) {
        List<Task> tasks = dbHelperTask.getTasksByPlanId(planId);

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
            }
        }

        if (startingTasks.isEmpty()) {
            startingTasks.addAll(tasks);
        }

        Duration maxDuration = new Duration(0, 0, 0, 0);

        for (Task startTask : startingTasks) {
            Duration chainDuration = calculateChainDuration(startTask, taskMap);

            if (chainDuration.getTotalDays() > maxDuration.getTotalDays()) {
                maxDuration = chainDuration;
            }
        }

        return maxDuration;
    }

    private Duration calculateChainDuration(Task startTask, Map<Integer, Task> taskMap) {
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

        return chainDuration;
    }
}