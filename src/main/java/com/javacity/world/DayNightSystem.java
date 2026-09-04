package com.javacity.world;

import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class DayNightSystem {
    private float gameTime = 18.2f; // 18:12 Sunset time
    private float speedMultiplier = 0.4f; // Relaxed realistic day-night speed 
    
    private DirectionalLight sun;
    private AmbientLight ambient;
    private com.jme3.renderer.ViewPort viewPort;
    private RealisticSky realisticSky;

    public DayNightSystem(DirectionalLight sun, AmbientLight ambient, com.jme3.renderer.ViewPort viewPort, com.jme3.asset.AssetManager assetManager, Node rootNode) {
        this.sun = sun;
        this.ambient = ambient;
        this.viewPort = viewPort;
        this.realisticSky = new RealisticSky(assetManager);
        rootNode.attachChild(realisticSky.getSkyNode());
    }

    public void update(float tpf, Vector3f playerPos) {
        gameTime += (tpf * speedMultiplier) / 60.0f;
        if (gameTime >= 24.0f) {
            gameTime -= 24.0f;
        }

        // Fixed Dramatic Golden-Orange Sunset Sky Theme
        ColorRGBA skyColor = new ColorRGBA(0.92f, 0.48f, 0.22f, 1.0f); // Warm Crimson Sunset
        if (sun != null) {
            sun.setDirection(new Vector3f(-0.85f, -0.22f, -0.35f).normalizeLocal());
            sun.setColor(new ColorRGBA(1.0f, 0.65f, 0.25f, 1.0f).mult(1.8f)); // Glowing Sun
        }
        if (ambient != null) {
            ambient.setColor(new ColorRGBA(0.55f, 0.42f, 0.48f, 1.0f)); // Sunset Fill Light
        }
        if (viewPort != null) {
            viewPort.setBackgroundColor(skyColor);
        }
        if (realisticSky != null && playerPos != null) {
            realisticSky.update(tpf, playerPos);
        }
    }
    
    public void update(float tpf) {
        update(tpf, Vector3f.ZERO);
    }

    public boolean isNight() {
        return gameTime > 20.0f || gameTime < 6.0f;
    }

    public float getGameTime() {
        return gameTime;
    }
    
    public void setGameTime(float time) {
        this.gameTime = time;
    }

    public String getTimeString() {
        int hours = (int) gameTime;
        int minutes = (int) ((gameTime - hours) * 60);
        return String.format("%02d:%02d", hours, minutes);
    }
}
