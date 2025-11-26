package com.tintomax.maxfighter.model;

import java.util.List;

public class Fighter {

    private String name;
    private int maxHp;
    private int currentHp;
    private int speed;
    private int baseSpeed;
    private List<Move> moves;

    private int bonusDamage;

    // Veneno contínuo
    private int poisonDamagePerTurn;
    private int poisonRemainingTurns;

    // Sprites (frente e costas)
    private String spriteUrl;      // Frente (sempre o inimigo)
    private String backSpriteUrl;  // Costas (sempre o player)

    public Fighter(String name, int maxHp, int speed, List<Move> moves) {
        this.name = name;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.speed = speed;
        this.baseSpeed = speed;
        this.moves = moves;
        this.bonusDamage = 0;
        this.poisonDamagePerTurn = 0;
        this.poisonRemainingTurns = 0;
    }

    // ========== GETTERS/SETTERS BÁSICOS ==========

    public String getName() {
        return name;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int currentHp) {
        if (currentHp < 0) currentHp = 0;
        if (currentHp > maxHp) currentHp = maxHp;
        this.currentHp = currentHp;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        if (speed < 0) speed = 0;
        this.speed = speed;
    }

    public int getBaseSpeed() {
        return baseSpeed;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public int getBonusDamage() {
        return bonusDamage;
    }

    public void addBonusDamage(int value) {
        this.bonusDamage += value;
    }

    // ========= Veneno =========

    public int getPoisonDamagePerTurn() {
        return poisonDamagePerTurn;
    }

    public void setPoisonDamagePerTurn(int poisonDamagePerTurn) {
        this.poisonDamagePerTurn = poisonDamagePerTurn;
    }

    public int getPoisonRemainingTurns() {
        return poisonRemainingTurns;
    }

    public void setPoisonRemainingTurns(int poisonRemainingTurns) {
        if (poisonRemainingTurns < 0) poisonRemainingTurns = 0;
        this.poisonRemainingTurns = poisonRemainingTurns;
    }

    // Compatibilidade usada no HTML
    public int getPoisonTurnsRemaining() {
        return poisonRemainingTurns;
    }

    public void setPoisonTurnsRemaining(int poisonTurnsRemaining) {
        setPoisonRemainingTurns(poisonTurnsRemaining);
    }

    // ========= Sprites =========

    public String getSpriteUrl() {
        return spriteUrl;
    }

    public void setSpriteUrl(String spriteUrl) {
        this.spriteUrl = spriteUrl;
    }

    public String getBackSpriteUrl() {
        return backSpriteUrl;
    }

    public void setBackSpriteUrl(String backSpriteUrl) {
        this.backSpriteUrl = backSpriteUrl;
    }
}
