package com.javacity.ui;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;

public class MissionUI {
    private BitmapText nameText;
    private BitmapText descText;
    private BitmapText completeText;
    private int screenWidth;

    public MissionUI(Node guiNode, BitmapFont font, int width, int height) {
        this.screenWidth = width;
        
        nameText = new BitmapText(font);
        nameText.setSize(font.getCharSet().getRenderedSize() * 1.2f);
        nameText.setColor(ColorRGBA.Yellow);
        nameText.setLocalTranslation(20, height - 80, 0);
        guiNode.attachChild(nameText);
        
        descText = new BitmapText(font);
        descText.setSize(font.getCharSet().getRenderedSize());
        descText.setColor(ColorRGBA.White);
        descText.setLocalTranslation(20, height - 110, 0);
        guiNode.attachChild(descText);
        
        completeText = new BitmapText(font);
        completeText.setSize(font.getCharSet().getRenderedSize() * 2f);
        completeText.setColor(ColorRGBA.Green);
        completeText.setText("MISSION COMPLETE");
        float w = completeText.getLineWidth();
        completeText.setLocalTranslation((width - w) / 2f, height / 2f, 0);
    }

    public void update(String missionName, String objectiveText, boolean showComplete) {
        nameText.setText(missionName);
        descText.setText(objectiveText);
        
        if (showComplete) {
            if (completeText.getParent() == null) {
                nameText.getParent().attachChild(completeText);
            }
        } else {
            if (completeText.getParent() != null) {
                completeText.removeFromParent();
            }
        }
    }
}
