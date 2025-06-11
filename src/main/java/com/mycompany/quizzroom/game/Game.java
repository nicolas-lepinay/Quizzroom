package com.mycompany.quizzroom.game;

import com.mycompany.quizzroom.model.Buzzer;
import com.mycompany.quizzroom.model.Question;

import java.util.*;

public class Game {
    private List<Buzzer> buzzers = new ArrayList<>();
    private Map<Integer, Integer> scores = new HashMap<>(); // id du buzzer -> score
    private List<Question> questions = new ArrayList<>();

    public void addBuzzer(Buzzer buzzer) {
        buzzers.add(buzzer);
        scores.put(buzzer.getId(), 0);
    }

    public void loadQuestions(List<Question> questions) { this.questions = questions; }
    public List<Buzzer> getBuzzers() { return buzzers; }
    public Map<Integer, Integer> getScores() { return scores; }
    public List<Question> getQuestions() { return questions; }

    public Buzzer getBuzzerById(int id) {
        for (Buzzer buzzer : buzzers) {
            if (buzzer.getId() == id) {
                return buzzer;
            }
        }
        return null;
    }

    public void incrementScore(int buzzerId) {
        scores.put(buzzerId, scores.getOrDefault(buzzerId, 0) + 1);
    }

    public void resetBuzzers() {
        for (Buzzer buzzer : buzzers) {
            buzzer.setCanBuzz(true);
        }
    }

}
