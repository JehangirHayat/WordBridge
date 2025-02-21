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

public class LoginFragment extends Fragment {

    private EditText emailInput, passwordInput;

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
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        // Check the user credentials in the database
        AppDatabase database = WordBridgeApp.getAppDatabase();
        User user = database.userDao().getUserByEmailAndPassword(email, password);

        if (user != null) {
            // Successful login
            Toast.makeText(getContext(), "Login Successful!", Toast.LENGTH_SHORT).show();
            // Navigate to the next fragment or activity
        } else {
            // Failed login
            Toast.makeText(getContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void openRegisterDialog() {
        // Create and show the RegisterDialogFragment
        RegisterFragment registerDialogFragment = new RegisterFragment();
        registerDialogFragment.show(getChildFragmentManager(), "RegisterDialog");
    }
}
