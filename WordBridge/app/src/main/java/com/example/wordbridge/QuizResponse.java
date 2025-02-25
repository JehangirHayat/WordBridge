package com.example.wordbridge;

import com.google.gson.annotations.SerializedName;

public class QuizResponse {
    @SerializedName("question_we")
    private String question_we;

    @SerializedName("answer_we")
    private String answer_we;

    @SerializedName("image_we")
    private String image_we;

    // Getters and Setters
    public String getQuestion_we() {
        return question_we;
    }

    public void setQuestion_we(String question_we) {
        this.question_we = question_we;
    }

    public String getAnswer_we() {
        return answer_we;
    }

    public void setAnswer_we(String answer_we) {
        this.answer_we = answer_we;
    }

    public String getImage_we() {
        return image_we;
    }

    public void setImage_we(String image_we) {
        this.image_we = image_we;
    }
}
