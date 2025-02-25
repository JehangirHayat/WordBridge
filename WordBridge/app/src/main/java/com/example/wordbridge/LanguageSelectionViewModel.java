package com.example.wordbridge;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LanguageSelectionViewModel extends ViewModel {

    private final MutableLiveData<String> selectedMainLanguage = new MutableLiveData<>();
    private final MutableLiveData<String> selectedLearningLanguage = new MutableLiveData<>();
    private final MutableLiveData<String> selectedPurpose = new MutableLiveData<>();

    // Getters for LiveData
    public LiveData<String> getSelectedMainLanguage() {
        return selectedMainLanguage;
    }

    public LiveData<String> getSelectedLearningLanguage() {
        return selectedLearningLanguage;
    }

    public LiveData<String> getSelectedPurpose() {
        return selectedPurpose;
    }

    // Setters for LiveData
    public void setSelectedMainLanguage(String language) {
        selectedMainLanguage.setValue(language);
    }

    public void setSelectedLearningLanguage(String language) {
        selectedLearningLanguage.setValue(language);
    }

    public void setSelectedPurpose(String purpose) {
        selectedPurpose.setValue(purpose);
    }
}
