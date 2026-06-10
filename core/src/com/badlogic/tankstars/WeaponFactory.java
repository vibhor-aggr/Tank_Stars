package com.badlogic.tankstars;

public final class WeaponFactory {
    private WeaponFactory() {
    }

    public static Weapon create(WeaponType type) {
        if (type == WeaponType.BIG_ONE) {
            return new Weapon(type, "Big One", 58f, 118f, 82f, 410f, 122f, true);
        }
        if (type == WeaponType.AIR_STRIKE) {
            return new Weapon(type, "Air Strike", 46f, 96f, 66f, 0f, 86f, true);
        }
        if (type == WeaponType.CLUSTER_SHOT) {
            return new Weapon(type, "Cluster Shot", 34f, 76f, 48f, 390f, 68f, true);
        }
        if (type == WeaponType.REPAIR_KIT) {
            return new Weapon(type, "Repair Kit", -35f, 0f, 0f, 0f, 0f, false);
        }
        return new Weapon(WeaponType.BASIC_SHELL, "Basic Shell", 38f, 82f, 54f, 380f, 74f, true);
    }
}
