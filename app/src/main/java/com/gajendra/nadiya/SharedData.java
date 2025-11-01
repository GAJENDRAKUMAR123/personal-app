package com.gajendra.nadiya;

public class SharedData {
    public String user;
    public String title;
    public String create_at;
    String key;
    String story;
    int id;

    public SharedData() {
        // Default constructor required for calls to DataSnapshot.getValue(SharedData.class)
    }

    public SharedData(String user, String title, String create_at) {
        this.user = user;
        this.title = title;
        this.create_at = create_at;
    }
    public SharedData(String user, String title, String create_at, String story) {
        this.user = user;
        this.title = title;
        this.create_at = create_at;
        this.story = story;
    }

    public SharedData(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}
}