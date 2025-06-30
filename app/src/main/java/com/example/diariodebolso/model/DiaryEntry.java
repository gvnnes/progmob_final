package com.example.diariodebolso.model;

public class DiaryEntry {
    private long id;
    private String title;
    private String content;
    private String photoPath;
    private String location;
    private String date;
    private long userId;

    public DiaryEntry(String title, String content, String date) {
        this.title = title;
        this.content = content;
        this.date = date;
    }
    public DiaryEntry() {

    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
}