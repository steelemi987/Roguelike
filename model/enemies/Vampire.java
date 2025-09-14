package model.enemies;

import model.level.Coordinate;
import static model.Support.*;

public class Vampire extends Enemy {
    /// уворот от атаки
    private boolean dodge = true;
    /// показатель защиты при увороте
    private static final int DODGE = 999;

    public Vampire(Coordinate position, int currentLevel) {
        super(position);
        this.type = VAMPIRE;
        this.maxHealth = 20 + currentLevel;
        this.health = maxHealth;
        this.agility = 20 + currentLevel;
        this.strength = 2 + currentLevel;
        this.hostility = 5;
    }

    public Vampire(Coordinate position, int maxHealth, int health, int agility, int strength, boolean dodge) {
        super(position);
        this.type = VAMPIRE;
        this.maxHealth = maxHealth;
        this.health = health;
        this.agility = agility;
        this.strength = strength;
        this.hostility = 5;
        this.dodge = dodge;
    }

    /**
     * Способность уменьшать максимальное здоровье персонажа.
     * @return соответствующий эффект
     */
    @Override
    public int getAttackEffect() {
        return Effect.VAMPIRE.ordinal();
    }

    /**
     * Вампир уворачивается от первой атаки.
     * @return защиту
     */
    @Override
    public int makeDefenseRoll() {
        int def;
        if (dodge) {
            def = DODGE;
            dodge = false;
        } else {
            def = agility;
        }
        return def;
    }

    public boolean isDodge() {
        return dodge;
    }
}
