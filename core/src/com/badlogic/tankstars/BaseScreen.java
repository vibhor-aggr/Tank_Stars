package com.badlogic.tankstars;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

public abstract class BaseScreen extends ScreenAdapter {
    protected final TankStars game;
    protected Stage stage;
    protected Skin skin;

    protected BaseScreen(TankStars game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(TankStars.V_WIDTH, TankStars.V_HEIGHT), game.batch);
        skin = game.createSkin();
        Gdx.input.setInputProcessor(stage);
        buildStage();
    }

    protected abstract void buildStage();

    @Override
    public void render(float delta) {
        clear();
        drawBackground();
        stage.act(delta);
        stage.draw();
    }

    protected void clear() {
        Gdx.gl.glClearColor(0.04f, 0.06f, 0.10f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    protected void drawBackground() {
        if (game.getBackgroundTexture() == null) {
            return;
        }
        game.batch.setProjectionMatrix(stage.getCamera().combined);
        game.batch.begin();
        game.batch.draw(game.getBackgroundTexture(), 0f, 0f, TankStars.V_WIDTH, TankStars.V_HEIGHT);
        game.batch.end();
    }

    protected TextButton button(String text) {
        TextButton button = new TextButton(text, skin, "default");
        button.getLabel().setAlignment(Align.center);
        return button;
    }

    protected Label label(String text) {
        Label label = new Label(text, skin);
        label.setAlignment(Align.center);
        return label;
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
        if (skin != null) {
            skin.dispose();
        }
    }
}
