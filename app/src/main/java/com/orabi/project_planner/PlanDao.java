package com.orabi.project_planner;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.orabi.project_planner.Plan;
import com.orabi.project_planner.Task;

import java.util.List;

@Dao
public interface PlanDao {
    @Insert
    void insertPlan(Plan plan);

    @Query("SELECT * FROM plans_table ORDER BY id DESC")
    List<Plan> getAllPlans();

    @Insert
    void insertTask(Task task);

    @Query("SELECT * FROM tasks_table WHERE parentPlanId = :planId")
    List<Task> getTasksForPlan(int planId);
}