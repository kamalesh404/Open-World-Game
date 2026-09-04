package com.javacity;

import com.javacity.audio.AudioManager;
import com.javacity.camera.ThirdPersonCamera;
import com.javacity.core.GameConfig;
import com.javacity.core.GameConstants;
import com.javacity.core.GameStateManager;
import com.javacity.interaction.InteractionSystem;
import com.javacity.mission.MissionManager;
import com.javacity.npc.NPCSpawner;
import com.javacity.player.Player;
import com.javacity.player.PlayerState;
import com.javacity.police.PoliceManager;
import com.javacity.police.WantedSystem;
import com.javacity.save.SaveData;
import com.javacity.save.SaveManager;
import com.javacity.simulation.SustainabilityMetrics;
import com.javacity.ui.HUD;
import com.javacity.ui.PauseMenu;
import com.javacity.utilities.DebugUtils;
import com.javacity.vehicles.Vehicle;
import com.javacity.vehicles.VehicleController;
import com.javacity.vehicles.VehicleSpawner;
import com.javacity.world.DayNightSystem;
import com.javacity.world.WorldManager;
import java.util.ArrayList;
import java.util.List;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;

public class GameApplication extends SimpleApplication implements ActionListener, PauseMenu.PauseMenuCallback {

    private BulletAppState bulletAppState;
    private GameStateManager gameStateManager;
    private boolean debugEnabled = false;

    private WorldManager worldManager;
    private Player player;
    private ThirdPersonCamera thirdPersonCamera;
    private VehicleSpawner vehicleSpawner;
    private NPCSpawner npcSpawner;
    private PoliceManager policeManager;
    private MissionManager missionManager;
    private WantedSystem wantedSystem;
    private HUD hud;
    private com.javacity.ui.MiniMap miniMap;
    private com.javacity.mission.StreetRaceManager raceManager;
    private AudioManager audioManager;
    private DayNightSystem dayNightSystem;
    private SustainabilityMetrics sustainabilityMetrics;
    private SaveManager saveManager;
    private InteractionSystem interactionSystem;
    private DebugUtils debugUtils;
    private PauseMenu pauseMenu;

    private VehicleController vehicleController;
    private com.javacity.world.TrafficAI trafficAI;
    private DirectionalLight sun;
    private AmbientLight ambient;

    private boolean[] inputState = new boolean[6]; // forward, left, backward, right, sprint, jump
    private Vehicle currentVehicle;

    public static void main(String[] args) {
        GameApplication app = new GameApplication();
        AppSettings settings = new AppSettings(true);
        settings.setTitle("JAVA CITY");
        settings.setWidth(GameConfig.DISPLAY_WIDTH);
        settings.setHeight(GameConfig.DISPLAY_HEIGHT);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // Initialize physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(debugEnabled);
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, GameConfig.PHYSICS_GRAVITY, 0));

        gameStateManager = new GameStateManager();
        gameStateManager.setState(GameStateManager.GameState.PLAYING);

        setupLighting();

        flyCam.setEnabled(false);
        inputManager.setCursorVisible(false);
        cam.setFrustumPerspective(45f, (float) cam.getWidth() / (float) cam.getHeight(), 0.1f, 4000f);

        worldManager = new WorldManager(assetManager, bulletAppState.getPhysicsSpace());
        worldManager.initialize(sun, ambient, viewPort);
        rootNode.attachChild(worldManager.getRootNode());

        player = new Player(assetManager, bulletAppState.getPhysicsSpace());
        player.setPosition(worldManager.getSpawnManager().getPlayerSpawnPoint());
        rootNode.attachChild(player.getNode());

        thirdPersonCamera = new ThirdPersonCamera(cam, worldManager.getRootNode(), inputManager);

        vehicleSpawner = new VehicleSpawner(assetManager, rootNode, bulletAppState);
        for (int i = 0; i < 10; i++) {
            vehicleSpawner.addSpawnPoint(worldManager.getSpawnManager().getRandomVehicleSpawnPoint());
        }
        vehicleSpawner.spawnInitialVehicles();

        npcSpawner = new NPCSpawner(assetManager, bulletAppState.getPhysicsSpace(), rootNode, worldManager.getSpawnManager());
        npcSpawner.spawnInitialNPCs();

        wantedSystem = new WantedSystem();

        policeManager = new PoliceManager(assetManager, bulletAppState.getPhysicsSpace(), rootNode, worldManager.getSpawnManager());
        policeManager.setVehicleSpawner(vehicleSpawner);

        missionManager = new MissionManager();

        interactionSystem = new InteractionSystem();

        com.javacity.interaction.MissionTerminal getTerminal = new com.javacity.interaction.MissionTerminal(assetManager,
            new Vector3f(600, 1.5f, 600), "PRESS [E] Start GETAWAY Mission",
            () -> {
                com.javacity.mission.Mission m = missionManager.getMissionByName("GETAWAY");
                if (m != null && m.state == com.javacity.mission.MissionState.AVAILABLE) {
                    missionManager.startMission(m);
                }
            }
        );
        interactionSystem.register(getTerminal);
        rootNode.attachChild(getTerminal.getNode());

        com.javacity.interaction.MissionTerminal dataTerminal = new com.javacity.interaction.MissionTerminal(assetManager,
            new Vector3f(6000, 1.5f, -4800), "PRESS [E] Start DATA HEIST Mission",
            () -> {
                com.javacity.mission.Mission m = missionManager.getMissionByName("DATA HEIST");
                if (m != null && m.state == com.javacity.mission.MissionState.AVAILABLE) {
                    missionManager.startMission(m);
                }
            }
        );
        interactionSystem.register(dataTerminal);
        rootNode.attachChild(dataTerminal.getNode());

        hud = new HUD(guiNode, assetManager, guiFont, settings.getWidth(), settings.getHeight());

        audioManager = new AudioManager(assetManager, rootNode);
        audioManager.init();
        audioManager.playAmbient();

        miniMap = new com.javacity.ui.MiniMap(guiNode, assetManager, settings.getWidth(), settings.getHeight());
        raceManager = new com.javacity.mission.StreetRaceManager(rootNode, assetManager, audioManager);
        raceManager.startRace();

        dayNightSystem = worldManager.getDayNightSystem();

        sustainabilityMetrics = new SustainabilityMetrics();

        saveManager = new SaveManager();

        debugUtils = new DebugUtils(guiNode, guiFont);

        pauseMenu = new PauseMenu(guiNode, assetManager, guiFont, settings.getWidth(), settings.getHeight(), this);

        vehicleController = new VehicleController();
        trafficAI = new com.javacity.world.TrafficAI();

        setupInputs();
    }

    private void setupLighting() {
        // Bright Warm Sun Light
        sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -0.8f, -0.3f).normalizeLocal());
        sun.setColor(new ColorRGBA(1.0f, 0.95f, 0.82f, 1.0f).mult(1.8f));
        rootNode.addLight(sun);

        // Vibrant Sky Ambient Fill
        ambient = new AmbientLight();
        ambient.setColor(new ColorRGBA(0.55f, 0.70f, 0.95f, 1.0f).mult(0.75f));
        rootNode.addLight(ambient);

        // Secondary Sci-Fi Rim/Bounce Light
        DirectionalLight rim = new DirectionalLight();
        rim.setDirection(new Vector3f(0.6f, -0.3f, 0.5f).normalizeLocal());
        rim.setColor(new ColorRGBA(0.8f, 0.5f, 1.0f, 1.0f).mult(0.4f));
        rootNode.addLight(rim);

        // Soft Directional Shadows (Optimized 1024 map for low GPU TGP)
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 1024, 2);
        dlsr.setLight(sun);
        dlsr.setShadowIntensity(0.25f);
        dlsr.setEdgeFilteringMode(com.jme3.shadow.EdgeFilteringMode.Bilinear);
        viewPort.addProcessor(dlsr);

        // Subtle Futuristic Bloom & Realistic Sunset Depth Fog
        try {
            com.jme3.post.FilterPostProcessor fpp = new com.jme3.post.FilterPostProcessor(assetManager);
            com.jme3.post.filters.BloomFilter bloom = new com.jme3.post.filters.BloomFilter(com.jme3.post.filters.BloomFilter.GlowMode.SceneAndObjects);
            bloom.setBloomIntensity(0.45f);
            bloom.setExposurePower(1.05f);
            bloom.setBlurScale(0.7f);
            fpp.addFilter(bloom);

            // Atmospheric sunset horizon fog for real-world visual experience
            com.jme3.post.filters.FogFilter fog = new com.jme3.post.filters.FogFilter();
            fog.setFogColor(new ColorRGBA(0.88f, 0.45f, 0.25f, 1.0f));
            fog.setFogDistance(4000f);
            fog.setFogDensity(0.00005f);
            fpp.addFilter(fog);

            viewPort.addProcessor(fpp);
        } catch (Exception ex) {
            System.err.println("PostProcessing notification: " + ex.getMessage());
        }

        // Warm Sunset Sky Color
        viewPort.setBackgroundColor(new ColorRGBA(0.92f, 0.48f, 0.22f, 1.0f));
    }

    private void setupInputs() {
        inputManager.addMapping(GameConstants.INPUT_FORWARD, new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping(GameConstants.INPUT_BACKWARD, new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping(GameConstants.INPUT_LEFT, new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping(GameConstants.INPUT_RIGHT, new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping(GameConstants.INPUT_SPRINT, new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addMapping(GameConstants.INPUT_JUMP, new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping(GameConstants.INPUT_INTERACT, new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping(GameConstants.INPUT_ENTER_VEHICLE, new KeyTrigger(KeyInput.KEY_F), new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping(GameConstants.INPUT_TOGGLE_DEBUG, new KeyTrigger(KeyInput.KEY_F3));
        inputManager.addMapping(GameConstants.INPUT_PAUSE, new KeyTrigger(KeyInput.KEY_ESCAPE));

        inputManager.addListener(this, GameConstants.INPUT_FORWARD, GameConstants.INPUT_BACKWARD,
                GameConstants.INPUT_LEFT, GameConstants.INPUT_RIGHT, GameConstants.INPUT_SPRINT,
                GameConstants.INPUT_JUMP, GameConstants.INPUT_INTERACT, GameConstants.INPUT_ENTER_VEHICLE,
                GameConstants.INPUT_TOGGLE_DEBUG, GameConstants.INPUT_PAUSE);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals(GameConstants.INPUT_FORWARD)) inputState[0] = isPressed;
        if (name.equals(GameConstants.INPUT_LEFT)) inputState[1] = isPressed;
        if (name.equals(GameConstants.INPUT_BACKWARD)) inputState[2] = isPressed;
        if (name.equals(GameConstants.INPUT_RIGHT)) inputState[3] = isPressed;
        if (name.equals(GameConstants.INPUT_SPRINT)) inputState[4] = isPressed;
        if (name.equals(GameConstants.INPUT_JUMP)) inputState[5] = isPressed;

        if (isPressed) {
            if (name.equals(GameConstants.INPUT_TOGGLE_DEBUG)) {
                debugUtils.toggle();
                bulletAppState.setDebugEnabled(!bulletAppState.isDebugEnabled());
            } else if (name.equals(GameConstants.INPUT_PAUSE)) {
                if (gameStateManager.getCurrentState() == GameStateManager.GameState.PLAYING) {
                    gameStateManager.setState(GameStateManager.GameState.PAUSED);
                    pauseMenu.show();
                    bulletAppState.setEnabled(false);
                    inputManager.setCursorVisible(true);
                } else if (gameStateManager.getCurrentState() == GameStateManager.GameState.PAUSED) {
                    onResume();
                }
            } else if (name.equals(GameConstants.INPUT_INTERACT) || name.equals(GameConstants.INPUT_ENTER_VEHICLE)) {
                if (player.getState() == PlayerState.ON_FOOT) {
                    Vehicle nearest = vehicleSpawner.findNearestVehicle(player.getPosition(), 45.0f);
                    if (nearest != null && !nearest.isOccupied()) {
                        enterVehicle(nearest);
                    } else {
                        interactionSystem.tryInteract(player);
                    }
                } else if (player.getState() == PlayerState.IN_VEHICLE) {
                    exitVehicle();
                }
            }
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (gameStateManager.getCurrentState() != GameStateManager.GameState.PLAYING) {
            return;
        }

        if (player.getState() == PlayerState.ON_FOOT) {
            player.update(tpf, cam, inputState);
            thirdPersonCamera.update(tpf, player.getPosition(), false);
            interactionSystem.update(tpf, player.getPosition());
            audioManager.stopEngineLoop();
            
            if (inputState[0] || inputState[1] || inputState[2] || inputState[3]) {
                audioManager.playFootstep();
            }
        } else if (player.getState() == PlayerState.IN_VEHICLE) {
            boolean[] vehInputState = new boolean[]{inputState[0], inputState[2], inputState[1], inputState[3], inputState[5]};
            vehicleController.update(tpf, currentVehicle, vehInputState);
            thirdPersonCamera.update(tpf, currentVehicle.getPosition(), true);
            audioManager.playEngineLoop();
        }

        Vector3f activePos = player.getState() == PlayerState.IN_VEHICLE && currentVehicle != null ? currentVehicle.getPosition() : player.getPosition();
        worldManager.update(tpf, activePos);
        trafficAI.update(tpf, vehicleSpawner.getActiveVehicles(), player.getPosition());
        npcSpawner.update(tpf, player.getPosition());
        sustainabilityMetrics.update(tpf, currentVehicle != null ? Math.abs(currentVehicle.getCurrentSpeed()) : 0f, player.getState() == PlayerState.IN_VEHICLE, wantedSystem.getWantedLevel());
        wantedSystem.update(tpf, true);
        policeManager.update(tpf, wantedSystem.getWantedLevel(), player.getPosition());
        
        if (wantedSystem.getWantedLevel() > 0) {
            audioManager.playSiren();
        } else {
            audioManager.stopSiren();
        }

        raceManager.update(tpf, activePos);

        // Collect positions for 2D MiniMap Radar Overlay
        List<Vector3f> vehPositions = new ArrayList<>();
        for (Vehicle v : vehicleSpawner.getActiveVehicles()) {
            vehPositions.add(v.getPosition());
        }

        List<Vector3f> policePositions = new ArrayList<>();
        for (Vehicle pv : policeManager.getPoliceVehicles()) {
            policePositions.add(pv.getPosition());
        }
        miniMap.update(player.getPosition(), vehPositions, raceManager.getRaceCheckpoints(), policePositions);
        
        String pState = player.getState() == PlayerState.IN_VEHICLE ? "IN_VEHICLE" : "ON_FOOT";
        missionManager.update(tpf, player.getPosition(), pState, wantedSystem.getWantedLevel());

        String missionText = "";
        if (raceManager.isRaceActive()) {
            int mins = (int)(raceManager.getRaceTimer() / 60f);
            float secs = raceManager.getRaceTimer() % 60f;
            missionText = String.format("STREET RACE CP: %d/%d | TIME: %02d:%04.1f", raceManager.getCurrentCheckpointIndex() + 1, raceManager.getTotalCheckpoints(), mins, secs);
        } else if (raceManager.isRaceCompleted()) {
            missionText = String.format("VICTORY! BEST TIME: %.1fs", raceManager.getBestRaceTime());
        } else if (missionManager.getActiveMission() != null && missionManager.getActiveMission().getCurrentObjective() != null) {
            missionText = missionManager.getActiveMission().getCurrentObjective().description;
        }

        float speed = currentVehicle != null && player.getState() == PlayerState.IN_VEHICLE ? currentVehicle.getCurrentSpeed() : -1f;
        hud.update(tpf, player.getStats().getHealth(), wantedSystem.getWantedLevel(), missionText, speed, dayNightSystem.getTimeString(), sustainabilityMetrics);

        debugUtils.update(tpf, player.getPosition(), wantedSystem.getWantedLevel(), missionManager.getActiveMission() != null ? missionManager.getActiveMission().state.name() : "NONE", npcSpawner.getActiveNPCs().size(), vehicleSpawner.getActiveVehicles().size(), timer.getFrameRate());
    }

    private void enterVehicle(Vehicle v) {
        currentVehicle = v;
        currentVehicle.setOccupied(true);
        player.setPosition(v.getPosition());
        player.hide();
        player.setState(PlayerState.IN_VEHICLE);
        audioManager.playEngineLoop();
    }

    private void exitVehicle() {
        if (currentVehicle != null) {
            currentVehicle.setOccupied(false);
            Vector3f exitPos = currentVehicle.getPosition().add(new Vector3f(2, 0, 0));
            player.setPosition(exitPos);
            currentVehicle = null;
        }
        player.show();
        player.setState(PlayerState.ON_FOOT);
        audioManager.stopEngineLoop();
    }

    @Override
    public void onSave() {
        saveGame();
    }

    private void saveGame() {
        SaveData data = new SaveData();
        Vector3f pos = player.getPosition();
        data.playerPosition = new float[]{pos.x, pos.y, pos.z};
        data.playerHealth = player.getStats().getHealth();
        data.money = player.getStats().getMoney();
        data.wantedLevel = wantedSystem.getWantedLevel();
        if (missionManager.getActiveMission() != null) {
            data.currentMission = missionManager.getActiveMission().name;
            data.missionState = missionManager.getActiveMission().state.name();
        }
        data.timeOfDay = dayNightSystem.getGameTime();
        
        saveManager.save(data, "savegame");
        onResume();
    }

    @Override
    public void onLoad() {
        loadGame();
    }

    private void loadGame() {
        SaveData data = saveManager.load("savegame");
        if (data != null) {
            player.setPosition(new Vector3f(data.playerPosition[0], data.playerPosition[1], data.playerPosition[2]));
            float diff = data.playerHealth - player.getStats().getHealth();
            if (diff > 0) player.getStats().heal(diff);
            else if (diff < 0) player.getStats().damage(-diff);
            
            wantedSystem.setWantedLevel(data.wantedLevel);
            dayNightSystem.setGameTime(data.timeOfDay);
        }
        onResume();
    }

    @Override
    public void onResume() {
        gameStateManager.setState(GameStateManager.GameState.PLAYING);
        pauseMenu.hide();
        bulletAppState.setEnabled(true);
        inputManager.setCursorVisible(false);
    }

    @Override
    public void onQuit() {
        stop();
    }
}
