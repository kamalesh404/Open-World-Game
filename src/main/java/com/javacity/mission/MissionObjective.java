package com.javacity.mission;

import com.jme3.math.Vector3f;

public class MissionObjective {
    public enum ObjectiveType {
        REACH_LOCATION, ENTER_VEHICLE, TALK_TO_NPC, ESCAPE_POLICE, REACH_DESTINATION, HACK_TERMINAL
    }

    public String description;
    public ObjectiveType type;
    public Vector3f targetLocation;
    public float completionRadius = 5f;
    public boolean isComplete = false;
    
    public MissionObjective(String description, ObjectiveType type, Vector3f targetLocation) {
        this.description = description;
        this.type = type;
        this.targetLocation = targetLocation;
    }
    
    public boolean checkCompletion(Vector3f playerPos, String playerState, int wantedLevel) {
        if (isComplete) return true;
        
        switch (type) {
            case REACH_LOCATION:
            case REACH_DESTINATION:
            case TALK_TO_NPC:
                if (targetLocation != null && playerPos.distance(targetLocation) <= completionRadius) {
                    return true;
                }
                break;
            case ENTER_VEHICLE:
                if ("IN_VEHICLE".equals(playerState)) {
                    return true;
                }
                break;
            case HACK_TERMINAL:
                if (targetLocation != null && playerPos.distance(targetLocation) <= completionRadius) {
                    return true;
                }
                break;
            case ESCAPE_POLICE:
                if (wantedLevel == 0) {
                    return true;
                }
                break;
        }
        return false;
    }
}
