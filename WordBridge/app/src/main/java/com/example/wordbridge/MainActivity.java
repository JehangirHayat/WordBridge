package com.example.wordbridge;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout file for the activity
        setContentView(R.layout.activity_main);  // Make sure activity_main.xml has the correct container

        // Add LoginFragment if it's the first time the activity is created
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_fragment_container, new LoginFragment());  // Correct container ID
            transaction.commit();
        }
    }
}
