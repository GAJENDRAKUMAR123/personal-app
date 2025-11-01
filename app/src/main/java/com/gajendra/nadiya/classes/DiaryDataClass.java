package com.gajendra.nadiya.classes;

public class DiaryDataClass {

    String title,Story,created_at,key,user;

    public DiaryDataClass() {
    }

    public DiaryDataClass(String title, String story, String created_at) {
        this.title = title;
        Story = story;
        this.created_at = created_at;
    }
  //  public BinClass(String date, String month, String token, String user) {

    public DiaryDataClass(String title, String story, String created_at, String user) {
        this.title = title;
        Story = story;
        this.created_at = created_at;
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStory() {
        return Story;
    }

    public void setStory(String story) {
        Story = story;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
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
}
