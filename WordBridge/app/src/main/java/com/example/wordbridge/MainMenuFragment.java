package com.example.wordbridge;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.FragmentTransaction;

public class MainMenuFragment extends Fragment {

    public MainMenuFragment() {
        // Required empty public constructor
    }

    public static MainMenuFragment newInstance() {
        return new MainMenuFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);

        // Find buttons from the layout
        Button playGameButton = rootView.findViewById(R.id.playGameButton);
        Button sentencesButton = rootView.findViewById(R.id.sentencesButton);
        Button exitButton = rootView.findViewById(R.id.exitButton);

        // Set click listener for the Play Game button
        playGameButton.setOnClickListener(v -> navigateToGameLanguageFragment());

        // Set click listener for the Sentences button
        sentencesButton.setOnClickListener(v -> navigateToSentencesFragment());

        // Set click listener for the Exit button
        exitButton.setOnClickListener(v -> exitApplication());

        return rootView;
    }

    private void navigateToGameLanguageFragment() {
        // Navigate to the GameFragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, new GameLanguageFragment());
        transaction.addToBackStack(null); // Allow back navigation
        transaction.commit();
    }

    private void navigateToSentencesFragment() {
        // Navigate to the SentencesFragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, new LanguageSelectionMenuFragment());
        transaction.addToBackStack(null); // Allow back navigation
        transaction.commit();
    }

    private void exitApplication() {
        // Exit the application
        getActivity().finish(); // Close the activity, which will exit the app
    }
}
