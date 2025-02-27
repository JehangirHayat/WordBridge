package com.example.wordbridge;

import android.content.Context;
import android.content.SharedPreferences;

public class LanguageManager {
    private static final String PREF_NAME = "AppPreferences";
    private static final String LANGUAGE_KEY = "selected_language";
    private SharedPreferences sharedPreferences;

    public LanguageManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setLanguage(String languageCode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LANGUAGE_KEY, languageCode);
        editor.apply();
    }

    public String getLanguage() {
        return sharedPreferences.getString(LANGUAGE_KEY, "en"); // Idioma predeterminado: inglés
    }
}
