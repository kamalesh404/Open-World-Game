package com.javacity.interaction;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;

public class InteractionPrompt {
    private BitmapText promptText;
    private Node guiNode;
    private int screenWidth, screenHeight;

    public InteractionPrompt(Node guiNode, BitmapFont font, int width, int height) {
        this.guiNode = guiNode;
        this.screenWidth = width;
        this.screenHeight = height;

        promptText = new BitmapText(font);
        promptText.setSize(font.getCharSet().getRenderedSize() * 1.5f);
        promptText.setColor(ColorRGBA.White);
    }

    public void show(String text) {
        promptText.setText(text);
        float textWidth = promptText.getLineWidth();
        promptText.setLocalTranslation((screenWidth - textWidth) / 2f, screenHeight * 0.2f, 0);
        
        if (promptText.getParent() == null) {
            guiNode.attachChild(promptText);
        }
    }

    public void hide() {
        if (promptText.getParent() != null) {
            promptText.removeFromParent();
        }
    }
}
