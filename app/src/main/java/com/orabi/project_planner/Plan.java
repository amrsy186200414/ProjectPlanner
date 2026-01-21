package com.orabi.project_planner;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "plans_table")
public class Plan {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String startDate;
    private String expectedEndDate;
    private String status; // (completed, late, in_progress)
    private long allDurationMillis; // سنخزن المدة بالملي ثانية ليسهل حسابها

    public Plan(){
        this.title = null;
        this.startDate = null;
        this.expectedEndDate = null;
        this.status = null;
    }
    public Plan(String title, String startDate, String expectedEndDate, String status) {
        this.title = title;
        this.startDate = startDate;
        this.expectedEndDate = expectedEndDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getAllDurationMillis() {
        return allDurationMillis;
    }

    public void setAllDurationMillis(long allDurationMillis) {
        this.allDurationMillis = allDurationMillis;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getExpectedEndDate() {
        return expectedEndDate;
    }

    public void setExpectedEndDate(String expectedEndDate) {
        this.expectedEndDate = expectedEndDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
