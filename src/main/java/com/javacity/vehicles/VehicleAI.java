package com.javacity.vehicles;

import com.jme3.math.Vector3f;

public class VehicleAI {
    public enum State {
        PARKED,
        DRIVING_SLOW,
        PURSUING
    }

    private State currentState;
    private VehicleController controller;

    public VehicleAI() {
        this.currentState = State.PARKED;
        this.controller = new VehicleController();
    }

    public void setState(State state) {
        this.currentState = state;
    }

    public void update(float tpf, Vehicle vehicle, Vector3f targetPos) {
        if (vehicle == null || vehicle.isOccupied()) {
            return; 
        }

        boolean[] inputState = new boolean[5]; 

        switch (currentState) {
            case PARKED:
                inputState[4] = true; 
                break;
                
            case DRIVING_SLOW:
                inputState[0] = true;
                if (vehicle.getCurrentSpeed() > vehicle.getMaxSpeed() * 0.3f) {
                    inputState[0] = false; 
                }
                break;
                
            case PURSUING:
                if (targetPos != null) {
                    Vector3f dirToTarget = targetPos.subtract(vehicle.getPosition()).normalizeLocal();
                    Vector3f vehicleForward = vehicle.getVehicleNode().getLocalRotation().getRotationColumn(2);
                    
                    float dotForward = vehicleForward.dot(dirToTarget);
                    
                    Vector3f right = vehicle.getVehicleNode().getLocalRotation().getRotationColumn(0);
                    float dotRight = right.dot(dirToTarget);

                    inputState[0] = true; 
                    
                    if (dotRight > 0.1f) {
                        inputState[3] = true; 
                    } else if (dotRight < -0.1f) {
                        inputState[2] = true; 
                    }
                    
                    if (dotForward < 0) { 
                        inputState[0] = false;
                        inputState[1] = true; 
                    }
                }
                break;
        }

        controller.update(tpf, vehicle, inputState);
    }
}
