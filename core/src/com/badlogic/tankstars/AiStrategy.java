package com.badlogic.tankstars;

public interface AiStrategy {
    AiAction chooseAction(GameModel model);
}
