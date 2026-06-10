package com.badlogic.tankstars;

import java.io.Serializable;

public class Weapon implements Serializable {
    private static final long serialVersionUID = 1L;

    private final WeaponType type;
    private final String displayName;
    private final float baseDamage;
    private final float blastRadius;
    private final float craterRadius;
    private final float projectileSpeed;
    private final float knockback;
    private final boolean destroysTerrain;

    public Weapon(WeaponType type, String displayName, float baseDamage, float blastRadius,
                  float craterRadius, float projectileSpeed, float knockback, boolean destroysTerrain) {
        this.type = type;
        this.displayName = displayName;
        this.baseDamage = baseDamage;
        this.blastRadius = blastRadius;
        this.craterRadius = craterRadius;
        this.projectileSpeed = projectileSpeed;
        this.knockback = knockback;
        this.destroysTerrain = destroysTerrain;
    }

    public WeaponType getType() {
        return type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public float getBaseDamage() {
        return baseDamage;
    }

    public float getBlastRadius() {
        return blastRadius;
    }

    public float getCraterRadius() {
        return craterRadius;
    }

    public float getProjectileSpeed() {
        return projectileSpeed;
    }

    public float getKnockback() {
        return knockback;
    }

    public boolean destroysTerrain() {
        return destroysTerrain;
    }
}
