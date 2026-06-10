package com.badlogic.tankstars;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class LoadingScreen extends ScreenAdapter {
    private final TankStars game;
    private Texture loadingTank;
    private float progress;

    public LoadingScreen(TankStars game) {
        this.game = game;
    }

    @Override
    public void show() {
        loadingTank = new Texture(Gdx.files.internal("img/LSTank.png"));
        game.setBackgroundTexture(new Texture(Gdx.files.internal("img/background.png")));
        queueAssets();
    }

    private void queueAssets() {
        String[] images = {
                "img/MMSTank.png", "img/TSSTank1.png", "img/TSSTank2.png", "img/TSSTank3.png",
                "img/TSSP1.png", "img/TSSP2.png", "img/PSP1Tank1.png", "img/PSP1Tank2.png",
                "img/PSP1Tank3.png", "img/PSP2Tank1.png", "img/PSP2Tank2.png", "img/PSP2Tank3.png",
                "img/PSP1Bullet1.png", "img/PSP1Bullet2.png", "img/PSP1Bullet3.png",
                "img/PSP2Bullet1.png", "img/PSP2Bullet2.png", "img/PSP2Bullet3.png",
                "img/PSAirDrop.png", "img/PSPause.png", "img/PSP1Won.png", "img/PSP2Won.png"
        };
        for (String image : images) {
            game.assets.load(image, Texture.class);
        }
        game.assets.load("sound/PSMusic.mp3", Music.class);
        game.assets.load("sound/PSShot.wav", Sound.class);
        game.assets.load("sound/PSHit.wav", Sound.class);
        game.assets.load("sound/PSTankMove.wav", Sound.class);
        game.assets.load("ui/uiskin.atlas", TextureAtlas.class);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.04f, 0.06f, 0.11f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        progress = MathUtils.lerp(progress, game.assets.getProgress(), 0.12f);
        if (game.assets.update() && progress >= game.assets.getProgress() - 0.001f) {
            game.showMainMenu();
            return;
        }
        game.batch.setProjectionMatrix(game.camera.combined);
        game.batch.begin();
        game.batch.draw(game.getBackgroundTexture(), 0f, 0f, TankStars.V_WIDTH, TankStars.V_HEIGHT);
        game.batch.draw(loadingTank, 220f, 310f, 1000f, 563f);
        game.font.draw(game.batch, "Loading assets...", 640f, 190f);
        game.batch.end();

        game.shapes.setProjectionMatrix(game.camera.combined);
        game.shapes.begin(ShapeRenderer.ShapeType.Filled);
        game.shapes.setColor(Color.DARK_GRAY);
        game.shapes.rect(320f, 140f, 800f, 24f);
        game.shapes.setColor(Color.SKY);
        game.shapes.rect(320f, 140f, 800f * progress, 24f);
        game.shapes.end();
    }

    @Override
    public void dispose() {
        if (loadingTank != null) {
            loadingTank.dispose();
        }
    }
}
