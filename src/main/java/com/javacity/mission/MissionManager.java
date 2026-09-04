package com.javacity.mission;

import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;

interface OnMissionComplete {
    void onMissionComplete(Mission mission);
}

interface OnObjectiveComplete {
    void onObjectiveComplete(MissionObjective objective);
}

public class MissionManager {
    private List<Mission> missions = new ArrayList<>();
    private Mission activeMission;
    
    private OnMissionComplete missionCompleteListener;
    private OnObjectiveComplete objectiveCompleteListener;

    public MissionManager() {
        createGetawayMission();
        createDataHeistMission();
    }
    
    public void setListeners(OnMissionComplete mcl, OnObjectiveComplete ocl) {
        this.missionCompleteListener = mcl;
        this.objectiveCompleteListener = ocl;
    }

    private void createGetawayMission() {
        List<MissionObjective> objs = new ArrayList<>();
        objs.add(new MissionObjective("Talk to the contact", MissionObjective.ObjectiveType.TALK_TO_NPC, new Vector3f(600, 1.5f, 600)));
        objs.add(new MissionObjective("Get to the vehicle", MissionObjective.ObjectiveType.REACH_LOCATION, new Vector3f(1200, 1.5f, 1200)));
        objs.add(new MissionObjective("Enter the vehicle", MissionObjective.ObjectiveType.ENTER_VEHICLE, null));
        objs.add(new MissionObjective("Drive to the safehouse", MissionObjective.ObjectiveType.REACH_DESTINATION, new Vector3f(12000, 1.5f, -6000)));
        objs.add(new MissionObjective("Lose the cops", MissionObjective.ObjectiveType.ESCAPE_POLICE, null));
        
        Mission mission = new Mission("GETAWAY", "Meet the contact and make the delivery.", objs);
        mission.state = MissionState.AVAILABLE;
        missions.add(mission);
    }

    private void createDataHeistMission() {
        List<MissionObjective> objs = new ArrayList<>();
        objs.add(new MissionObjective("Locate the data terminal", MissionObjective.ObjectiveType.REACH_LOCATION, new Vector3f(6000, 1.5f, -4800)));
        objs.add(new MissionObjective("Hack the terminal", MissionObjective.ObjectiveType.HACK_TERMINAL, new Vector3f(6000, 1.5f, -4800)));
        objs.add(new MissionObjective("Reach the getaway vehicle", MissionObjective.ObjectiveType.REACH_LOCATION, new Vector3f(9000, 1.5f, -3000)));
        objs.add(new MissionObjective("Escape to the safehouse", MissionObjective.ObjectiveType.REACH_DESTINATION, new Vector3f(-3000, 1.5f, 4800)));

        Mission mission = new Mission("DATA HEIST", "Infiltrate, steal the data, and escape.", objs);
        mission.state = MissionState.AVAILABLE;
        missions.add(mission);
    }

    public Mission getMissionByName(String name) {
        for (Mission mission : missions) {
            if (mission.name.equals(name)) {
                return mission;
            }
        }
        return null;
    }

    public List<Mission> getAvailableMissions() {
        return missions;
    }

    public void startMission(Mission mission) {
        this.activeMission = mission;
        mission.state = MissionState.ACTIVE;
        mission.currentObjectiveIndex = 0;
    }

    public Mission getActiveMission() {
        return activeMission;
    }

    public void update(float tpf, Vector3f playerPos, String playerState, int wantedLevel) {
        if (activeMission != null) {
            int oldIdx = activeMission.currentObjectiveIndex;
            
            activeMission.update(tpf, playerPos, playerState, wantedLevel);
            
            if (activeMission.state == MissionState.COMPLETE) {
                if (missionCompleteListener != null) missionCompleteListener.onMissionComplete(activeMission);
                activeMission = null;
            } else if (activeMission.currentObjectiveIndex > oldIdx) {
                if (objectiveCompleteListener != null) objectiveCompleteListener.onObjectiveComplete(activeMission.objectives.get(oldIdx));
            }
        }
    }
}
