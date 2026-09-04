package com.javacity.player;

public class PlayerStats {
    private float health = 100f;
    private int money = 500;
    private int wantedLevel = 0;

    public float getHealth() {
        return health;
    }

    public void damage(float amount) {
        this.health -= amount;
        if (this.health < 0) {
            this.health = 0;
        }
    }

    public void heal(float amount) {
        this.health += amount;
        if (this.health > 100f) {
            this.health = 100f;
        }
    }

    public boolean isDead() {
        return this.health <= 0;
    }

    public int getMoney() {
        return money;
    }

    public void addMoney(int amount) {
        this.money += amount;
    }

    public boolean spendMoney(int amount) {
        if (this.money >= amount) {
            this.money -= amount;
            return true;
        }
        return false;
    }

    public int getWantedLevel() {
        return wantedLevel;
    }

    public void setWantedLevel(int wantedLevel) {
        this.wantedLevel = Math.max(0, Math.min(5, wantedLevel));
    }
}
