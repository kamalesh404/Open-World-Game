package com.javacity.player;

import com.jme3.anim.AnimComposer;
import com.jme3.scene.Spatial;

public class PlayerAnimationController {
    private AnimComposer animComposer;
    private String currentAction = "";
    
    // Grace period so Jump animation has time to play before isOnGround flickers back
    private float airborneTimer = 0f;
    private static final float AIRBORNE_GRACE = 0.3f;

    public PlayerAnimationController(Spatial playerModel) {
        this.animComposer = com.javacity.utilities.AssetHelper.findAnimComposer(playerModel);
    }

    public void update(float tpf, float speed, boolean isOnGround, boolean isInVehicle) {
        if (animComposer == null) return;

        // Track airborne time
        if (!isOnGround) {
            airborneTimer += tpf;
        } else {
            airborneTimer = 0f;
        }

        String targetAction = "Idle";

        if (isInVehicle) {
            targetAction = "Drive";
        } else if (!isOnGround && airborneTimer > 0.05f) {
            // Only switch to Jump after being airborne for a brief moment
            targetAction = "Jump";
        } else if (isOnGround) {
            if (speed < 0.5f) {
                targetAction = "Idle";
            } else if (speed < 4.5f) {
                targetAction = "Walk";
            } else if (speed < 9.0f) {
                targetAction = "Run";
            } else {
                targetAction = "Sprint";
            }
        }

        if (!targetAction.equals(currentAction)) {
            if (animComposer.getAnimClip(targetAction) != null) {
                animComposer.setCurrentAction(targetAction);
                currentAction = targetAction;
            } else if (!targetAction.equals("Drive") && !targetAction.equals("Sprint")) {
                // Fallback: if clip missing, stay on current or go Idle
                if (animComposer.getAnimClip("Idle") != null && !currentAction.equals("Idle")) {
                    animComposer.setCurrentAction("Idle");
                    currentAction = "Idle";
                }
            }
        }
    }
}
