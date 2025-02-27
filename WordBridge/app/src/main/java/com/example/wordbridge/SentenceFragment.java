package com.example.wordbridge;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public class SentenceFragment extends Fragment {

    private TextView learningText1, learningText2, learningText3;
    private ImageView audioButton1, audioButton2, audioButton3;
    private LinearLayout sentencesContainer;
    private Button nextButton;
    private List<Sentence> sentenceList = new ArrayList<>();
    private int currentSentenceIndex = 0;
    private MediaPlayer mediaPlayer;

    private static final String PREF_NAME = "LanguagePreference";
    private static final String SELECTED_PURPOSE_KEY = "selectedPurpose";
    private static final String SUPABASE_URL = "https://twvfjmxoylpqsdifboio.supabase.co";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InR3dmZqbXhveWxwcXNkaWZib2lvIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzk4MTE2NjQsImV4cCI6MjA1NTM4NzY2NH0.S8odO5RgX3B2Zt5SdXsJfvv8rqldD8-uNFSPdnNiCas"; // Update with your actual API key

    private SupabaseApi supabaseApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sentence, container, false);

        // Initialize views
        learningText1 = view.findViewById(R.id.learningLanguageText1);
        learningText2 = view.findViewById(R.id.learningLanguageText2);
        learningText3 = view.findViewById(R.id.learningLanguageText3);

        audioButton1 = view.findViewById(R.id.learningAudioButton1);
        audioButton2 = view.findViewById(R.id.learningAudioButton2);
        audioButton3 = view.findViewById(R.id.learningAudioButton3);
        nextButton = view.findViewById(R.id.nextButton); // Next button to load more sentences

        setupRetrofit();

        String selectedPurpose = getSelectedPurpose();
        if (selectedPurpose != null) {
            loadSentences(selectedPurpose);
        } else {
            Toast.makeText(getContext(), getString(R.string.purpose_not_selected), Toast.LENGTH_SHORT).show();
        }

        // Set up Next button to load more sentences
        nextButton.setOnClickListener(v -> loadMoreSentences());

        return view;
    }

    private void setupRetrofit() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SUPABASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        supabaseApi = retrofit.create(SupabaseApi.class);
    }

    private String getSelectedPurpose() {
        SharedPreferences preferences = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(SELECTED_PURPOSE_KEY, null);
    }

    private void loadSentences(String selectedPurpose) {
        String tableName = getTableNameForPurpose(selectedPurpose);
        if (tableName.isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.invalid_purpose), Toast.LENGTH_SHORT).show();
            return;
        }

        Call<List<Sentence>> call = supabaseApi.getSentences(
                "Bearer " + API_KEY,
                API_KEY,
                tableName
        );

        call.enqueue(new Callback<List<Sentence>>() {
            @Override
            public void onResponse(Call<List<Sentence>> call, Response<List<Sentence>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sentenceList = response.body();
                    if (!sentenceList.isEmpty()) {
                        displaySentences();
                    } else {
                        Toast.makeText(getContext(), getString(R.string.no_sentences_available), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.failed_to_load_sentences), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Sentence>> call, Throwable t) {
                Toast.makeText(getContext(), getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getTableNameForPurpose(String purpose) {
        switch (purpose) {
            case "Normal Conversation Dutch":
                return "Normal_Conversation_Netherland";
            case "Normal Conversation English":
                return "Normal_Conversation_English";
            case "Normal Conversation Spanish":
                return "Normal_Conversation_Spanish";
            case "School Phrase Dutch":
                return "School_Phrase_Netherland";
            case "School Phrase English":
                return "School_Phrase_English";
            case "School Phrase Spanish":
                return "School_Phrase_Spanish";
            case "Situational Phrases English":
                return "Situational_Phrases_English";
            case "Situational Phrases Dutch":
                return "Situational_Phrases_Netherland";
            case "Situational Phrases Spanish":
                return "Situational_Phrases_Spanish";
            default:
                return "";
        }
    }

    private void displaySentences() {
        // Display sentences and set visibility for audio buttons
        if (sentenceList.size() > currentSentenceIndex) {
            learningText1.setText(sentenceList.get(currentSentenceIndex).getCnPhrace());
            audioButton1.setVisibility(View.VISIBLE);  // Make audio button visible
        } else {
            audioButton1.setVisibility(View.GONE);  // Hide audio button if no sentence
        }

        if (sentenceList.size() > currentSentenceIndex + 1) {
            learningText2.setText(sentenceList.get(currentSentenceIndex + 1).getCnPhrace());
            audioButton2.setVisibility(View.VISIBLE);  // Make audio button visible
        } else {
            audioButton2.setVisibility(View.GONE);  // Hide audio button if no sentence
        }

        if (sentenceList.size() > currentSentenceIndex + 2) {
            learningText3.setText(sentenceList.get(currentSentenceIndex + 2).getCnPhrace());
            audioButton3.setVisibility(View.VISIBLE);  // Make audio button visible
        } else {
            audioButton3.setVisibility(View.GONE);  // Hide audio button if no sentence
        }

        setupAudioButtons();  // Set up audio buttons after sentences are displayed
    }

    private void setupAudioButtons() {
        // Set up audio buttons only if the sentences exist
        if (sentenceList.size() > currentSentenceIndex) {
            audioButton1.setOnClickListener(v -> playAudio(sentenceList.get(currentSentenceIndex).getCnAudio()));
        }
        if (sentenceList.size() > currentSentenceIndex + 1) {
            audioButton2.setOnClickListener(v -> playAudio(sentenceList.get(currentSentenceIndex + 1).getCnAudio()));
        }
        if (sentenceList.size() > currentSentenceIndex + 2) {
            audioButton3.setOnClickListener(v -> playAudio(sentenceList.get(currentSentenceIndex + 2).getCnAudio()));
        }
    }

    private void playAudio(String audioUrl) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), getString(R.string.audio_play_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMoreSentences() {
        if (currentSentenceIndex + 3 < sentenceList.size()) {
            currentSentenceIndex += 3;
            displaySentences();
        } else {
            Toast.makeText(getContext(), getString(R.string.no_more_sentences), Toast.LENGTH_SHORT).show();
        }
    }

    private interface SupabaseApi {
        @GET("rest/v1/{tableName}?select=cn_phrase,cn_audio")
        Call<List<Sentence>> getSentences(
                @Header("Authorization") String authToken,
                @Header("apikey") String apiKey,
                @Path("tableName") String tableName
        );
    }
}
