package com.example.wordbridge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.util.Log;

public class RegisterFragment extends DialogFragment {

    private EditText firstNameInput, lastNameInput, emailInput, passwordInput;
    private static final String TAG = "RegisterFragment"; // For logging

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for the floating window
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Initialize the EditTexts
        firstNameInput = view.findViewById(R.id.firstNameInput);
        lastNameInput = view.findViewById(R.id.lastNameInput);
        emailInput = view.findViewById(R.id.emailInput);
        passwordInput = view.findViewById(R.id.passwordInput);

        // Set dialog properties (animation & rounded corners)
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setWindowAnimations(R.style.DialogSlideUpAnimation); // Set the slide-up animation
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded_background); // Apply rounded background
        }

        // Set up the Register button
        Button registerButton = view.findViewById(R.id.RegisterButton);
        registerButton.setOnClickListener(v -> performRegistration());

        // Set up the Close button
        Button closeButton = view.findViewById(R.id.CloseButton);
        closeButton.setOnClickListener(v -> dismiss());

        return view;
    }

    // Method to perform user registration
    private void performRegistration() {
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate inputs
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Log the user input (for debugging)
        Log.d(TAG, "First Name: " + firstName);
        Log.d(TAG, "Last Name: " + lastName);
        Log.d(TAG, "Email: " + email);

        // Create a new User object
        User newUser = new User(firstName, lastName, email, password);

        // Run the database operation in the background
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                // Get the database instance safely
                AppDatabase db = AppDatabase.getInstance(getActivity().getApplicationContext());

                if (db != null) {
                    // Insert user into the database
                    db.userDao().insert(newUser);

                    // Show a success message (on the UI thread)
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getActivity(), "Registration successful", Toast.LENGTH_SHORT).show();
                            dismiss();  // Close the dialog after successful registration
                        });
                    }
                } else {
                    // Log an error if the database is null
                    Log.e(TAG, "Database is not initialized.");
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getActivity(), "Database is not available. Try again.", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            } catch (Exception e) {
                // Log the exception
                Log.e(TAG, "Error during registration", e);

                // Handle database errors (optional)
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}
