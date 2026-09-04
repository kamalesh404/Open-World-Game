package com.javacity.police;

import com.javacity.vehicles.Vehicle;
import com.javacity.vehicles.VehicleSpawner;
import com.javacity.vehicles.VehicleType;
import com.javacity.world.SpawnManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PoliceManager {
    private List<PoliceNPC> activePolice = new ArrayList<>();
    private VehicleSpawner vehicleSpawner;
    private List<Vehicle> policeVehicles = new ArrayList<>();
    private AssetManager assetManager;
    private PhysicsSpace physicsSpace;
    private Node rootNode;
    private SpawnManager spawnManager;

    private float spawnTimer = 0f;

    public PoliceManager(AssetManager assetManager, PhysicsSpace physicsSpace, Node rootNode, SpawnManager spawnManager) {
        this.assetManager = assetManager;
        this.physicsSpace = physicsSpace;
        this.rootNode = rootNode;
        this.spawnManager = spawnManager;
    }

    public void update(float tpf, int wantedLevel, Vector3f playerPos) {
        if (wantedLevel == 0) {
            if (!activePolice.isEmpty()) {
                despawnAllPolice();
            }
            return;
        }

        Iterator<PoliceNPC> iter = activePolice.iterator();
        while (iter.hasNext()) {
            PoliceNPC cop = iter.next();
            if (cop.getPosition().distance(playerPos) > 100f) {
                cop.destroy();
                iter.remove();
            } else {
                cop.updateChase(tpf, playerPos);
            }
        }

        int targetCops = Math.min(5, wantedLevel * 2);
        
        spawnTimer -= tpf;
        if (spawnTimer <= 0 && activePolice.size() < targetCops) {
            Vector3f spawnPos = playerPos.add(new Vector3f((float)Math.random()*40-20, 0, (float)Math.random()*40-20));
            spawnPos.y = 1.5f;
            PoliceNPC cop = new PoliceNPC(assetManager, physicsSpace, spawnPos);
            rootNode.attachChild(cop.npcNode);
            activePolice.add(cop);
            spawnTimer = 5f;
        }

        if (vehicleSpawner != null && wantedLevel >= 2 && policeVehicles.size() < wantedLevel) {
            Vector3f vehSpawn = playerPos.add(new Vector3f((float)Math.random()*60-30, 0, (float)Math.random()*60-30));
            Vehicle pv = vehicleSpawner.spawnVehicle(VehicleType.POLICE, vehSpawn, (float)Math.random() * FastMath.TWO_PI);
            if (pv != null) policeVehicles.add(pv);
        }

        Iterator<Vehicle> vIter = policeVehicles.iterator();
        while (vIter.hasNext()) {
            Vehicle pv = vIter.next();
            if (pv.getPosition().distance(playerPos) > 150f) {
                vehicleSpawner.despawnVehicle(pv);
                vIter.remove();
            } else {
                Vector3f dir = playerPos.subtract(pv.getPosition());
                dir.y = 0;
                if (dir.length() > 5f) {
                    dir.normalizeLocal();
                    pv.accelerate(0.7f);
                    pv.steer(dir.dot(pv.getVehicleControl().getPhysicsLocation().normalize()) > 0 ? 0.5f : -0.5f);
                }
            }
        }
    }

    public void setVehicleSpawner(VehicleSpawner vs) {
        this.vehicleSpawner = vs;
    }

    public void despawnAllPolice() {
        for (PoliceNPC cop : activePolice) {
            cop.destroy();
        }
        activePolice.clear();
        for (Vehicle pv : policeVehicles) {
            if (vehicleSpawner != null) {
                vehicleSpawner.despawnVehicle(pv);
            }
        }
        policeVehicles.clear();
    }

    public List<Vehicle> getPoliceVehicles() {
        return policeVehicles;
    }

    public Vector3f getNearestPolicePosition(Vector3f to) {
        Vector3f nearest = null;
        float minDistSq = Float.MAX_VALUE;
        for (PoliceNPC cop : activePolice) {
            float dSq = cop.getPosition().distanceSquared(to);
            if (dSq < minDistSq) {
                minDistSq = dSq;
                nearest = cop.getPosition();
            }
        }
        for (Vehicle pv : policeVehicles) {
            float dSq = pv.getPosition().distanceSquared(to);
            if (dSq < minDistSq) {
                minDistSq = dSq;
                nearest = pv.getPosition();
            }
        }
        return nearest;
    }
}
