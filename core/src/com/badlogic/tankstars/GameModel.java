package com.badlogic.tankstars;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameModel implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final float WORLD_WIDTH = 1440f;
    public static final float WORLD_HEIGHT = 900f;
    public static final float GRAVITY = 330f;

    private float worldWidth = WORLD_WIDTH;
    private float worldHeight = WORLD_HEIGHT;
    private GameMode mode;
    private AiDifficulty aiDifficulty;
    private Terrain terrain;
    private Player[] players;
    private int activePlayerIndex;
    private int turnNumber;
    private float wind;
    private TurnState turnState;
    private Projectile activeProjectile;
    private final List<Drop> drops = new ArrayList<Drop>();
    private MatchStats stats = new MatchStats();
    private int winnerIndex = -1;
    private final Random random = new Random(2022L);

    public static GameModel create(GameMode mode, AiDifficulty aiDifficulty, TankType playerOneTank, TankType playerTwoTank) {
        GameModel model = new GameModel();
        model.mode = mode == null ? GameMode.PLAYER_VS_PLAYER : mode;
        model.aiDifficulty = aiDifficulty == null ? AiDifficulty.NORMAL : aiDifficulty;
        model.terrain = new Terrain(WORLD_WIDTH, 240, 105f, 285f, System.nanoTime());
        model.players = new Player[] {
                new Player("Player 1", TankFactory.create(playerOneTank), false),
                new Player(model.mode == GameMode.PLAYER_VS_COMPUTER ? "Computer" : "Player 2",
                        TankFactory.create(playerTwoTank), model.mode == GameMode.PLAYER_VS_COMPUTER)
        };
        model.players[0].getTank().setX(165f);
        model.players[1].getTank().setX(WORLD_WIDTH - 165f);
        model.updateTankOnTerrain(model.players[0].getTank());
        model.updateTankOnTerrain(model.players[1].getTank());
        model.activePlayerIndex = 0;
        model.turnNumber = 1;
        model.turnState = TurnState.AIMING;
        model.wind = model.randomWind();
        model.seedStartingWeapons();
        return model;
    }

    private void seedStartingWeapons() {
        players[0].getShooter().addAmmo(WeaponType.BIG_ONE, 1);
        players[1].getShooter().addAmmo(WeaponType.BIG_ONE, 1);
        players[0].getShooter().addAmmo(WeaponType.REPAIR_KIT, 1);
        players[1].getShooter().addAmmo(WeaponType.REPAIR_KIT, 1);
    }

    public float getWorldWidth() {
        return worldWidth;
    }

    public float getWorldHeight() {
        return worldHeight;
    }

    public GameMode getMode() {
        return mode;
    }

    public AiDifficulty getAiDifficulty() {
        return aiDifficulty;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public Player[] getPlayers() {
        return players;
    }

    public Player getActivePlayer() {
        return players[activePlayerIndex];
    }

    public Player getOpponent() {
        return players[1 - activePlayerIndex];
    }

    public int getActivePlayerIndex() {
        return activePlayerIndex;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public float getWind() {
        return wind;
    }

    public void setWind(float wind) {
        this.wind = clamp(wind, -70f, 70f);
    }

    public TurnState getTurnState() {
        return turnState;
    }

    public Projectile getActiveProjectile() {
        return activeProjectile;
    }

    public List<Drop> getDrops() {
        return Collections.unmodifiableList(drops);
    }

    public MatchStats getStats() {
        return stats;
    }

    public int getWinnerIndex() {
        return winnerIndex;
    }

    public boolean isGameOver() {
        return turnState == TurnState.GAME_OVER;
    }

    public void setAim(float angleDegrees, float power) {
        getActivePlayer().getShooter().setAngleDegrees(angleDegrees);
        getActivePlayer().getShooter().setPower(power);
    }

    public boolean selectWeapon(WeaponType type) {
        Shooter shooter = getActivePlayer().getShooter();
        if (shooter.getAmmo(type) <= 0) {
            return false;
        }
        shooter.selectWeapon(type);
        return true;
    }

    public boolean moveActiveTank(float direction, float delta) {
        if (turnState != TurnState.AIMING || Math.abs(direction) < 0.01f) {
            return false;
        }
        Player player = getActivePlayer();
        Tank tank = player.getTank();
        if (player.getFuelRemaining() <= 0f) {
            return false;
        }
        float distance = direction * tank.getMoveSpeed() * delta;
        float fuelCost = Math.abs(distance) * 0.62f;
        if (fuelCost > player.getFuelRemaining()) {
            distance *= player.getFuelRemaining() / fuelCost;
            fuelCost = player.getFuelRemaining();
        }
        float halfWidth = tank.getWidth() * 0.5f;
        float nextX = terrain.clampX(tank.getX() + distance, halfWidth);
        Tank other = getOpponent().getTank();
        if (activePlayerIndex == 0) {
            nextX = Math.min(nextX, other.getX() - other.getWidth() * 0.65f - halfWidth);
        } else {
            nextX = Math.max(nextX, other.getX() + other.getWidth() * 0.65f + halfWidth);
        }
        tank.setX(terrain.clampX(nextX, halfWidth));
        updateTankOnTerrain(tank);
        player.setFuelRemaining(player.getFuelRemaining() - fuelCost);
        collectDrops(player);
        return true;
    }

    public boolean fireSelectedWeapon() {
        if (turnState != TurnState.AIMING || isGameOver()) {
            return false;
        }
        Player player = getActivePlayer();
        Shooter shooter = player.getShooter();
        WeaponType type = shooter.getSelectedWeapon();
        if (type == WeaponType.REPAIR_KIT) {
            if (!shooter.consumeSelectedWeapon()) {
                return false;
            }
            player.heal(35f);
            stats.shotsFired++;
            nextTurn();
            return true;
        }
        if (!shooter.consumeSelectedWeapon()) {
            return false;
        }
        Weapon weapon = WeaponFactory.create(type);
        float sign = activePlayerIndex == 0 ? 1f : -1f;
        double radians = Math.toRadians(shooter.getAngleDegrees());
        float startX = player.getTank().getX() + sign * player.getTank().getWidth() * 0.45f;
        float startY = player.getTank().getY() + player.getTank().getHeight() * 0.92f;
        if (type == WeaponType.AIR_STRIKE) {
            float targetX = clamp(player.getTank().getX() + sign * shooter.getPower() * 6.5f, 45f, worldWidth - 45f);
            activeProjectile = new Projectile(new Vec2(targetX, worldHeight - 24f), new Vec2(wind * 0.22f, -weapon.getProjectileSpeed() - 360f),
                    type, activePlayerIndex, true);
        } else {
            float speed = weapon.getProjectileSpeed() * (shooter.getPower() / 100f);
            activeProjectile = new Projectile(new Vec2(startX, startY),
                    new Vec2(sign * (float) Math.cos(radians) * speed, (float) Math.sin(radians) * speed),
                    type, activePlayerIndex, false);
        }
        stats.shotsFired++;
        turnState = TurnState.PROJECTILE_IN_FLIGHT;
        return true;
    }

    public void update(float delta) {
        if (turnState == TurnState.PROJECTILE_IN_FLIGHT && activeProjectile != null && activeProjectile.isActive()) {
            activeProjectile.update(delta, GRAVITY, wind);
            Vec2 pos = activeProjectile.getPosition();
            if (pos.x < -70f || pos.x > worldWidth + 70f || pos.y < 0f || pos.y > worldHeight + 150f) {
                activeProjectile.deactivate();
                nextTurn();
            } else if (hitsTank(pos) || pos.y <= terrain.getHeightAt(pos.x)) {
                resolveImpact(pos.x, Math.max(pos.y, terrain.getHeightAt(pos.x)), activeProjectile.getWeaponType(), activeProjectile.getOwnerIndex());
                activeProjectile.deactivate();
                if (!isGameOver()) {
                    nextTurn();
                }
            }
        }
        updateDrops(delta);
    }

    public List<Vec2> getTrajectoryPreview(int steps) {
        List<Vec2> result = new ArrayList<Vec2>();
        if (turnState != TurnState.AIMING || getActivePlayer().getShooter().getSelectedWeapon() == WeaponType.REPAIR_KIT) {
            return result;
        }
        Player player = getActivePlayer();
        Shooter shooter = player.getShooter();
        Weapon weapon = WeaponFactory.create(shooter.getSelectedWeapon());
        float sign = activePlayerIndex == 0 ? 1f : -1f;
        double radians = Math.toRadians(shooter.getAngleDegrees());
        float speed = Math.max(280f, weapon.getProjectileSpeed()) * (shooter.getPower() / 100f);
        float x = player.getTank().getX() + sign * player.getTank().getWidth() * 0.45f;
        float y = player.getTank().getY() + player.getTank().getHeight() * 0.92f;
        float vx = sign * (float) Math.cos(radians) * speed;
        float vy = (float) Math.sin(radians) * speed;
        for (int i = 0; i < steps; i++) {
            float dt = 0.08f;
            vx += wind * dt;
            vy -= GRAVITY * dt;
            x += vx * dt;
            y += vy * dt;
            if (x < 0f || x > worldWidth || y < terrain.getHeightAt(x)) {
                break;
            }
            result.add(new Vec2(x, y));
        }
        return result;
    }

    public Drop spawnFairDrop() {
        float left = Math.min(players[0].getTank().getX(), players[1].getTank().getX());
        float right = Math.max(players[0].getTank().getX(), players[1].getTank().getX());
        float center = (left + right) * 0.5f;
        float range = Math.min(240f, Math.max(90f, (right - left) * 0.24f));
        float x = clamp(center + randomRange(-range, range), left + 80f, right - 80f);
        WeaponType[] rewards = {WeaponType.BIG_ONE, WeaponType.AIR_STRIKE, WeaponType.CLUSTER_SHOT, WeaponType.REPAIR_KIT};
        Drop drop = new Drop(x, worldHeight - 46f, rewards[random.nextInt(rewards.length)]);
        drops.add(drop);
        return drop;
    }

    public float computeDamageForDistance(WeaponType type, float distance) {
        Weapon weapon = WeaponFactory.create(type);
        if (distance > weapon.getBlastRadius()) {
            return 0f;
        }
        float factor = 1f - distance / weapon.getBlastRadius();
        return weapon.getBaseDamage() * factor;
    }

    public void forceNextTurnForTest() {
        nextTurn();
    }

    public void restore(GameMode mode, AiDifficulty aiDifficulty, Terrain terrain, Player[] players, int activePlayerIndex,
                        int turnNumber, float wind, TurnState turnState, MatchStats stats, List<Drop> drops,
                        Projectile projectile, int winnerIndex) {
        this.mode = mode;
        this.aiDifficulty = aiDifficulty;
        this.terrain = terrain;
        this.players = players;
        this.activePlayerIndex = activePlayerIndex;
        this.turnNumber = turnNumber;
        this.wind = wind;
        this.turnState = turnState;
        this.stats = stats == null ? new MatchStats() : stats;
        this.drops.clear();
        if (drops != null) {
            this.drops.addAll(drops);
        }
        this.activeProjectile = projectile;
        this.winnerIndex = winnerIndex;
        updateTankOnTerrain(players[0].getTank());
        updateTankOnTerrain(players[1].getTank());
    }

    private boolean hitsTank(Vec2 pos) {
        for (Player player : players) {
            Tank tank = player.getTank();
            float centerY = tank.getY() + tank.getHeight() * 0.48f;
            if (pos.distanceTo(tank.getX(), centerY) <= tank.getCollisionRadius()) {
                return true;
            }
        }
        return false;
    }

    private void resolveImpact(float x, float y, WeaponType type, int ownerIndex) {
        applySingleImpact(x, y, type, ownerIndex, 1f);
        if (type == WeaponType.CLUSTER_SHOT) {
            applySingleImpact(x - 48f, terrain.getHeightAt(x - 48f), type, ownerIndex, 0.55f);
            applySingleImpact(x + 48f, terrain.getHeightAt(x + 48f), type, ownerIndex, 0.55f);
        }
        updateTankOnTerrain(players[0].getTank());
        updateTankOnTerrain(players[1].getTank());
        if (!players[0].isAlive() || !players[1].isAlive()) {
            winnerIndex = players[0].isAlive() ? 0 : 1;
            turnState = TurnState.GAME_OVER;
        }
    }

    private void applySingleImpact(float x, float y, WeaponType type, int ownerIndex, float scale) {
        Weapon weapon = WeaponFactory.create(type);
        if (weapon.destroysTerrain()) {
            terrain.createCrater(x, weapon.getCraterRadius() * scale, weapon.getCraterRadius() * 0.48f * scale);
        }
        boolean directHitRecorded = false;
        for (int i = 0; i < players.length; i++) {
            Player player = players[i];
            Tank tank = player.getTank();
            float centerY = tank.getY() + tank.getHeight() * 0.52f;
            float dx = tank.getX() - x;
            float dy = centerY - y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            float damage = computeDamageForDistance(type, distance) * scale * tank.getDamageTakenMultiplier();
            if (damage > 0f) {
                player.applyDamage(damage);
                stats.recordDamage(ownerIndex, i == ownerIndex ? 0f : damage);
                float direction = Math.abs(dx) < 0.01f ? (ownerIndex == 0 ? 1f : -1f) : Math.signum(dx);
                float push = (1f - Math.min(1f, distance / weapon.getBlastRadius())) * weapon.getKnockback() / tank.getMass() * scale;
                tank.setX(terrain.clampX(tank.getX() + direction * push, tank.getWidth() * 0.5f));
                if (i != ownerIndex && distance <= tank.getCollisionRadius() && !directHitRecorded) {
                    stats.directHits++;
                    directHitRecorded = true;
                }
            }
        }
    }

    private void nextTurn() {
        if (isGameOver()) {
            return;
        }
        activeProjectile = null;
        activePlayerIndex = 1 - activePlayerIndex;
        turnNumber++;
        players[activePlayerIndex].resetFuel();
        wind = randomWind();
        turnState = TurnState.AIMING;
        if (turnNumber % 3 == 0 && drops.size() < 2) {
            spawnFairDrop();
        }
    }

    private void updateDrops(float delta) {
        for (Drop drop : drops) {
            if (drop.collected) {
                continue;
            }
            float groundY = terrain.getHeightAt(drop.x) + 38f;
            if (drop.y > groundY) {
                drop.y = Math.max(groundY, drop.y - 92f * delta);
            }
        }
        collectDrops(players[0]);
        collectDrops(players[1]);
    }

    private void collectDrops(Player player) {
        Tank tank = player.getTank();
        for (Drop drop : drops) {
            if (!drop.collected && Math.abs(drop.x - tank.getX()) <= tank.getWidth() * 0.75f
                    && Math.abs(drop.y - (tank.getY() + tank.getHeight())) <= 72f) {
                drop.collected = true;
                player.getShooter().addAmmo(drop.reward, drop.reward == WeaponType.REPAIR_KIT ? 1 : 2);
                stats.pickupsCollected++;
            }
        }
    }

    private void updateTankOnTerrain(Tank tank) {
        tank.setY(terrain.getHeightAt(tank.getX()));
        tank.setRotationDegrees((float) Math.toDegrees(terrain.getSlopeAt(tank.getX())));
    }

    private float randomWind() {
        return randomRange(-42f, 42f);
    }

    private float randomRange(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
