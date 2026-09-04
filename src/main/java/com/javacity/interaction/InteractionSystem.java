package com.javacity.interaction;

import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class InteractionSystem {
    private List<Interactable> interactables = new ArrayList<>();
    private Interactable currentTarget = null;

    public void register(Interactable interactable) {
        if (!interactables.contains(interactable)) {
            interactables.add(interactable);
        }
    }

    public void unregister(Interactable interactable) {
        interactables.remove(interactable);
        if (currentTarget == interactable) {
            currentTarget = null;
        }
    }

    public void update(float tpf, Vector3f playerPos) {
        Interactable nearest = null;
        float minDist = Float.MAX_VALUE;

        for (Interactable inter : interactables) {
            float dist = inter.getPosition().distance(playerPos);
            if (dist <= inter.getInteractionRadius() && dist < minDist) {
                nearest = inter;
                minDist = dist;
            }
        }

        currentTarget = nearest;
    }

    public boolean tryInteract(Object player) {
        if (currentTarget != null) {
            currentTarget.interact(player);
            return true;
        }
        return false;
    }

    public String getCurrentPrompt() {
        if (currentTarget != null) {
            return currentTarget.getInteractionPrompt();
        }
        return null;
    }
}
