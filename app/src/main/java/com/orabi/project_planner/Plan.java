package com.orabi.project_planner;

// Remove these Room imports and annotations
// import androidx.room.Entity;
// import androidx.room.PrimaryKey;

import java.util.List;

public class Plan {
    private int id;
    private String endDate;

    private String title;
    private String startDate;
    private String expectedEndDate;
    private String status;
    private String describtion;
    private Duration duration;

    // Empty constructor
    public Plan() {
        this.title = "";
        this.startDate = "";
        this.expectedEndDate = "";
        this.status = "";
        this.describtion = "";
        duration=new Duration();
        this.endDate = ""; // Initialize

    }

    // Constructor with ID
    public Plan(int id, String title, String startDate, String status, String description) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.status = status;
        this.describtion = description;
        this.expectedEndDate = "";
        duration=new Duration();

    }

    public Plan(String title, String startDate, String status, String description) {
        this.title = title;
        this.startDate = startDate;
        this.status = status;
        this.describtion = description;
        this.expectedEndDate = "";
        duration=new Duration();

    }
    public boolean isAllTasksCompleted(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return false;
        }

        for (Task task : tasks) {
            if (!"completed".equals(task.getStatus())) {
                return false;
            }
        }
        return true;
    }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getExpectedEndDate() { return expectedEndDate; }
    public void setExpectedEndDate(String expectedEndDate) { this.expectedEndDate = expectedEndDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescribtion() { return describtion; }
    public void setDescribtion(String describtion) { this.describtion = describtion; }

    public Duration getDuration() { return duration; }
    public void setDuration(Duration duration) { this.duration = duration; }
}