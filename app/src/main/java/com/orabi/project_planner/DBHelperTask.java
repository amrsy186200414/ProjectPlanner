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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



//import model.StudentInfo;
/**
 * Created by user on 5/4/2023.
 */

public class DBHelperTask extends SQLiteOpenHelper {
    // Database Name
    static final String DATABASE = "Project_Planner.db";
    // Database Version
    static final int DB_VERSION = 1;
    // Table Name
    static final String TABLE_TASK = "Task";

    // Table Field Name
    static final String T_ID = "id";
    static final String T_NAME = "name";
    static final String T_STATUS = "status";
    static final String T_EXPECTED_DURATION = "expected_duration";
    static final String T_REAL_DURATION = "real_duration";

    static final String T_START_DATE = "start_date";

    static final String T_PREVIOUS_TASK="previous_task";
    static final String T_NEXT_TASK="next_task";

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    Context context ;
    // Override constructor
    public DBHelperTask(Context context) {
        super(context, DATABASE, null, DB_VERSION);
        this.context = context;
        //   Toast.makeText(context, " here constructor dbhelper"   , Toast.LENGTH_LONG).show();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // CORRECT CREATE TABLE STATEMENT
        String CREATE_PLANS_TABLE = "CREATE TABLE " + TABLE_TASK + "("
                + T_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + T_NAME + " TEXT,"
                + T_STATUS + " TEXT,"
                + T_START_DATE + " TEXT,"
                + T_EXPECTED_DURATION  + " TEXT,"
                + T_REAL_DURATION  + " TEXT,"
                + T_PREVIOUS_TASK+" TEXT,"
                + T_NEXT_TASK+" INTEGER"

                + ")";

        System.out.println("Creating table with query: " + CREATE_PLANS_TABLE);
        db.execSQL(CREATE_PLANS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println(" ----- upgrade query : " );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        onCreate(db);
    }



    public void addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(T_NAME, task.getName());
        values.put(T_STATUS, task.getStatus());
        values.put(T_EXPECTED_DURATION, task.getExpected_duration().toString());


        // Convert Date to String for storage
        Date StartDate = task.getStart_date();
        if (StartDate != null) {
            values.put(T_START_DATE, dateFormat.format(StartDate));
        } else {
            values.putNull(T_START_DATE);
        }


        Duration realDuration = task.getReal_duration();
        if (realDuration != null) {
            values.put(T_REAL_DURATION,task.getReal_duration().toString() );
        } else {
            values.putNull(T_REAL_DURATION);
        }

        Task previous=task.getPrevious_task();
        if (previous != null) {
            values.put(T_PREVIOUS_TASK,task.getPrevious_task().getId() );
        } else {
            values.putNull(T_PREVIOUS_TASK);
        }

        Task next=task.getNext_task();
        if (next != null) {
            values.put(T_NEXT_TASK,task.getNext_task().getId() );
        } else {
            values.putNull(T_NEXT_TASK);
        }


        db.insert(TABLE_TASK, null, values);
        db.close();
    }

    public int updateTaskDetails(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(T_NAME, task.getName());
        values.put(T_STATUS, task.getStatus());
        values.put(T_EXPECTED_DURATION, task.getExpected_duration().toString());


        // Convert Date to String for storage
        Date StartDate = task.getStart_date();
        if (StartDate != null) {
            values.put(T_START_DATE, dateFormat.format(StartDate));
        } else {
            values.putNull(T_START_DATE);
        }


        Duration realDuration = task.getReal_duration();
        if (realDuration != null) {
            values.put(T_REAL_DURATION,task.getReal_duration().toString() );
        } else {
            values.putNull(T_REAL_DURATION);
        }

        Task previous=task.getPrevious_task();
        if (previous != null) {
            values.put(T_PREVIOUS_TASK,task.getPrevious_task().getId() );
        } else {
            values.putNull(T_PREVIOUS_TASK);
        }

        Task next=task.getNext_task();
        if (next != null) {
            values.put(T_NEXT_TASK,task.getNext_task().getId() );
        } else {
            values.putNull(T_NEXT_TASK);
        }


        int result  = db.update(TABLE_TASK, values, T_ID + " = ?",
                new String[]{String.valueOf(task.getId())});
        //@return the number of rows affected

        db.close();
        return result;

    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TASK;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Task task = cursorToTask(cursor);
                tasks.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tasks;
    }

    public Task getTaskById(int taskId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASK,
                new String[]{T_ID, T_NAME, T_STATUS,
                        T_START_DATE, T_EXPECTED_DURATION, T_REAL_DURATION,
                        T_PREVIOUS_TASK, T_NEXT_TASK},
                T_ID + "=?",
                new String[]{String.valueOf(taskId)},
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Task task = cursorToTask(cursor);
            cursor.close();
            db.close();
            return task;
        }

        if (cursor != null) cursor.close();
        db.close();
        return null;
    }


    private Task cursorToTask(Cursor cursor) {
        Task task = new Task();

        // Basic fields
        task.setId(cursor.getInt(cursor.getColumnIndexOrThrow(T_ID)));
        task.setName(cursor.getString(cursor.getColumnIndexOrThrow(T_NAME)));
        task.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(T_STATUS)));

        // Expected start date
        if (!cursor.isNull(cursor.getColumnIndexOrThrow(T_START_DATE))) {
            String dateStr = cursor.getString(cursor.getColumnIndexOrThrow(T_START_DATE));
            try {
                Date date = dateFormat.parse(dateStr);
                task.setStart_date(date);
            } catch (Exception e) {
                e.printStackTrace();
                task.setStart_date(null);
            }
        }



        // Expected duration
        if (!cursor.isNull(cursor.getColumnIndexOrThrow(T_EXPECTED_DURATION))) {
            String durationStr = cursor.getString(cursor.getColumnIndexOrThrow(T_EXPECTED_DURATION));
            Duration duration = stringToDuration(durationStr);
            task.setExpected_duration(duration);
        }

        // Real duration
        if (!cursor.isNull(cursor.getColumnIndexOrThrow(T_REAL_DURATION))) {
            String durationStr = cursor.getString(cursor.getColumnIndexOrThrow(T_REAL_DURATION));
            Duration duration = stringToDuration(durationStr);
            task.setReal_duration(duration);
        }

        return task;
    }
    private String durationToString(Duration duration) {
        if (duration == null) return null;

        return duration.getMonths() + ":" +
                duration.getDays() + ":" +
                duration.getHours() + ":" +
                duration.getMinutes();
    }

    private Duration stringToDuration(String durationStr) {
        if (durationStr == null || durationStr.isEmpty()) {
            return null;
        }

        try {
            String[] parts = durationStr.split(":");
            if (parts.length == 4) {
                Duration duration = new Duration();
                duration.setMonths(Integer.parseInt(parts[0]));
                duration.setDays(Integer.parseInt(parts[1]));
                duration.setHours(Integer.parseInt(parts[2]));
                duration.setMinutes(Integer.parseInt(parts[3]));
                return duration;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

//    private Plan getTaskById(int TaskId) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(TABLE_TASK,
//                new String[]{T_ID, T_NAME, T_STATUS, T_EPECTED_START_DATE, T_REAL_START_DATE,T_EXPECTED_DURATION,T_REAL_DURATION,T_PREVIOUS_TASK,T_NEXT_TASK},
//                T_ID + "=?",
//                new String[]{String.valueOf(TaskId)},
//                null, null, null, null);
//
//        if (cursor != null && cursor.moveToFirst()) {
//            Task task = cursorToPlan(cursor);
//            cursor.close();
//            db.close();
//            return plan;
//        }
//
//        if (cursor != null) cursor.close();
//        db.close();
//        return null;
//    }

    //    public List<S





}
