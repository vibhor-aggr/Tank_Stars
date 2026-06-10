package com.badlogic.tankstars;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ModeSelectScreen extends BaseScreen {
    public ModeSelectScreen(TankStars game) {
        super(game);
    }

    @Override
    protected void buildStage() {
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Label title = label("Select Mode");
        table.add(title).width(360f).height(54f).row();
        addModeButton(table, "Player vs Player", GameMode.PLAYER_VS_PLAYER, AiDifficulty.NORMAL);
        addModeButton(table, "Player vs Computer - Easy", GameMode.PLAYER_VS_COMPUTER, AiDifficulty.EASY);
        addModeButton(table, "Player vs Computer - Normal", GameMode.PLAYER_VS_COMPUTER, AiDifficulty.NORMAL);
        addModeButton(table, "Player vs Computer - Hard", GameMode.PLAYER_VS_COMPUTER, AiDifficulty.HARD);

        TextButton back = button("Back");
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.showMainMenu();
            }
        });
        table.add(back).width(320f).height(52f).padTop(24f);
        stage.addActor(table);
    }

    private void addModeButton(Table table, String text, final GameMode mode, final AiDifficulty difficulty) {
        TextButton button = button(text);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.showTankSelect(mode, difficulty);
            }
        });
        table.add(button).width(360f).height(52f).padTop(14f).row();
    }
}
