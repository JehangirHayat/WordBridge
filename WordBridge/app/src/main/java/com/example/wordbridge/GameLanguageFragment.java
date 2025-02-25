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

import androidx.fragment.app.Fragment;

public class GameLanguageFragment extends Fragment {

    private Spinner languageSpinner;
    private Button confirmButton;

    private static final String PREF_NAME = "LanguagePreference";
    private static final String SELECTED_LANGUAGE_KEY = "selectedLanguage";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_language, container, false);

        // Initialize UI elements
        languageSpinner = view.findViewById(R.id.languageSpinner);
        confirmButton = view.findViewById(R.id.confirmButton);

        // Array of languages
        String[] languages = {"English", "Netherland", "Spanish"};

        // Create an ArrayAdapter to bind the array of languages to the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Set up confirm button click listener
        confirmButton.setOnClickListener(v -> {
            String selectedLanguage = languageSpinner.getSelectedItem().toString();
            if (selectedLanguage.isEmpty()) {
                Toast.makeText(getContext(), "Please select a language.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save selected language in SharedPreferences
            SharedPreferences preferences = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(SELECTED_LANGUAGE_KEY, selectedLanguage);
            editor.apply();

            // Proceed to the next fragment (GameFragment)
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, new GameFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
