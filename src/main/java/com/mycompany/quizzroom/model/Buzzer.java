package com.mycompany.quizzroom.model;

public class Buzzer {
    private int id;
    private int reactionTime; // en millisecondes
    private boolean canBuzz = true;

    public Buzzer(int id, int reactionTime) {
        this.id = id;
        this.reactionTime = reactionTime;
    }

    public int getId() { return id; }

    public int getReactionTime() { return reactionTime; }

    public boolean canBuzz() { return canBuzz; }
    public void setCanBuzz(boolean canBuzz) { this.canBuzz = canBuzz; }

    public String toString() {
        return "Buzzer #" + id + " (réaction : " + reactionTime + " ms, peut buzzer : " + canBuzz + ")";
    }
}

