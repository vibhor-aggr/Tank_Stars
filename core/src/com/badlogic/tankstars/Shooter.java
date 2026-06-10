package com.badlogic.tankstars;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

public class Shooter implements Serializable {
    private static final long serialVersionUID = 1L;

    private float angleDegrees = 45f;
    private float power = 60f;
    private WeaponType selectedWeapon = WeaponType.BASIC_SHELL;
    private final EnumMap<WeaponType, Integer> inventory = new EnumMap<WeaponType, Integer>(WeaponType.class);

    public Shooter() {
        inventory.put(WeaponType.BASIC_SHELL, 999);
        inventory.put(WeaponType.BIG_ONE, 0);
        inventory.put(WeaponType.AIR_STRIKE, 0);
        inventory.put(WeaponType.CLUSTER_SHOT, 0);
        inventory.put(WeaponType.REPAIR_KIT, 0);
    }

    public float getAngleDegrees() {
        return angleDegrees;
    }

    public void setAngleDegrees(float angleDegrees) {
        this.angleDegrees = clamp(angleDegrees, 0f, 90f);
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = clamp(power, 15f, 100f);
    }

    public WeaponType getSelectedWeapon() {
        return selectedWeapon;
    }

    public void selectWeapon(WeaponType selectedWeapon) {
        if (getAmmo(selectedWeapon) > 0) {
            this.selectedWeapon = selectedWeapon;
        }
    }

    public int getAmmo(WeaponType type) {
        Integer value = inventory.get(type);
        return value == null ? 0 : value;
    }

    public void addAmmo(WeaponType type, int amount) {
        inventory.put(type, Math.max(0, getAmmo(type) + amount));
    }

    public boolean consumeSelectedWeapon() {
        if (selectedWeapon == WeaponType.BASIC_SHELL) {
            return true;
        }
        int ammo = getAmmo(selectedWeapon);
        if (ammo <= 0) {
            selectedWeapon = WeaponType.BASIC_SHELL;
            return false;
        }
        inventory.put(selectedWeapon, ammo - 1);
        if (ammo - 1 <= 0) {
            selectedWeapon = WeaponType.BASIC_SHELL;
        }
        return true;
    }

    public Map<WeaponType, Integer> getInventory() {
        return inventory;
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
