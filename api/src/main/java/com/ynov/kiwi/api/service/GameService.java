package com.ynov.kiwi.api.service;

import org.springframework.stereotype.Service;

@Service
public class GameService {
    private int currentQuestionId = 0;
    public void setCurrentQuestion(int id) { this.currentQuestionId = id; }
    public int getCurrentQuestion() { return currentQuestionId; }
}
