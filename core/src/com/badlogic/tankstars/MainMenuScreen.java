package com.badlogic.tankstars;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainMenuScreen extends BaseScreen {
    public MainMenuScreen(TankStars game) {
        super(game);
    }

    @Override
    protected void buildStage() {
        Table menu = new Table();
        menu.setFillParent(true);
        menu.right().padRight(118f);

        Label title = label("TANK STARS");
        TextButton newGame = button("New Game");
        TextButton resume = button("Resume Game");
        TextButton exit = button("Exit Game");

        newGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.showModeSelect();
            }
        });
        resume.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.showLoadGame();
            }
        });
        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        menu.add(title).width(300f).height(56f).row();
        menu.add(newGame).width(250f).height(56f).padTop(20f).row();
        menu.add(resume).width(250f).height(56f).padTop(16f).row();
        menu.add(exit).width(250f).height(56f).padTop(16f);
        stage.addActor(menu);
    }

    @Override
    protected void drawBackground() {
        Texture menuImage = game.assets.get("img/MMSTank.png", Texture.class);
        game.batch.setProjectionMatrix(stage.getCamera().combined);
        game.batch.begin();
        game.batch.draw(menuImage, 0f, 0f, TankStars.V_WIDTH, TankStars.V_HEIGHT);
        game.batch.end();
    }
}
