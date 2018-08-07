package com.example.ichin.storyapp.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "story")
public class StoryModel {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private int size;
    @ColumnInfo(name="updated_at")
    private Date updatedAt;

    public StoryModel(int id, String title, String description, int size, Date updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.size = size;

    }

    @Ignore
    public StoryModel(String title, String description, int size, Date updatedAt) {
        this.title = title;
        this.description = description;
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
