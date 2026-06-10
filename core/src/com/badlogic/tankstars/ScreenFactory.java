package com.badlogic.tankstars;

import com.badlogic.gdx.Screen;

public class ScreenFactory {
    private final TankStars game;

    public ScreenFactory(TankStars game) {
        this.game = game;
    }

    public Screen loading() {
        return new LoadingScreen(game);
    }

    public Screen mainMenu() {
        return new MainMenuScreen(game);
    }

    public Screen modeSelect() {
        return new ModeSelectScreen(game);
    }

    public Screen tankSelect(GameMode mode, AiDifficulty difficulty) {
        return new TankSelectScreen(game, mode, difficulty);
    }

    public Screen loadGame() {
        return new LoadGameScreen(game);
    }

    public Screen game(GameModel model) {
        return new GameScreen(game, model);
    }
}
