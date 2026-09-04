package com.javacity.vehicles;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

public class Vehicle {
    private Node vehicleNode;
    private VehicleControl vehicleControl;
    private String modelPath;
    private float maxSpeed;
    private float acceleration;
    private float brakeForce;
    private float steeringMax;
    private float health;
    private boolean isOccupied;
    private com.jme3.anim.AnimComposer activeAnimComposer;
    private VehicleType type;

    public Vehicle(VehicleType type) {
        this.type = type;
        this.modelPath = type.getModelPath();
        this.maxSpeed = type.getMaxSpeed();
        this.acceleration = type.getAccelerationForce();
        this.brakeForce = type.getMass() * 2f;
        this.steeringMax = FastMath.QUARTER_PI * 0.5f;
        this.health = 100f;
        this.isOccupied = false;
    }

    public void initialize(AssetManager assetManager) {
        Spatial model = null;
        try {
            model = assetManager.loadModel(modelPath);
        } catch (Exception e) {
            System.err.println("Vehicle model not found: " + modelPath + ". Creating placeholder.");
            Box b = new Box(1.0f, 0.5f, 2.5f);
            com.jme3.scene.Geometry geom = new com.jme3.scene.Geometry("Vehicle_Placeholder", b);
            Material mat = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
            mat.setColor("BaseColor", new ColorRGBA(0.2f, 0.2f, 0.2f, 1.0f));
            mat.setFloat("Metallic", 0.1f);
            mat.setFloat("Roughness", 0.4f);
            geom.setMaterial(mat);
            geom.setLocalTranslation(0, 0.5f, 0);
            model = geom;
        }
        float scale = com.javacity.core.GameConfig.CAR_SCALE;
        model.setLocalScale(scale);
        model.setLocalTranslation(0, 0.20f, 0);
        model.setLocalRotation(new com.jme3.math.Quaternion().fromAngles(0, FastMath.HALF_PI, 0));
        vehicleNode = new Node("VehicleNode");
        vehicleNode.attachChild(model);

        // Apply PBR materials based on vehicle type
        applyVehicleMaterials(assetManager, model);

        // Physics chassis collision shape elevated so chassis never scrapes road geometry
        BoxCollisionShape boxShape = new BoxCollisionShape(new Vector3f(1.2f, 0.40f, 2.5f));
        CompoundCollisionShape compoundShape = new CompoundCollisionShape();
        compoundShape.addChildShape(boxShape, new Vector3f(0, 0.75f, 0));

        vehicleControl = new VehicleControl(compoundShape, type.getMass());
        vehicleNode.addControl(vehicleControl);

        Spatial wheelFL = findWheel(model, "wheel_fl");
        if (wheelFL == null) wheelFL = findWheel(model, "Wheel_FL");
        Spatial wheelFR = findWheel(model, "wheel_fr");
        if (wheelFR == null) wheelFR = findWheel(model, "Wheel_FR");
        Spatial wheelRL = findWheel(model, "wheel_rl");
        if (wheelRL == null) wheelRL = findWheel(model, "Wheel_RL");
        Spatial wheelRR = findWheel(model, "wheel_rr");
        if (wheelRR == null) wheelRR = findWheel(model, "Wheel_RR");

        float suspensionRestLength = 0.25f;
        float wheelRadius = 0.42f;

        Vector3f wheelDirection = new Vector3f(0, -1, 0);
        Vector3f wheelAxle = new Vector3f(-1, 0, 0);

        vehicleControl.addWheel(wheelFL, new Vector3f(-1.3f, 0.40f, 1.8f),
                wheelDirection, wheelAxle, suspensionRestLength, wheelRadius, true);
        vehicleControl.addWheel(wheelFR, new Vector3f(1.3f, 0.40f, 1.8f),
                wheelDirection, wheelAxle, suspensionRestLength, wheelRadius, true);
        vehicleControl.addWheel(wheelRL, new Vector3f(-1.3f, 0.40f, -1.8f),
                wheelDirection, wheelAxle, suspensionRestLength, wheelRadius, false);
        vehicleControl.addWheel(wheelRR, new Vector3f(1.3f, 0.40f, -1.8f),
                wheelDirection, wheelAxle, suspensionRestLength, wheelRadius, false);

        for (int i = 0; i < 4; i++) {
            vehicleControl.setSuspensionStiffness(i, 35f);
            vehicleControl.setSuspensionCompression(i, 2.5f);
            vehicleControl.setSuspensionDamping(i, 3.5f);
            vehicleControl.setFrictionSlip(i, 4.0f);
            vehicleControl.setRollInfluence(i, 0.05f);
        }
    }

    private void applyVehicleMaterials(AssetManager assetManager, Spatial spatial) {
        if (spatial instanceof com.jme3.scene.Geometry) {
            com.jme3.scene.Geometry geo = (com.jme3.scene.Geometry) spatial;
            Material mat = geo.getMaterial();
            if (mat != null && mat.getMaterialDef().getName().contains("PBR")) {
                String matName = mat.getName() != null ? mat.getName().toLowerCase() : "";
                
                // Set base metallic/roughness from vehicle type
                mat.setFloat("Metallic", type.getMetallic());
                mat.setFloat("Roughness", type.getRoughness());
                
                float intensity = type.getColorIntensity();
                ColorRGBA tint = new ColorRGBA(intensity, intensity, intensity, 1.0f);
                mat.setColor("BaseColor", tint);
                
                // Special handling for specific materials
                if (matName.contains("glass") || matName.contains("window")) {
                    mat.setFloat("Metallic", 0.2f);
                    mat.setFloat("Roughness", 0.1f);
                    mat.setColor("BaseColor", new ColorRGBA(0.1f, 0.3f, 0.5f, 0.7f));
                } else if (matName.contains("light") || matName.contains("led") || matName.contains("neon")) {
                    mat.setFloat("Metallic", 0.0f);
                    mat.setFloat("Roughness", 0.1f);
                    mat.getAdditionalRenderState().setBlendMode(com.jme3.material.RenderState.BlendMode.Additive);
                } else if (matName.contains("tire") || matName.contains("wheel") || matName.contains("rubber")) {
                    mat.setFloat("Metallic", 0.0f);
                    mat.setFloat("Roughness", 0.9f);
                    mat.setColor("BaseColor", new ColorRGBA(0.08f, 0.08f, 0.08f, 1.0f));
                } else if (matName.contains("chrome") || matName.contains("rim") || matName.contains("metal")) {
                    mat.setFloat("Metallic", 0.9f);
                    mat.setFloat("Roughness", 0.1f);
                }
            }
        }
        
        if (spatial instanceof Node) {
            for (Spatial child : ((Node) spatial).getChildren()) {
                applyVehicleMaterials(assetManager, child);
            }
        }
    }

    public void updateVisualBodyDynamics(float tpf, float throttle, float steering) {
        Spatial m = vehicleNode.getChild(0);
        if (m == null) return;
        m.setLocalRotation(new com.jme3.math.Quaternion().fromAngles(0, FastMath.HALF_PI, 0));
    }

    public void setDrifting(boolean drifting) {
        if (drifting) {
            vehicleControl.setFrictionSlip(2, 0.8f);
            vehicleControl.setFrictionSlip(3, 0.8f);
        } else {
            vehicleControl.setFrictionSlip(2, 4.0f);
            vehicleControl.setFrictionSlip(3, 4.0f);
        }
    }

    private Spatial findWheel(Spatial root, String name) {
        if (root instanceof Node) {
            Spatial found = ((Node) root).getChild(name);
            if (found != null) {
                return found;
            }
            for (Spatial child : ((Node) root).getChildren()) {
                found = findWheel(child, name);
                if (found != null) {
                    return found;
                }
            }
        }
        return new Node(name); 
    }

    public void accelerate(float value) {
        if (vehicleControl != null) {
            vehicleControl.activate();
            if (Math.abs(value) > 0.01f) {
                vehicleControl.brake(0, 0);
                vehicleControl.brake(1, 0);
                vehicleControl.brake(2, 0);
                vehicleControl.brake(3, 0);
            }
            vehicleControl.accelerate(0, value * acceleration);
            vehicleControl.accelerate(1, value * acceleration);
            vehicleControl.accelerate(2, value * acceleration);
            vehicleControl.accelerate(3, value * acceleration);
        }
    }

    public void brake(float value) {
        if (vehicleControl != null) {
            vehicleControl.activate();
            vehicleControl.brake(0, value * brakeForce);
            vehicleControl.brake(1, value * brakeForce);
            vehicleControl.brake(2, value * brakeForce);
            vehicleControl.brake(3, value * brakeForce);
        }
    }

    public void steer(float value) {
        if (vehicleControl != null) {
            vehicleControl.activate();
            vehicleControl.steer(0, value * steeringMax);
            vehicleControl.steer(1, value * steeringMax);
        }
    }

    public void handbrake() {
        if (vehicleControl != null) {
            vehicleControl.activate();
            vehicleControl.brake(2, brakeForce * 2.5f);
            vehicleControl.brake(3, brakeForce * 2.5f);
        }
    }

    public float getCurrentSpeed() {
        return vehicleControl.getCurrentVehicleSpeedKmHour();
    }

    public Vector3f getPosition() {
        if (vehicleControl != null) {
            return vehicleControl.getPhysicsLocation();
        }
        return vehicleNode.getWorldTranslation();
    }

    public void reset() {
        vehicleControl.clearForces();
        vehicleControl.setLinearVelocity(Vector3f.ZERO);
        vehicleControl.setAngularVelocity(Vector3f.ZERO);
    }

    public Node getVehicleNode() {
        return vehicleNode;
    }

    public VehicleControl getVehicleControl() {
        return vehicleControl;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public VehicleType getType() {
        return type;
    }

    public float getHealth() {
        return health;
    }
}
