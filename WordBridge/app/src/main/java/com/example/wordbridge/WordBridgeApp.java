package com.example.wordbridge;

import android.app.Application;
import androidx.room.Room;

public class WordBridgeApp extends Application {

    private static AppDatabase appDatabase;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the database when the application is created
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(getApplicationContext(),
                            AppDatabase.class, "word_bridge_db")
                    .fallbackToDestructiveMigration() // Optional: handles migrations
                    .build();
        }
    }

    public static AppDatabase getAppDatabase() {
        if (appDatabase == null) {
            throw new IllegalStateException("AppDatabase is not initialized yet");
        }
        return appDatabase;
    }
}
