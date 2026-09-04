package com.javacity.police;

import com.javacity.npc.NPC;
import com.javacity.npc.NPCState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;

public class PoliceNPC extends NPC {

    public boolean sirenActive = true;
    public boolean arresting = false;

    public PoliceNPC(AssetManager assetManager, PhysicsSpace physicsSpace, Vector3f spawnPos) {
        super(assetManager, physicsSpace, "Models/NPC/police.glb", spawnPos);
        this.currentState = NPCState.CHASE;
        this.sirenActive = true;
    }

    public void updateChase(float tpf, Vector3f playerPos) {
        if (currentState != NPCState.CHASE) return;

        Vector3f dir = playerPos.subtract(getPosition());
        dir.y = 0;

        if (dir.length() < 2f) {
            characterControl.setWalkDirection(Vector3f.ZERO);
            arresting = true;
            sirenActive = true;
        } else {
            arresting = false;
            dir.normalizeLocal();
            characterControl.setViewDirection(dir);
            characterControl.setWalkDirection(dir.mult(14f));

            if (animComposer != null) {
                try {
                    if (animComposer.getAnimClips().contains("Run")) {
                        animComposer.setCurrentAction("Run");
                    }
                } catch(Exception e) {}
            }
        }
    }
}
