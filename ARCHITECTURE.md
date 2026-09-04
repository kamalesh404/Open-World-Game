# Architecture Overview

## System Overview
```text
[Main App] -> [AppStates]
  -> WorldState (Loads environment)
  -> PlayerState (Manages character)
  -> VehicleState (Manages cars)
  -> NPCState (Manages AI)
```

## Packages
- `core`: Base classes and game loop
- `player`: Player logic and input
- `vehicle`: Vehicle control and physics components
- `npc`: AI components, state machines
- `world`: Environment setup and model loading
- `ui`: Heads-up display, menus
- `mission`: Objectives and event tracking

## Game Loop
jMonkeyEngine handles the main loop via `simpleUpdate(float tpf)`. Each AppState updates independently. Game state checks run in deterministic intervals where possible.

## State Machine
Player states: IDLE -> WALKING -> RUNNING -> IN_VEHICLE
Vehicle states: PARKED -> DRIVING -> DAMAGED -> DESTROYED

## Physics Architecture
Uses Minie (`com.github.stephengold:Minie` 9.0.3).
- Player/NPCs: BetterCharacterControl
- Vehicles: VehicleControl

## Asset Pipeline
GLB format for all 3D models, loaded via jMonkeyEngine AssetManager. Textures use PBR (Physically Based Rendering) exclusively.
