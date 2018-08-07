package com.example.ichin.storyapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.ichin.storyapp.model.StoryModel;

import java.util.List;

@Dao
public interface StoryDao {

    @Query("SELECT * FROM story")
    LiveData<List<StoryModel>> loadAllStory();

    @Insert
    void insertStory(StoryModel model);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateStory(StoryModel model);

    @Delete
    void deleteStory(StoryModel model);

    @Query("SELECT * FROM story where id=:id")
    LiveData<StoryModel> loadTaskbyId(int id);
}
