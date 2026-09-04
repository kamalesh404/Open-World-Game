package com.javacity.core;

public class GameConfig {
    // Display
    public static final int DISPLAY_WIDTH = 1920;
    public static final int DISPLAY_HEIGHT = 1080;
    public static final boolean DISPLAY_FULLSCREEN = false;
    public static final boolean DISPLAY_VSYNC = true;

    // Player
    public static final float PLAYER_WALK_SPEED = 4f;
    public static final float PLAYER_RUN_SPEED = 8f;
    public static final float PLAYER_SPRINT_SPEED = 12f;
    public static final float PLAYER_JUMP_FORCE = 400f;

    // Camera
    public static final float CAMERA_DISTANCE = 8f;
    public static final float CAMERA_MIN_DISTANCE = 3f;
    public static final float CAMERA_MAX_DISTANCE = 15f;
    public static final float CAMERA_SENSITIVITY = 2f;

    // Vehicle
    public static final float VEHICLE_MAX_SPEED = 50f;
    public static final float VEHICLE_ACCELERATION = 800f;
    public static final float VEHICLE_BRAKE_FORCE = 100f;
    public static final float VEHICLE_STEERING_SPEED = 0.5f;

    // World
    public static final int NPC_POPULATION_LIMIT = 60;
    public static final int VEHICLE_POPULATION_LIMIT = 35;
    public static final float ACTIVATION_DISTANCE = 150f;

    // Physics
    public static final float PHYSICS_GRAVITY = -29.4f;

    // DayNight
    public static final float DAY_DURATION_SECONDS = 300f;

    // Scaling Configuration
    public static final float WORLD_SCALE = 80.0f;
    public static final float PLAYER_SCALE = 0.5f;
    public static final float CAR_SCALE = 0.80f;
    public static final float NPC_SCALE = 1.05f;
}
