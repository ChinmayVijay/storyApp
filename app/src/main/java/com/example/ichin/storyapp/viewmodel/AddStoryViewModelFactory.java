package com.example.ichin.storyapp.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;

import com.example.ichin.storyapp.database.StoryDatabase;

public class AddStoryViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final StoryDatabase db;
    private final int storyId;

    public AddStoryViewModelFactory(StoryDatabase db, int storyId) {
        this.db = db;
        this.storyId = storyId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddStoryViewModel(db,storyId);
    }
}
