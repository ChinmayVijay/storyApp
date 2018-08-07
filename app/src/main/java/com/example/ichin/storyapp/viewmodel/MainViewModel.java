package com.example.ichin.storyapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.ichin.storyapp.database.StoryDatabase;
import com.example.ichin.storyapp.model.StoryModel;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<StoryModel>> stories;
    private StoryDatabase db;

    public MainViewModel(@NonNull Application application) {
        super(application);
        db = StoryDatabase.getInstance(application);
        stories = db.storyDao().loadAllStory();
    }

    public LiveData<List<StoryModel>> getStories() {
        return stories;
    }
}
