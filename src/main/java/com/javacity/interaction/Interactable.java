package com.javacity.interaction;

import com.jme3.math.Vector3f;

public interface Interactable {
    String getInteractionPrompt();
    void interact(Object player);
    Vector3f getPosition();
    float getInteractionRadius();
}
