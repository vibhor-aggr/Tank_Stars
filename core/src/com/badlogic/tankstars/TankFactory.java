package com.badlogic.tankstars;

public final class TankFactory {
    private TankFactory() {
    }

    public static Tank create(TankType type) {
        if (type == TankType.TITAN) {
            return new TitanTank();
        }
        if (type == TankType.SCOUT) {
            return new ScoutTank();
        }
        return new VanguardTank();
    }
}
