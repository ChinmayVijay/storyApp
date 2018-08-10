package com.example.ichin.storyapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.example.ichin.storyapp.database.StoryDatabase;
import com.example.ichin.storyapp.executors.StoryAppExecutors;
import com.example.ichin.storyapp.listeners.OnStoryCoverListener;
import com.example.ichin.storyapp.model.StoryModel;
import com.example.ichin.storyapp.viewmodel.MainViewModel;

import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class MainActivity extends AppCompatActivity {

    private RecyclerView storyRecyclerView;
    private FloatingActionButton btn_fab_add_story;
    private OnStoryCoverListener mListener;
    private StoryAdapter mAdadpter;
    private StoryDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storyRecyclerView = findViewById(R.id.rv_stories);
        btn_fab_add_story = findViewById(R.id.fab_add_story);

        mListener = new OnStoryCoverListener() {
            @Override
            public void onClick(int itemId) {
                Intent storyIntent = new Intent(MainActivity.this,AddStoryActivity.class);
                storyIntent.putExtra(AddStoryActivity.EXTRA_TASK_ID,itemId);
                startActivity(storyIntent);
            }
        };

        mAdadpter = new StoryAdapter(this,mListener);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
        storyRecyclerView.setLayoutManager(layoutManager);
        storyRecyclerView.setHasFixedSize(true);
        storyRecyclerView.setAdapter(mAdadpter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        storyRecyclerView.addItemDecoration(decoration);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                //put delete logic
                StoryAppExecutors.getInstance().getDiskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<StoryModel> stories = mAdadpter.getStories();
                        mDb.storyDao().deleteStory(stories.get(position));
                    }
                });

            }
        }).attachToRecyclerView(storyRecyclerView);

        btn_fab_add_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddStoryActivity.class);
                startActivity(intent);
            }
        });

        mDb = StoryDatabase.getInstance(getApplicationContext());

        setupViewModel();




    }

    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getStories().observe(this, new Observer<List<StoryModel>>() {
            @Override
            public void onChanged(@Nullable List<StoryModel> storyModels) {
                mAdadpter.setStories(storyModels);
            }
        });
    }


}
