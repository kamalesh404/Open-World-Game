package com.javacity.vehicles;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import java.util.ArrayList;
import java.util.List;

public class VehicleSpawner {
    private List<Vector3f> spawnPoints;
    private List<Vehicle> activeVehicles;
    private int maxActiveVehicles = 35; 
    private AssetManager assetManager;
    private Node rootNode;
    private BulletAppState physicsSpace;

    public VehicleSpawner(AssetManager assetManager, Node rootNode, BulletAppState physicsSpace) {
        this.assetManager = assetManager;
        this.rootNode = rootNode;
        this.physicsSpace = physicsSpace;
        this.spawnPoints = new ArrayList<>();
        this.activeVehicles = new ArrayList<>();
    }

    public void addSpawnPoint(Vector3f position) {
        spawnPoints.add(position);
    }

    public Vehicle spawnVehicle(VehicleType type, Vector3f position, float yRotation) {
        if (activeVehicles.size() >= maxActiveVehicles) {
            return null; 
        }

        Vehicle vehicle = new Vehicle(type);
        vehicle.initialize(assetManager);
        
        Vector3f roadPos = new Vector3f(position.x, 0.5f, position.z);
        Quaternion rot = new Quaternion();
        rot.fromAngles(0, yRotation, 0);
        vehicle.getVehicleNode().setLocalTranslation(roadPos);
        vehicle.getVehicleNode().setLocalRotation(rot);
        
        vehicle.getVehicleControl().setPhysicsLocation(roadPos);
        vehicle.getVehicleControl().setPhysicsRotation(rot);

        rootNode.attachChild(vehicle.getVehicleNode());
        physicsSpace.getPhysicsSpace().add(vehicle.getVehicleControl());

        activeVehicles.add(vehicle);
        return vehicle;
    }

    public void despawnVehicle(Vehicle vehicle) {
        rootNode.detachChild(vehicle.getVehicleNode());
        physicsSpace.getPhysicsSpace().remove(vehicle.getVehicleControl());
        activeVehicles.remove(vehicle);
    }

    public Vehicle findNearestVehicle(Vector3f playerPos, float maxDist) {
        Vehicle nearest = null;
        float minDistSq = maxDist * maxDist;

        for (Vehicle vehicle : activeVehicles) {
            float distSq = vehicle.getPosition().distanceSquared(playerPos);
            if (distSq < minDistSq) {
                minDistSq = distSq;
                nearest = vehicle;
            }
        }

        return nearest;
    }

    public void update(float tpf, Vector3f playerPos) {
        float maxDistanceSq = 350f * 350f; 
        
        List<Vehicle> toDespawn = new ArrayList<>();
        for (Vehicle v : activeVehicles) {
            Vector3f pos = v.getPosition();
            
            // Handle void falls for all vehicles (occupied or unoccupied)
            if (pos.y < -0.5f) {
                Vector3f respawn = new Vector3f(pos.x, 0.5f, pos.z);
                v.getVehicleControl().setPhysicsLocation(respawn);
                v.getVehicleControl().setLinearVelocity(Vector3f.ZERO);
                v.getVehicleControl().setAngularVelocity(Vector3f.ZERO);
                v.getVehicleControl().setPhysicsRotation(com.jme3.math.Matrix3f.IDENTITY);
                if (!v.isOccupied() && !spawnPoints.isEmpty()) {
                    Vector3f sp = spawnPoints.get(new java.util.Random().nextInt(spawnPoints.size()));
                    v.getVehicleControl().setPhysicsLocation(sp);
                }
                continue;
            }
            
            if (pos.distanceSquared(playerPos) > maxDistanceSq && !v.isOccupied()) {
                toDespawn.add(v);
            }
        }
        
        for (Vehicle v : toDespawn) {
            despawnVehicle(v);
        }
        
        if (activeVehicles.size() < maxActiveVehicles && !spawnPoints.isEmpty()) {
            for (Vector3f sp : spawnPoints) {
                float distSq = sp.distanceSquared(playerPos);
                if (distSq > 30f * 30f && distSq < 200f * 200f) {
                    boolean occupied = false;
                    for (Vehicle existing : activeVehicles) {
                        if (existing.getPosition().distanceSquared(sp) < 15f * 15f) {
                            occupied = true;
                            break;
                        }
                    }
                    if (!occupied) {
                        spawnVehicle(VehicleType.CYBERPUNK, sp, new java.util.Random().nextFloat() * FastMath.TWO_PI);
                        break;
                    }
                }
            }
        }
    }

    public void spawnInitialVehicles() {
        java.util.Random rand = new java.util.Random();
        for (Vector3f sp : spawnPoints) {
            if (activeVehicles.size() >= maxActiveVehicles) break;
            boolean occupied = false;
            for (Vehicle existing : activeVehicles) {
                if (existing.getPosition().distanceSquared(sp) < 15f * 15f) {
                    occupied = true;
                    break;
                }
            }
            if (!occupied) {
                spawnVehicle(VehicleType.CYBERPUNK, sp, rand.nextFloat() * FastMath.TWO_PI);
            }
        }
    }

    public List<Vehicle> getActiveVehicles() {
        return activeVehicles;
    }
}
