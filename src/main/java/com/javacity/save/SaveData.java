package com.javacity.save;

public class SaveData {
    public float[] playerPosition = new float[3];
    public float playerHealth = 100f;
    public int money = 0;
    public int wantedLevel = 0;
    public String currentMission = "";
    public String missionState = "NOT_STARTED";
    public float timeOfDay = 10.0f;
    public int completedMissions = 0;
    public float[] completedMissionTimes = new float[10];
    public int bestRaceTime = 9999;
    public float sustainabilityScore = 65f;
    
    public SaveData() {
    }
}
