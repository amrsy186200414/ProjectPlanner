package com.orabi.project_planner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DBHelperTask extends SQLiteOpenHelper {
    static final String DATABASE = "Project_Planner.db";
    static final int DB_VERSION = 2;
    static final String TABLE_TASK = "Task";

    // Table Field Names
    static final String T_ID = "id";
    static final String T_NAME = "name";
    static final String T_STATUS = "status";
    static final String T_EXPECTED_DURATION = "expected_duration";
    static final String T_REAL_DURATION = "real_duration";
    static final String T_START_DATE = "start_date";
    static final String T_PREVIOUS_TASK = "previous_task";
    static final String T_NEXT_TASK = "next_task";
    static final String T_PLAN_ID = "planid";
    static final String T_END_DATE = "end_date";

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    public DBHelperTask(Context context) {
        super(context, DATABASE, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TASK_TABLE = "CREATE TABLE " + TABLE_TASK + "("
                + T_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + T_NAME + " TEXT,"
                + T_STATUS + " TEXT,"
                + T_START_DATE + " TEXT,"
                + T_END_DATE + " TEXT,"  // Add this line
                + T_EXPECTED_DURATION + " TEXT,"
                + T_REAL_DURATION + " TEXT,"
                + T_PREVIOUS_TASK + " INTEGER,"
                + T_NEXT_TASK + " INTEGER,"
                + T_PLAN_ID + " INTEGER"
                + ")";
        db.execSQL(CREATE_TASK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_TASK + " ADD COLUMN " + T_END_DATE + " TEXT");
        }
        if (oldVersion < 1) {
            onCreate(db);
        }
    }

    // Method 1: Add a new task
    public long addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(T_NAME, task.getName());
        values.put(T_STATUS, task.getStatus());
        values.put(T_EXPECTED_DURATION, durationToString(task.getExpected_duration()));
        values.put(T_PLAN_ID, task.getPlanid());

        // Convert Date to String for storage
        if (task.getStart_date() != null) {
            values.put(T_START_DATE, dateFormat.format(task.getStart_date()));
        } else {
            values.putNull(T_START_DATE);
        }
        if (task.getEndDate() != null) {
            values.put(T_END_DATE, dateFormat.format(task.getEndDate()));
        } else {
            values.putNull(T_END_DATE);
        }
        // Real duration
        values.put(T_REAL_DURATION, durationToString(task.getReal_duration()));

        // Previous task ID (store as integer)
        if (task.getPrevious_task() != null) {
            values.put(T_PREVIOUS_TASK, task.getPrevious_task().getId());
        } else {
            values.put(T_PREVIOUS_TASK, -1); // -1 means no previous task
        }

        // Next task ID (store as integer)
        if (task.getNext_task() != null) {
            values.put(T_NEXT_TASK, task.getNext_task().getId());
        } else {
            values.put(T_NEXT_TASK, -1); // -1 means no next task
        }

        long id = db.insert(TABLE_TASK, null, values);
        db.close();
        return id;
    }

    // Method 2: Update an existing task
    public int updateTaskDetails(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(T_NAME, task.getName());
        values.put(T_STATUS, task.getStatus());

        // Convert Date to String for storage
        Date startDate = task.getStart_date();
        if (startDate != null) {
            values.put(T_START_DATE, dateFormat.format(startDate));
        } else {
            values.putNull(T_START_DATE);
        }
        if (task.getEndDate() != null) {
            values.put(T_END_DATE, dateFormat.format(task.getEndDate()));
        } else {
            values.putNull(T_END_DATE);
        }
        // Convert durations to string
        if (task.getExpected_duration() != null) {
            values.put(T_EXPECTED_DURATION, durationToString(task.getExpected_duration()));
        }

        if (task.getReal_duration() != null) {
            values.put(T_REAL_DURATION, durationToString(task.getReal_duration()));
        }

        values.put(T_PLAN_ID, task.getPlanid());

        // Store previous and next task IDs
        if (task.getPrevious_task() != null) {
            values.put(T_PREVIOUS_TASK, task.getPrevious_task().getId());
        } else {
            values.put(T_PREVIOUS_TASK, -1);
        }

        if (task.getNext_task() != null) {
            values.put(T_NEXT_TASK, task.getNext_task().getId());
        } else {
            values.put(T_NEXT_TASK, -1);
        }

        // Update the task
        int result = db.update(TABLE_TASK, values, T_ID + " = ?",
                new String[]{String.valueOf(task.getId())});

        db.close();
        return result;
    }

    // Method 3: Get task by ID
    public Task getTaskById(int taskId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_TASK + " WHERE " + T_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(taskId)});

        if (cursor != null && cursor.moveToFirst()) {
            Task task = cursorToTask(cursor);
            cursor.close();
            db.close();
            return task;
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return null;
    }

    // Method 4: Get tasks without next_task for a specific plan
    public List<Task> getTasksWithoutNextTask(int planId) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_TASK +
                " WHERE " + T_PLAN_ID + " = ? AND " +
                "(" + T_NEXT_TASK + " IS NULL OR " + T_NEXT_TASK + " = -1)";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(planId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Task task = cursorToTask(cursor);
                tasks.add(task);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return tasks;
    }

    // Method 5: Get all tasks by plan ID
    public List<Task> getTasksByPlanId(int planId) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_TASK +
                " WHERE " + T_PLAN_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(planId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Task task = cursorToTask(cursor);
                tasks.add(task);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return tasks;
    }

    // Method 6: Get all tasks
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_TASK;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Task task = cursorToTask(cursor);
                tasks.add(task);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return tasks;
    }

    // Method 7: Delete a task
    public int deleteTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_TASK, T_ID + " = ?",
                new String[]{String.valueOf(task.getId())});
        db.close();
        return result;
    }

    // Helper method to convert Duration to string
    private String durationToString(Duration duration) {
        if (duration == null) {
            return "0:0:0:0";
        }
        return duration.getMonths() + ":" +
                duration.getDays() + ":" +
                duration.getHours() + ":" +
                duration.getMinutes();
    }

    // Helper method to convert string to Duration
    private Duration stringToDuration(String durationStr) {
        if (durationStr == null || durationStr.isEmpty()) {
            return new Duration(0, 0, 0, 0);
        }

        try {
            String[] parts = durationStr.split(":");
            if (parts.length == 4) {
                return new Duration(
                        Integer.parseInt(parts[0]), // months
                        Integer.parseInt(parts[1]), // days
                        Integer.parseInt(parts[2]), // hours
                        Integer.parseInt(parts[3])  // minutes
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Duration(0, 0, 0, 0);
    }

    // Convert cursor to Task object
    // Convert cursor to Task object
// Convert cursor to Task object - SIMPLE FIX
    private Task cursorToTask(Cursor cursor) {
        Task task = new Task();

        task.setId(cursor.getInt(cursor.getColumnIndexOrThrow(T_ID)));
        task.setName(cursor.getString(cursor.getColumnIndexOrThrow(T_NAME)));
        task.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(T_STATUS)));
        task.setPlanid(cursor.getInt(cursor.getColumnIndexOrThrow(T_PLAN_ID)));

        // Parse dates
        String dateStr = cursor.getString(cursor.getColumnIndexOrThrow(T_START_DATE));
        if (dateStr != null) {
            try {
                task.setStart_date(dateFormat.parse(dateStr));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String endDateStr = cursor.getString(cursor.getColumnIndexOrThrow(T_END_DATE));
        if (endDateStr != null && !endDateStr.isEmpty()) {
            try {
                task.setEndDate(dateFormat.parse(endDateStr));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Parse durations
        String expectedDurationStr = cursor.getString(cursor.getColumnIndexOrThrow(T_EXPECTED_DURATION));
        task.setExpected_duration(stringToDuration(expectedDurationStr));

        String realDurationStr = cursor.getString(cursor.getColumnIndexOrThrow(T_REAL_DURATION));
        task.setReal_duration(stringToDuration(realDurationStr));

        // CRITICAL FIX: Create placeholder Task objects for relationships
        int previousTaskId = cursor.getInt(cursor.getColumnIndexOrThrow(T_PREVIOUS_TASK));
        if (previousTaskId != -1 && previousTaskId > 0) {
            Task previousTask = new Task();
            previousTask.setId(previousTaskId);
            task.setPrevious_task(previousTask);
        } else {
            task.setPrevious_task(null);
        }

        int nextTaskId = cursor.getInt(cursor.getColumnIndexOrThrow(T_NEXT_TASK));
        if (nextTaskId != -1 && nextTaskId > 0) {
            Task nextTask = new Task();
            nextTask.setId(nextTaskId);
            task.setNext_task(nextTask);
        } else {
            task.setNext_task(null);
        }

        return task;
    }}