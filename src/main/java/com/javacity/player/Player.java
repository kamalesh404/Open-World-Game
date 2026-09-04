package com.javacity.player;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.Armature;
import com.jme3.anim.SkinningControl;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Player {
    private Node playerNode;
    private Spatial playerModel;
    private BetterCharacterControl characterControl;
    private PlayerStats stats;
    private PlayerState state;
    private PlayerController controller;
    private PlayerAnimationController animationController;
    private PhysicsSpace physicsSpace;

    public Player(AssetManager assetManager, PhysicsSpace physicsSpace) {
        this.physicsSpace = physicsSpace;
        this.stats = new PlayerStats();
        this.state = PlayerState.ON_FOOT;
        
        this.playerNode = new Node("PlayerNode");
        
        try {
            this.playerModel = assetManager.loadModel(com.javacity.core.GameConstants.PATH_PLAYER_MODEL);
        } catch (Exception e) {
            System.err.println("Player model load failed: " + e.getMessage());
            e.printStackTrace();
            com.jme3.scene.shape.Box b = new com.jme3.scene.shape.Box(0.4f, 0.9f, 0.4f);
            com.jme3.scene.Geometry geom = new com.jme3.scene.Geometry("Player_Placeholder", b);
            com.jme3.material.Material mat = new com.jme3.material.Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
            mat.setColor("BaseColor", com.jme3.math.ColorRGBA.Red);
            geom.setMaterial(mat);
            geom.setLocalTranslation(0, 0.9f, 0);
            this.playerModel = geom;
        }
        
        // Dynamic Animation Merging for Mixamo GLB models
        AnimComposer mainComposer = findAnimComposer(this.playerModel);
        SkinningControl mainSkinControl = findSkinningControl(this.playerModel);
        Armature targetArmature = (mainSkinControl != null) ? mainSkinControl.getArmature() : null;
        
        if (mainComposer != null) {
            System.out.println("Loading custom player model with armature. Joints count: " + (targetArmature != null ? targetArmature.getJointCount() : 0));
            System.out.println("--- MAIN PLAYER CLIPS ---");
            for (com.jme3.anim.AnimClip c : mainComposer.getAnimClips()) {
                System.out.println("Main Clip: name='" + c.getName() + "', length=" + c.getLength() + "s, tracks=" + (c.getTracks() != null ? c.getTracks().length : 0));
            }
            com.jme3.anim.AnimClip walkClip = findMixamoClip(mainComposer);
            if (walkClip != null) {
                com.jme3.anim.AnimClip walkClipCopy = cloneClip(walkClip, "Walk", targetArmature);
                mainComposer.addAnimClip(walkClipCopy);
                System.out.println("Mapped Walk clip to " + walkClip.getName());
            }
            
            // Load Idle animation
            try {
                Spatial idleSpatial = assetManager.loadModel("Models/Player/player_idle.glb");
                AnimComposer idleComposer = findAnimComposer(idleSpatial);
                if (idleComposer != null) {
                    System.out.println("--- IDLE CLIPS ---");
                    for (com.jme3.anim.AnimClip c : idleComposer.getAnimClips()) {
                        System.out.println("Idle Clip: name='" + c.getName() + "', length=" + c.getLength() + "s, tracks=" + (c.getTracks() != null ? c.getTracks().length : 0));
                    }
                    com.jme3.anim.AnimClip idleClip = findMixamoClip(idleComposer);
                    if (idleClip != null) {
                        com.jme3.anim.AnimClip idleClipCopy = cloneClip(idleClip, "Idle", targetArmature);
                        mainComposer.addAnimClip(idleClipCopy);
                        System.out.println("Mapped Idle clip to " + idleClip.getName());
                    }
                }
            } catch (Exception ex) {
                System.err.println("Could not load idle animation: " + ex.getMessage());
            }
            
            // Load Run animation
            try {
                Spatial runSpatial = assetManager.loadModel("Models/Player/player_run.glb");
                AnimComposer runComposer = findAnimComposer(runSpatial);
                if (runComposer != null) {
                    System.out.println("--- RUN CLIPS ---");
                    for (com.jme3.anim.AnimClip c : runComposer.getAnimClips()) {
                        System.out.println("Run Clip: name='" + c.getName() + "', length=" + c.getLength() + "s, tracks=" + (c.getTracks() != null ? c.getTracks().length : 0));
                    }
                    com.jme3.anim.AnimClip runClip = findMixamoClip(runComposer);
                    if (runClip != null) {
                        com.jme3.anim.AnimClip runClipCopy = cloneClip(runClip, "Run", targetArmature);
                        com.jme3.anim.AnimClip sprintClip = cloneClip(runClip, "Sprint", targetArmature);
                        mainComposer.addAnimClip(runClipCopy);
                        mainComposer.addAnimClip(sprintClip);
                    }
                }
            } catch (Exception ex) {
                System.err.println("Could not load run animation: " + ex.getMessage());
            }
            
            // Load Jump animation
            try {
                Spatial jumpSpatial = assetManager.loadModel("Models/Player/player_jump.glb");
                AnimComposer jumpComposer = findAnimComposer(jumpSpatial);
                if (jumpComposer != null) {
                    System.out.println("--- JUMP CLIPS ---");
                    for (com.jme3.anim.AnimClip c : jumpComposer.getAnimClips()) {
                        System.out.println("Jump Clip: name='" + c.getName() + "', length=" + c.getLength() + "s, tracks=" + (c.getTracks() != null ? c.getTracks().length : 0));
                    }
                    com.jme3.anim.AnimClip jumpClip = findMixamoClip(jumpComposer);
                    if (jumpClip != null) {
                        com.jme3.anim.AnimClip jumpClipCopy = cloneClip(jumpClip, "Jump", targetArmature);
                        mainComposer.addAnimClip(jumpClipCopy);
                    }
                }
            } catch (Exception ex) {
                System.err.println("Could not load jump animation: " + ex.getMessage());
            }
        }
        
        this.playerModel.setLocalScale(com.javacity.core.GameConfig.PLAYER_SCALE);
        this.playerNode.attachChild(this.playerModel);
        
        // Disable hardware skinning to prevent T-pose on certain GPUs/Intel drivers (Commented out to use hardware skinning)
        // com.javacity.utilities.AssetHelper.disableHardwareSkinning(this.playerModel);
        
        float capRadius = 0.45f;
        float capHeight = 1.75f;
        this.characterControl = new BetterCharacterControl(capRadius, capHeight, 90f);
        this.playerNode.addControl(this.characterControl);
        this.physicsSpace.add(this.characterControl);
        
        this.controller = new PlayerController(this.characterControl);
        this.animationController = new PlayerAnimationController(this.playerModel);
    }

    public void update(float tpf, com.jme3.renderer.Camera cam, boolean[] inputState) {
        if (state == PlayerState.ON_FOOT) {
            controller.update(tpf, cam, inputState);
            float speed = controller.getCurrentSpeed();
            boolean onGround = characterControl.isOnGround();
            animationController.update(tpf, speed, onGround, false);
        } else if (state == PlayerState.IN_VEHICLE) {
            animationController.update(tpf, 0f, true, true);
        } else if (state == PlayerState.DEAD) {
            animationController.update(tpf, 0f, true, false);
        }
    }

    public Vector3f getPosition() {
        return playerNode.getLocalTranslation();
    }

    public void setPosition(Vector3f position) {
        characterControl.warp(position);
    }

    public Node getNode() {
        return playerNode;
    }

    public BetterCharacterControl getCharacterControl() {
        return characterControl;
    }

    public PlayerStats getStats() {
        return stats;
    }

    public PlayerState getState() {
        return state;
    }

    public void setState(PlayerState state) {
        this.state = state;
    }
    
    public void show() {
        playerNode.setCullHint(Spatial.CullHint.Inherit);
        physicsSpace.add(characterControl);
    }
    
    public void hide() {
        playerNode.setCullHint(Spatial.CullHint.Always);
        physicsSpace.remove(characterControl);
    }

    private AnimComposer findAnimComposer(Spatial spatial) {
        if (spatial.getControl(AnimComposer.class) != null) {
            return spatial.getControl(AnimComposer.class);
        }
        if (spatial instanceof Node) {
            Node node = (Node) spatial;
            for (Spatial child : node.getChildren()) {
                AnimComposer composer = findAnimComposer(child);
                if (composer != null) return composer;
            }
        }
        return null;
    }

    private com.jme3.anim.AnimClip findClipByLength(AnimComposer composer, double minLen, double maxLen) {
        for (com.jme3.anim.AnimClip clip : composer.getAnimClips()) {
            double len = clip.getLength();
            if (len >= minLen && len <= maxLen) {
                return clip;
            }
        }
        return null;
    }

    private com.jme3.anim.AnimClip findClipByLengthAndSuffix(AnimComposer composer, double minLen, double maxLen, String suffix) {
        com.jme3.anim.AnimClip bestMatch = null;
        for (com.jme3.anim.AnimClip clip : composer.getAnimClips()) {
            double len = clip.getLength();
            if (len >= minLen && len <= maxLen) {
                if (clip.getName().endsWith(suffix)) {
                    return clip;
                }
                if (bestMatch == null) {
                    bestMatch = clip;
                }
            }
        }
        return bestMatch;
    }

    private com.jme3.anim.AnimClip findMixamoClip(AnimComposer composer) {
        com.jme3.anim.AnimClip bestClip = null;
        double maxSuffix = -1.0;
        
        for (com.jme3.anim.AnimClip clip : composer.getAnimClips()) {
            String name = clip.getName();
            if (name.toLowerCase().contains("mixamo") || name.toLowerCase().contains("layer0")) {
                double suffix = 0.0;
                int lastDot = name.lastIndexOf('.');
                if (lastDot != -1) {
                    try {
                        suffix = Double.parseDouble(name.substring(lastDot));
                    } catch (NumberFormatException e) {
                        suffix = 0.0;
                    }
                } else if (name.endsWith("Layer0")) {
                    suffix = 0.0;
                }
                
                if (suffix > maxSuffix) {
                    maxSuffix = suffix;
                    bestClip = clip;
                }
            }
        }
        
        if (bestClip != null) {
            System.out.println("Selected best animation clip: " + bestClip.getName() + " (duration: " + bestClip.getLength() + "s)");
            return bestClip;
        }
        if (!composer.getAnimClips().isEmpty()) {
            com.jme3.anim.AnimClip first = composer.getAnimClips().iterator().next();
            System.out.println("No matching mixamo clip name. Defaulting to first clip: " + first.getName());
            return first;
        }
        return null;
    }

    private com.jme3.anim.SkinningControl findSkinningControl(Spatial spatial) {
        if (spatial.getControl(com.jme3.anim.SkinningControl.class) != null) {
            return spatial.getControl(com.jme3.anim.SkinningControl.class);
        }
        if (spatial instanceof Node) {
            Node node = (Node) spatial;
            for (Spatial child : node.getChildren()) {
                com.jme3.anim.SkinningControl control = findSkinningControl(child);
                if (control != null) return control;
            }
        }
        return null;
    }

    private com.jme3.anim.AnimClip cloneClip(com.jme3.anim.AnimClip original, String newName, Armature targetArmature) {
        com.jme3.anim.AnimClip copy = new com.jme3.anim.AnimClip(newName);
        if (targetArmature == null) {
            copy.setTracks(original.getTracks());
            return copy;
        }
        
        com.jme3.anim.AnimTrack[] originalTracks = original.getTracks();
        com.jme3.anim.AnimTrack[] clonedTracks = new com.jme3.anim.AnimTrack[originalTracks.length];
        
        for (int i = 0; i < originalTracks.length; i++) {
            com.jme3.anim.AnimTrack track = originalTracks[i];
            if (track instanceof com.jme3.anim.TransformTrack) {
                com.jme3.anim.TransformTrack tt = (com.jme3.anim.TransformTrack) track;
                com.jme3.anim.TransformTrack clonedTrack = new com.jme3.anim.TransformTrack(
                    null, // set below
                    tt.getTimes(),
                    tt.getTranslations(),
                    tt.getRotations(),
                    tt.getScales()
                );
                
                com.jme3.anim.util.HasLocalTransform oldTarget = tt.getTarget();
                if (oldTarget instanceof com.jme3.anim.Joint) {
                    com.jme3.anim.Joint oldJoint = (com.jme3.anim.Joint) oldTarget;
                    com.jme3.anim.Joint newJoint = findMatchingJoint(targetArmature, oldJoint.getName());
                    if (newJoint != null) {
                        clonedTrack.setTarget(newJoint);
                    } else {
                        System.err.println("WARNING: Could not find match for joint " + oldJoint.getName());
                        clonedTrack.setTarget(oldTarget);
                    }
                } else {
                    clonedTrack.setTarget(oldTarget);
                }
                clonedTracks[i] = clonedTrack;
            } else {
                clonedTracks[i] = track;
            }
        }
        copy.setTracks(clonedTracks);
        return copy;
    }

    private com.jme3.anim.Joint findMatchingJoint(Armature armature, String oldName) {
        com.jme3.anim.Joint joint = armature.getJoint(oldName);
        if (joint != null) return joint;
        
        String normalizedOld = cleanBoneName(oldName);
        for (int i = 0; i < armature.getJointCount(); i++) {
            com.jme3.anim.Joint j = armature.getJoint(i);
            if (cleanBoneName(j.getName()).equals(normalizedOld)) {
                return j;
            }
        }
        return null;
    }

    private String cleanBoneName(String name) {
        String clean = name.toLowerCase();
        if (clean.contains(":")) {
            clean = clean.substring(clean.lastIndexOf(":") + 1);
        }
        if (clean.contains("_")) {
            clean = clean.substring(clean.lastIndexOf("_") + 1);
        }
        clean = clean.replaceAll("[^a-zA-Z0-9]", "");
        return clean;
    }
}
