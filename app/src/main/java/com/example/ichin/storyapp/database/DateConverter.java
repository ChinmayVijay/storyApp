package com.example.ichin.storyapp.database;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class DateConverter {

    /**
     * Room uses this method to read the date stored as timestamp from db and convert back to Date format.
     * @param timeStamp
     * @return
     */
    @TypeConverter
    public static Date toDate(Long timeStamp){ return timeStamp==null?null:new Date(timeStamp); }

    /**
     * Room uses this method to convert the Date into the timestamp format used to store in the database.
     * @param date
     * @return
     */
    @TypeConverter
    public static Long toTimeStamp(Date date){ return date==null?null:date.getTime(); }
}
