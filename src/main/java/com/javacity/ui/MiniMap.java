package com.javacity.ui;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import java.util.ArrayList;
import java.util.List;

public class MiniMap {
    private Node guiNode;
    private int screenWidth, screenHeight;
    private float mapSize = 150f;
    private float radarWorldRadius = 150f; // 150 meters world radius

    private Picture mapBg;
    private Picture playerDot;
    private List<Picture> markerPool = new ArrayList<>();
    private AssetManager assetManager;

    public MiniMap(Node guiNode, AssetManager assetManager, int screenWidth, int screenHeight) {
        this.guiNode = guiNode;
        this.assetManager = assetManager;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        // Radar background in top-left or bottom-left corner
        float posX = 20;
        float posY = 20;

        mapBg = new Picture("MiniMapBg");
        com.jme3.material.Material matBg = new com.jme3.material.Material(assetManager, "Common/MatDefs/Gui/Gui.j3md");
        matBg.setColor("Color", new ColorRGBA(0.05f, 0.08f, 0.12f, 0.75f));
        mapBg.setMaterial(matBg);
        mapBg.setWidth(mapSize);
        mapBg.setHeight(mapSize);
        mapBg.setLocalTranslation(posX, posY, 0);
        guiNode.attachChild(mapBg);

        // Player marker (center of radar)
        playerDot = new Picture("MiniMapPlayer");
        com.jme3.material.Material matPlayer = new com.jme3.material.Material(assetManager, "Common/MatDefs/Gui/Gui.j3md");
        matPlayer.setColor("Color", ColorRGBA.Cyan);
        playerDot.setMaterial(matPlayer);
        playerDot.setWidth(8);
        playerDot.setHeight(8);
        playerDot.setLocalTranslation(posX + mapSize / 2f - 4, posY + mapSize / 2f - 4, 1);
        guiNode.attachChild(playerDot);

        // Pre-create marker pool for vehicles, race checkpoints, and police
        for (int i = 0; i < 20; i++) {
            Picture p = new Picture("MiniMapMarker_" + i);
            com.jme3.material.Material mat = new com.jme3.material.Material(assetManager, "Common/MatDefs/Gui/Gui.j3md");
            mat.setColor("Color", ColorRGBA.Yellow);
            p.setMaterial(mat);
            p.setWidth(6);
            p.setHeight(6);
            p.setLocalTranslation(-100, -100, 1);
            guiNode.attachChild(p);
            markerPool.add(p);
        }
    }

    public void update(Vector3f playerPos, List<Vector3f> vehiclePositions, List<Vector3f> checkpointPositions, List<Vector3f> policePositions) {
        float posX = 20;
        float posY = 20;
        float centerRadarX = posX + mapSize / 2f;
        float centerRadarY = posY + mapSize / 2f;

        int markerIndex = 0;

        // Render Vehicle Markers (Cyan)
        if (vehiclePositions != null) {
            for (Vector3f vPos : vehiclePositions) {
                if (markerIndex >= markerPool.size()) break;
                float dx = vPos.x - playerPos.x;
                float dz = vPos.z - playerPos.z;
                if (dx * dx + dz * dz < radarWorldRadius * radarWorldRadius) {
                    float mapX = centerRadarX + (dx / radarWorldRadius) * (mapSize / 2f);
                    float mapY = centerRadarY + (dz / radarWorldRadius) * (mapSize / 2f);
                    Picture p = markerPool.get(markerIndex++);
                    p.getMaterial().setColor("Color", ColorRGBA.Cyan);
                    p.setLocalTranslation(mapX - 3, mapY - 3, 1);
                }
            }
        }

        // Render Race Checkpoints (Yellow / Amber)
        if (checkpointPositions != null) {
            for (Vector3f cPos : checkpointPositions) {
                if (markerIndex >= markerPool.size()) break;
                float dx = cPos.x - playerPos.x;
                float dz = cPos.z - playerPos.z;
                if (dx * dx + dz * dz < radarWorldRadius * radarWorldRadius) {
                    float mapX = centerRadarX + (dx / radarWorldRadius) * (mapSize / 2f);
                    float mapY = centerRadarY + (dz / radarWorldRadius) * (mapSize / 2f);
                    Picture p = markerPool.get(markerIndex++);
                    p.getMaterial().setColor("Color", ColorRGBA.Yellow);
                    p.setLocalTranslation(mapX - 4, mapY - 4, 2);
                }
            }
        }

        // Render Police Chasers (Red)
        if (policePositions != null) {
            for (Vector3f pPos : policePositions) {
                if (markerIndex >= markerPool.size()) break;
                float dx = pPos.x - playerPos.x;
                float dz = pPos.z - playerPos.z;
                if (dx * dx + dz * dz < radarWorldRadius * radarWorldRadius) {
                    float mapX = centerRadarX + (dx / radarWorldRadius) * (mapSize / 2f);
                    float mapY = centerRadarY + (dz / radarWorldRadius) * (mapSize / 2f);
                    Picture p = markerPool.get(markerIndex++);
                    p.getMaterial().setColor("Color", ColorRGBA.Red);
                    p.setLocalTranslation(mapX - 3, mapY - 3, 2);
                }
            }
        }

        // Hide unused pool markers
        for (int i = markerIndex; i < markerPool.size(); i++) {
            markerPool.get(i).setLocalTranslation(-100, -100, 1);
        }
    }
}
