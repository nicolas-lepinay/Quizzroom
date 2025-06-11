package com.mycompany.quizzroom.game;

import com.mycompany.quizzroom.model.Buzzer;
import java.util.*;

public class Game {
    private List<Buzzer> buzzers = new ArrayList<>();
    private Map<Integer, Integer> scores = new HashMap<>(); // id du buzzer -> score

    public void addBuzzer(Buzzer buzzer) {
        buzzers.add(buzzer);
        scores.put(buzzer.getId(), 0);
    }

    public List<Buzzer> getBuzzers() { return buzzers; }

    /*
    public void listBuzzers() {
        buzzers.forEach(System.out::println);
    }
        */
}
