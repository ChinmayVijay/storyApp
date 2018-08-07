package com.example.ichin.storyapp.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.ichin.storyapp.database.StoryDatabase;
import com.example.ichin.storyapp.model.StoryModel;

public class AddStoryViewModel extends ViewModel {
    private LiveData<StoryModel> story;

    public AddStoryViewModel(StoryDatabase storyDb, int storyId) {
        story = storyDb.storyDao().loadTaskbyId(storyId);
    }

    public LiveData<StoryModel> getStory() {
        return story;
    }
}
