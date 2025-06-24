package com.ynov.kiwi.buzzer;

import java.util.*;

public class BuzzerManager {
    private final List<Buzzer> buzzers = new ArrayList<>();

    public Buzzer addBuzzer() {
        Buzzer b = new Buzzer(buzzers.size() + 1);
        buzzers.add(b);
        return b;
    }

    public List<Buzzer> getBuzzers() { return buzzers; }

    public Optional<Buzzer> getBuzzerById(int id) {
        return buzzers.stream().filter(b -> b.getId() == id).findFirst();
    }
}
