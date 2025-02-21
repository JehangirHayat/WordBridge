package com.example.wordbridge;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserDao {

    // Insert method with conflict strategy to replace existing user
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    // Query to get user by email and password
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    User getUserByEmailAndPassword(String email, String password);
}
