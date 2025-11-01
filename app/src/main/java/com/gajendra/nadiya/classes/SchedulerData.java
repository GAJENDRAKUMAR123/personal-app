package com.gajendra.nadiya.classes;

public class SchedulerData {
    String title,desc,currentDate,startTime,endTime,key;

    public SchedulerData() {
    }

    public SchedulerData(String title, String desc, String currentDate, String startTime, String endTime) {
        this.title = title;
        this.desc = desc;
        this.currentDate = currentDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}