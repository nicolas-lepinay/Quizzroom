package com.ynov.kiwi.cli.buzzer;

import java.util.*;

public class BuzzerManager {
    private final List<Buzzer> buzzers = new ArrayList<>();
    private int nextId = 1;

    public Buzzer addBuzzer() {
        Buzzer b = new Buzzer(nextId++);
        buzzers.add(b);
        return b;
    }

    public List<Buzzer> getBuzzers() {
        return buzzers;
    }

    public Optional<Buzzer> getBuzzerById(int id) {
        return buzzers.stream().filter(b -> b.getId() == id).findFirst();
    }

    public void clear() {
        buzzers.clear();
        nextId = 1;
    }
}
