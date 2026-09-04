package com.javacity.core;

public class GameStateManager {
    public enum GameState {
        MENU, PLAYING, PAUSED, MISSION_COMPLETE
    }

    private GameState currentState = GameState.MENU;

    public void setState(GameState newState) {
        if (this.currentState == newState) {
            return;
        }
        this.currentState = newState;
        onStateChanged(newState);
    }

    public GameState getCurrentState() {
        return currentState;
    }

    private void onStateChanged(GameState newState) {
        switch (newState) {
            case PLAYING:
                // Unpause physics, enable input
                break;
            case PAUSED:
                // Pause physics, show pause menu
                break;
            case MENU:
                // Show main menu
                break;
            case MISSION_COMPLETE:
                // Show mission complete screen
                break;
        }
    }
}
