package com.badlogic.tankstars;

import java.util.Random;

public class HeuristicAiStrategy implements AiStrategy {
    private final AiDifficulty difficulty;
    private final Random random = new Random(73L);

    public HeuristicAiStrategy(AiDifficulty difficulty) {
        this.difficulty = difficulty == null ? AiDifficulty.NORMAL : difficulty;
    }

    @Override
    public AiAction chooseAction(GameModel model) {
        int aiIndex = model.getActivePlayerIndex();
        Player ai = model.getActivePlayer();
        Player target = model.getOpponent();
        float bestScore = Float.MAX_VALUE;
        float bestAngle = 45f;
        float bestPower = 65f;
        float targetX = target.getTank().getX();
        float targetY = target.getTank().getY() + target.getTank().getHeight() * 0.55f;

        for (float angle = 12f; angle <= 82f; angle += 2f) {
            for (float power = 25f; power <= 100f; power += 3f) {
                float score = simulateShot(model, aiIndex, angle, power, targetX, targetY);
                if (score < bestScore) {
                    bestScore = score;
                    bestAngle = angle;
                    bestPower = power;
                }
            }
        }

        float noise = difficulty == AiDifficulty.EASY ? 14f : difficulty == AiDifficulty.NORMAL ? 6f : 1.5f;
        AiAction action = new AiAction();
        action.moveDirection = chooseMovement(ai, target);
        action.moveSeconds = difficulty == AiDifficulty.EASY ? 0.15f : difficulty == AiDifficulty.NORMAL ? 0.28f : 0.42f;
        action.angleDegrees = clamp(bestAngle + randomRange(-noise, noise), 5f, 88f);
        action.power = clamp(bestPower + randomRange(-noise, noise), 20f, 100f);
        action.weaponType = chooseWeapon(ai, bestScore);
        return action;
    }

    private float simulateShot(GameModel model, int shooterIndex, float angleDegrees, float power,
                               float targetX, float targetY) {
        Player shooter = model.getPlayers()[shooterIndex];
        float sign = shooterIndex == 0 ? 1f : -1f;
        float speed = WeaponFactory.create(WeaponType.BASIC_SHELL).getProjectileSpeed() * (power / 100f);
        double radians = Math.toRadians(angleDegrees);
        float x = shooter.getTank().getX() + sign * shooter.getTank().getWidth() * 0.42f;
        float y = shooter.getTank().getY() + shooter.getTank().getHeight() * 0.92f;
        float vx = sign * (float) Math.cos(radians) * speed;
        float vy = (float) Math.sin(radians) * speed;
        float best = Float.MAX_VALUE;
        for (int i = 0; i < 150; i++) {
            float dt = 0.035f;
            vx += model.getWind() * dt;
            vy -= GameModel.GRAVITY * dt;
            x += vx * dt;
            y += vy * dt;
            float dx = x - targetX;
            float dy = y - targetY;
            best = Math.min(best, (float) Math.sqrt(dx * dx + dy * dy));
            if (x < 0f || x > model.getWorldWidth() || y < model.getTerrain().getHeightAt(x) || y > model.getWorldHeight()) {
                break;
            }
        }
        return best;
    }

    private float chooseMovement(Player ai, Player target) {
        float distance = Math.abs(ai.getTank().getX() - target.getTank().getX());
        if (distance > 560f) {
            return ai.getTank().getX() < target.getTank().getX() ? 1f : -1f;
        }
        if (distance < 330f) {
            return ai.getTank().getX() < target.getTank().getX() ? -1f : 1f;
        }
        return 0f;
    }

    private WeaponType chooseWeapon(Player ai, float bestScore) {
        Shooter shooter = ai.getShooter();
        if (difficulty != AiDifficulty.EASY && shooter.getAmmo(WeaponType.AIR_STRIKE) > 0 && bestScore > 45f) {
            return WeaponType.AIR_STRIKE;
        }
        if (shooter.getAmmo(WeaponType.BIG_ONE) > 0 && bestScore < 80f) {
            return WeaponType.BIG_ONE;
        }
        if (shooter.getAmmo(WeaponType.CLUSTER_SHOT) > 0 && difficulty == AiDifficulty.HARD) {
            return WeaponType.CLUSTER_SHOT;
        }
        if (shooter.getAmmo(WeaponType.REPAIR_KIT) > 0 && ai.getCurrentHealth() < ai.getTank().getMaxHealth() * 0.42f) {
            return WeaponType.REPAIR_KIT;
        }
        return WeaponType.BASIC_SHELL;
    }

    private float randomRange(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
