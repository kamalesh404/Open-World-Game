package com.javacity.vehicles;

public enum VehicleType {
    CYBERPUNK("Models/Vehicles/cyberpunk_car.glb", 1200f, 6500f, 50f, 1.0f, 0.1f, 0.3f),
    SCI_FI("Models/Vehicles/scifi_car.glb", 1100f, 7000f, 55f, 0.8f, 0.15f, 0.25f),
    SEDAN("Models/Vehicles/car_sedan.glb", 1300f, 5500f, 45f, 0.6f, 0.1f, 0.4f),
    SUV("Models/Vehicles/car_suv.glb", 1500f, 6000f, 45f, 0.5f, 0.08f, 0.5f),
    TAXI("Models/Vehicles/taxi.glb", 1300f, 5500f, 40f, 0.4f, 0.1f, 0.45f),
    POLICE("Models/Vehicles/police_car.glb", 1400f, 7500f, 55f, 0.7f, 0.1f, 0.3f);

    private final String modelPath;
    private final float mass;
    private final float accelerationForce;
    private final float maxSpeed;
    private final float metallic;
    private final float roughness;
    private final float colorIntensity;

    VehicleType(String modelPath, float mass, float accelerationForce, float maxSpeed, 
                float metallic, float roughness, float colorIntensity) {
        this.modelPath = modelPath;
        this.mass = mass;
        this.accelerationForce = accelerationForce;
        this.maxSpeed = maxSpeed;
        this.metallic = metallic;
        this.roughness = roughness;
        this.colorIntensity = colorIntensity;
    }

    public String getModelPath() { return modelPath; }
    public float getMass() { return mass; }
    public float getAccelerationForce() { return accelerationForce; }
    public float getMaxSpeed() { return maxSpeed; }
    public float getMetallic() { return metallic; }
    public float getRoughness() { return roughness; }
    public float getColorIntensity() { return colorIntensity; }
}
