package com.javacity.npc;

import com.javacity.utilities.MathUtils;
import com.jme3.math.Vector3f;
import java.util.List;
import java.util.Random;

public class NPCController {
    private Random random = new Random();

    public void update(float tpf, NPC npc) {
        npc.update(tpf);

        switch (npc.currentState) {
            case IDLE:
                handleIdle(tpf, npc);
                break;
            case WALKING:
                handleWalking(tpf, npc);
                break;
            case FLEEING:
                handleFleeing(tpf, npc);
                break;
            case TALKING:
                handleTalking(tpf, npc);
                break;
            case RUNNING:
                handleRunning(tpf, npc);
                break;
            default:
                break;
        }
    }

    private void handleIdle(float tpf, NPC npc) {
        npc.idleTimer -= tpf;
        if (npc.idleTimer <= 0) {
            float chance = random.nextFloat();
            if (chance < 0.15f && npc.canTalk) {
                npc.currentState = NPCState.TALKING;
                npc.talkTimer = 2f + random.nextFloat() * 4f;
                playAnim(npc, "Idle");
                return;
            }
            Vector3f dest = MathUtils.randomPointInRadius(npc.getPosition(), 40f);
            npc.setDestination(dest);
            npc.currentState = NPCState.WALKING;
            playAnim(npc, "Walk");
            npc.walkTimer = 20f + random.nextFloat() * 30f;
        }
        npc.characterControl.setWalkDirection(Vector3f.ZERO);
    }

    private void handleWalking(float tpf, NPC npc) {
        npc.walkTimer -= tpf;
        Vector3f pos = npc.getPosition();
        Vector3f dir = npc.currentDestination.subtract(pos);
        dir.y = 0;

        if (dir.length() < 1.5f || npc.walkTimer <= 0) {
            if (random.nextFloat() < 0.3f) {
                npc.currentState = NPCState.IDLE;
                npc.idleTimer = 1f + random.nextFloat() * 5f;
                playAnim(npc, "Idle");
                return;
            }
            Vector3f newDest = MathUtils.randomPointInRadius(pos, 40f);
            npc.setDestination(newDest);
            npc.walkTimer = 15f + random.nextFloat() * 25f;
            playAnim(npc, "Walk");
            return;
        }

        dir.normalizeLocal();

        if (npc.canTalk && random.nextFloat() < 0.001f) {
            npc.currentState = NPCState.TALKING;
            npc.talkTimer = 2f + random.nextFloat() * 3f;
            playAnim(npc, "Idle");
            npc.characterControl.setWalkDirection(Vector3f.ZERO);
            return;
        }

        npc.characterControl.setViewDirection(dir);
        npc.characterControl.setWalkDirection(dir.mult(npc.walkSpeed));
    }

    private void handleTalking(float tpf, NPC npc) {
        npc.talkTimer -= tpf;
        npc.characterControl.setWalkDirection(Vector3f.ZERO);
        if (npc.talkTimer <= 0) {
            npc.currentState = NPCState.IDLE;
            npc.idleTimer = 1f + random.nextFloat() * 3f;
            playAnim(npc, "Idle");
        }
    }

    private void handleRunning(float tpf, NPC npc) {
        Vector3f pos = npc.getPosition();
        Vector3f dir = pos.subtract(npc.threatPosition);
        dir.y = 0;

        if (dir.length() > 40f || npc.threatPosition.equals(Vector3f.ZERO)) {
            npc.currentState = NPCState.IDLE;
            npc.idleTimer = 2f + random.nextFloat() * 3f;
            playAnim(npc, "Idle");
            return;
        }

        if (dir.length() < 0.01f) {
            dir.set(1, 0, 0);
        }

        dir.normalizeLocal();
        npc.characterControl.setViewDirection(dir);
        npc.characterControl.setWalkDirection(dir.mult(6f));
    }

    private void handleFleeing(float tpf, NPC npc) {
        Vector3f pos = npc.getPosition();
        Vector3f fleeDir = pos.subtract(npc.currentDestination);
        fleeDir.y = 0;

        if (fleeDir.length() > 30f) {
            npc.currentState = NPCState.IDLE;
            npc.idleTimer = 0.5f;
            playAnim(npc, "Idle");
            return;
        }

        if (fleeDir.length() < 0.01f) {
            fleeDir.set(1, 0, 0);
        }

        fleeDir.normalizeLocal();
        npc.characterControl.setViewDirection(fleeDir);
        npc.characterControl.setWalkDirection(fleeDir.mult(8f));
    }

    public void triggerFlee(NPC npc, Vector3f threatPos) {
        npc.currentState = NPCState.FLEEING;
        npc.setDestination(threatPos);
        playAnim(npc, "Run");
    }

    public void triggerRun(NPC npc, Vector3f threatPos) {
        npc.currentState = NPCState.RUNNING;
        npc.threatPosition.set(threatPos);
        playAnim(npc, "Run");
    }

    public void avoidNearbyNPCs(NPC npc, List<NPC> nearbyNPCs) {
        if (npc.currentState != NPCState.WALKING) return;
        Vector3f pos = npc.getPosition();
        Vector3f avoidance = Vector3f.ZERO.clone();
        int count = 0;

        for (NPC other : nearbyNPCs) {
            if (other == npc) continue;
            Vector3f otherPos = other.getPosition();
            float dist = pos.distance(otherPos);
            if (dist < 3f && dist > 0.01f) {
                Vector3f away = pos.subtract(otherPos);
                away.y = 0;
                away.normalizeLocal();
                avoidance.addLocal(away);
                count++;
            }
        }

        if (count > 0) {
            avoidance.divideLocal(count);
            Vector3f currentDir = npc.currentDestination.subtract(pos);
            currentDir.y = 0;
            currentDir.normalizeLocal();
            Vector3f blended = currentDir.add(avoidance.mult(0.5f));
            if (blended.length() > 0.01f) {
                blended.normalizeLocal();
                Vector3f newDest = pos.add(blended.mult(10f));
                npc.setDestination(newDest);
            }
        }
    }

    private void playAnim(NPC npc, String animName) {
        if (npc.animComposer != null) {
            try {
                if (npc.animComposer.getAction(animName) != null) {
                    npc.animComposer.setCurrentAction(animName);
                } else if (npc.animComposer.getAnimClip(animName) != null) {
                    npc.animComposer.setCurrentAction(animName);
                } else if (npc.animComposer.getAction("Walk") != null) {
                    npc.animComposer.setCurrentAction("Walk");
                }
            } catch (Exception e) {}
        }
    }
}
