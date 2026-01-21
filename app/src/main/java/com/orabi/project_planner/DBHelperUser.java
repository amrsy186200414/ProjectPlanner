//package com.example.projectplanner;
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.content.ContentValues;
//
//import java.text.SimpleDateFormat;
//import java.util.Locale;
//
//public class DBHelperUser extends SQLiteOpenHelper {
//    // Database Name
//    static final String DATABASE = "Project_Planner.db";
//    // Database Version
//    static final int DB_VERSION = 1;
//    // Table Name
//    static final String TABLE_USER = "User";
//
//    // Table Field Name
//    static final String U_ID = "id";
//    static final String U_NAME = "name";
//
//
//    Context context ;
//    // Override constructor
//    public DBHelperUser(Context context) {
//        super(context, DATABASE, null, DB_VERSION);
//        this.context = context;
//        //   Toast.makeText(context, " here constructor dbhelper"   , Toast.LENGTH_LONG).show();
//    }
//
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        // CORRECT CREATE TABLE STATEMENT
//        String CREATE_PLANS_TABLE = "CREATE TABLE " + TABLE_USER + "("
//                + U_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + U_NAME + " TEXT DEFAULT NULL"
//                + ")";
//
//        System.out.println("Creating table with query: " + CREATE_PLANS_TABLE);
//        db.execSQL(CREATE_PLANS_TABLE);
//    }
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        System.out.println(" ----- upgrade query : " );
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
//        onCreate(db);
//    }
//
//
//    public int updateUserDetails(User user) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(U_NAME, user.getName());
//
//        int result  = db.update(TABLE_USER, values, U_ID + " = ?",
//                new String[]{String.valueOf(user.getId())});
//        //@return the number of rows affected
//
//        db.close();
//        return result;
//
//    }
//
//
//}
