package com.badlogic.tankstars;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;

public class GameScreen extends InputAdapter implements com.badlogic.gdx.Screen {
    private final TankStars game;
    private final GameModel model;
    private final AiStrategy aiStrategy;
    private final List<VisualEffect> effects = new ArrayList<VisualEffect>();
    private final EnumMap<WeaponType, TextButton> weaponButtons = new EnumMap<WeaponType, TextButton>(WeaponType.class);

    private Stage stage;
    private com.badlogic.gdx.scenes.scene2d.ui.Skin skin;
    private Label turnLabel;
    private Label statusLabel;
    private Label statsLabel;
    private Label angleLabel;
    private Label powerLabel;
    private Label weaponLabel;
    private Table pauseOverlay;
    private Table gameOverOverlay;
    private Music music;
    private boolean paused;
    private int lastTurnSeen;
    private int lastDirectHits;
    private float aiTimer;
    private float shakeTime;

    public GameScreen(TankStars game, GameModel model) {
        this.game = game;
        this.model = model;
        this.aiStrategy = new HeuristicAiStrategy(model.getAiDifficulty());
        this.lastTurnSeen = model.getTurnNumber();
        this.lastDirectHits = model.getStats().directHits;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(TankStars.V_WIDTH, TankStars.V_HEIGHT), game.batch);
        skin = game.createSkin();
        buildHud();
        InputMultiplexer multiplexer = new InputMultiplexer(stage, this);
        Gdx.input.setInputProcessor(multiplexer);
        music = game.assets.get("sound/PSMusic.mp3", Music.class);
        music.setLooping(true);
        music.setVolume(0.22f);
        music.play();
    }

    private void buildHud() {
        Table top = new Table();
        top.setFillParent(true);
        top.top().pad(14f);
        turnLabel = new Label("", skin);
        statusLabel = new Label("", skin);
        statsLabel = new Label("", skin);
        top.add(turnLabel).width(390f).height(32f).left();
        top.add(statusLabel).width(520f).height(32f);
        top.add(statsLabel).width(470f).height(32f).right();
        stage.addActor(top);

        Table controls = new Table();
        controls.setFillParent(true);
        controls.bottom().padBottom(18f);
        angleLabel = new Label("", skin);
        powerLabel = new Label("", skin);
        weaponLabel = new Label("", skin);
        controls.add(angleLabel).width(120f).height(46f);
        controls.add(button("- Angle", new Runnable() {
            @Override
            public void run() {
                adjustAim(-2f, 0f);
            }
        })).width(104f).height(46f).padLeft(8f);
        controls.add(button("+ Angle", new Runnable() {
            @Override
            public void run() {
                adjustAim(2f, 0f);
            }
        })).width(104f).height(46f).padLeft(8f);
        controls.add(powerLabel).width(120f).height(46f).padLeft(20f);
        controls.add(button("- Power", new Runnable() {
            @Override
            public void run() {
                adjustAim(0f, -4f);
            }
        })).width(104f).height(46f).padLeft(8f);
        controls.add(button("+ Power", new Runnable() {
            @Override
            public void run() {
                adjustAim(0f, 4f);
            }
        })).width(104f).height(46f).padLeft(8f);
        controls.add(weaponLabel).width(180f).height(46f).padLeft(18f);
        controls.add(button("Fire", new Runnable() {
            @Override
            public void run() {
                fire();
            }
        })).width(122f).height(50f).padLeft(12f);
        controls.add(button("Pause", new Runnable() {
            @Override
            public void run() {
                pauseGame();
            }
        })).width(122f).height(50f).padLeft(12f);
        stage.addActor(controls);

        Table weapons = new Table();
        weapons.setFillParent(true);
        weapons.bottom().left().padLeft(18f).padBottom(78f);
        for (final WeaponType type : WeaponType.values()) {
            TextButton weaponButton = button(typeName(type), new Runnable() {
                @Override
                public void run() {
                    model.selectWeapon(type);
                    refreshLabels();
                }
            });
            weaponButtons.put(type, weaponButton);
            weapons.add(weaponButton).width(150f).height(42f).padRight(8f);
        }
        stage.addActor(weapons);

        buildPauseOverlay();
        buildGameOverOverlay();
        refreshLabels();
    }

    private void buildPauseOverlay() {
        pauseOverlay = new Table();
        pauseOverlay.setFillParent(true);
        pauseOverlay.center();
        pauseOverlay.setVisible(false);
        pauseOverlay.add(new Label("Paused", skin)).width(320f).height(50f).row();
        pauseOverlay.add(button("Resume", new Runnable() {
            @Override
            public void run() {
                paused = false;
                pauseOverlay.setVisible(false);
            }
        })).width(260f).height(52f).padTop(12f).row();
        pauseOverlay.add(button("Save Game", new Runnable() {
            @Override
            public void run() {
                game.saveCurrentGame("Manual Save");
                addBanner("Game saved");
            }
        })).width(260f).height(52f).padTop(12f).row();
        pauseOverlay.add(button("Load Game", new Runnable() {
            @Override
            public void run() {
                game.showLoadGame();
            }
        })).width(260f).height(52f).padTop(12f).row();
        pauseOverlay.add(button("Main Menu", new Runnable() {
            @Override
            public void run() {
                game.showMainMenu();
            }
        })).width(260f).height(52f).padTop(12f).row();
        pauseOverlay.add(button("Exit", new Runnable() {
            @Override
            public void run() {
                Gdx.app.exit();
            }
        })).width(260f).height(52f).padTop(12f);
        stage.addActor(pauseOverlay);
    }

    private void buildGameOverOverlay() {
        gameOverOverlay = new Table();
        gameOverOverlay.setFillParent(true);
        gameOverOverlay.center();
        gameOverOverlay.setVisible(false);
        gameOverOverlay.add(new Label("", skin)).width(420f).height(50f).row();
        gameOverOverlay.add(button("Restart", new Runnable() {
            @Override
            public void run() {
                Player[] players = model.getPlayers();
                game.startNewGame(model.getMode(), model.getAiDifficulty(), players[0].getTank().getType(), players[1].getTank().getType());
            }
        })).width(260f).height(52f).padTop(12f).row();
        gameOverOverlay.add(button("Save Summary", new Runnable() {
            @Override
            public void run() {
                game.saveCurrentGame("Finished Match");
                addBanner("Summary saved");
            }
        })).width(260f).height(52f).padTop(12f).row();
        gameOverOverlay.add(button("Main Menu", new Runnable() {
            @Override
            public void run() {
                game.showMainMenu();
            }
        })).width(260f).height(52f).padTop(12f);
        stage.addActor(gameOverOverlay);
    }

    private TextButton button(String text, final Runnable action) {
        TextButton button = new TextButton(text, skin, "default");
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
        return button;
    }

    @Override
    public void render(float delta) {
        if (!paused && !model.isGameOver()) {
            handleKeyboard(delta);
            handleAi(delta);
            model.update(delta);
        }
        detectTurnChanges();
        updateEffects(delta);
        refreshLabels();

        Gdx.gl.glClearColor(0.04f, 0.06f, 0.10f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        drawWorld(delta);
        stage.act(delta);
        stage.draw();
    }

    private void handleKeyboard(float delta) {
        boolean aiTurn = model.getActivePlayer().isComputerControlled();
        if (aiTurn || model.getTurnState() != TurnState.AIMING) {
            return;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            model.moveActiveTank(-1f, delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            model.moveActiveTank(1f, delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            adjustAim(55f * delta, 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            adjustAim(-55f * delta, 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.I)) {
            adjustAim(0f, 60f * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.K)) {
            adjustAim(0f, -60f * delta);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            fire();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.P) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            pauseGame();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            model.selectWeapon(WeaponType.BASIC_SHELL);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            model.selectWeapon(WeaponType.BIG_ONE);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            model.selectWeapon(WeaponType.AIR_STRIKE);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            model.selectWeapon(WeaponType.CLUSTER_SHOT);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            model.selectWeapon(WeaponType.REPAIR_KIT);
        }
    }

    private void handleAi(float delta) {
        if (!model.getActivePlayer().isComputerControlled() || model.getTurnState() != TurnState.AIMING) {
            aiTimer = 0f;
            return;
        }
        aiTimer += delta;
        if (aiTimer < 0.9f) {
            return;
        }
        AiAction action = aiStrategy.chooseAction(model);
        model.moveActiveTank(action.moveDirection, action.moveSeconds);
        model.selectWeapon(action.weaponType);
        model.setAim(action.angleDegrees, action.power);
        fire();
        aiTimer = 0f;
    }

    private void adjustAim(float angleDelta, float powerDelta) {
        if (model.getTurnState() != TurnState.AIMING || model.getActivePlayer().isComputerControlled()) {
            return;
        }
        Shooter shooter = model.getActivePlayer().getShooter();
        model.setAim(shooter.getAngleDegrees() + angleDelta, shooter.getPower() + powerDelta);
    }

    private void fire() {
        if (paused || model.getTurnState() != TurnState.AIMING) {
            return;
        }
        if (model.fireSelectedWeapon()) {
            game.assets.get("sound/PSShot.wav", Sound.class).play(0.55f);
        }
    }

    private void pauseGame() {
        if (model.isGameOver()) {
            return;
        }
        paused = true;
        pauseOverlay.setVisible(true);
        game.autosaveCurrentGame();
    }

    private void detectTurnChanges() {
        if (model.getStats().directHits > lastDirectHits) {
            lastDirectHits = model.getStats().directHits;
            addExplosion(model.getOpponent().getTank().getX(), model.getOpponent().getTank().getY() + 42f, "Direct hit");
            game.assets.get("sound/PSHit.wav", Sound.class).play(0.65f);
        }
        if (model.getTurnNumber() != lastTurnSeen) {
            lastTurnSeen = model.getTurnNumber();
            game.autosaveCurrentGame();
            addBanner(model.getActivePlayer().getName() + "'s turn");
            shakeTime = 0.22f;
        }
        gameOverOverlay.setVisible(model.isGameOver());
        if (model.isGameOver()) {
            paused = false;
            pauseOverlay.setVisible(false);
        }
    }

    private void refreshLabels() {
        Player active = model.getActivePlayer();
        Player[] players = model.getPlayers();
        Shooter shooter = active.getShooter();
        turnLabel.setText("Turn " + model.getTurnNumber() + " | " + active.getName());
        statusLabel.setText("Wind " + Math.round(model.getWind()) + " | P1 " + Math.round(players[0].getCurrentHealth())
                + " HP | P2 " + Math.round(players[1].getCurrentHealth()) + " HP");
        statsLabel.setText("Shots " + model.getStats().shotsFired + " | Hits " + model.getStats().directHits
                + " | Drops " + model.getStats().pickupsCollected);
        angleLabel.setText("Angle " + Math.round(shooter.getAngleDegrees()));
        powerLabel.setText("Power " + Math.round(shooter.getPower()));
        weaponLabel.setText(typeName(shooter.getSelectedWeapon()));
        for (WeaponType type : WeaponType.values()) {
            TextButton button = weaponButtons.get(type);
            if (button != null) {
                int ammo = active.getShooter().getAmmo(type);
                button.setText(typeName(type) + (type == WeaponType.BASIC_SHELL ? "" : " (" + ammo + ")"));
                button.setDisabled(ammo <= 0);
            }
        }
        if (gameOverOverlay != null && model.isGameOver()) {
            ((Label) gameOverOverlay.getChildren().first()).setText(model.getPlayers()[model.getWinnerIndex()].getName() + " wins");
        }
    }

    private void drawWorld(float delta) {
        float shake = shakeTime > 0f ? (float) Math.sin(shakeTime * 80f) * 5f : 0f;
        shakeTime = Math.max(0f, shakeTime - delta);
        game.camera.position.set(TankStars.V_WIDTH / 2f + shake, TankStars.V_HEIGHT / 2f, 0f);
        game.camera.update();

        game.batch.setProjectionMatrix(game.camera.combined);
        game.batch.begin();
        game.batch.draw(game.getBackgroundTexture(), 0f, 0f, TankStars.V_WIDTH, TankStars.V_HEIGHT);
        game.batch.end();

        drawTerrainAndBars();
        drawTrajectoryPreview();
        drawSprites();

        game.camera.position.set(TankStars.V_WIDTH / 2f, TankStars.V_HEIGHT / 2f, 0f);
        game.camera.update();
    }

    private void drawTerrainAndBars() {
        game.shapes.setProjectionMatrix(game.camera.combined);
        game.shapes.begin(ShapeRenderer.ShapeType.Filled);
        float[] heights = model.getTerrain().copyHeights();
        float sampleWidth = model.getWorldWidth() / (heights.length - 1);
        for (int i = 0; i < heights.length; i++) {
            float x = i * sampleWidth;
            game.shapes.setColor(0.18f, 0.48f, 0.18f, 1f);
            game.shapes.rect(x, 0f, sampleWidth + 2f, heights[i]);
        }
        drawHealthBar(40f, 840f, model.getPlayers()[0], Color.FOREST);
        drawHealthBar(1080f, 840f, model.getPlayers()[1], Color.FOREST);
        game.shapes.end();
    }

    private void drawHealthBar(float x, float y, Player player, Color color) {
        game.shapes.setColor(Color.DARK_GRAY);
        game.shapes.rect(x, y, 310f, 24f);
        game.shapes.setColor(color);
        float width = 310f * (player.getCurrentHealth() / player.getTank().getMaxHealth());
        game.shapes.rect(x, y, width, 24f);
        game.shapes.setColor(Color.ORANGE);
        game.shapes.rect(x, y - 31f, 310f * (player.getFuelRemaining() / player.getTank().getMaxFuel()), 18f);
    }

    private void drawTrajectoryPreview() {
        if (paused || model.getActivePlayer().isComputerControlled() || model.getTurnState() != TurnState.AIMING) {
            return;
        }
        List<Vec2> preview = model.getTrajectoryPreview(44);
        if (preview.size() < 2) {
            return;
        }
        game.shapes.setProjectionMatrix(game.camera.combined);
        game.shapes.begin(ShapeRenderer.ShapeType.Line);
        game.shapes.setColor(Color.WHITE);
        for (int i = 1; i < preview.size(); i++) {
            Vec2 a = preview.get(i - 1);
            Vec2 b = preview.get(i);
            game.shapes.line(a.x, a.y, b.x, b.y);
        }
        game.shapes.end();
    }

    private void drawSprites() {
        game.batch.setProjectionMatrix(game.camera.combined);
        game.batch.begin();
        for (Drop drop : model.getDrops()) {
            if (!drop.collected) {
                Texture texture = game.assets.get("img/PSAirDrop.png", Texture.class);
                game.batch.draw(texture, drop.x - 24f, drop.y - 24f, 48f, 48f);
            }
        }
        drawTank(model.getPlayers()[0], 0);
        drawTank(model.getPlayers()[1], 1);
        Projectile projectile = model.getActiveProjectile();
        if (projectile != null && projectile.isActive()) {
            Texture texture = game.assets.get(projectile.getOwnerIndex() == 0 ? "img/PSP1Bullet1.png" : "img/PSP2Bullet1.png", Texture.class);
            Vec2 p = projectile.getPosition();
            game.batch.draw(texture, p.x - 18f, p.y - 12f, 36f, 24f);
        }
        for (VisualEffect effect : effects) {
            if (effect.text != null) {
                game.font.draw(game.batch, effect.text, effect.x - 40f, effect.y + effect.age * 24f);
            }
        }
        game.batch.end();

        game.shapes.setProjectionMatrix(game.camera.combined);
        game.shapes.begin(ShapeRenderer.ShapeType.Line);
        for (VisualEffect effect : effects) {
            game.shapes.setColor(Color.ORANGE);
            game.shapes.circle(effect.x, effect.y, effect.radius * (1f + effect.age));
        }
        game.shapes.end();
    }

    private void drawTank(Player player, int index) {
        Tank tank = player.getTank();
        Texture texture = game.assets.get(tankAsset(index, tank.getType()), Texture.class);
        float width = tank.getWidth() * 1.25f;
        float height = tank.getHeight() * 1.25f;
        game.batch.draw(texture, tank.getX() - width / 2f, tank.getY(), width / 2f, height / 2f,
                width, height, 1f, 1f, tank.getRotationDegrees(), 0, 0, texture.getWidth(), texture.getHeight(), false, false);
    }

    private String tankAsset(int playerIndex, TankType type) {
        String prefix = playerIndex == 0 ? "img/PSP1Tank" : "img/PSP2Tank";
        if (type == TankType.TITAN) {
            return prefix + "2.png";
        }
        if (type == TankType.SCOUT) {
            return prefix + "3.png";
        }
        return prefix + "1.png";
    }

    private void addBanner(String text) {
        effects.add(new VisualEffect(TankStars.V_WIDTH / 2f, 760f, 0f, 1.5f, text));
    }

    private void addExplosion(float x, float y, String text) {
        effects.add(new VisualEffect(x, y, 34f, 0.75f, text));
    }

    private void updateEffects(float delta) {
        Iterator<VisualEffect> iterator = effects.iterator();
        while (iterator.hasNext()) {
            VisualEffect effect = iterator.next();
            effect.age += delta;
            if (effect.age >= effect.duration) {
                iterator.remove();
            }
        }
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        aimAt(screenX, screenY);
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        aimAt(screenX, screenY);
        return false;
    }

    private void aimAt(int screenX, int screenY) {
        if (paused || model.getTurnState() != TurnState.AIMING || model.getActivePlayer().isComputerControlled()) {
            return;
        }
        Vector2 world = stage.getViewport().unproject(new Vector2(screenX, screenY));
        Tank tank = model.getActivePlayer().getTank();
        float sign = model.getActivePlayerIndex() == 0 ? 1f : -1f;
        float dx = Math.max(1f, sign * (world.x - tank.getX()));
        float dy = Math.max(1f, world.y - tank.getY());
        float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
        float power = Math.min(100f, Math.max(15f, (float) Math.sqrt(dx * dx + dy * dy) / 6.5f));
        model.setAim(angle, power);
    }

    private String typeName(WeaponType type) {
        return WeaponFactory.create(type).getDisplayName();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        pauseGame();
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        if (music != null) {
            music.stop();
        }
        if (stage != null) {
            stage.dispose();
        }
        if (skin != null) {
            skin.dispose();
        }
    }

    private static class VisualEffect {
        final float x;
        final float y;
        final float radius;
        final float duration;
        final String text;
        float age;

        VisualEffect(float x, float y, float radius, float duration, String text) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.duration = duration;
            this.text = text;
        }
    }
}
