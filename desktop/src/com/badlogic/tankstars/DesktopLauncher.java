package com.badlogic.tankstars;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public final class DesktopLauncher {
    private DesktopLauncher() {
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle(TankStars.TITLE);
        config.setWindowedMode(TankStars.V_WIDTH, TankStars.V_HEIGHT);
        config.useVsync(true);
        config.setForegroundFPS(60);
        config.setResizable(false);
        new Lwjgl3Application(new TankStars(), config);
    }
}
