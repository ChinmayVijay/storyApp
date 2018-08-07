package com.example.ichin.storyapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.ichin.storyapp.database.StoryDatabase;
import com.example.ichin.storyapp.executors.StoryAppExecutors;
import com.example.ichin.storyapp.model.StoryModel;
import com.example.ichin.storyapp.viewmodel.AddStoryViewModel;
import com.example.ichin.storyapp.viewmodel.AddStoryViewModelFactory;

import java.util.Date;

public class AddStoryActivity extends AppCompatActivity {

    private EditText storyTitle;
    private EditText storyDescription;
    private FloatingActionButton fabSaveStory;
    private static final int DEFAULT_TASK_ID = -1;
    private static final String INSTANCE_TASK_ID = "instance";
    public static final String EXTRA_TASK_ID = "extraTaskId";

    private StoryDatabase mDb;

    private int mTaskId = DEFAULT_TASK_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);

        initViews();

        mDb = StoryDatabase.getInstance(getApplicationContext());


        if(savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)){
            mTaskId = savedInstanceState.getInt(INSTANCE_TASK_ID,DEFAULT_TASK_ID);
        }

        Intent intent = getIntent();
        if(intent!=null && intent.hasExtra(EXTRA_TASK_ID)){
            mTaskId = intent.getIntExtra(EXTRA_TASK_ID,DEFAULT_TASK_ID);
            AddStoryViewModelFactory addStoryViewModelFactory = new AddStoryViewModelFactory(mDb,mTaskId);

            final AddStoryViewModel addStoryViewModel = ViewModelProviders.of(this,addStoryViewModelFactory).get(AddStoryViewModel.class);

            addStoryViewModel.getStory().observe(this, new Observer<StoryModel>() {
                @Override
                public void onChanged(@Nullable StoryModel storyModel) {
                    addStoryViewModel.getStory().removeObserver(this);
                    populateUI(storyModel);
                }
            });
        }

    }

    private void initViews() {
        storyTitle = findViewById(R.id.et_story_title);
        storyDescription = findViewById(R.id.et_story_description);

        fabSaveStory = findViewById(R.id.fab_save_story);

        fabSaveStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveStory();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_TASK_ID,mTaskId);
        super.onSaveInstanceState(outState);
    }

    protected void saveStory(){
        String description = storyDescription.getText().toString();
        String title = storyTitle.getText().toString();
        Date date = new Date();
        int size = 250;

        final StoryModel storyModel = new StoryModel(title,description,size,date);
        StoryAppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                if(mTaskId == DEFAULT_TASK_ID){
                    mDb.storyDao().insertStory(storyModel);
                }
                else{
                    storyModel.setId(mTaskId);
                    mDb.storyDao().updateStory(storyModel);
                }
                finish();
            }
        });

    }

    private void populateUI(StoryModel storyModel){
        storyTitle.setText(storyModel.getTitle());
        storyDescription.setText(storyModel.getDescription());
    }
}
