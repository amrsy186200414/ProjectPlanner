//package com.orabi.project_planner;
//
//import androidx.room.Database;
//import androidx.room.Room;
//import androidx.room.RoomDatabase;
//import android.content.Context;
//
//import com.orabi.project_planner.Plan;
//import com.orabi.project_planner.Task;
//
//@Database(entities = {Plan.class, Task.class}, version = 1)
//public abstract class AppDatabase extends RoomDatabase {
//
//    private static AppDatabase instance;
//    public abstract PlanDao planDao();
//
//    public static synchronized AppDatabase getInstance(Context context) {
//        if (instance == null) {
//            instance = Room.databaseBuilder(context.getApplicationContext(),
//                            AppDatabase.class, "planner_db")
//                    .fallbackToDestructiveMigration()
//                    .allowMainThreadQueries() // للتسهيل في البداية فقط
//                    .build();
//        }
//        return instance;
//    }
//}