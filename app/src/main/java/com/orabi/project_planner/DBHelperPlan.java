package com.orabi.project_planner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 5/4/2023.
 */

public class DBHelperPlan extends SQLiteOpenHelper {
    // Database Name
    static final String DATABASE = "PROJECT_PLANNER.db";
    // Database Version
    static final int DB_VERSION = 1;
    // Table Name
    static final String TABLE = "Plan";
    // Table Field Name
    static final String P_ID = "id";
    static final String P_TITLE = "title";
    static final String P_STATUS = "status";

    static final String P_STARTDATE = "startDate";
    static final String P_EXPECTED_END_DATE = "expectedEndDate";
    static final String P_ALL_DURATION = "allDurationMillis";
    Context context ;
    // Override constructor
    public DBHelperPlan(Context context) {
        super(context, DATABASE, null, DB_VERSION);
        this.context = context;
        //   Toast.makeText(context, " here constructor dbhelper"   , Toast.LENGTH_LONG).show();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuery = "CREATE TABLE " + TABLE + " ( "
                + P_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + P_TITLE + " TEXT,"
                + P_STATUS + " TEXT,"  // Default to 0 (false)
                + P_STARTDATE + " TEXT,"
                + P_EXPECTED_END_DATE  + " TEXT,"
                + P_ALL_DURATION  + " INTEGER DEFAULT 0"
                + ")";
        System.out.println(" ----- create query : " + createQuery);
        // Toast.makeText(context, " here createQuery" + createQuery   , Toast.LENGTH_LONG).show();
// Create StudentInfo table using SQL query
        db.execSQL(createQuery);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println(" ----- upgrade query : " );
        //Toast.makeText(context, " here  upgrade query : "     , Toast.LENGTH_LONG).show();
// Drop old version table
        db.execSQL("Drop table " + TABLE);
// Create New Version table
        onCreate(db);
    }

    public void addPlan(Plan plan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(P_TITLE, plan.getTitle());
        values.put(P_ALL_DURATION, plan.getAllDurationInSeconds());
        values.put(P_STATUS, plan.getStatus());
        values.put(P_STARTDATE, plan.getStartDate());
        values.put(P_EXPECTED_END_DATE, plan.getExpectedEndDate());
// Inserting Row
        db.insert(TABLE, null, values);
        db.close();
    }

    public int updateStudentDetails(Plan plan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(P_TITLE, plan.getTitle());
        values.put(P_ALL_DURATION, plan.getAllDurationInSeconds());
        values.put(P_STATUS, plan.getStatus());
        values.put(P_STARTDATE, plan.getStartDate());
        values.put(P_EXPECTED_END_DATE, plan.getExpectedEndDate());
// updating row
        int result  = db.update(TABLE, values, P_ID + " = ?",
                new String[]{String.valueOf(plan.getId())});
        //@return the number of rows affected

        db.close();
        return result;

    }

    public List<Plan> getPlansDetails() {
        List<Plan> plans = new ArrayList<>();
// Select All Query
        String selectQuery = "SELECT * FROM " + TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Plan plan = new Plan();
                plan.setId(cursor.getInt(0));
                plan.setTitle(cursor.getString(1));
                plan.setStatus(cursor.getString(2));
                plan.setStartDate(cursor.getString(3));
                plan.setExpectedEndDate(cursor.getString(4));
                plan.setAllDurationInSeconds(cursor.getInt(5));
// Adding student information to list
                plans.add(plan);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return plans;
    }

    public Plan getPlanByID(int planid) {
// Select All Query
        String selectQuery = "SELECT * FROM " + TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Plan wantedPlan = new Plan();

        if (cursor.moveToFirst()) {
            do {
                if(cursor.getInt(0)==planid){
                    wantedPlan.setId(cursor.getInt(0));
                    wantedPlan.setTitle(cursor.getString(1));
                    wantedPlan.setStatus(cursor.getString(2));
                    wantedPlan.setStartDate(cursor.getString(3));
                    wantedPlan.setExpectedEndDate(cursor.getString(4));
                    wantedPlan.setAllDurationInSeconds(cursor.getInt(5));
                    return wantedPlan;

                }

// Adding student information to list
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return null;
    }

    public int deletePlan(Plan plan) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE, P_ID + " = ?",
                new String[]{String.valueOf(plan.getId())});
        db.close();
        //@return the number of rows affected
        return result;
    }

}
