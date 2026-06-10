package com.badlogic.tankstars;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.List;

public class LoadGameScreen extends BaseScreen {
    public LoadGameScreen(TankStars game) {
        super(game);
    }

    @Override
    protected void buildStage() {
        rebuild();
    }

    private void rebuild() {
        stage.clear();
        Table table = new Table();
        table.setFillParent(true);
        table.top().padTop(42f);
        table.add(label("Saved Games")).width(520f).height(54f).colspan(3).row();

        List<SaveSlot> slots = game.getSaveGameService().listSlots();
        if (slots.isEmpty()) {
            table.add(label("No saved games found")).width(520f).height(52f).padTop(30f).colspan(3).row();
        } else {
            int shown = 0;
            for (final SaveSlot slot : slots) {
                if (shown++ >= 8) {
                    break;
                }
                TextButton load = button(slot.getDisplayText());
                load.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.showGame(game.getSaveGameService().load(slot.id));
                    }
                });
                TextButton delete = button("Delete");
                delete.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.getSaveGameService().delete(slot.id);
                        rebuild();
                    }
                });
                table.add(load).width(760f).height(48f).padTop(10f);
                table.add(delete).width(130f).height(48f).padTop(10f).padLeft(12f).row();
            }
        }

        TextButton back = button("Back");
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.showMainMenu();
            }
        });
        table.add(back).width(240f).height(52f).padTop(28f).colspan(3);
        stage.addActor(table);
    }
}
