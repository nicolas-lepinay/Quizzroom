package com.ynov.kiwi.api.entity;

public class Question {
    private int id;
    private String text;
    private String answer;

    public Question() {} // Pour Jackson Databind Postconstruct
    public Question(int id, String text, String answer) {
        this.id = id; this.text = text; this.answer = answer;
    }
    public int getId() { return id; }
    public String getText() { return text; }
    public String getAnswer() { return answer; }
    public void setId(int id) { this.id = id; }
    public void setText(String text) { this.text = text; }
    public void setAnswer(String answer) { this.answer = answer; }
}
