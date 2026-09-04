package com.javacity.ui;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import com.javacity.simulation.SustainabilityMetrics;

public class HUD {
    private Node guiNode;
    private int screenWidth, screenHeight;
    
    private Picture healthBarBg;
    private Picture healthBar;
    
    private WantedUI wantedUI;
    private MissionUI missionUI;
    
    private BitmapText speedText;
    private BitmapText timeText;
    private BitmapText promptText;
    private BitmapText sustainText;

    public HUD(Node guiNode, com.jme3.asset.AssetManager assetManager, BitmapFont font, int width, int height) {
        this.guiNode = guiNode;
        this.screenWidth = width;
        this.screenHeight = height;
        
        healthBarBg = new Picture("HealthBg");
        com.jme3.material.Material matBg = new com.jme3.material.Material(assetManager, "Common/MatDefs/Gui/Gui.j3md");
        matBg.setColor("Color", ColorRGBA.DarkGray);
        healthBarBg.setMaterial(matBg);
        healthBarBg.setWidth(200);
        healthBarBg.setHeight(20);
        healthBarBg.setLocalTranslation(20, height - 40, 0);
        guiNode.attachChild(healthBarBg);
        
        healthBar = new Picture("Health");
        com.jme3.material.Material matHealth = new com.jme3.material.Material(assetManager, "Common/MatDefs/Gui/Gui.j3md");
        matHealth.setColor("Color", ColorRGBA.Red);
        healthBar.setMaterial(matHealth);
        healthBar.setWidth(200);
        healthBar.setHeight(20);
        healthBar.setLocalTranslation(20, height - 40, 0);
        guiNode.attachChild(healthBar);
        
        wantedUI = new WantedUI(guiNode, font, width, height);
        missionUI = new MissionUI(guiNode, font, width, height);
        
        speedText = new BitmapText(font);
        speedText.setSize(font.getCharSet().getRenderedSize() * 1.2f);
        speedText.setColor(ColorRGBA.Cyan);
        speedText.setLocalTranslation(width - 180, 60, 0);
        guiNode.attachChild(speedText);
        
        timeText = new BitmapText(font);
        timeText.setSize(font.getCharSet().getRenderedSize() * 1.5f);
        timeText.setColor(ColorRGBA.Yellow);
        timeText.setLocalTranslation(width / 2f - 40, height - 20, 0);
        guiNode.attachChild(timeText);

        promptText = new BitmapText(font);
        promptText.setSize(font.getCharSet().getRenderedSize() * 1.1f);
        promptText.setColor(ColorRGBA.Cyan);
        promptText.setLocalTranslation(width / 2f - 220, 40, 0);
        guiNode.attachChild(promptText);

        sustainText = new BitmapText(font);
        sustainText.setSize(font.getCharSet().getRenderedSize() * 0.9f);
        sustainText.setColor(ColorRGBA.Green);
        sustainText.setLocalTranslation(width - 300, 80, 0);
        guiNode.attachChild(sustainText);
    }
    
    public void update(float tpf, float health, int wantedLevel, String missionText, float vehicleSpeed, String timeStr, SustainabilityMetrics metrics) {
        float hpWidth = 200 * (Math.max(0, health) / 100f);
        healthBar.setWidth(hpWidth);
        
        wantedUI.update(wantedLevel);
        
        if (missionText != null && !missionText.isEmpty()) {
            missionUI.update("MISSION", missionText, false);
        }
        
        if (vehicleSpeed >= 0) {
            speedText.setText(String.format("SPEED: %.0f KM/H", vehicleSpeed * 3.6f));
            promptText.setText("PRESS [E] / [F] EXIT  |  [W/S/A/D] DRIVE  |  [SPACE] BRAKE");
        } else {
            speedText.setText("");
            promptText.setText("PRESS [E] / [F] ENTER VEHICLE WHEN NEAR A CAR");
        }
        
        timeText.setText(timeStr);

        if (metrics != null) {
            sustainText.setText(String.format(
                "ECOCITY GRADE: %s\nAIR: %.0f | ENERGY: %.0f\nGREEN: %.0f | CO2: %.0fppm",
                metrics.getGrade(),
                metrics.getAirQuality(),
                metrics.getCleanEnergy(),
                metrics.getGreenCoverage(),
                metrics.getCO2Level()));
        }
    }
}
