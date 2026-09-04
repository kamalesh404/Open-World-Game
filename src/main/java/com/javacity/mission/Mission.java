package com.javacity.mission;

import com.jme3.math.Vector3f;
import java.util.List;

public class Mission {
    public String name;
    public String description;
    public List<MissionObjective> objectives;
    public int currentObjectiveIndex = 0;
    public MissionState state = MissionState.NOT_STARTED;

    public Mission(String name, String description, List<MissionObjective> objectives) {
        this.name = name;
        this.description = description;
        this.objectives = objectives;
    }

    public MissionObjective getCurrentObjective() {
        if (currentObjectiveIndex >= 0 && currentObjectiveIndex < objectives.size()) {
            return objectives.get(currentObjectiveIndex);
        }
        return null;
    }

    public boolean advanceObjective() {
        MissionObjective current = getCurrentObjective();
        if (current != null) {
            current.isComplete = true;
        }
        
        currentObjectiveIndex++;
        if (currentObjectiveIndex >= objectives.size()) {
            state = MissionState.COMPLETE;
            return true;
        }
        return false;
    }

    public void update(float tpf, Vector3f playerPos, String playerState, int wantedLevel) {
        if (state != MissionState.ACTIVE && !state.name().startsWith("OBJECTIVE_")) return;

        MissionObjective obj = getCurrentObjective();
        if (obj != null && obj.checkCompletion(playerPos, playerState, wantedLevel)) {
            advanceObjective();
        }
    }
}
