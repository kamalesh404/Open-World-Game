package com.javacity.ui;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;

public class PauseMenu {
    private Node guiNode;
    private Node menuNode;
    private boolean visible = false;

    public interface PauseMenuCallback {
        void onResume();
        void onSave();
        void onLoad();
        void onQuit();
    }
    
    private PauseMenuCallback callback;

    public PauseMenu(Node guiNode, com.jme3.asset.AssetManager assetManager, BitmapFont font, int width, int height, PauseMenuCallback callback) {
        this.guiNode = guiNode;
        this.callback = callback;
        
        menuNode = new Node("PauseMenu");
        
        Picture bg = new Picture("PauseBg");
        com.jme3.material.Material matBg = new com.jme3.material.Material(assetManager, "Common/MatDefs/Gui/Gui.j3md");
        matBg.setColor("Color", new ColorRGBA(0, 0, 0, 0.7f));
        bg.setMaterial(matBg);
        bg.setWidth(width);
        bg.setHeight(height);
        bg.setLocalTranslation(0, 0, 0);
        menuNode.attachChild(bg);
        
        BitmapText title = new BitmapText(font);
        title.setSize(font.getCharSet().getRenderedSize() * 3f);
        title.setText("PAUSED");
        title.setColor(ColorRGBA.White);
        title.setLocalTranslation((width - title.getLineWidth()) / 2f, height * 0.8f, 0);
        menuNode.attachChild(title);
        
        BitmapText resume = new BitmapText(font);
        resume.setSize(font.getCharSet().getRenderedSize() * 1.5f);
        resume.setText("[1] RESUME");
        resume.setLocalTranslation((width - resume.getLineWidth()) / 2f, height * 0.6f, 0);
        menuNode.attachChild(resume);
        
        BitmapText save = new BitmapText(font);
        save.setSize(font.getCharSet().getRenderedSize() * 1.5f);
        save.setText("[2] SAVE GAME");
        save.setLocalTranslation((width - save.getLineWidth()) / 2f, height * 0.5f, 0);
        menuNode.attachChild(save);
        
        BitmapText load = new BitmapText(font);
        load.setSize(font.getCharSet().getRenderedSize() * 1.5f);
        load.setText("[3] LOAD GAME");
        load.setLocalTranslation((width - load.getLineWidth()) / 2f, height * 0.4f, 0);
        menuNode.attachChild(load);
        
        BitmapText quit = new BitmapText(font);
        quit.setSize(font.getCharSet().getRenderedSize() * 1.5f);
        quit.setText("[4] QUIT");
        quit.setLocalTranslation((width - quit.getLineWidth()) / 2f, height * 0.3f, 0);
        menuNode.attachChild(quit);
    }

    public void show() {
        if (!visible) {
            guiNode.attachChild(menuNode);
            visible = true;
        }
    }

    public void hide() {
        if (visible) {
            menuNode.removeFromParent();
            visible = false;
        }
    }

    public boolean isVisible() {
        return visible;
    }
    
    public void handleInput(int key) {
        if (!visible) return;
        switch(key) {
            case 1: callback.onResume(); break;
            case 2: callback.onSave(); break;
            case 3: callback.onLoad(); break;
            case 4: callback.onQuit(); break;
        }
    }
}
