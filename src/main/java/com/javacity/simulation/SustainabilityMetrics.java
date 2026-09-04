package com.javacity.simulation;

public class SustainabilityMetrics {
    private float sustainabilityScore = 65f;
    private float airQuality = 70f;
    private float cleanEnergy = 55f;
    private float greenCoverage = 60f;
    private float recycling = 45f;
    private float trafficDensity = 50f;

    private float co2Level = 300f;
    private float emissionRate = 1.0f;

    public void update(float tpf, float playerSpeed, boolean inVehicle, int wantedLevel) {
        float dt = tpf / 60f;

        if (inVehicle && playerSpeed > 10f) {
            emissionRate = Math.min(5f, emissionRate + 0.1f * dt);
            co2Level = Math.min(800f, co2Level + emissionRate * dt * 10f);
            airQuality = Math.max(10f, airQuality - 0.5f * dt);
            trafficDensity = Math.min(100f, trafficDensity + 0.3f * dt);
        } else {
            emissionRate = Math.max(0.5f, emissionRate - 0.05f * dt);
            co2Level = Math.max(200f, co2Level - 0.2f * dt);
            airQuality = Math.min(100f, airQuality + 0.3f * dt);
            trafficDensity = Math.max(20f, trafficDensity - 0.2f * dt);
        }

        if (wantedLevel > 2) {
            trafficDensity = Math.min(100f, trafficDensity + 1.0f * dt);
            airQuality = Math.max(10f, airQuality - 1.0f * dt);
        }

        cleanEnergy = Math.min(100f, cleanEnergy + 0.05f * dt);
        greenCoverage = Math.min(95f, greenCoverage + 0.02f * dt);
        recycling = Math.min(100f, recycling + 0.03f * dt);

        sustainabilityScore = (airQuality * 0.3f + cleanEnergy * 0.2f + greenCoverage * 0.15f + recycling * 0.15f + (100f - trafficDensity) * 0.2f);
    }

    public float getSustainabilityScore() { return sustainabilityScore; }
    public float getAirQuality() { return airQuality; }
    public float getCleanEnergy() { return cleanEnergy; }
    public float getGreenCoverage() { return greenCoverage; }
    public float getRecycling() { return recycling; }
    public float getTrafficDensity() { return trafficDensity; }
    public float getCO2Level() { return co2Level; }
    public float getEmissionRate() { return emissionRate; }

    public String getGrade() {
        if (sustainabilityScore >= 80) return "A";
        if (sustainabilityScore >= 65) return "B";
        if (sustainabilityScore >= 50) return "C";
        if (sustainabilityScore >= 35) return "D";
        return "F";
    }
}
