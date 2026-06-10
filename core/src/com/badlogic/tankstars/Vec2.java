package com.badlogic.tankstars;

import java.io.Serializable;

public class Vec2 implements Serializable {
    private static final long serialVersionUID = 1L;

    public float x;
    public float y;

    public Vec2() {
        this(0f, 0f);
    }

    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vec2 copy() {
        return new Vec2(x, y);
    }

    public float distanceTo(float otherX, float otherY) {
        float dx = x - otherX;
        float dy = y - otherY;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
