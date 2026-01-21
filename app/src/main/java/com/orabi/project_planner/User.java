package com.orabi.project_planner;

public class User {
    private String Name;
    private int id;

    public User(String name){
        this.Name=name;

    }

    public User(){
        this.Name=null;

    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getName() {
        return Name;
    }
}
