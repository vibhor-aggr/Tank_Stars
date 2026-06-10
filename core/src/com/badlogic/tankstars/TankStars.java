package com.badlogic.tankstars;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TankStars extends Game {
    public static final String TITLE = "Tank Stars";
    public static final int V_WIDTH = 1440;
    public static final int V_HEIGHT = 900;

    public AssetManager assets;
    public SpriteBatch batch;
    public ShapeRenderer shapes;
    public BitmapFont font;
    public OrthographicCamera camera;
    public Viewport viewport;

    private ScreenFactory screenFactory;
    private SaveGameService saveGameService;
    private Texture backgroundTexture;
    private GameModel currentModel;

    @Override
    public void create() {
        assets = new AssetManager();
        batch = new SpriteBatch();
        shapes = new ShapeRenderer();
        font = new BitmapFont();
        camera = new OrthographicCamera();
        viewport = new FitViewport(V_WIDTH, V_HEIGHT, camera);
        viewport.apply();
        camera.position.set(V_WIDTH / 2f, V_HEIGHT / 2f, 0f);
        camera.update();
        saveGameService = new SaveGameService();
        screenFactory = new ScreenFactory(this);
        showLoading();
    }

    public void showLoading() {
        switchScreen(screenFactory.loading());
    }

    public void showMainMenu() {
        switchScreen(screenFactory.mainMenu());
    }

    public void showModeSelect() {
        switchScreen(screenFactory.modeSelect());
    }

    public void showTankSelect(GameMode mode, AiDifficulty difficulty) {
        switchScreen(screenFactory.tankSelect(mode, difficulty));
    }

    public void startNewGame(GameMode mode, AiDifficulty difficulty, TankType playerOneTank, TankType playerTwoTank) {
        currentModel = GameModel.create(mode, difficulty, playerOneTank, playerTwoTank);
        saveGameService.saveAutosave(currentModel);
        showGame(currentModel);
    }

    public void showGame(GameModel model) {
        currentModel = model;
        switchScreen(screenFactory.game(model));
    }

    public void showLoadGame() {
        switchScreen(screenFactory.loadGame());
    }

    public SaveSlot saveCurrentGame(String label) {
        if (currentModel == null) {
            return null;
        }
        return saveGameService.save(currentModel, label);
    }

    public void autosaveCurrentGame() {
        if (currentModel != null) {
            saveGameService.saveAutosave(currentModel);
        }
    }

    public SaveGameService getSaveGameService() {
        return saveGameService;
    }

    public Texture getBackgroundTexture() {
        return backgroundTexture;
    }

    public void setBackgroundTexture(Texture backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
    }

    public Skin createSkin() {
        Skin skin = new Skin();
        skin.addRegions(assets.get("ui/uiskin.atlas", TextureAtlas.class));
        skin.add("default-font", font);
        skin.load(com.badlogic.gdx.Gdx.files.internal("ui/uiskin.json"));
        return skin;
    }

    public void switchScreen(Screen next) {
        Screen previous = getScreen();
        if (previous == next) {
            return;
        }
        super.setScreen(next);
        if (previous != null) {
            previous.dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        if (getScreen() != null) {
            getScreen().resize(width, height);
        }
    }

    @Override
    public void dispose() {
        Screen screen = getScreen();
        if (screen != null) {
            screen.dispose();
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        assets.dispose();
        shapes.dispose();
        batch.dispose();
        font.dispose();
    }
}
