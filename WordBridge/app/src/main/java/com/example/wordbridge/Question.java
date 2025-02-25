package com.example.wordbridge;

public class Question {
    private int id; // Para la columna id (smallint/int2)
    private String question; // Para la columna question
    private String optionA; // Para la columna option_a
    private String optionB; // Para la columna option_b
    private String optionC; // Para la columna option_c
    private String optionD; // Para la columna option_d
    private String answer; // Para la columna correct_answere

    // Getters
    public int getId() { return id; }
    public String getQuestion() { return question; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getOptionD() { return optionD; }
    public String getanswer() { return answer; }

    // Setters (opcional si los necesitas)
    public void setId(int id) { this.id = id; }
    public void setQuestion(String question) { this.question = question; }
    public void setOptionA(String optionA) { this.optionA = optionA; }
    public void setOptionB(String optionB) { this.optionB = optionB; }
    public void setOptionC(String optionC) { this.optionC = optionC; }
    public void setOptionD(String optionD) { this.optionD = optionD; }
    public void setCorrect_answer(String answer) { this.answer = answer; }
}
