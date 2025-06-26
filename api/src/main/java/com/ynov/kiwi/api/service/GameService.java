package com.ynov.kiwi.api.service;

import com.ynov.kiwi.api.entity.Question;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {
    private final QuestionService questionService;
    private int questionIndex = 0;

    public GameService(QuestionService questionService) {
        this.questionService = questionService;
    }

    public Question getCurrentQuestion() {
        List<Question> questions = questionService.getQuestions();
        if (questions.isEmpty() || questionIndex >= questions.size())
            return null;
        return questions.get(questionIndex);
    }

    public boolean nextQuestion() {
        List<Question> questions = questionService.getQuestions();
        if (questions.isEmpty() || questionIndex + 1 >= questions.size()) return false;
        questionIndex++;
        return true;
    }

    public void resetQuestions() {
        questionIndex = 0;
    }

    public int getCurrentQuestionIndex() {
        return questionIndex;
    }

    public int getTotalQuestions() {
        return questionService.getQuestions().size();
    }
}
