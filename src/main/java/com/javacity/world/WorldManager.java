package com.javacity.world;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.scene.Node;

public class WorldManager {
    private CityLoader cityLoader;
    private SpawnManager spawnManager;
    private DayNightSystem dayNightSystem;
    private Node rootNode;
    private AssetManager assetManager;

    public WorldManager(AssetManager assetManager, PhysicsSpace physicsSpace) {
        this.assetManager = assetManager;
        this.cityLoader = new CityLoader(assetManager, physicsSpace);
        this.spawnManager = new SpawnManager();
    }

    public void initialize(com.jme3.light.DirectionalLight sun, com.jme3.light.AmbientLight ambient, com.jme3.renderer.ViewPort viewPort) {
        rootNode = cityLoader.loadCity();
        spawnManager.setupSpawnPoints();
        dayNightSystem = new DayNightSystem(sun, ambient, viewPort, assetManager, rootNode);
        EnvironmentDecorator decorator = new EnvironmentDecorator(assetManager, rootNode);
        decorator.decorate();
    }

    public void update(float tpf, com.jme3.math.Vector3f playerPos) {
        if (dayNightSystem != null) {
            dayNightSystem.update(tpf, playerPos);
        }
    }
    
    public void update(float tpf) {
        update(tpf, com.jme3.math.Vector3f.ZERO);
    }

    public Node getRootNode() {
        return rootNode;
    }

    public SpawnManager getSpawnManager() {
        return spawnManager;
    }

    public DayNightSystem getDayNightSystem() {
        return dayNightSystem;
    }
}
