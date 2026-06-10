package com.badlogic.tankstars;

import com.badlogic.gdx.utils.Json;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SaveGameService {
    public static final int SAVE_VERSION = 1;

    private final File saveDirectory;
    private final Json json;

    public SaveGameService() {
        this(defaultSaveDirectory());
    }

    public SaveGameService(File saveDirectory) {
        this.saveDirectory = saveDirectory;
        this.json = new Json();
        this.json.setUsePrototypes(false);
        if (!saveDirectory.exists()) {
            saveDirectory.mkdirs();
        }
    }

    public File getSaveDirectory() {
        return saveDirectory;
    }

    public SaveSlot save(GameModel model, String label) {
        String cleanLabel = label == null || label.trim().isEmpty() ? "Manual Save" : label.trim();
        String id = slug(cleanLabel) + "-" + System.currentTimeMillis();
        return write(model, id, cleanLabel);
    }

    public SaveSlot saveAutosave(GameModel model) {
        return write(model, "autosave", "Autosave");
    }

    public GameModel load(String id) {
        File file = fileFor(id);
        if (!file.exists()) {
            throw new IllegalArgumentException("Save slot not found: " + id);
        }
        try {
            String text = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            GameSaveData data = json.fromJson(GameSaveData.class, text);
            return fromData(data);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to read save slot: " + id, ex);
        }
    }

    public List<SaveSlot> listSlots() {
        List<SaveSlot> result = new ArrayList<SaveSlot>();
        File[] files = saveDirectory.listFiles();
        if (files == null) {
            return result;
        }
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File left, File right) {
                return Long.compare(right.lastModified(), left.lastModified());
            }
        });
        for (File file : files) {
            if (!file.getName().endsWith(".json")) {
                continue;
            }
            try {
                String text = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                GameSaveData data = json.fromJson(GameSaveData.class, text);
                result.add(toSlot(data));
            } catch (RuntimeException ignored) {
                // Corrupt slots are skipped in the list but remain on disk for inspection.
            } catch (IOException ignored) {
            }
        }
        return result;
    }

    public boolean delete(String id) {
        File file = fileFor(id);
        return file.exists() && file.delete();
    }

    private SaveSlot write(GameModel model, String id, String label) {
        GameSaveData data = toData(model, id, label);
        File file = fileFor(id);
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(json.prettyPrint(data).getBytes(StandardCharsets.UTF_8));
            out.close();
            return toSlot(data);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to write save slot: " + id, ex);
        }
    }

    private File fileFor(String id) {
        return new File(saveDirectory, slug(id) + ".json");
    }

    private GameSaveData toData(GameModel model, String id, String label) {
        GameSaveData data = new GameSaveData();
        data.version = SAVE_VERSION;
        data.id = id;
        data.label = label;
        data.updatedAt = System.currentTimeMillis();
        data.mode = model.getMode().name();
        data.aiDifficulty = model.getAiDifficulty().name();
        data.activePlayerIndex = model.getActivePlayerIndex();
        data.turnNumber = model.getTurnNumber();
        data.wind = model.getWind();
        data.turnState = model.getTurnState().name();
        data.winnerIndex = model.getWinnerIndex();
        data.terrainHeights = model.getTerrain().copyHeights();
        data.players = new PlayerData[model.getPlayers().length];
        for (int i = 0; i < model.getPlayers().length; i++) {
            data.players[i] = toPlayerData(model.getPlayers()[i]);
        }
        data.drops = new DropData[model.getDrops().size()];
        for (int i = 0; i < model.getDrops().size(); i++) {
            Drop drop = model.getDrops().get(i);
            DropData dropData = new DropData();
            dropData.x = drop.x;
            dropData.y = drop.y;
            dropData.reward = drop.reward.name();
            dropData.collected = drop.collected;
            data.drops[i] = dropData;
        }
        data.stats = new StatsData();
        data.stats.shotsFired = model.getStats().shotsFired;
        data.stats.directHits = model.getStats().directHits;
        data.stats.pickupsCollected = model.getStats().pickupsCollected;
        data.stats.damageDealt = model.getStats().damageDealt;
        Projectile projectile = model.getActiveProjectile();
        if (projectile != null && projectile.isActive()) {
            data.projectile = new ProjectileData();
            data.projectile.x = projectile.getPosition().x;
            data.projectile.y = projectile.getPosition().y;
            data.projectile.vx = projectile.getVelocity().x;
            data.projectile.vy = projectile.getVelocity().y;
            data.projectile.weaponType = projectile.getWeaponType().name();
            data.projectile.ownerIndex = projectile.getOwnerIndex();
            data.projectile.airStrike = projectile.isAirStrike();
        }
        return data;
    }

    private PlayerData toPlayerData(Player player) {
        PlayerData data = new PlayerData();
        data.name = player.getName();
        data.computerControlled = player.isComputerControlled();
        data.tankType = player.getTank().getType().name();
        data.health = player.getCurrentHealth();
        data.fuel = player.getFuelRemaining();
        data.x = player.getTank().getX();
        data.y = player.getTank().getY();
        data.rotation = player.getTank().getRotationDegrees();
        data.angle = player.getShooter().getAngleDegrees();
        data.power = player.getShooter().getPower();
        data.selectedWeapon = player.getShooter().getSelectedWeapon().name();
        data.inventory = new int[WeaponType.values().length];
        for (Map.Entry<WeaponType, Integer> entry : player.getShooter().getInventory().entrySet()) {
            data.inventory[entry.getKey().ordinal()] = entry.getValue();
        }
        return data;
    }

    private GameModel fromData(GameSaveData data) {
        if (data.version != SAVE_VERSION) {
            throw new IllegalArgumentException("Unsupported save version: " + data.version);
        }
        Terrain terrain = new Terrain();
        terrain.setHeights(GameModel.WORLD_WIDTH, 105f, 285f, data.terrainHeights);
        Player[] players = new Player[data.players.length];
        for (int i = 0; i < data.players.length; i++) {
            players[i] = fromPlayerData(data.players[i]);
        }
        MatchStats stats = new MatchStats();
        if (data.stats != null) {
            stats.shotsFired = data.stats.shotsFired;
            stats.directHits = data.stats.directHits;
            stats.pickupsCollected = data.stats.pickupsCollected;
            stats.damageDealt = data.stats.damageDealt == null ? new float[] {0f, 0f} : data.stats.damageDealt;
        }
        List<Drop> drops = new ArrayList<Drop>();
        if (data.drops != null) {
            for (DropData dropData : data.drops) {
                Drop drop = new Drop(dropData.x, dropData.y, WeaponType.valueOf(dropData.reward));
                drop.collected = dropData.collected;
                drops.add(drop);
            }
        }
        Projectile projectile = null;
        if (data.projectile != null) {
            projectile = new Projectile(new Vec2(data.projectile.x, data.projectile.y),
                    new Vec2(data.projectile.vx, data.projectile.vy),
                    WeaponType.valueOf(data.projectile.weaponType), data.projectile.ownerIndex,
                    data.projectile.airStrike);
        }
        GameModel model = new GameModel();
        model.restore(GameMode.valueOf(data.mode), AiDifficulty.valueOf(data.aiDifficulty), terrain, players,
                data.activePlayerIndex, data.turnNumber, data.wind, TurnState.valueOf(data.turnState), stats,
                drops, projectile, data.winnerIndex);
        return model;
    }

    private Player fromPlayerData(PlayerData data) {
        Player player = new Player(data.name, TankFactory.create(TankType.valueOf(data.tankType)), data.computerControlled);
        player.getTank().setX(data.x);
        player.getTank().setY(data.y);
        player.getTank().setRotationDegrees(data.rotation);
        player.setCurrentHealth(data.health);
        player.setFuelRemaining(data.fuel);
        player.getShooter().setAngleDegrees(data.angle);
        player.getShooter().setPower(data.power);
        if (data.inventory != null) {
            for (WeaponType type : WeaponType.values()) {
                int target = data.inventory.length > type.ordinal() ? data.inventory[type.ordinal()] : 0;
                int delta = target - player.getShooter().getAmmo(type);
                player.getShooter().addAmmo(type, delta);
            }
        }
        player.getShooter().selectWeapon(WeaponType.valueOf(data.selectedWeapon));
        return player;
    }

    private SaveSlot toSlot(GameSaveData data) {
        SaveSlot slot = new SaveSlot();
        slot.id = data.id;
        slot.label = data.label + " - " + new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(new Date(data.updatedAt));
        slot.updatedAt = data.updatedAt;
        slot.mode = GameMode.valueOf(data.mode);
        slot.turnNumber = data.turnNumber;
        if (data.players != null && data.players.length >= 2) {
            slot.playerOneTank = data.players[0].tankType;
            slot.playerTwoTank = data.players[1].tankType;
            slot.playerOneHealth = data.players[0].health;
            slot.playerTwoHealth = data.players[1].health;
        }
        return slot;
    }

    private static File defaultSaveDirectory() {
        return new File(System.getProperty("user.home"), ".tank-stars/saves");
    }

    private static String slug(String value) {
        String slug = value == null ? "save" : value.toLowerCase(Locale.US).replaceAll("[^a-z0-9_-]+", "-");
        slug = slug.replaceAll("-+", "-").replaceAll("^-|-$", "");
        return slug.isEmpty() ? "save" : slug;
    }

    public static class GameSaveData {
        public int version;
        public String id;
        public String label;
        public long updatedAt;
        public String mode;
        public String aiDifficulty;
        public int activePlayerIndex;
        public int turnNumber;
        public float wind;
        public String turnState;
        public int winnerIndex;
        public float[] terrainHeights;
        public PlayerData[] players;
        public DropData[] drops;
        public ProjectileData projectile;
        public StatsData stats;
    }

    public static class PlayerData {
        public String name;
        public boolean computerControlled;
        public String tankType;
        public float health;
        public float fuel;
        public float x;
        public float y;
        public float rotation;
        public float angle;
        public float power;
        public String selectedWeapon;
        public int[] inventory;
    }

    public static class DropData {
        public float x;
        public float y;
        public String reward;
        public boolean collected;
    }

    public static class ProjectileData {
        public float x;
        public float y;
        public float vx;
        public float vy;
        public String weaponType;
        public int ownerIndex;
        public boolean airStrike;
    }

    public static class StatsData {
        public int shotsFired;
        public int directHits;
        public int pickupsCollected;
        public float[] damageDealt;
    }
}
