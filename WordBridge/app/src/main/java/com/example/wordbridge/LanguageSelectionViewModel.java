package com.example.wordbridge;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LanguageSelectionViewModel extends ViewModel {

    private final MutableLiveData<String> selectedLearningLanguage = new MutableLiveData<>();
    private final MutableLiveData<String> selectedPurpose = new MutableLiveData<>();


    public LiveData<String> getSelectedLearningLanguage() {
        return selectedLearningLanguage;
    }

    public LiveData<String> getSelectedPurpose() {
        return selectedPurpose;
    }

    public void setSelectedLearningLanguage(String language) {
        selectedLearningLanguage.setValue(language);
    }

    public void setSelectedPurpose(String purpose) {
        selectedPurpose.setValue(purpose);
    }
}
