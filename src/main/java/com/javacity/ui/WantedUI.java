package com.javacity.ui;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;

public class WantedUI {
    private BitmapText wantedText;

    public WantedUI(Node guiNode, BitmapFont font, int width, int height) {
        wantedText = new BitmapText(font);
        wantedText.setSize(font.getCharSet().getRenderedSize() * 2f);
        wantedText.setColor(ColorRGBA.Yellow);
        wantedText.setLocalTranslation(width - 150, height - 30, 0);
        guiNode.attachChild(wantedText);
    }

    public void update(int wantedLevel) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            if (i < wantedLevel) {
                sb.append("*"); // Star filled equivalent for default font
            } else {
                sb.append("-"); // Star empty
            }
        }
        wantedText.setText(sb.toString());
    }
}
