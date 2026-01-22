package com.orabi.project_planner;

import java.util.Date;

public class Task {
    private String name;
    private int id;
    private String status;
    private Date start_date;
    private Date endDate; // Add this field

    private Duration expected_duration;
    private int planid;
    private Duration real_duration;
    private Task previous_task;
    private Task next_task;

    public Task(String name, int id, Date start_date, Duration expected_duration) {
        this.id = id;
        this.name = name;
        this.start_date = start_date;
        this.expected_duration = expected_duration;
        this.status = "Waiting";
        this.real_duration = new Duration();
        this.previous_task = null;
        this.next_task = null;
        this.planid = -1; // Default value
    }

    public Task() {
        this.id = 0;
        this.name = null;
        this.start_date = null;
        this.expected_duration = new Duration();
        this.status = "Waiting"; // Changed from null to "Waiting"
        this.real_duration = new Duration();
        this.previous_task = null;
        this.next_task = null;
        this.planid = -1; // Default value
    }

    public int getPlanid() { return planid; }
    public void setPlanid(int planid) { this.planid = planid; }

    public Duration getExpected_duration() { return expected_duration; }
    public void setExpected_duration(Duration expected_duration) { this.expected_duration = expected_duration; }

    public Duration getReal_duration() { return real_duration; }
    public void setReal_duration(Duration real_duration) { this.real_duration = real_duration; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getStart_date() { return start_date; }
    public void setStart_date(Date start_date) { this.start_date = start_date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Task getPrevious_task() { return previous_task; }
    public void setPrevious_task(Task previous_task) { this.previous_task = previous_task; }

    public Task getNext_task() { return next_task; }
    public void setNext_task(Task next_task) { this.next_task = next_task; }
}