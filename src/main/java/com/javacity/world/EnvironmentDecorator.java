package com.javacity.world;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

public class EnvironmentDecorator {
    private AssetManager assetManager;
    private Node rootNode;

    public EnvironmentDecorator(AssetManager assetManager, Node rootNode) {
        this.assetManager = assetManager;
        this.rootNode = rootNode;
    }

    public void decorate() {
        // Clean environment decor - relying on high-detail city GLTF model
    }
}
