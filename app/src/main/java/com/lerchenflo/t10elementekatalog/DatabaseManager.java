package com.lerchenflo.t10elementekatalog;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.*;
import androidx.room.RoomDatabase;

import java.util.ArrayList;
import java.util.List;

// TypeConverter to store List<Boolean> as a String
class Converters {
    @TypeConverter
    public static String fromList(List<Boolean> list) {
        StringBuilder sb = new StringBuilder();
        for (Boolean b : list) {
            sb.append(b ? "1" : "0"); // Convert true/false to "1"/"0"
        }
        return sb.toString();
    }

    @TypeConverter
    public static List<Boolean> toList(String data) {
        List<Boolean> list = new ArrayList<>();
        for (char c : data.toCharArray()) {
            list.add(c == '1');
        }
        return list;
    }
}

// Entity class with fixed Primary Key
@Entity
class PunkteCounterEntry {
    @PrimaryKey
    @NonNull
    public String Name = ""; // Primary Key, must be unique

    @TypeConverters(Converters.class)
    public List<Boolean> pressedbuttons = new ArrayList<>();
}

@Dao
interface PunkteCounterManager {
    @Insert
    void add(PunkteCounterEntry entry);

    @Query("SELECT * FROM PunkteCounterEntry ORDER BY Name DESC LIMIT 1")
    PunkteCounterEntry getLast();

    @Query("SELECT * FROM PunkteCounterEntry WHERE Name = :searchName LIMIT 1")
    PunkteCounterEntry findByName(String searchName);

    @Query("SELECT Name FROM PunkteCounterEntry")
    List<String> getAllNames(); // This is the function to get all saved names
}


// Database class
@Database(entities = {PunkteCounterEntry.class}, version = 1)
@TypeConverters(Converters.class) // Register TypeConverters
abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract PunkteCounterManager myObjectDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "punktecountersave.db")
                    .allowMainThreadQueries() // For simplicity (Not recommended for large apps)
                    .build();
        }
        return instance;
    }
}
