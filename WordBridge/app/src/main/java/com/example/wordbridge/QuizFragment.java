package com.example.wordbridge;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class QuizFragment extends Fragment {

    private TextView questionTitle, questionText, scoreText;
    private LinearLayout option1Layout, option2Layout, option3Layout, option4Layout;
    private TextView option1Text, option2Text, option3Text, option4Text;
    private Button backButton, nextButton;

    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int questionsAnsweredToday = 0;  // Esto se actualizará si es un nuevo día
    private static final int MAX_QUESTIONS_PER_DAY = 1;  // Solo 1 pregunta por día
    private static final int MAX_SCORE = 50;

    private static final String PREF_NAME = "LanguagePreference";
    private static final String LAST_ANSWER_DATE_KEY = "lastAnswerDate";  // Nuevo campo para guardar la fecha de la última respuesta
    private static final String SELECTED_LANGUAGE_KEY = "selectedLanguage";
    private static final String SUPABASE_URL = "https://twvfjmxoylpqsdifboio.supabase.co";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InR3dmZqbXhveWxwcXNkaWZib2lvIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzk4MTE2NjQsImV4cCI6MjA1NTM4NzY2NH0.S8odO5RgX3B2Zt5SdXsJfvv8rqldD8-uNFSPdnNiCas";  // Reemplaza con tu clave real

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        questionTitle = view.findViewById(R.id.questionTitle);
        questionText = view.findViewById(R.id.questionText);
        scoreText = view.findViewById(R.id.scoreText);
        option1Layout = view.findViewById(R.id.option1Layout);
        option2Layout = view.findViewById(R.id.option2Layout);
        option3Layout = view.findViewById(R.id.option3Layout);
        option4Layout = view.findViewById(R.id.option4Layout);
        option1Text = view.findViewById(R.id.option1Text);
        option2Text = view.findViewById(R.id.option2Text);
        option3Text = view.findViewById(R.id.option3Text);
        option4Text = view.findViewById(R.id.option4Text);
        backButton = view.findViewById(R.id.backButton);
        nextButton = view.findViewById(R.id.nextButton);

        // Cargar las preguntas
        loadQuestions();

        // Comprobar si es un nuevo día
        checkIfNewDay();

        // Botón de retroceso
        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, new GameLanguageFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Botón de siguiente pregunta
        nextButton.setOnClickListener(v -> showNextQuestion());

        // Agregar listeners a las opciones
        option1Layout.setOnClickListener(v -> checkAnswer(option1Text.getText().toString(), option1Layout));
        option2Layout.setOnClickListener(v -> checkAnswer(option2Text.getText().toString(), option2Layout));
        option3Layout.setOnClickListener(v -> checkAnswer(option3Text.getText().toString(), option3Layout));
        option4Layout.setOnClickListener(v -> checkAnswer(option4Text.getText().toString(), option4Layout));

        return view;
    }

    private void loadQuestions() {
        // Obtener el lenguaje seleccionado (esto debería obtenerse dinámicamente)
        String selectedLanguage = "English";  // Ejemplo de lenguaje, debería obtenerse dinámicamente
        String tableName = getTableNameByLanguage(selectedLanguage);

        if (tableName.isEmpty()) {
            Toast.makeText(getContext(), "Invalid language selected.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Logging URL para depuración
        String requestUrl = SUPABASE_URL + "/rest/v1/" + tableName + "?select=question,optionA,optionB,optionC,optionD,answer";
        Log.d("QuizFragment", "Request URL: " + requestUrl);

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

        SupabaseApi api = retrofit.create(SupabaseApi.class);
        Call<List<Question>> call = api.getQuestions(API_KEY, "Bearer " + API_KEY, tableName);

        call.enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        questions = response.body();
                        if (!questions.isEmpty()) {
                            // Barajar las preguntas
                            Collections.shuffle(questions);
                            displayQuestion(currentQuestionIndex);
                        } else {
                            Toast.makeText(getContext(), "No questions available.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("QuizFragment", "Response body is null.");
                        Toast.makeText(getContext(), "Response body is null.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Log error response body
                    try {
                        String errorResponse = response.errorBody().string();
                        Log.e("QuizFragment", "Error response: " + errorResponse);
                    } catch (Exception e) {
                        Log.e("QuizFragment", "Error reading error response body", e);
                    }
                    Toast.makeText(getContext(), "Error loading questions.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                Log.e("QuizFragment", "Failure: " + t.getMessage());
                Toast.makeText(getContext(), "Connection error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getTableNameByLanguage(String language) {
        switch (language) {
            case "English":
                return "Select_Quiz_English";
            case "Netherland":
                return "Select_Quiz_Netherland";
            case "Spanish":
                return "Select_Quiz_Spanish";
            default:
                return "";
        }
    }

    private void displayQuestion(int index) {
        if (questions == null || index < 0 || index >= questions.size()) {
            return;
        }

        Question question = questions.get(index);

        // Mostrar número de pregunta y título
        questionTitle.setText("Question " + (index + 1));
        questionText.setText(question.getQuestion());

        // Mostrar las opciones
        option1Text.setText(question.getOptionA());
        option2Text.setText(question.getOptionB());
        option3Text.setText(question.getOptionC());
        option4Text.setText(question.getOptionD());

        scoreText.setText("Score: " + score);
    }

    private void showNextQuestion() {
        if (questionsAnsweredToday < MAX_QUESTIONS_PER_DAY) {
            if (questions != null && currentQuestionIndex < questions.size() - 1) {
                // Reset the borders of the options to default
                resetOptionsBorders();

                // Move to the next question
                currentQuestionIndex++;
                displayQuestion(currentQuestionIndex);
                questionsAnsweredToday++;  // Incrementar el contador de preguntas respondidas
            } else {
                Toast.makeText(getContext(), "No more questions.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "You have already answered a question today.", Toast.LENGTH_SHORT).show();
        }
    }


    private void checkAnswer(String selectedAnswer, LinearLayout optionLayout) {
        if (questions == null || currentQuestionIndex >= questions.size()) return;

        Question question = questions.get(currentQuestionIndex);
        String correctAnswer = question.getanswer();

        // Deshabilitar todas las opciones después de responder
        disableAllOptions();

        // Verificar si la respuesta es correcta
        if (selectedAnswer.equals(correctAnswer)) {
            if (score < MAX_SCORE) {
                score += 10;
                score = Math.min(score, MAX_SCORE);
                optionLayout.setBackgroundResource(R.drawable.correct_answer_border); // Borde verde
            }
        } else {
            optionLayout.setBackgroundResource(R.drawable.incorrect_answer_border); // Borde rojo
        }

        // Actualizar la visualización de la puntuación
        scoreText.setText("Score: " + score);
    }

    private void disableAllOptions() {
        option1Layout.setClickable(false);
        option2Layout.setClickable(false);
        option3Layout.setClickable(false);
        option4Layout.setClickable(false);
    }

    private void resetOptionsBorders() {
        // Restablecer el fondo de todas las opciones al estado predeterminado
        option1Layout.setBackgroundResource(R.drawable.default_option_border); // Estilo de borde predeterminado
        option2Layout.setBackgroundResource(R.drawable.default_option_border);
        option3Layout.setBackgroundResource(R.drawable.default_option_border);
        option4Layout.setBackgroundResource(R.drawable.default_option_border);

        // Volver a habilitar las opciones para que el usuario pueda interactuar con ellas
        option1Layout.setClickable(true);
        option2Layout.setClickable(true);
        option3Layout.setClickable(true);
        option4Layout.setClickable(true);
    }

    private void checkIfNewDay() {
        // Obtener la fecha actual y la última fecha en que se respondió una pregunta
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        long lastAnswerDate = sharedPreferences.getLong(LAST_ANSWER_DATE_KEY, 0);
        long currentDate = System.currentTimeMillis();

        // Si han pasado más de 24 horas, reiniciar el contador de preguntas
        if (lastAnswerDate == 0 || (currentDate - lastAnswerDate) > 86400000) {
            questionsAnsweredToday = 0;  // Reiniciar el contador
        }

        // Guardar la fecha actual como la última fecha de respuesta
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(LAST_ANSWER_DATE_KEY, currentDate);
        editor.apply();
    }

    private interface SupabaseApi {
        @GET("rest/v1/{tableName}?select=question,optionA,optionB,optionC,optionD,answer")
        Call<List<Question>> getQuestions(
                @Header("apikey") String apiKey,
                @Header("Authorization") String authToken,
                @Path("tableName") String tableName  // Nombre de la tabla dinámico
        );
    }
}
