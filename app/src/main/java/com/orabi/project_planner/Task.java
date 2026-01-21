package com.orabi.project_planner;
//
//import androidx.room.Entity;
//import androidx.room.PrimaryKey;
//
//@Entity(tableName = "tasks_table")
//public class Task {
//    @PrimaryKey(autoGenerate = true)
//    public int taskId;
//
//    public int parentPlanId; // يربط المهمة بالخطة
//    public String taskName;
//    public String duration;
//    public String startAfterTask; // كما في واجهة الإضافة الخاصة بك
//    public String status;
//
//    public Task(int parentPlanId, String taskName, String duration, String status) {
//        this.parentPlanId = parentPlanId;
//        this.taskName = taskName;
//        this.duration = duration;
//        this.status = status;
//    }
//}
//
//package com.example.projectplanner;

import java.util.Date;

public class Task {
    private String name;
    private int id;
    private String status;
    private Date start_date;
    private Duration expected_duration;

    private Duration real_duration;

    private Task previous_task;
    private Task next_task;


    public Task(String name, int id, Date start_date, Duration expected_duration) {
        this.id = id;
        this.name = name;
        this.start_date = start_date;
        this.expected_duration = expected_duration;
        this.status = "Waiting";
    }
    public Task() {
        this.id = 0;
        this.name = null;
        this.start_date = null;
        this.expected_duration = null;
        this.status = null;
    }



    public Duration getExpected_duration() {
        return expected_duration;
    }

    public Duration getReal_duration() {
        return real_duration;
    }

    public void setExpected_duration(Duration expected_duration) {
        this.expected_duration = expected_duration;
    }

    public void setReal_duration(Duration real_duration) {
        this.real_duration = real_duration;
    }

    public String getName() { return name; }
    public int getId() { return id; }
    public Date getStart_date() { return start_date; }
    public String getStatus() { return status; }
    public Task getPrevious_task() { return previous_task; }
    public Task getNext_task() { return next_task; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public void setStatus(String status) { this.status = status; }
    public void setPrevious_task(Task previous_task) { this.previous_task = previous_task; }
    public void setNext_task(Task next_task) { this.next_task = next_task; }
}