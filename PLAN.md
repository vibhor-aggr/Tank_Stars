# Tank Stars Full Rebuild Plan

## Success Criteria
- Runnable LibGDX desktop game with loading, main menu, mode select, tank select, save/load, game, pause, and game-over screens.
- Turn-based PvP and PvC gameplay with selectable tanks, health/fuel HUD, angle/power controls, projectile physics, knockback, destructible terrain, airdrops, special weapons, wind, trajectory preview, autosaves, and match stats.
- Multiple versioned JSON save slots preserving health, exact tank positions, turn state, terrain samples, tank choices, inventories, drops, mode, AI difficulty, wind, and stats.
- Modular OOP implementation with inheritance, factories, strategy classes, serializable model types, and focused unit tests.
- Verification through Gradle compile and test tasks using a workspace-local JDK.

## Implementation Steps
1. Configure Gradle, source sets, launcher, `.gitignore`, and local verification prerequisites.
2. Implement serializable domain model: game state, players, tanks, shooter, terrain, weapons, drops, stats, save slots, AI.
3. Implement LibGDX UI/screens and rendering: menu flows, controls, terrain/tanks/projectiles/effects, save/load UI.
4. Add tests for persistence, damage falloff, terrain mutation, turn transitions, airdrops, and AI.
5. Run `:core:test`, `:core:compileJava`, `:desktop:compileJava`, and launch the desktop app if the display environment permits.

## Risks
- The host PATH currently has Java runtime only and no `javac`; verification needs a workspace-local JDK.
- This is a broad rebuild. The implementation prioritizes a complete, maintainable game over preserving generated documentation or old compiled artifacts.
