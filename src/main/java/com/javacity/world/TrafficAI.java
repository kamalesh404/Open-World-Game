package com.javacity.world;

import com.javacity.vehicles.Vehicle;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TrafficAI {
    private List<Vector3f> laneWaypoints;
    private Random random = new Random();
    private float[] vehicleTargetSpeed;
    private int[] vehicleWaypointIndex;

    public TrafficAI() {
        laneWaypoints = generateCityLanes();
    }

    private List<Vector3f> generateCityLanes() {
        List<Vector3f> lanes = new ArrayList<>();
        float spacing = 30f;
        for (float x = -200f; x <= 200f; x += spacing) {
            lanes.add(new Vector3f(x, 0.5f, -300f));
            lanes.add(new Vector3f(x, 0.5f, 300f));
        }
        for (float z = -200f; z <= 200f; z += spacing) {
            lanes.add(new Vector3f(-300f, 0.5f, z));
            lanes.add(new Vector3f(300f, 0.5f, z));
        }
        return lanes;
    }

    public void update(float tpf, List<Vehicle> vehicles, Vector3f playerPos) {
        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle v = vehicles.get(i);
            if (v.isOccupied()) continue;

            float distToPlayer = v.getPosition().distance(playerPos);
            if (distToPlayer > 120f) continue;

            float speed = Math.abs(v.getCurrentSpeed());
            float targetSpeed = 15f + random.nextFloat() * 10f;

            if (speed < targetSpeed) {
                v.accelerate(0.3f);
            } else {
                v.brake(0.15f);
            }

            for (Vehicle other : vehicles) {
                if (other == v) continue;
                float dist = v.getPosition().distance(other.getPosition());
                if (dist < 6f) {
                    v.brake(0.5f);
                    break;
                }
            }

            if (distToPlayer < 15f) {
                v.brake(0.4f);
            }
        }
    }
}
