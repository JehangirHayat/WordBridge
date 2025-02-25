package com.example.wordbridge;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
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

public class GameFragment extends Fragment {

    private TextView questionText, scoreText, feedbackText;
    private EditText answerInput;
    private Button backButton, exitButton, nextButton;
    private ImageView topImage;
    private QuizResponse currentQuestion;
    private int score = 0;
    private int questionsAnsweredToday = 0;
    private static final int MAX_QUESTIONS_PER_DAY = 1; // Limitar a una pregunta por día
    private static final int MAX_SCORE = 50;

    private List<QuizResponse> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;

    private static final String PREF_NAME = "LanguagePreference";
    private static final String SELECTED_LANGUAGE_KEY = "selectedLanguage";
    private static final String SUPABASE_URL = "https://twvfjmxoylpqsdifboio.supabase.co";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InR3dmZqbXhveWxwcXNkaWZib2lvIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzk4MTE2NjQsImV4cCI6MjA1NTM4NzY2NH0.S8odO5RgX3B2Zt5SdXsJfvv8rqldD8-uNFSPdnNiCas";

    private SupabaseApi supabaseApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        questionText = view.findViewById(R.id.questionText);
        scoreText = view.findViewById(R.id.scoreText);
        feedbackText = view.findViewById(R.id.feedbackText);
        answerInput = view.findViewById(R.id.answerInput);
        backButton = view.findViewById(R.id.backButton);
        exitButton = view.findViewById(R.id.exitButton);
        nextButton = view.findViewById(R.id.nextButton); // Next button
        topImage = view.findViewById(R.id.topImage);

        setupRetrofit();

        String selectedLanguage = getSelectedLanguage();
        if (selectedLanguage != null) {
            loadQuestions(selectedLanguage);
        } else {
            Toast.makeText(getContext(), "Language not selected.", Toast.LENGTH_SHORT).show();
        }

        // Back button
        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, new GameLanguageFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Exit button with confirmation dialog
        exitButton.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(getContext())
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        getActivity().finish(); // Close the app
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // Next button
        nextButton.setOnClickListener(v -> {
            // Verify the answer before moving to the next question
            String userAnswer = answerInput.getText().toString().trim();
            checkAnswer(userAnswer);

            // Move to the next question (if there's more for the day)
            if (questionsAnsweredToday < MAX_QUESTIONS_PER_DAY) {
                loadNextQuestion();
            } else {
                Toast.makeText(getContext(), "You can only answer one question per day.", Toast.LENGTH_SHORT).show();
            }
        });

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

    private String getSelectedLanguage() {
        SharedPreferences preferences = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(SELECTED_LANGUAGE_KEY, null);
    }

    private void loadQuestions(String selectedLanguage) {
        String tableName = "";

        switch (selectedLanguage) {
            case "English":
                tableName = "Write_Quiz_English";
                break;
            case "Netherland":
                tableName = "Write_Quiz_Netherland";
                break;
            case "Spanish":
                tableName = "Write_Quiz_Spanish";
                break;
            default:
                Toast.makeText(getContext(), "Invalid language selected.", Toast.LENGTH_SHORT).show();
                return;
        }

        Call<List<QuizResponse>> call = supabaseApi.getQuestions(
                "Bearer " + API_KEY,
                API_KEY,
                tableName
        );

        call.enqueue(new Callback<List<QuizResponse>>() {
            @Override
            public void onResponse(Call<List<QuizResponse>> call, Response<List<QuizResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<QuizResponse> questions = response.body();
                    if (questions != null && !questions.isEmpty()) {
                        Collections.shuffle(questions);

                        questionList = questions;
                        currentQuestionIndex = 0;

                        // Display first question
                        displayQuestion(questionList.get(currentQuestionIndex));
                    } else {
                        Toast.makeText(getContext(), "No questions available.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load questions.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<QuizResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Connection error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayQuestion(QuizResponse question) {
        if (question == null) return;

        currentQuestion = question;
        questionText.setText(question.getQuestion_we());

        if (question.getImage_we() != null && !question.getImage_we().isEmpty()) {
            topImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(question.getImage_we()).into(topImage);
        } else {
            topImage.setVisibility(View.GONE);
        }

        scoreText.setText("Score: " + score);
    }

    private void checkAnswer(String userAnswer) {
        if (currentQuestion == null) return;

        String correctAnswer = currentQuestion.getAnswer_we();

        if (userAnswer.equalsIgnoreCase(correctAnswer)) {
            if (score < MAX_SCORE) {
                score += 10;
                score = Math.min(score, MAX_SCORE);
                feedbackText.setText("Correct! +10 points.");
            }
        } else {
            feedbackText.setText("Incorrect. Try again.");
        }

        scoreText.setText("Score: " + score);
        questionsAnsweredToday++;
    }

    private void loadNextQuestion() {
        if (currentQuestionIndex + 1 < questionList.size()) {
            currentQuestionIndex++;
            displayQuestion(questionList.get(currentQuestionIndex));
        } else {
            Toast.makeText(getContext(), "You have answered all available questions.", Toast.LENGTH_SHORT).show();
        }
    }

    private interface SupabaseApi {
        @GET("rest/v1/{tableName}?select=question_we,answer_we,image_we")
        Call<List<QuizResponse>> getQuestions(
                @Header("Authorization") String authToken,
                @Header("apikey") String apiKey,
                @Path("tableName") String tableName
        );
    }
}
