package com.badlogic.tankstars;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GameModelTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void saveLoadRoundTripPreservesCriticalState() throws Exception {
        File saveDir = temporaryFolder.newFolder("saves");
        SaveGameService service = new SaveGameService(saveDir);
        GameModel model = GameModel.create(GameMode.PLAYER_VS_COMPUTER, AiDifficulty.HARD, TankType.SCOUT, TankType.TITAN);
        model.getPlayers()[0].applyDamage(17f);
        model.getPlayers()[1].getTank().setX(990f);
        model.getTerrain().createCrater(620f, 85f, 45f);
        model.setAim(52f, 77f);
        model.setWind(31f);
        model.forceNextTurnForTest();

        SaveSlot slot = service.save(model, "Round Trip");
        GameModel restored = service.load(slot.id);

        assertEquals(model.getMode(), restored.getMode());
        assertEquals(model.getAiDifficulty(), restored.getAiDifficulty());
        assertEquals(model.getActivePlayerIndex(), restored.getActivePlayerIndex());
        assertEquals(model.getTurnNumber(), restored.getTurnNumber());
        assertEquals(model.getWind(), restored.getWind(), 0.001f);
        assertEquals(model.getPlayers()[0].getCurrentHealth(), restored.getPlayers()[0].getCurrentHealth(), 0.001f);
        assertEquals(model.getPlayers()[1].getTank().getX(), restored.getPlayers()[1].getTank().getX(), 0.001f);
        assertEquals(model.getTerrain().getHeightAt(620f), restored.getTerrain().getHeightAt(620f), 0.001f);
    }

    @Test
    public void projectileDamageFallsOffWithDistance() {
        GameModel model = GameModel.create(GameMode.PLAYER_VS_PLAYER, AiDifficulty.NORMAL, TankType.VANGUARD, TankType.TITAN);

        float close = model.computeDamageForDistance(WeaponType.BIG_ONE, 15f);
        float far = model.computeDamageForDistance(WeaponType.BIG_ONE, 95f);
        float outside = model.computeDamageForDistance(WeaponType.BIG_ONE, 150f);

        assertTrue(close > far);
        assertEquals(0f, outside, 0.001f);
    }

    @Test
    public void terrainCraterMutatesGroundWithinBounds() {
        Terrain terrain = new Terrain(GameModel.WORLD_WIDTH, 120, 105f, 285f, 42L);
        float before = terrain.getHeightAt(500f);

        boolean changed = terrain.createCrater(500f, 70f, 45f);
        float after = terrain.getHeightAt(500f);

        assertTrue(changed);
        assertTrue(after < before);
        assertTrue(after >= 52.5f);
    }

    @Test
    public void turnTransitionResetsFuelAndSwapsActivePlayer() {
        GameModel model = GameModel.create(GameMode.PLAYER_VS_PLAYER, AiDifficulty.NORMAL, TankType.VANGUARD, TankType.SCOUT);
        assertEquals(0, model.getActivePlayerIndex());

        model.moveActiveTank(1f, 0.8f);
        assertTrue(model.getPlayers()[0].getFuelRemaining() < model.getPlayers()[0].getTank().getMaxFuel());

        model.forceNextTurnForTest();

        assertEquals(1, model.getActivePlayerIndex());
        assertEquals(model.getPlayers()[1].getTank().getMaxFuel(), model.getPlayers()[1].getFuelRemaining(), 0.001f);
    }

    @Test
    public void airdropPlacementIsBetweenTanksAndReachable() {
        GameModel model = GameModel.create(GameMode.PLAYER_VS_PLAYER, AiDifficulty.NORMAL, TankType.VANGUARD, TankType.SCOUT);
        Drop drop = model.spawnFairDrop();
        float left = model.getPlayers()[0].getTank().getX();
        float right = model.getPlayers()[1].getTank().getX();

        assertTrue(drop.x > left);
        assertTrue(drop.x < right);
        assertNotNull(drop.reward);
    }

    @Test
    public void aiChoosesAimedShotTowardOpponent() {
        GameModel model = GameModel.create(GameMode.PLAYER_VS_COMPUTER, AiDifficulty.HARD, TankType.VANGUARD, TankType.TITAN);
        model.forceNextTurnForTest();
        AiStrategy strategy = new HeuristicAiStrategy(AiDifficulty.HARD);

        AiAction action = strategy.chooseAction(model);

        assertNotNull(action.weaponType);
        assertTrue(action.angleDegrees >= 5f && action.angleDegrees <= 88f);
        assertTrue(action.power >= 20f && action.power <= 100f);
        assertTrue(Math.abs(action.moveDirection) <= 1f);
    }
}
