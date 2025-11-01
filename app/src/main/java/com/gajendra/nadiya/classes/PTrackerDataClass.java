package com.gajendra.nadiya.classes;

public class PTrackerDataClass {
    String user,month, create_at,key;

    public PTrackerDataClass() {
    }

    public PTrackerDataClass(String user, String month, String create_at) {
        this.user = user;
        this.month = month;
        this.create_at = create_at;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
    public String getCreate_at() {
        return create_at;
    }
    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public String getKey() {return key;}

    public void setKey(String key) {this.key = key;}
}
