package com.gajendra.nadiya.classes;

public class TODOTasks {
    String task,date;
    String key;

    public TODOTasks() {
    }

    public TODOTasks(String task, String date) {
        this.task = task;
        this.date = date;
    }

    public TODOTasks(String task, String date, String key) {
        this.task = task;
        this.date = date;
        this.key = key;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
