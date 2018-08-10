package com.example.ichin.storyapp;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
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
    private EditText plot;
    private FloatingActionButton fabSaveStory;
    private ImageView coverPhoto;
    private ImageView charPhotoOne;
    private ImageView charPhotoTwo;
    private ImageView charPhotoThree;
    private static final int DEFAULT_TASK_ID = -1;
    private static final String INSTANCE_TASK_ID = "instance";
    public static final String EXTRA_TASK_ID = "extraTaskId";
    private Uri file;
    private Uri cOne;
    private Uri cTwo;
    private Uri cThree;


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
                requestCameraPermissions(0);
            }
        });
        charPhotoOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCameraPermissions(1);
            }
        });
        charPhotoTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCameraPermissions(2);
            }
        });

        charPhotoThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCameraPermissions(3);
            }
        });


    }

    private void requestCameraPermissions(int pic) {
        if (ContextCompat.checkSelfPermission(AddStoryActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            coverPhoto.setEnabled(false);
            ActivityCompat.requestPermissions(AddStoryActivity.this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, pic);
        }
        else{

            openAddPicDailog(pic);
//            takePicture(pic);
        }
    }

    private void initViews() {
        storyTitle = findViewById(R.id.et_story_title);
        storyDescription = findViewById(R.id.et_story_description);

        plot = findViewById(R.id.et_plot);
        coverPhoto = findViewById(R.id.iv_story_poster);
        charPhotoOne = findViewById(R.id.iv_character_one);
        charPhotoTwo = findViewById(R.id.iv_character_two);
        charPhotoThree = findViewById(R.id.iv_character_three);

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
        String posterPath;

        Date date = new Date();
        int size = 250;

        posterPath = file.toString();

        String plotText = plot.getText().toString();
        String chOne = cOne.toString();
        String chTwo = cTwo.toString();
        String chThree = cThree.toString();



        final StoryModel storyModel = new StoryModel(title,description,size,posterPath,date,plotText,chOne,chTwo,chThree);
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
        plot.setText(storyModel.getPlot());
        file = Uri.parse(storyModel.getPosterPath());
        cOne = Uri.parse(storyModel.getCharacterOnePath());
        cTwo = Uri.parse(storyModel.getCharacterTwoPath());
        cThree = Uri.parse(storyModel.getCharacterThreePath());

        coverPhoto.setImageURI(file);
        charPhotoOne.setImageURI(cOne);
        charPhotoTwo.setImageURI(cTwo);
        charPhotoThree.setImageURI(cThree);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 0 || requestCode==1 || requestCode==2 || requestCode == 3){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                coverPhoto.setEnabled(true);
                takePicture(requestCode);
            }
        }
    }

    public void takePicture(int pic){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        switch(pic){
            case 0:
                file = Uri.fromFile(getOutputMediaFile());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
                startActivityForResult(intent,100);
                break;
            case 1:
                cOne = Uri.fromFile(getOutputMediaFile());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cOne);
                startActivityForResult(intent,101);
                break;
            case 2:
                cTwo = Uri.fromFile(getOutputMediaFile());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cTwo);
                startActivityForResult(intent,102);
                break;
            case 3:
                cThree = Uri.fromFile(getOutputMediaFile());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cThree);
                startActivityForResult(intent,103);
                break;
        }

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
        if(requestCode == 100 && resultCode == RESULT_OK){ coverPhoto.setImageURI(file); }
        if(requestCode == 101 && resultCode == RESULT_OK){ charPhotoOne.setImageURI(cOne); }
        if(requestCode == 102 && resultCode == RESULT_OK){ charPhotoTwo.setImageURI(cTwo); }
        if(requestCode == 103 && resultCode == RESULT_OK){ charPhotoThree.setImageURI(cThree);}

        if(requestCode == 200 && resultCode == RESULT_OK){ file=data.getData(); coverPhoto.setImageURI(data.getData()); }
        if(requestCode == 201 && resultCode == RESULT_OK){ cOne =data.getData(); charPhotoOne.setImageURI(data.getData()); }
        if(requestCode == 202 && resultCode == RESULT_OK){ cTwo =data.getData();charPhotoTwo.setImageURI(data.getData()); }
        if(requestCode == 203 && resultCode == RESULT_OK){ cThree =data.getData();charPhotoThree.setImageURI(data.getData());}


    }

    private void openAddPicDailog(final int pic){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddStoryActivity.this);
        dialogBuilder.setTitle("Add an Image using");
        dialogBuilder.setItems(getResources().getStringArray(R.array.add_pic_options), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case 0:
                        takePicture(pic);
                        break;
                    case 1:
                        openGallery(pic);
                        break;
                }

            }
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

    }

    private void openGallery(int pic) {
        Intent intent = new Intent();

        switch(pic){
            case 0:
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(Intent.createChooser(intent,"Choose an image from gallery"),200);
                break;
            case 1:
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(Intent.createChooser(intent,"Choose an image from gallery"),201);
                break;
            case 2:
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(Intent.createChooser(intent,"Choose an image from gallery"),202);
                break;
            case 3:
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(Intent.createChooser(intent,"Choose an image from gallery"),203);
                break;
        }



    }
}
