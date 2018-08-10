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
    @ColumnInfo(name = "poster_path")
    private String posterPath;
    @ColumnInfo(name="updated_at")
    private Date updatedAt;
    private String plot;
    @ColumnInfo(name="character_one_path")
    private String characterOnePath;
    @ColumnInfo(name="character_two_path")
    private String characterTwoPath;
    @ColumnInfo(name="character_three_path")
    private String characterThreePath;

    public StoryModel(int id, String title, String description, int size,String posterPath, Date updatedAt,
                      String plot, String characterOnePath, String characterTwoPath, String characterThreePath) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.size = size;
        this.updatedAt = updatedAt;
        this.posterPath = posterPath;
        this.plot = plot;
        this.characterOnePath = characterOnePath;
        this.characterTwoPath = characterTwoPath;
        this.characterThreePath = characterThreePath;

    }

    @Ignore
    public StoryModel(String title, String description, int size,String posterPath, Date updatedAt, String plot, String characterOnePath, String characterTwoPath, String characterThreePath) {
        this.title = title;
        this.description = description;
        this.size = size;
        this.posterPath = posterPath;
        this.plot = plot;
        this.characterOnePath = characterOnePath;
        this.characterTwoPath = characterTwoPath;
        this.characterThreePath = characterThreePath;
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

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getCharacterOnePath() {
        return characterOnePath;
    }

    public void setCharacterOnePath(String characterOnePath) {
        this.characterOnePath = characterOnePath;
    }

    public String getCharacterTwoPath() {
        return characterTwoPath;
    }

    public void setCharacterTwoPath(String characterTwoPath) {
        this.characterTwoPath = characterTwoPath;
    }

    public String getCharacterThreePath() {
        return characterThreePath;
    }

    public void setCharacterThreePath(String characterThreePath) {
        this.characterThreePath = characterThreePath;
    }
}

