package com.badlogic.tankstars;

import java.io.Serializable;

public class Projectile implements Serializable {
    private static final long serialVersionUID = 1L;

    private Vec2 position;
    private Vec2 velocity;
    private WeaponType weaponType;
    private int ownerIndex;
    private boolean active;
    private boolean airStrike;

    public Projectile(Vec2 position, Vec2 velocity, WeaponType weaponType, int ownerIndex, boolean airStrike) {
        this.position = position;
        this.velocity = velocity;
        this.weaponType = weaponType;
        this.ownerIndex = ownerIndex;
        this.active = true;
        this.airStrike = airStrike;
    }

    public Vec2 getPosition() {
        return position;
    }

    public Vec2 getVelocity() {
        return velocity;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }

    public int getOwnerIndex() {
        return ownerIndex;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    public boolean isAirStrike() {
        return airStrike;
    }

    public void update(float delta, float gravity, float wind) {
        if (!active) {
            return;
        }
        velocity.x += wind * delta;
        velocity.y -= gravity * delta;
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;
    }
}
