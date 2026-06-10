package com.badlogic.tankstars;

import java.io.Serializable;

public class Drop implements Serializable {
    private static final long serialVersionUID = 1L;

    public float x;
    public float y;
    public WeaponType reward;
    public boolean collected;

    public Drop() {
    }

    public Drop(float x, float y, WeaponType reward) {
        this.x = x;
        this.y = y;
        this.reward = reward;
    }
}
