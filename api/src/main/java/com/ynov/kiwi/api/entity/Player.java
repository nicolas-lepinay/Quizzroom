package com.ynov.kiwi.api.entity;

public class Player {
    private int id;
    private int score = 0;
    private String givenName;
    private boolean enabled = true;
    //private boolean hasAttempted = false;
    private boolean inControl = false;

    public Player(int id) { this.id = id; }
    public int getId() { return id; }
    public int getScore() { return score; }
    public void setGivenName(String name) { this.givenName = name; }
    public String getGivenName() { return givenName; }
    public void addPoint() { score++; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    //public boolean hasAttempted() { return hasAttempted; }
    //public void setHasAttempted(boolean hasAttempted) { this.hasAttempted = hasAttempted; }

    public boolean isInControl() { return inControl; }
    public void setInControl(boolean inControl) { this.inControl = inControl; }
}
