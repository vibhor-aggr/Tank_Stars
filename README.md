# Tank Stars

A desktop Tank Stars-style artillery game built in Java with LibGDX. The project implements a local turn-based tank battle with hilly terrain, projectile physics, save/load support, AI opponents, and bonus gameplay systems such as airdrops and terrain destruction.

## Features

- Local 1v1 Player vs Player mode.
- Player vs Computer mode with Easy, Normal, and Hard AI.
- Three tank types with different health, fuel, speed, mass, and damage profiles:
  - Vanguard: balanced.
  - Titan: high health, heavier, slower.
  - Scout: high fuel and speed, lower health.
- Turn-based movement with limited fuel per turn.
- Mouse and keyboard aiming with angle and power controls.
- Projectile physics with gravity and wind.
- Trajectory preview while aiming.
- Damage falloff based on blast distance.
- Knockback on impact.
- Destructible hilly terrain with crater deformation.
- Airdrops that grant special weapons.
- Weapon inventory:
  - Basic Shell
  - Big One
  - Air Strike
  - Cluster Shot
  - Repair Kit
- Pause menu with resume, save, load, main menu, and exit.
- Multiple JSON save slots with metadata.
- Autosave on pause and after completed turns.
- Game-over overlay with restart, summary save, and main menu.
- Match stats for shots, direct hits, damage, and pickups.
- Sound effects, background music, explosion feedback, camera shake, and turn banners.
- Unit tests for core gameplay systems.

## Tech Stack

- Java
- LibGDX `1.11.0`
- LWJGL3 desktop backend
- Gradle wrapper
- JUnit `4.13.2`

## Requirements

- JDK 8 or newer.
- A graphical desktop environment for running the game window.

The Gradle build targets Java 8 bytecode. If `./gradlew` is not executable on your system, run:

```bash
chmod +x ./gradlew
```

## Running the Game

From the repository root:

```bash
./gradlew :desktop:run
```

The desktop launcher starts a fixed-size `1440 x 900` game window.

## Running Tests

```bash
./gradlew :core:test
```

Useful full verification command:

```bash
./gradlew clean :core:test :desktop:compileJava
```

## Controls

Gameplay can be controlled through on-screen buttons or keyboard/mouse input.

| Action | Control |
| --- | --- |
| Move left | Left Arrow or `A` |
| Move right | Right Arrow or `D` |
| Increase angle | Up Arrow or `W` |
| Decrease angle | Down Arrow or `S` |
| Increase power | `I` |
| Decrease power | `K` |
| Fire | Space or `F` |
| Pause | `P` or Escape |
| Aim with mouse | Click or drag in the game world |
| Basic Shell | `1` |
| Big One | `2` |
| Air Strike | `3` |
| Cluster Shot | `4` |
| Repair Kit | `5` |

## Game Flow

1. Start from the main menu.
2. Choose a mode: Player vs Player or Player vs Computer.
3. If playing against the computer, choose the AI difficulty.
4. Select tanks for both sides.
5. Take turns moving, aiming, choosing weapons, and firing.
6. Use the pause menu to save, load, resume, return to the main menu, or exit.
7. The match ends when one tank's health reaches zero.

## Save Files

Save games are stored as versioned JSON files under:

```text
~/.tank-stars/saves
```

Saved state includes:

- Game mode and AI difficulty.
- Active turn and turn number.
- Wind.
- Tank types, health, fuel, exact positions, and orientation.
- Shooter angle, power, selected weapon, and inventory.
- Terrain height samples and crater changes.
- Active drops.
- Active projectile state, when applicable.
- Match statistics.

The game keeps an `autosave` slot and allows manual save slots from the pause menu.

## Project Structure

```text
.
|-- assets/                         # Images, sounds, fonts, and Scene2D skin
|-- core/
|   |-- src/com/badlogic/tankstars/  # Core game model, screens, AI, persistence
|   `-- test/com/badlogic/tankstars/ # JUnit tests
|-- desktop/
|   `-- src/com/badlogic/tankstars/  # LWJGL3 desktop launcher
|-- Class-Diagram/                  # UML class diagram images
|-- latex/                          # Generated Doxygen LaTeX documentation
|-- build.gradle
|-- settings.gradle
`-- gradlew
```

## Important Classes

- `TankStars`: LibGDX application entry point and screen coordinator.
- `DesktopLauncher`: LWJGL3 desktop launcher.
- `GameModel`: Serializable game state and main gameplay rules.
- `GameScreen`: Rendering, HUD, controls, pause overlay, and game-over UI.
- `Terrain`: Hilly ground generation and crater deformation.
- `Tank`, `VanguardTank`, `TitanTank`, `ScoutTank`: tank hierarchy.
- `Shooter`, `Weapon`, `WeaponFactory`: aiming, weapon inventory, and weapon definitions.
- `HeuristicAiStrategy`: automated computer opponent.
- `SaveGameService`: JSON save/load implementation.
- `GameModelTest`: unit tests for persistence, damage, terrain, turns, drops, and AI.

## Design Notes

The implementation uses object-oriented design throughout:

- Inheritance for tank variants.
- Factory pattern for tanks, weapons, and screens.
- Strategy pattern for computer-player behavior.
- Serializable domain model for save/load support.
- Separate rendering/UI code from gameplay model logic so the core rules can be unit tested without launching LibGDX.

## Documentation and Assignment Artifacts

The repository includes the original project requirement and rubric PDFs:

- `Project Requirements Doc_ AP Tank Stars Game 2022.pdf`
- `Tank Stars_ Rubric - Deadline 1.pdf`
- `Tank Stars_ Rubric - Deadline 2.pdf`

It also includes UML and generated documentation artifacts under `Class-Diagram/`, `UML-Diagram.PNG`, `Doxyfile`, and `latex/`.

## Known Limitations

- The game is a local desktop game only. It does not include online multiplayer or mobile packaging.
- Running the desktop game requires a machine with a graphical display.
- Save files are local to the current operating-system user.
