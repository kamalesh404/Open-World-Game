package com.javacity.mission;

import com.javacity.audio.AudioManager;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Torus;
import java.util.ArrayList;
import java.util.List;

public class StreetRaceManager {
    private Node rootNode;
    private AssetManager assetManager;
    private AudioManager audioManager;

    private List<Vector3f> raceCheckpoints = new ArrayList<>();
    private List<Geometry> checkpointRings = new ArrayList<>();
    private int currentCheckpointIndex = 0;

    private boolean raceActive = false;
    private boolean raceCompleted = false;
    private float raceTimer = 0f;
    private float bestRaceTime = 999.9f;

    public StreetRaceManager(Node rootNode, AssetManager assetManager, AudioManager audioManager) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.audioManager = audioManager;

        setupRaceTrack();
    }

    private void setupRaceTrack() {
        // 8 Spaced Street Checkpoints across the City Grid
        raceCheckpoints.add(new Vector3f(15f, 2.0f, 30f));
        raceCheckpoints.add(new Vector3f(80f, 2.0f, 30f));
        raceCheckpoints.add(new Vector3f(140f, 2.0f, 80f));
        raceCheckpoints.add(new Vector3f(140f, 2.0f, -60f));
        raceCheckpoints.add(new Vector3f(40f, 2.0f, -120f));
        raceCheckpoints.add(new Vector3f(-60f, 2.0f, -120f));
        raceCheckpoints.add(new Vector3f(-120f, 2.0f, -20f));
        raceCheckpoints.add(new Vector3f(-30f, 2.0f, 30f));

        // Create glowing neon Torus rings for each checkpoint
        for (int i = 0; i < raceCheckpoints.size(); i++) {
            Vector3f pos = raceCheckpoints.get(i);
            Torus torus = new Torus(16, 32, 0.4f, 2.5f);
            Geometry ring = new Geometry("CheckpointRing_" + i, torus);
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", i == 0 ? ColorRGBA.Yellow : new ColorRGBA(0.1f, 0.75f, 1.0f, 0.8f));
            ring.setMaterial(mat);
            ring.setLocalTranslation(pos);
            ring.setLocalRotation(new com.jme3.math.Quaternion().fromAngleAxis(com.jme3.math.FastMath.HALF_PI, com.jme3.math.Vector3f.UNIT_Y));
            rootNode.attachChild(ring);
            checkpointRings.add(ring);
        }
    }

    public void startRace() {
        raceActive = true;
        raceCompleted = false;
        currentCheckpointIndex = 0;
        raceTimer = 0f;
        if (audioManager != null) audioManager.playMissionStart();
    }

    public void update(float tpf, Vector3f vehiclePos) {
        if (!raceActive || raceCompleted) return;

        raceTimer += tpf;

        // Check collision with current active checkpoint ring
        if (currentCheckpointIndex < raceCheckpoints.size()) {
            Vector3f cpPos = raceCheckpoints.get(currentCheckpointIndex);
            if (vehiclePos.distanceSquared(cpPos) < 6.0f * 6.0f) {
                // Passed Checkpoint!
                checkpointRings.get(currentCheckpointIndex).getMaterial().setColor("Color", ColorRGBA.DarkGray);
                currentCheckpointIndex++;
                if (audioManager != null) audioManager.playMissionComplete();

                if (currentCheckpointIndex < raceCheckpoints.size()) {
                    checkpointRings.get(currentCheckpointIndex).getMaterial().setColor("Color", ColorRGBA.Yellow);
                } else {
                    // Race Finished!
                    raceCompleted = true;
                    raceActive = false;
                    if (raceTimer < bestRaceTime) {
                        bestRaceTime = raceTimer;
                    }
                }
            }
        }
    }

    public boolean isRaceActive() { return raceActive; }
    public boolean isRaceCompleted() { return raceCompleted; }
    public int getCurrentCheckpointIndex() { return currentCheckpointIndex; }
    public int getTotalCheckpoints() { return raceCheckpoints.size(); }
    public float getRaceTimer() { return raceTimer; }
    public float getBestRaceTime() { return bestRaceTime; }
    public List<Vector3f> getRaceCheckpoints() { return raceCheckpoints; }
}
