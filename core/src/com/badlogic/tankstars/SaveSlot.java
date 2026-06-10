package com.badlogic.tankstars;

public class SaveSlot {
    public String id;
    public String label;
    public long updatedAt;
    public GameMode mode;
    public int turnNumber;
    public String playerOneTank;
    public String playerTwoTank;
    public float playerOneHealth;
    public float playerTwoHealth;

    public String getDisplayText() {
        return label + " | Turn " + turnNumber + " | " + playerOneTank + " "
                + Math.round(playerOneHealth) + " HP vs " + playerTwoTank + " "
                + Math.round(playerTwoHealth) + " HP";
    }
}
