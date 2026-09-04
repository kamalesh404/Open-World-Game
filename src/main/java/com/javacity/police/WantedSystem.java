package com.javacity.police;

interface OnWantedLevelChanged {
    void onWantedLevelChanged(int newLevel);
}

public class WantedSystem {
    private int wantedLevel = 0;
    private float cooldownTimer = 0f;
    private float cooldownDuration = 30f;
    private OnWantedLevelChanged listener;

    public void setListener(OnWantedLevelChanged listener) {
        this.listener = listener;
    }

    public void increaseWantedLevel(int amount) {
        int old = wantedLevel;
        wantedLevel = Math.min(5, wantedLevel + amount);
        cooldownTimer = 0f;
        if (wantedLevel != old && listener != null) {
            listener.onWantedLevelChanged(wantedLevel);
        }
    }

    public void decreaseWantedLevel() {
        int old = wantedLevel;
        wantedLevel = Math.max(0, wantedLevel - 1);
        cooldownTimer = 0f;
        if (wantedLevel != old && listener != null) {
            listener.onWantedLevelChanged(wantedLevel);
        }
    }
    
    public void setWantedLevel(int level) {
        wantedLevel = Math.min(5, Math.max(0, level));
        cooldownTimer = 0f;
        if (listener != null) {
            listener.onWantedLevelChanged(wantedLevel);
        }
    }

    public boolean isWanted() {
        return wantedLevel > 0;
    }

    public int getWantedLevel() {
        return wantedLevel;
    }

    public void update(float tpf, boolean isPlayerVisible) {
        if (!isWanted()) return;

        if (!isPlayerVisible) {
            cooldownTimer += tpf;
            if (cooldownTimer >= cooldownDuration) {
                decreaseWantedLevel();
            }
        } else {
            cooldownTimer = 0f;
        }
    }
}
