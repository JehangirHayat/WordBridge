package com.example.wordbridge;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    private LanguageManager languageManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Inicializar LanguageManager
        languageManager = new LanguageManager(requireContext());

        // Configurar botones de cambio de idioma
        ImageButton buttonRussian = view.findViewById(R.id.button_ukraine);
        ImageButton buttonArabic = view.findViewById(R.id.button_arabe);
        ImageButton buttonSpanish = view.findViewById(R.id.button_spanish);
        ImageButton buttonEnglish = view.findViewById(R.id.button_english);

        buttonRussian.setOnClickListener(v -> changeLanguage("uk")); // ukranien
        buttonArabic.setOnClickListener(v -> changeLanguage("ar")); // Árabe
        buttonSpanish.setOnClickListener(v -> changeLanguage("es")); // Español
        buttonEnglish.setOnClickListener(v -> changeLanguage("en")); // Inglés

        // Botón de "Volver a Login"
        Button buttonBackToLogin = view.findViewById(R.id.button_back_to_login);
        buttonBackToLogin.setOnClickListener(v -> navigateToLoginFragment());

        return view;
    }

    private void changeLanguage(String languageCode) {
        languageManager.setLanguage(languageCode);

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = getResources().getConfiguration();
        config.setLocale(locale);
        requireContext().getResources().updateConfiguration(config, requireContext().getResources().getDisplayMetrics());

        Toast.makeText(getActivity(), getString(R.string.language_changed), Toast.LENGTH_SHORT).show();

        requireActivity().recreate(); // Reinicia la actividad para aplicar los cambios
    }

    private void navigateToLoginFragment() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, new LoginFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
