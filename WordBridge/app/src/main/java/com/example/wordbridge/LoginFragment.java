package com.example.wordbridge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.util.Log;

public class LoginFragment extends Fragment {

    private EditText emailInput, passwordInput;
    private static final String TAG = "LoginFragment"; // Logging tag

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        emailInput = rootView.findViewById(R.id.email);
        passwordInput = rootView.findViewById(R.id.password);
        Button loginButton = rootView.findViewById(R.id.loginButton);
        TextView createAccountTextView = rootView.findViewById(R.id.createAccountTextView);

        // Handle login button click
        loginButton.setOnClickListener(v -> loginUser());

        // Handle create account text view click to open the registration dialog
        createAccountTextView.setOnClickListener(v -> openRegisterDialog());

        return rootView;
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Run database query in a background thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                // Get database instance safely
                AppDatabase database = AppDatabase.getInstance(getActivity().getApplicationContext());

                if (database != null) {
                    // Fetch user from database
                    User user = database.userDao().getUserByEmailAndPassword(email, password);

                    if (user != null) {
                        Log.d(TAG, "Login Successful for: " + email);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getActivity(), "Login Successful!", Toast.LENGTH_SHORT).show();
                                navigateToMainMenu();
                            });
                        }
                    } else {
                        Log.d(TAG, "Invalid credentials for: " + email);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getActivity(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                } else {
                    Log.e(TAG, "Database instance is null");
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getActivity(), "Database is not initialized", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error during login", e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void navigateToMainMenu() {
        MainMenuFragment mainMenuFragment = new MainMenuFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, mainMenuFragment); // Ensure this ID matches activity_main.xml
        transaction.addToBackStack(null); // Allows back navigation
        transaction.commit();
    }


    private void openRegisterDialog() {
        RegisterFragment registerDialogFragment = new RegisterFragment();
        registerDialogFragment.show(getChildFragmentManager(), "RegisterDialog");
    }
}
