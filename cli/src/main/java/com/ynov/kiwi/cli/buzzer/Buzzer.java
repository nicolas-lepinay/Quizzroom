package com.ynov.kiwi.cli.buzzer;

public class Buzzer {
    private final int id;
    private boolean enabled = true;

    public Buzzer(int id) { this.id = id; }

    public int getId() { return id; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
