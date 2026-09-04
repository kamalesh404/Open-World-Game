package com.javacity.world;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpawnManager {
    private List<Vector3f> npcSpawnPoints = new ArrayList<>();
    private List<Vector3f> vehicleSpawnPoints = new ArrayList<>();
    private List<Float> vehicleSpawnRotations = new ArrayList<>();
    private Vector3f playerSpawnPoint;
    private Vector3f missionNPCSpawnPoint;
    
    private Random random = new Random();

    public void setupSpawnPoints() {
        playerSpawnPoint = new Vector3f(0, 10.0f, 0);
        missionNPCSpawnPoint = new Vector3f(10, 10.0f, 10);
        
        // NPCs clustered across player district
        for (int i = 0; i < 25; i++) {
            npcSpawnPoints.add(new Vector3f(
                (random.nextFloat() - 0.5f) * 600f,
                10.0f,
                (random.nextFloat() - 0.5f) * 600f
            ));
        }
        
        // Vehicles spawned across city roads
        for (int i = 0; i < 20; i++) {
            float x = (random.nextFloat() - 0.5f) * 800f;
            float z = (random.nextFloat() - 0.5f) * 800f;
            vehicleSpawnPoints.add(new Vector3f(x, 0.5f, z));
            vehicleSpawnRotations.add(random.nextFloat() * FastMath.TWO_PI);
        }

        float[][] fixedPoints = {
            {20f, 30f}, {-40f, 60f}, {50f, -30f}, {-20f, -50f},
            {80f, 10f}, {-60f, -20f}, {30f, -80f}, {-90f, 40f},
            {70f, 70f}, {-10f, 90f}, {110f, -60f}, {-80f, -90f},
            {40f, 120f}, {120f, 50f}, {-120f, -40f}
        };
        for (float[] pt : fixedPoints) {
            vehicleSpawnPoints.add(new Vector3f(pt[0], 0.5f, pt[1]));
            vehicleSpawnRotations.add(random.nextFloat() * FastMath.TWO_PI);
        }
    }
    
    public Vector3f getPlayerSpawnPoint() {
        return playerSpawnPoint;
    }
    
    public Vector3f getMissionNPCSpawnPoint() {
        return missionNPCSpawnPoint;
    }
    
    public Vector3f getRandomNPCSpawnPoint() {
        if (npcSpawnPoints.isEmpty()) return new Vector3f();
        return npcSpawnPoints.get(random.nextInt(npcSpawnPoints.size()));
    }
    
    public Vector3f getRandomVehicleSpawnPoint() {
        if (vehicleSpawnPoints.isEmpty()) return new Vector3f();
        return vehicleSpawnPoints.get(random.nextInt(vehicleSpawnPoints.size()));
    }
    
    public float getVehicleRotation(Vector3f spawnPoint) {
        int idx = vehicleSpawnPoints.indexOf(spawnPoint);
        if (idx >= 0 && idx < vehicleSpawnRotations.size()) {
            return vehicleSpawnRotations.get(idx);
        }
        return 0f;
    }
}
