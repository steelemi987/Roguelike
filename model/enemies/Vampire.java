package model.enemies;

import model.Coordinate;

import static model.Support.*;

public class Vampire extends Enemy {
    private boolean dodge = true; // уворот от атаки
    private static final int DODGE = 999; // показатель защиты при увороте
    public Vampire(Coordinate position, int currentLevel) {
        super(position);
        this.type = VAMPIRE;
        this.health = 20 + currentLevel;
        this.agility = 20 + currentLevel;
        this.strength = 2 + currentLevel;
        this.hostility = 5;
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
}
