package com.example.wordbridge;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class GameLanguageFragment extends Fragment {

    private Spinner languageSpinner, gameTypeSpinner;
    private Button confirmButton;
    private Button backButton; // Back button added

    private static final String PREF_NAME = "LanguagePreference";
    private static final String SELECTED_LANGUAGE_KEY = "selectedLanguage";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_language, container, false);

        // Initialize UI elements
        languageSpinner = view.findViewById(R.id.languageSpinner);
        gameTypeSpinner = view.findViewById(R.id.gameTypeSpinner);
        confirmButton = view.findViewById(R.id.confirmButton);
        backButton = view.findViewById(R.id.backButton);

        // Set up back button click listener
        backButton.setOnClickListener(v -> navigateToMainMenu());

        // Array of languages
        String[] languages = {"English", "Dutch", "Spanish"};

        // Array of game types
        String[] gameTypes = {"Quiz", "Write"};

        // Create ArrayAdapter for the language spinner
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, languages);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(languageAdapter);

        // Create ArrayAdapter for the game type spinner
        ArrayAdapter<String> gameTypeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, gameTypes);
        gameTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gameTypeSpinner.setAdapter(gameTypeAdapter);

        // Set up confirm button click listener
        confirmButton.setOnClickListener(v -> {
            String selectedLanguage = languageSpinner.getSelectedItem().toString();
            String selectedGameType = gameTypeSpinner.getSelectedItem().toString();

            if (selectedLanguage.isEmpty() || selectedGameType.isEmpty()) {
                String errorMessage = getString(R.string.error_select_language);
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                return;
            }

            // Save selected language in SharedPreferences
            SharedPreferences preferences = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(SELECTED_LANGUAGE_KEY, selectedLanguage);
            editor.apply();

            // Create a Bundle and pass the selected language
            Bundle bundle = new Bundle();
            bundle.putString("selectedLanguage", selectedLanguage);

            // Navigate to the appropriate fragment based on the game type selected
            Fragment fragment;
            if (selectedGameType.equals("Quiz")) {
                fragment = new QuizFragment();
                fragment.setArguments(bundle);  // Pass the language argument for QuizFragment
            } else {
                fragment = new GameFragment();
            }

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void navigateToMainMenu() {
        MainMenuFragment MainMenuFragment = new MainMenuFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, MainMenuFragment);
        transaction.addToBackStack(null); // Enables back navigation
        transaction.commit();
    }
}
