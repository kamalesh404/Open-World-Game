package com.javacity.utilities;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class DebugUtils {
    private boolean debugEnabled = false;
    private BitmapText debugText;
    private Node guiNode;

    public DebugUtils(Node guiNode, BitmapFont guiFont) {
        this.guiNode = guiNode;
        debugText = new BitmapText(guiFont);
        debugText.setSize(guiFont.getCharSet().getRenderedSize());
        debugText.setColor(ColorRGBA.White);
        debugText.setLocalTranslation(10, 200, 0);
    }

    public void toggle() {
        debugEnabled = !debugEnabled;
        if (debugEnabled) {
            guiNode.attachChild(debugText);
        } else {
            guiNode.detachChild(debugText);
        }
    }

    public void update(float tpf, Vector3f playerPos, int wantedLevel, String missionState, int npcCount, int vehicleCount, float fps) {
        if (!debugEnabled) return;
        
        String text = String.format(
            "FPS: %.1f\nPlayer: X:%.1f Y:%.1f Z:%.1f\nWanted: %d\nMission: %s\nNPCs: %d\nVehicles: %d",
            fps,
            playerPos != null ? playerPos.x : 0,
            playerPos != null ? playerPos.y : 0,
            playerPos != null ? playerPos.z : 0,
            wantedLevel,
            missionState != null ? missionState : "NONE",
            npcCount,
            vehicleCount
        );
        debugText.setText(text);
    }
}
