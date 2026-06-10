package com.badlogic.tankstars;

import java.io.Serializable;

public abstract class Tank implements Serializable {
    private static final long serialVersionUID = 1L;

    private final TankType type;
    private final String displayName;
    private final float maxHealth;
    private final float maxFuel;
    private final float mass;
    private final float width;
    private final float height;
    private final float moveSpeed;
    private final float damageTakenMultiplier;

    private float x;
    private float y;
    private float rotationDegrees;

    protected Tank(TankType type, String displayName, float maxHealth, float maxFuel, float mass,
                   float width, float height, float moveSpeed, float damageTakenMultiplier) {
        this.type = type;
        this.displayName = displayName;
        this.maxHealth = maxHealth;
        this.maxFuel = maxFuel;
        this.mass = mass;
        this.width = width;
        this.height = height;
        this.moveSpeed = moveSpeed;
        this.damageTakenMultiplier = damageTakenMultiplier;
    }

    public TankType getType() {
        return type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public float getMaxFuel() {
        return maxFuel;
    }

    public float getMass() {
        return mass;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public float getDamageTakenMultiplier() {
        return damageTakenMultiplier;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getRotationDegrees() {
        return rotationDegrees;
    }

    public void setRotationDegrees(float rotationDegrees) {
        this.rotationDegrees = rotationDegrees;
    }

    public float getCollisionRadius() {
        return Math.max(width, height) * 0.55f;
    }
}
