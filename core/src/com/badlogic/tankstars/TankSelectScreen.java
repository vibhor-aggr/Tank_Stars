package com.badlogic.tankstars;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class TankSelectScreen extends BaseScreen {
    private final GameMode mode;
    private final AiDifficulty difficulty;
    private TankType playerOne = TankType.VANGUARD;
    private TankType playerTwo = TankType.TITAN;
    private Label p1Label;
    private Label p2Label;

    public TankSelectScreen(TankStars game, GameMode mode, AiDifficulty difficulty) {
        super(game);
        this.mode = mode;
        this.difficulty = difficulty;
    }

    @Override
    protected void buildStage() {
        Table root = new Table();
        root.setFillParent(true);
        root.top().padTop(54f);

        root.add(label("Choose Tanks")).width(420f).height(54f).colspan(3).row();
        p1Label = label("");
        p2Label = label("");
        updateSelectionLabels();

        root.add(p1Label).width(360f).height(44f).padTop(22f);
        root.add().width(80f);
        root.add(p2Label).width(360f).height(44f).padTop(22f).row();
        addTankButtons(root, true);
        addTankButtons(root, false);

        TextButton start = button("Start Match");
        start.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.startNewGame(mode, difficulty, playerOne, playerTwo);
            }
        });
        TextButton back = button("Back");
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.showModeSelect();
            }
        });
        root.add(start).width(260f).height(54f).padTop(32f);
        root.add();
        root.add(back).width(220f).height(54f).padTop(32f);
        stage.addActor(root);
    }

    private void addTankButtons(Table root, final boolean leftSide) {
        Table table = new Table();
        addTankButton(table, leftSide, TankType.VANGUARD);
        addTankButton(table, leftSide, TankType.TITAN);
        addTankButton(table, leftSide, TankType.SCOUT);
        if (leftSide) {
            root.add(table).width(360f).padTop(18f);
            root.add().width(80f);
        } else {
            root.add(table).width(360f).padTop(18f).row();
        }
    }

    private void addTankButton(Table table, final boolean playerOneSide, final TankType type) {
        TextButton button = button(labelFor(type));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (playerOneSide) {
                    playerOne = type;
                } else {
                    playerTwo = type;
                }
                updateSelectionLabels();
            }
        });
        table.add(button).width(310f).height(50f).padTop(12f).row();
    }

    private void updateSelectionLabels() {
        if (p1Label != null) {
            p1Label.setText("Player 1: " + labelFor(playerOne));
        }
        if (p2Label != null) {
            p2Label.setText((mode == GameMode.PLAYER_VS_COMPUTER ? "Computer: " : "Player 2: ") + labelFor(playerTwo));
        }
    }

    private String labelFor(TankType type) {
        return TankFactory.create(type).getDisplayName();
    }

    @Override
    protected void drawBackground() {
        super.drawBackground();
        game.batch.setProjectionMatrix(stage.getCamera().combined);
        game.batch.begin();
        drawTankPreview("img/TSSTank1.png", 140f, 185f, playerOne == TankType.VANGUARD || playerTwo == TankType.VANGUARD);
        drawTankPreview("img/TSSTank2.png", 470f, 185f, playerOne == TankType.TITAN || playerTwo == TankType.TITAN);
        drawTankPreview("img/TSSTank3.png", 800f, 185f, playerOne == TankType.SCOUT || playerTwo == TankType.SCOUT);
        game.batch.end();
    }

    private void drawTankPreview(String asset, float x, float y, boolean selected) {
        Texture texture = game.assets.get(asset, Texture.class);
        float scale = selected ? 1.04f : 0.9f;
        game.batch.draw(texture, x, y, 250f * scale, 128f * scale);
    }
}
