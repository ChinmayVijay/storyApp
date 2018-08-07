package com.example.ichin.storyapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

import com.example.ichin.storyapp.model.StoryModel;

@Database(entities = {StoryModel.class}, version=1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class StoryDatabase extends RoomDatabase {

    private static final String TAG = StoryDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static StoryDatabase sInstance;
    private static final String DATABSE_NAME = "storyDatabase";

    public static StoryDatabase getInstance(Context context){
        if(sInstance == null){
            synchronized (LOCK){
                Log.d(TAG,"Creating database storyDatabase" );
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        StoryDatabase.class,DATABSE_NAME)
                        .build();
            }
        }
        Log.d(TAG, "Database created ");
        return sInstance;
    }

    public abstract StoryDao storyDao();

}
