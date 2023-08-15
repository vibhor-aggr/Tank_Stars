package com.badlogic.tankstars;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
//import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
//import com.badlogic.gdx.graphics.g2d.TextureAtlas;
//import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
//import com.badlogic.gdx.math.Interpolation;
//import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
//import com.badlogic.gdx.scenes.scene2d.ui.Label;
//import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
//import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
//import com.badlogic.tankstars.MyTextInputListener;
//import com.badlogic.tankstars.TankStars;
//import com.badlogic.tankstars.LoadingScreen;
//import com.badlogic.tankstars.TankSelectScreen;
//import com.badlogic.tankstars.PlayScreen;

public class MainMenuScreen implements Screen {

  private final TankStars ts;

  //private ShapeRenderer shapeRenderer;
  // Stage vars
  private Stage stage;
  private Skin skin;

  // Nav-Buttons
  private TextButton buttonNew;
  private TextButton buttonResume;
  private TextButton buttonExit;

  public MainMenuScreen(final TankStars ts) {
    this.ts = ts;
    //this.shapeRenderer = new ShapeRenderer();
    this.stage = new Stage(new FitViewport(ts.camera.viewportWidth, ts.camera.viewportHeight, ts.camera));
  }

  @Override
  public void show() {
    //System.out.println("MainMenu");
    //ts.camera.setToOrtho(false, V_WIDTH, V_HEIGHT);
    //shapeRenderer.setProjectionMatrix(ts.camera.combined);
    Gdx.input.setInputProcessor(stage);
    stage.clear();

    this.skin = new Skin();
    this.skin.addRegions(ts.assets.get("ui/uiskin.atlas", TextureAtlas.class));
    this.skin.add("default-font", ts.font);
    this.skin.load(Gdx.files.internal("ui/uiskin.json"));

    initNavigationButtons();
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    // tell the camera to update its matrices.
    ts.camera.update();

    // tell the SpriteBatch to render in the
    // coordinate system specified by the camera.
    ts.batch.setProjectionMatrix(ts.camera.combined);

    Texture tankImage = ts.assets.get("img/MMSTank.png", Texture.class);
    ts.batch.begin();
    //ts.batch.draw(ts.backgroundTexture, 0, 0, ts.camera.viewportWidth, ts.camera.viewportHeight);
    ts.batch.draw(tankImage, 0,0, ts.camera.viewportWidth, ts.camera.viewportHeight);
    //ts.font.draw(ts.batch, "MainMenu ...", ts.camera.viewportWidth/2 - 6, ts.camera.viewportHeight/2 - 28);
    ts.batch.end();

    stage.act(delta);
    stage.draw();
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
    //shapeRenderer.dispose();
    stage.dispose();
    skin.dispose();
  }

  // Initialize various buttons
  private void initNavigationButtons() {
    //buttonNew = new TextButton("New Game", skin, "default");
    buttonNew = new TextButton("New Game", skin, "default");
    //buttonNew.setPosition(2*ts.camera.viewportWidth/3+ts.camera.viewportWidth/6-50, 2*ts.camera.viewportHeight/3 + ts.camera.viewportHeight/6 - 25);
    buttonNew.setPosition(2*ts.camera.viewportWidth/3+ts.camera.viewportWidth/6-100, 2*ts.camera.viewportHeight/3 - 25);
    buttonNew.setSize(200, 50);
    buttonNew.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
          ts.setScreen(ts.tankSelectScreen1);
        }
    });
    stage.addActor(buttonNew);

    //buttonResume = new TextButton("Resume Game", skin, "default");
    buttonResume = new TextButton("Resume Game", skin, "default");
    buttonResume.setPosition(2*ts.camera.viewportWidth/3+ts.camera.viewportWidth/6-100, ts.camera.viewportHeight/3 + ts.camera.viewportHeight/6 - 25);
    buttonResume.setSize(200, 50);
    buttonResume.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
          ts.restoreGame("tankstars.ts");
          ts.playScreen.isRestore = true;
          ts.setScreen(ts.playScreen);
          ts.playScreen.isRestore = false;
          /*
          ts.setInputText("");
          MyTextInputListener listener = new MyTextInputListener(ts);
          Gdx.input.getTextInput(listener, "Specify Saved Game file", "", "");
          //Gdx.input.getTextInput(listener, "Specify Saved Game file", "Initial Textfield Value", "Hint Value");
          if (!ts.getInputText().isEmpty()) {
            ts.restoreGame(ts.getInputText());
          }
          */
        }
    });
    stage.addActor(buttonResume);

    //buttonExit = new TextButton("Exit Game", skin, "default");
    buttonExit = new TextButton("Exit Game", skin, "default");
    //buttonExit.setPosition(2*ts.camera.viewportWidth/3+ts.camera.viewportWidth/6-50, ts.camera.viewportHeight/6 - 25);
    buttonExit.setPosition(2*ts.camera.viewportWidth/3+ts.camera.viewportWidth/6-100, ts.camera.viewportHeight/3 - 25);
    buttonExit.setSize(200, 50);
    buttonExit.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
          Gdx.app.exit();
        }
    });
    stage.addActor(buttonExit);
  }

}
