package com.javacity.interaction;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

public class MissionTerminal implements Interactable {
    private Node terminalNode;
    private Vector3f position;
    private String prompt;
    private boolean activated = false;
    private Runnable onInteract;

    public MissionTerminal(AssetManager assetManager, Vector3f position, String prompt, Runnable onInteract) {
        this.position = position;
        this.prompt = prompt;
        this.onInteract = onInteract;
        this.terminalNode = new Node("MissionTerminal");
        
        Box box = new Box(0.8f, 1.2f, 0.3f);
        com.jme3.scene.Geometry geo = new com.jme3.scene.Geometry("Terminal", box);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
        mat.setColor("BaseColor", new ColorRGBA(0.0f, 0.8f, 1.0f, 1.0f));
        mat.setFloat("Metallic", 0.8f);
        mat.setFloat("Roughness", 0.1f);
        geo.setMaterial(mat);
        terminalNode.attachChild(geo);
        terminalNode.setLocalTranslation(position);
    }

    @Override
    public String getInteractionPrompt() { return prompt; }

    @Override
    public void interact(Object player) {
        if (!activated && onInteract != null) {
            activated = true;
            onInteract.run();
        }
    }

    @Override
    public Vector3f getPosition() { return position; }

    @Override
    public float getInteractionRadius() { return 4.0f; }

    public Node getNode() { return terminalNode; }
    public boolean isActivated() { return activated; }
    public void reset() { activated = false; }
}
