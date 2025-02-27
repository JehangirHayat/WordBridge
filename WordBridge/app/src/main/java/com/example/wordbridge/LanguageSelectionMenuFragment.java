package com.example.wordbridge;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class LanguageSelectionMenuFragment extends Fragment {

    private Spinner purposeSpinner;
    private Button confirmButton;
    private Button backButton;

    private static final String PREF_NAME = "LanguagePreference";
    private static final String SELECTED_PURPOSE_KEY = "selectedPurpose";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_language_selection_menu, container, false);

        // Initialize UI components
        purposeSpinner = rootView.findViewById(R.id.purposeSpinner);
        confirmButton = rootView.findViewById(R.id.confirmButton);
        backButton = rootView.findViewById(R.id.BackButton);

        // Set up back button click listener
        backButton.setOnClickListener(v -> navigateToMainMenu());

        // List of purposes directly in the code
        String[] purposes = {
                "Normal Conversation Dutch",
                "Normal Conversation English",
                "Normal Conversation Spanish",
                "School Phrase Dutch",
                "School Phrase English",
                "School Phrase Spanish",
                "Situational Phrases English",
                "Situational Phrases Dutch",
                "Situational Phrases Spanish"
        };

        // Create ArrayAdapter for purposeSpinner with the list of purposes
        ArrayAdapter<String> purposeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, purposes);
        purposeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        purposeSpinner.setAdapter(purposeAdapter);

        // Set up confirm button click listener
        confirmButton.setOnClickListener(v -> {
            String selectedPurpose = purposeSpinner.getSelectedItem().toString();

            if (selectedPurpose.isEmpty()) {
                String errorMessage = getString(R.string.error_select_purpose);
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                return;
            }

            // Save selected purpose in SharedPreferences
            SharedPreferences preferences = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(SELECTED_PURPOSE_KEY, selectedPurpose);
            editor.apply();

            // Create a Bundle and pass the selected purpose
            Bundle bundle = new Bundle();
            bundle.putString("selectedPurpose", selectedPurpose);

            // Navigate to the appropriate fragment based on the selected purpose
            Fragment fragment = new SentenceFragment();
            fragment.setArguments(bundle);  // Pass the purpose argument for SentenceFragment

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        return rootView;
    }

    private void navigateToMainMenu() {
        MainMenuFragment MainMenuFragment = new MainMenuFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, MainMenuFragment);
        transaction.addToBackStack(null); // Enables back navigation
        transaction.commit();
    }
}
