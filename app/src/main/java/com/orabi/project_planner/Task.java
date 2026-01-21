package com.orabi.project_planner;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks_table")
public class Task {
    @PrimaryKey(autoGenerate = true)
    public int taskId;

    public int parentPlanId; // يربط المهمة بالخطة
    public String taskName;
    public String duration;
    public String startAfterTask; // كما في واجهة الإضافة الخاصة بك
    public String status;

    public Task(int parentPlanId, String taskName, String duration, String status) {
        this.parentPlanId = parentPlanId;
        this.taskName = taskName;
        this.duration = duration;
        this.status = status;
    }
}