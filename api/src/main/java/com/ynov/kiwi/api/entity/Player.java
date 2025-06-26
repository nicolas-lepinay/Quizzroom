package com.ynov.kiwi.api.entity;

public class Player {
    private int id;
    private int score = 0;
    private boolean enabled = true;

    public Player(int id) { this.id = id; }
    public int getId() { return id; }
    public int getScore() { return score; }
    public void addPoint() { score++; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
