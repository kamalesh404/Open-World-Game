package com.javacity.vehicles;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

public class VehicleController {

    private float currentSteering = 0f;

    public void update(float tpf, Vehicle vehicle, boolean[] inputState) {
        if (vehicle == null || vehicle.getVehicleControl() == null) return;

        boolean wPressed = inputState[0];
        boolean sPressed = inputState[1];
        boolean aPressed = inputState[2];
        boolean dPressed = inputState[3];
        boolean spacePressed = inputState[4];

        vehicle.getVehicleControl().activate(); // Keep physics body active

        float currentSpeed = Math.abs(vehicle.getCurrentSpeed());
        float throttleInput = 0f;
        if (wPressed) {
            throttleInput = 1.0f;
            vehicle.accelerate(1.0f);
            
            // Controlled smooth forward drive boost
            if (currentSpeed < vehicle.getMaxSpeed()) {
                Vector3f fwd = vehicle.getVehicleControl().getPhysicsRotation().getRotationColumn(2);
                vehicle.getVehicleControl().applyCentralForce(fwd.mult(3500f));
            }
        } else if (sPressed) {
            throttleInput = -0.5f;
            vehicle.accelerate(-0.5f);
            
            if (currentSpeed < vehicle.getMaxSpeed() * 0.5f) {
                Vector3f fwd = vehicle.getVehicleControl().getPhysicsRotation().getRotationColumn(2);
                vehicle.getVehicleControl().applyCentralForce(fwd.mult(-2500f));
            }
        } else {
            vehicle.accelerate(0f);
            vehicle.brake(150f);
        }

        if (spacePressed) {
            vehicle.handbrake();
        }

        float steerSpeed = 4.5f;
        if (aPressed) {
            currentSteering += steerSpeed * tpf;
        } else if (dPressed) {
            currentSteering -= steerSpeed * tpf;
        } else {
            if (currentSteering > 0) {
                currentSteering -= steerSpeed * tpf * 5.0f;
                if (currentSteering < 0) currentSteering = 0;
            } else if (currentSteering < 0) {
                currentSteering += steerSpeed * tpf * 5.0f;
                if (currentSteering > 0) currentSteering = 0;
            }
        }
        
        currentSteering = FastMath.clamp(currentSteering, -1.0f, 1.0f);
        vehicle.steer(currentSteering);
        vehicle.updateVisualBodyDynamics(tpf, throttleInput, currentSteering);
    }
}
