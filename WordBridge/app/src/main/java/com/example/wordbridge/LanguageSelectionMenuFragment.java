package com.example.wordbridge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

public class LanguageSelectionMenuFragment extends Fragment {

    private Spinner mainLanguageSpinner, learningLanguageSpinner, purposeSpinner;
    private Button confirmButton;
    private LanguageSelectionViewModel viewModel;

    public LanguageSelectionMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_language_selection_menu, container, false);

        // Initialize the Spinners and Button
        mainLanguageSpinner = rootView.findViewById(R.id.mainLanguageSpinner);
        learningLanguageSpinner = rootView.findViewById(R.id.learningLanguageSpinner);
        purposeSpinner = rootView.findViewById(R.id.purposeSpinner);
        confirmButton = rootView.findViewById(R.id.confirmButton);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(LanguageSelectionViewModel.class);

        // Set up the Spinners with data from resources
        setupSpinners();

        // Set an OnClickListener for the Confirm Button
        confirmButton.setOnClickListener(v -> onConfirmButtonClick());

        return rootView;
    }

    private void setupSpinners() {
        // Main Language Spinner
        ArrayAdapter<CharSequence> mainLanguageAdapter = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.main_language_array,
                android.R.layout.simple_spinner_item
        );
        mainLanguageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mainLanguageSpinner.setAdapter(mainLanguageAdapter);

        // Learning Language Spinner
        ArrayAdapter<CharSequence> learningLanguageAdapter = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.learning_language_array,
                android.R.layout.simple_spinner_item
        );
        learningLanguageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        learningLanguageSpinner.setAdapter(learningLanguageAdapter);

        // Purpose Spinner
        ArrayAdapter<CharSequence> purposeAdapter = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.purpose_array,
                android.R.layout.simple_spinner_item
        );
        purposeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        purposeSpinner.setAdapter(purposeAdapter);
    }

    private void onConfirmButtonClick() {
        // Get selected values
        String selectedMainLanguage = mainLanguageSpinner.getSelectedItem().toString();
        String selectedLearningLanguage = learningLanguageSpinner.getSelectedItem().toString();
        String selectedPurpose = purposeSpinner.getSelectedItem().toString();

        // Set data in ViewModel
        viewModel.setSelectedMainLanguage(selectedMainLanguage);
        viewModel.setSelectedLearningLanguage(selectedLearningLanguage);
        viewModel.setSelectedPurpose(selectedPurpose);

        // Show Toast (optional)
        Toast.makeText(getActivity(),
                "Main Language: " + selectedMainLanguage + "\nLearning Language: " + selectedLearningLanguage
                        + "\nPurpose: " + selectedPurpose, Toast.LENGTH_SHORT).show();

        // Navigate to SentenceFragment
        navigateToSentencesFragment();
    }

    private void navigateToSentencesFragment() {
        // Navigate to SentenceFragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, new SentenceFragment());
        transaction.addToBackStack(null); // Allow back navigation
        transaction.commit();
    }

}
