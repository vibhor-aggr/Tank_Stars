package com.badlogic.tankstars;

//import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
//import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
//import com.badlogic.tankstars.TankStars;
//import com.badlogic.tankstars.MainMenuScreen;
//import com.badlogic.tankstars.TankSelectScreen;
//import com.badlogic.tankstars.PlayScreen;

public class LoadingScreen implements Screen {

  private final TankStars ts;

  private ShapeRenderer shapeRenderer;
  private float progress;

  private Texture tankImage;

  public LoadingScreen(final TankStars ts) {
    this.ts = ts;
    this.shapeRenderer = new ShapeRenderer();
    this.tankImage = new Texture(Gdx.files.internal("img/LSTank.png"));
  }

  private void queueAssets() {
    //ts.assets.load("img/LSTank.png", Texture.class);
    ts.assets.load("img/MMSTank.png", Texture.class);
    ts.assets.load("img/TSSTank1.png", Texture.class);
    ts.assets.load("img/TSSTank2.png", Texture.class);
    ts.assets.load("img/TSSTank3.png", Texture.class);
    ts.assets.load("img/TSSP1.png", Texture.class);
    ts.assets.load("img/TSSP2.png", Texture.class);

    ts.assets.load("img/PSP1Tank1.png", Texture.class);
    ts.assets.load("img/PSP1Tank2.png", Texture.class);
    ts.assets.load("img/PSP1Tank3.png", Texture.class);
    ts.assets.load("img/PSP1Bullet1.png", Texture.class);
    ts.assets.load("img/PSP1Bullet2.png", Texture.class);
    ts.assets.load("img/PSP1Bullet3.png", Texture.class);
    ts.assets.load("img/PSP1TankNozzle.JPG", Texture.class);
    ts.assets.load("img/PSP1Won.png", Texture.class);

    ts.assets.load("img/PSP2Tank1.png", Texture.class);
    ts.assets.load("img/PSP2Tank2.png", Texture.class);
    ts.assets.load("img/PSP2Tank3.png", Texture.class);
    ts.assets.load("img/PSP2Bullet1.png", Texture.class);
    ts.assets.load("img/PSP2Bullet2.png", Texture.class);
    ts.assets.load("img/PSP2Bullet3.png", Texture.class);
    ts.assets.load("img/PSP2TankNozzle.JPG", Texture.class);
    ts.assets.load("img/PSP2Won.png", Texture.class);

    ts.assets.load("img/PSAirDrop.png", Texture.class);
    ts.assets.load("img/PSMenu.png", Texture.class);
    ts.assets.load("img/PSPause.png", Texture.class);

    // load music and sound here
    ts.assets.load("sound/PSMusic.mp3", Music.class);
    ts.assets.load("sound/PSShot.wav", Sound.class);
    ts.assets.load("sound/PSHit.wav", Sound.class);
    ts.assets.load("sound/PSTankMove.wav", Sound.class);

    //ts.assets.load("img/background.jpg", Texture.class);
    ts.assets.load("ui/uiskin.atlas", TextureAtlas.class);
  }

  @Override
  public void show() {
    //System.out.println("LOADING");
    //ts.camera.setToOrtho(false, V_WIDTH, V_HEIGHT);
    shapeRenderer.setProjectionMatrix(ts.camera.combined);
    this.progress = 0f;
    queueAssets();
  }

  private void update(float delta) {
    progress = MathUtils.lerp(progress, ts.assets.getProgress(), .1f);
    if (ts.assets.update() && progress >= ts.assets.getProgress() - .001f) {
      ts.setScreen(ts.mainMenuScreen);
    }
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    update(delta);

    // tell the camera to update its matrices.
    ts.camera.update();

    // tell the SpriteBatch to render in the
    // coordinate system specified by the camera.
    ts.batch.setProjectionMatrix(ts.camera.combined);

    ts.batch.begin();
    ts.batch.draw(tankImage, 0, ts.camera.viewportHeight/3, ts.camera.viewportWidth, 2*ts.camera.viewportHeight/3);
    ts.batch.draw(ts.backgroundTexture, 0, 0, ts.camera.viewportWidth, ts.camera.viewportHeight/3);
    ts.font.draw(ts.batch, "Loading ...", ts.camera.viewportWidth/2 - 6, ts.camera.viewportHeight/6 - 28);
    ts.batch.end();

    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    shapeRenderer.setColor(Color.BLACK);
    shapeRenderer.rect(32, ts.camera.viewportHeight/6 - 8, ts.camera.viewportWidth - 64, 16);

    shapeRenderer.setColor(Color.BLUE);
    shapeRenderer.rect(32, ts.camera.viewportHeight/6 - 8, progress * (ts.camera.viewportWidth - 64), 16);
    shapeRenderer.end();
  }

  @Override
  public void resize(int width, int height) {

  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  @Override
  public void hide() {

  }

  @Override
  public void dispose() {
    shapeRenderer.dispose();
    tankImage.dispose();
  }
}
