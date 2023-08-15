package com.badlogic.tankstars;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
//import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

//import com.badlogic.tankstars.LoadingScreen;
//import com.badlogic.tankstars.MainMenuScreen;
//import com.badlogic.tankstars.TankSelectScreen;
//import com.badlogic.tankstars.PlayScreen;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
//import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class TankStars extends Game implements Serializable {

  public static String TITLE = "Tank Stars Game";
  public static final float VERSION = 1.0f;
  //public static final int V_WIDTH = 480;
  //public static final int V_HEIGHT = 420;
  public static final int V_WIDTH = 1440; //1920;
  public static final int V_HEIGHT = 900; //1080; //1229;

  public OrthographicCamera camera;
  public SpriteBatch batch;

  public BitmapFont font;

  public AssetManager assets;

  public ScreenFactory screenFactory;
  public LoadingScreen loadingScreen;
  public MainMenuScreen mainMenuScreen;
  public TankSelectScreen tankSelectScreen1;
  public TankSelectScreen tankSelectScreen2;
  public PlayScreen playScreen;

  public Texture backgroundImage;
  public TextureRegion backgroundTexture;

  private int player1Tank;
  private int player2Tank;
  private String inputText;

  private static TankStars gen = null;
  public static TankStars getInstance()
  {
    if (gen == null) {
    gen = new TankStars();
    }
    return gen;
  }

  private TankStars() {}

  @Override
  public void create() {
    assets = new AssetManager();
    camera = new OrthographicCamera();
    camera.setToOrtho(false, V_WIDTH, V_HEIGHT);
    batch = new SpriteBatch();

    font = new BitmapFont(); // use libGDX's default Arial font
    //initFonts();
    screenFactory=new ScreenFactory();
    loadingScreen = (LoadingScreen)screenFactory.createScreen("Load");
    mainMenuScreen = (MainMenuScreen)screenFactory.createScreen("MainMenu");
    tankSelectScreen1 = (TankSelectScreen)screenFactory.createScreen("TankSelect");
    tankSelectScreen1.setForPlayer(1);
    tankSelectScreen2 = (TankSelectScreen)screenFactory.createScreen("TankSelect");
    tankSelectScreen2.setForPlayer(2);
    playScreen = (PlayScreen)screenFactory.createScreen("Play");

    backgroundImage = new Texture(Gdx.files.internal("img/background.png"));
    backgroundTexture = new TextureRegion(backgroundImage, 0, 0, V_WIDTH, V_HEIGHT);

    player1Tank = 1;
    player2Tank = 1;
    inputText = "";
    this.setScreen(loadingScreen);
  }

  @Override
  public void render() {
    super.render();
  }

  @Override
  public void dispose() {
    assets.dispose();
    //camera.dispose();
    batch.dispose();
    font.dispose();
    loadingScreen.dispose();
    mainMenuScreen.dispose();
    tankSelectScreen1.dispose();
    tankSelectScreen2.dispose();
    playScreen.dispose();
    //backgroundTexture.dispose();
    backgroundImage.dispose();
  }

  public boolean saveGame(String file) {
    // save playScreen contents in file
    try {
        FileOutputStream fos = new FileOutputStream(file);
        //todo: show error in this case
        //if (fos == null) return false;
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        // write object to file
        oos.writeObject(playScreen);

        // close writer
        oos.close();

        //todo: print a dialogue that Game is successfully saved in given file
    }
    catch (IOException ex) {
      // Display the exception/s
      System.out.print(ex.getMessage());
      //ex.printStackTrace();
      return false;
    }
    return true;
  }

  public boolean restoreGame(String file) {
    // restore playScreen contents from given file
    try {
      FileInputStream fileIn = new FileInputStream(file);
      //todo: show error in this case
      //if (fileIn == null) return false;
      ObjectInputStream objectIn = new ObjectInputStream(fileIn);

      playScreen = (PlayScreen) objectIn.readObject();
      playScreen.ts = this;
      //todo: print a dialogue that Game is successfully restored from the given file

      objectIn.close();
    }
    catch (Exception ex) {
      // Display the exception/s
      System.out.print(ex.getMessage());
      //ex.printStackTrace();
      return false;
    }
    return true;
  }

  public void setPlayer1Tank(int id) {
    if (id > 3 || id < 1) return;
    player1Tank = id;
  }
  public int getPlayer1Tank() {
    return player1Tank;
  }

  public void setPlayer2Tank(int id) {
    if (id > 3 || id < 1) return;
    player2Tank = id;
  }
  public int getPlayer2Tank() {
    return player2Tank;
  }

  public void setInputText(String text) {
    this.inputText = text;
  }
  public String getInputText() {
    return this.inputText;
  }
  /* 
  private void initFonts() {
    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Arcon.ttf"));
    FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();

    params.size = 24;
    params.color = Color.BLACK;
    font = generator.generateFont(params);
  }
  */

  @Override
  public void write(Json json) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void read(Json json, JsonValue jsonData) {
    // TODO Auto-generated method stub
    
  }
}
