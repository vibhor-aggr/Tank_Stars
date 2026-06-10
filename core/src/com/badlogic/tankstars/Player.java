package com.badlogic.tankstars;

import java.io.Serializable;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private Tank tank;
    private Shooter shooter;
    private float currentHealth;
    private float fuelRemaining;
    private boolean computerControlled;

    public Player(String name, Tank tank, boolean computerControlled) {
        this.name = name;
        this.tank = tank;
        this.shooter = new Shooter();
        this.currentHealth = tank.getMaxHealth();
        this.fuelRemaining = tank.getMaxFuel();
        this.computerControlled = computerControlled;
    }

    public String getName() {
        return name;
    }

    public Tank getTank() {
        return tank;
    }

    public Shooter getShooter() {
        return shooter;
    }

    public float getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(float currentHealth) {
        this.currentHealth = Math.max(0f, Math.min(tank.getMaxHealth(), currentHealth));
    }

    public float getFuelRemaining() {
        return fuelRemaining;
    }

    public void setFuelRemaining(float fuelRemaining) {
        this.fuelRemaining = Math.max(0f, Math.min(tank.getMaxFuel(), fuelRemaining));
    }

    public void resetFuel() {
        fuelRemaining = tank.getMaxFuel();
    }

    public void applyDamage(float damage) {
        setCurrentHealth(currentHealth - Math.max(0f, damage));
    }

    public void heal(float amount) {
        setCurrentHealth(currentHealth + Math.max(0f, amount));
    }

    public boolean isAlive() {
        return currentHealth > 0f;
    }

    public boolean isComputerControlled() {
        return computerControlled;
    }

    public void setComputerControlled(boolean computerControlled) {
        this.computerControlled = computerControlled;
    }
}
