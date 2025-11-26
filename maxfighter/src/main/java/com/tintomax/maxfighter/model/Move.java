package com.tintomax.maxfighter.model;

public class Move {

    public enum MoveType {
        DAMAGE,
        HEAL,
        BUFF_SPEED,
        DEBUFF_SPEED,
        BUFF_DAMAGE,
        POISON
    }

    private String name;
    private int power;
    private MoveType type;
    private String description;

    private int maxUses;
    private int remainingUses;

    // Só usados para moves de veneno (efeito principal)
    private int poisonDamagePerTurn;
    private int poisonDuration;

    // ====== EFEITO SECUNDÁRIO OPCIONAL ======
    private MoveType secondaryType;        // tipo do segundo efeito (pode ser null)
    private int secondaryPower;            // "força" do efeito secundário
    private boolean secondaryTargetsSelf;  // true = aplica no atacante, false = no alvo padrão

    // =========================
    // CONSTRUTORES
    // =========================

    // Construtor simples (sem veneno, sem efeito secundário)
    public Move(String name, int power, MoveType type, String description, int maxUses) {
        this(name, power, type, description, maxUses, 0, 0, null, 0, false);
    }

    // Construtor com veneno (efeito principal)
    public Move(String name,
                int power,
                MoveType type,
                String description,
                int maxUses,
                int poisonDamagePerTurn,
                int poisonDuration) {
        this(name, power, type, description, maxUses,
                poisonDamagePerTurn, poisonDuration,
                null, 0, false);
    }

    // Construtor com efeito secundário (sem veneno)
    public Move(String name,
                int power,
                MoveType type,
                String description,
                int maxUses,
                MoveType secondaryType,
                int secondaryPower,
                boolean secondaryTargetsSelf) {
        this(name, power, type, description, maxUses,
                0, 0,
                secondaryType, secondaryPower, secondaryTargetsSelf);
    }

    // Construtor mestre (interno)
    private Move(String name,
                 int power,
                 MoveType type,
                 String description,
                 int maxUses,
                 int poisonDamagePerTurn,
                 int poisonDuration,
                 MoveType secondaryType,
                 int secondaryPower,
                 boolean secondaryTargetsSelf) {

        this.name = name;
        this.power = power;
        this.type = type;
        this.description = description;
        this.maxUses = maxUses;
        this.remainingUses = maxUses;
        this.poisonDamagePerTurn = poisonDamagePerTurn;
        this.poisonDuration = poisonDuration;

        this.secondaryType = secondaryType;
        this.secondaryPower = secondaryPower;
        this.secondaryTargetsSelf = secondaryTargetsSelf;
    }

    // ====== MÉTODOS DE USO ======

    public boolean hasUsesLeft() {
        return remainingUses > 0;
    }

    public void consumeUse() {
        if (remainingUses > 0) {
            remainingUses--;
        }
    }

    // ====== GETTERS ======

    public String getName() {
        return name;
    }

    public int getPower() {
        return power;
    }

    public MoveType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxUses() {
        return maxUses;
    }

    public int getRemainingUses() {
        return remainingUses;
    }

    public int getPoisonDamagePerTurn() {
        return poisonDamagePerTurn;
    }

    public int getPoisonDuration() {
        return poisonDuration;
    }

    // ====== EFEITO SECUNDÁRIO ======

    public MoveType getSecondaryType() {
        return secondaryType;
    }

    public int getSecondaryPower() {
        return secondaryPower;
    }

    public boolean isSecondaryTargetsSelf() {
        return secondaryTargetsSelf;
    }
}