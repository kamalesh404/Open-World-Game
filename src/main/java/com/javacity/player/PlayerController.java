package com.javacity.player;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

public class PlayerController {
    private BetterCharacterControl characterControl;
    private float currentSpeed = 0f;
    private final float WALK_SPEED = 3.5f;
    private final float RUN_SPEED = 8.0f;
    private final float SPRINT_SPEED = 12.0f;
    private final float JUMP_FORCE = 15.0f;
    
    private Vector3f walkDirection = new Vector3f();
    private Vector3f viewDirection = new Vector3f(0, 0, 1);
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();

    public PlayerController(BetterCharacterControl characterControl) {
        this.characterControl = characterControl;
        this.characterControl.setJumpForce(new Vector3f(0, 450f, 0));
    }

    public void update(float tpf, Camera cam, boolean[] inputState) {
        boolean forward = inputState[0];
        boolean left = inputState[1];
        boolean backward = inputState[2];
        boolean right = inputState[3];
        boolean sprint = inputState[4];
        boolean jump = inputState[5];

        camDir.set(cam.getDirection()).multLocal(1, 0, 1).normalizeLocal();
        camLeft.set(cam.getLeft()).multLocal(1, 0, 1).normalizeLocal();

        walkDirection.set(0, 0, 0);

        if (forward) walkDirection.addLocal(camDir);
        if (backward) walkDirection.addLocal(camDir.negate());
        if (left) walkDirection.addLocal(camLeft);
        if (right) walkDirection.addLocal(camLeft.negate());

        if (walkDirection.lengthSquared() > 0) {
            walkDirection.normalizeLocal();
            viewDirection.set(walkDirection);
            
            // WALK by default (3.5m/s), RUN ONLY when LSHIFT sprint is held (8.0m/s)
            float targetSpeed = sprint ? RUN_SPEED : WALK_SPEED;
            currentSpeed = FastMath.interpolateLinear(tpf * 10.0f, currentSpeed, targetSpeed);
            walkDirection.multLocal(currentSpeed);
        } else {
            currentSpeed = FastMath.interpolateLinear(tpf * 15.0f, currentSpeed, 0f);
            if (currentSpeed > 0.05f) {
                walkDirection.set(viewDirection).multLocal(currentSpeed);
            } else {
                currentSpeed = 0f;
            }
        }

        characterControl.setWalkDirection(walkDirection);
        characterControl.setViewDirection(viewDirection);

        if (jump && characterControl.isOnGround()) {
            characterControl.jump();
        }
    }

    public float getCurrentSpeed() {
        return currentSpeed;
    }
}
