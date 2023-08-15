package com.badlogic.tankstars;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
//import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.TextureAtlas;
//import com.badlogic.gdx.graphics.g2d.TextureRegion;
//import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
//import com.badlogic.gdx.math.Interpolation;
//import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
//import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
//import com.badlogic.gdx.scenes.scene2d.ui.Label;
//import com.badlogic.gdx.scenes.scene2d.ui.Skin;
//import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
//import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
//import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
//import com.badlogic.tankstars.TankStars;
//import com.badlogic.tankstars.LoadingScreen;
//import com.badlogic.tankstars.MainMenuScreen;
//import com.badlogic.tankstars.PlayScreen;

public class TankSelectScreen implements Screen {

  private final TankStars ts;

  //private ShapeRenderer shapeRenderer;
  // Stage vars
  private Stage stage;
  //private Skin skin;

  // Nav-Buttons
  //private ImageButton buttonTank1;
  //private ImageButton buttonTank2;
  //private ImageButton buttonTank3;

  private boolean forPlayer1;

  public TankSelectScreen(final TankStars ts) {
    this.ts = ts;
    //this.shapeRenderer = new ShapeRenderer();
    this.stage = new Stage(new FitViewport(TankStars.V_WIDTH, TankStars.V_HEIGHT, ts.camera));
  }

  @Override
  public void show() {
    /*
    if (forPlayer1) {
      Gdx.graphics.setTitle("Select Tank for Player1");
    } else {
      Gdx.graphics.setTitle("Select Tank for Player2");
    }
    */

    //System.out.println("MainMenu");
    //ts.camera.setToOrtho(false, V_WIDTH, V_HEIGHT);
    //shapeRenderer.setProjectionMatrix(ts.camera.combined);
    Gdx.input.setInputProcessor(stage);
    stage.clear();

    /*
    this.skin = new Skin();
    this.skin.addRegions(ts.assets.get("ui/uiskin.atlas", TextureAtlas.class));
    this.skin.add("default-font", ts.font24);
    this.skin.load(Gdx.files.internal("ui/uiskin.json"));
    */

    initTankButtons();
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

    ts.batch.begin();
    ts.batch.draw(ts.backgroundTexture, 0, 0, ts.camera.viewportWidth, ts.camera.viewportHeight);
    //ts.font.draw(ts.batch, "MainMenu ...", ts.camera.viewportWidth/2 - 6, ts.camera.viewportHeight/2 - 28);
    if (forPlayer1) {
      Texture selImage = ts.assets.get("img/TSSP1.png", Texture.class);
      ts.batch.draw(selImage, ts.camera.viewportWidth/2-500, ts.camera.viewportHeight-200, 1000, 100);
    } else {
      Texture selImage = ts.assets.get("img/TSSP2.png", Texture.class);
      ts.batch.draw(selImage, ts.camera.viewportWidth/2-500, ts.camera.viewportHeight-200, 1000, 100);
    }
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
  }

  public void setForPlayer(int playerId) {
    if (playerId == 1) forPlayer1 = true;
    else forPlayer1 = false;
  }

  // Initialize various buttons
  private void initTankButtons() {
    int iw = 500;
    int ih = 400;
    Texture tank1Image = ts.assets.get("img/TSSTank1.png", Texture.class);
    //TextureRegion tank1TextureR = new TextureRegion(tank1Image, ts.camera.viewportWidth/3 - ts.camera.viewportWidth/6 - iw/2, ts.camera.viewportHeight/2 - ih/2, iw, ih);
    //TextureRegionDrawable tank1TextureRD = new TextureRegionDrawable(tank1TextureR);
    //ImageButton buttonTank1 = new ImageButton(tank1TextureRD);
    Image buttonTank1 = new Image(tank1Image);
    buttonTank1.setPosition(ts.camera.viewportWidth/3 - ts.camera.viewportWidth/6 - iw/2, ts.camera.viewportHeight/2 - ih/2);
    buttonTank1.setSize(iw, ih);
    buttonTank1.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
          if (forPlayer1) {
            ts.setPlayer1Tank(1);
            ts.setScreen(ts.tankSelectScreen2);
          } else {
            ts.setPlayer2Tank(1);
            ts.setScreen(ts.playScreen);
          }
        }
    });
    stage.addActor(buttonTank1);

    Texture tank2Image = ts.assets.get("img/TSSTank2.png", Texture.class);
    //TextureRegion tank2TextureR = new TextureRegion(tank2Image, 2*ts.camera.viewportWidth/3 - ts.camera.viewportWidth/6 - iw/2, ts.camera.viewportHeight/2 - ih/2, iw, ih);
    //TextureRegionDrawable tank2TextureRD = new TextureRegionDrawable(tank2TextureR);
    //ImageButton buttonTank2 = new ImageButton(tank2TextureRD);
    Image buttonTank2 = new Image(tank2Image);
    buttonTank2.setPosition(2*ts.camera.viewportWidth/3 - ts.camera.viewportWidth/6 - iw/2, ts.camera.viewportHeight/2 - ih/2);
    buttonTank2.setSize(iw, ih);
    buttonTank2.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
          if (forPlayer1) {
            ts.setPlayer1Tank(2);
            ts.setScreen(ts.tankSelectScreen2);
          } else {
            ts.setPlayer2Tank(2);
            ts.setScreen(ts.playScreen);
          }
        }
    });
    stage.addActor(buttonTank2);

    Texture tank3Image = ts.assets.get("img/TSSTank3.png", Texture.class);
    //TextureRegion tank3TextureR = new TextureRegion(tank3Image, ts.camera.viewportWidth - ts.camera.viewportWidth/6 - iw/2, ts.camera.viewportHeight/2 - ih/2, iw, ih);
    //TextureRegionDrawable tank3TextureRD = new TextureRegionDrawable(tank3TextureR);
    //ImageButton buttonTank3 = new ImageButton(tank3TextureRD);
    Image buttonTank3 = new Image(tank3Image);
    buttonTank3.setPosition(ts.camera.viewportWidth - ts.camera.viewportWidth/6 - iw/2, ts.camera.viewportHeight/2 - ih/2);
    buttonTank3.setSize(iw, ih);
    buttonTank3.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
          if (forPlayer1) {
            ts.setPlayer1Tank(3);
            ts.setScreen(ts.tankSelectScreen2);
          } else {
            ts.setPlayer2Tank(3);
            ts.setScreen(ts.playScreen);
          }
        }
    });
    stage.addActor(buttonTank3);
  }

}
