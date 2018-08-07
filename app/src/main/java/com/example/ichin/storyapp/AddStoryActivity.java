package com.example.ichin.storyapp;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.ichin.storyapp.database.StoryDatabase;
import com.example.ichin.storyapp.executors.StoryAppExecutors;
import com.example.ichin.storyapp.model.StoryModel;
import com.example.ichin.storyapp.viewmodel.AddStoryViewModel;
import com.example.ichin.storyapp.viewmodel.AddStoryViewModelFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddStoryActivity extends AppCompatActivity {

    private EditText storyTitle;
    private EditText storyDescription;
    private FloatingActionButton fabSaveStory;
    private ImageView coverPhoto;
    private static final int DEFAULT_TASK_ID = -1;
    private static final String INSTANCE_TASK_ID = "instance";
    public static final String EXTRA_TASK_ID = "extraTaskId";
    private Uri file;

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

        coverPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCameraPermissions();
            }
        });


    }

    private void requestCameraPermissions() {
        if (ContextCompat.checkSelfPermission(AddStoryActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            coverPhoto.setEnabled(false);
            ActivityCompat.requestPermissions(AddStoryActivity.this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
        else{
            takePicture();
        }
    }

    private void initViews() {
        storyTitle = findViewById(R.id.et_story_title);
        storyDescription = findViewById(R.id.et_story_description);

        coverPhoto = findViewById(R.id.iv_story_poster);
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
        String posterPath = file.toString();
        Date date = new Date();
        int size = 250;

        final StoryModel storyModel = new StoryModel(title,description,size,posterPath,date);
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
        Uri posterUri = Uri.parse(storyModel.getPosterPath());
        coverPhoto.setImageURI(posterUri);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 0){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                coverPhoto.setEnabled(true);
                takePicture();
            }
        }
    }

    public void takePicture(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Uri.fromFile(getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
        startActivityForResult(intent,100);
    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"StoryApp");
        if(!mediaStorageDir.exists()){
            if(!mediaStorageDir.mkdirs()){
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath()+File.separator+"IMG_"+timeStamp+".jpg");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 100){
            if(resultCode == RESULT_OK){
                coverPhoto.setImageURI(file);
            }
        }
    }
}
