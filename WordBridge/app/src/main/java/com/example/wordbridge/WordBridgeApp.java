package com.example.wordbridge;

import android.app.Application;
import androidx.room.Room;

public class WordBridgeApp extends Application {
    private static AppDatabase appDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the database only once
        appDatabase = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "wordbridge_db")
                .fallbackToDestructiveMigration()
                .build();
    }

    public static AppDatabase getAppDatabase() {
        if (appDatabase == null) {
            throw new IllegalStateException("AppDatabase is not initialized yet");
        }
        return appDatabase;
    }
}
