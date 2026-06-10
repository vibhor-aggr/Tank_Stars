package com.badlogic.tankstars;

import java.io.Serializable;

public class MatchStats implements Serializable {
    private static final long serialVersionUID = 1L;

    public int shotsFired;
    public int directHits;
    public int pickupsCollected;
    public float[] damageDealt = new float[] {0f, 0f};

    public void recordDamage(int playerIndex, float amount) {
        if (playerIndex >= 0 && playerIndex < damageDealt.length) {
            damageDealt[playerIndex] += Math.max(0f, amount);
        }
    }
}
