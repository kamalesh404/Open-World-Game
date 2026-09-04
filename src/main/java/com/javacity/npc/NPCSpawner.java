package com.javacity.npc;

import com.javacity.world.SpawnManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class NPCSpawner {
    private List<NPC> activeNPCs = new ArrayList<>();
    private int maxNPCs = 20;
    private String[] civilianModels = {
        "Models/NPC/civilian_01.glb", 
        "Models/NPC/civilian_02.glb"
    };
    
    private AssetManager assetManager;
    private PhysicsSpace physicsSpace;
    private Node rootNode;
    private SpawnManager spawnManager;
    private NPCController npcController;
    private Random random = new Random();

    public NPCSpawner(AssetManager assetManager, PhysicsSpace physicsSpace, Node rootNode, SpawnManager spawnManager) {
        this.assetManager = assetManager;
        this.physicsSpace = physicsSpace;
        this.rootNode = rootNode;
        this.spawnManager = spawnManager;
        this.npcController = new NPCController();
    }
    
    public void spawnInitialNPCs() {
        for (int i = 0; i < maxNPCs; i++) {
            Vector3f pos = spawnManager.getRandomNPCSpawnPoint();
            if (pos != null) {
                spawnNPC(pos);
            }
        }
    }
    
    public NPC spawnNPC(Vector3f pos) {
        if (activeNPCs.size() >= maxNPCs) return null;
        
        String model = civilianModels[random.nextInt(civilianModels.length)];
        NPC npc = new NPC(assetManager, physicsSpace, model, pos);
        rootNode.attachChild(npc.npcNode);
        activeNPCs.add(npc);
        return npc;
    }
    
    public void despawnNPC(NPC npc) {
        npc.destroy();
        activeNPCs.remove(npc);
    }
    
    public void update(float tpf, Vector3f playerPos) {
        Iterator<NPC> iter = activeNPCs.iterator();
        while (iter.hasNext()) {
            NPC npc = iter.next();
            Vector3f pos = npc.getPosition();
            
            // If NPC fell through geometry or void, warp them back to street level
            if (pos.y < -5.0f) {
                Vector3f respawnPos = spawnManager.getRandomNPCSpawnPoint();
                if (respawnPos != null) {
                    npc.characterControl.warp(respawnPos);
                } else {
                    npc.characterControl.warp(new Vector3f(pos.x, 10.0f, pos.z));
                }
                continue;
            }
            
            // Despawn only if very far away (350+ units)
            if (pos.distance(playerPos) > 350f) {
                npc.destroy();
                iter.remove();
            } else {
                npcController.update(tpf, npc);
            }
        }
        
        if (activeNPCs.size() < maxNPCs) {
            Vector3f pos = spawnManager.getRandomNPCSpawnPoint();
            if (pos != null && pos.distance(playerPos) < 200f && pos.distance(playerPos) > 15f) {
                spawnNPC(pos);
            }
        }
    }
    
    public List<NPC> getActiveNPCs() {
        return activeNPCs;
    }
}
