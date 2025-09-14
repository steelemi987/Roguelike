package model.enemies;

import model.character.Character;
import model.level.Coordinate;
import static model.Support.*;

public class Ogre extends Enemy{
    /// отдыхает ли огр
    private boolean sleep;
    /// Возможность контратаки
    private boolean counterattack;
    /// Количество ходов
    private final int steps;

    public Ogre(Coordinate position, int currentLevel) {
        super(position);
        this.steps = 2;
        this.sleep = false;
        this.counterattack = false;
        this.type = OGRE;
        this.maxHealth = 25 + currentLevel;
        this.health = maxHealth;
        this.agility = 10 + currentLevel;
        this.strength = 4 + currentLevel;
        this.hostility = 4;
    }

    public Ogre(Coordinate position, int maxHealth, int health, int agility, int strength, boolean sleep, boolean counterattack) {
        super(position);
        this.steps = 2;
        this.sleep = sleep;
        this.counterattack = counterattack;
        this.type = OGRE;
        this.maxHealth = maxHealth;
        this.health = health;
        this.agility = agility;
        this.strength = strength;
        this.hostility = 4;
    }

    /**
     * Модификация одноименной функции супер класса с учетом особенностей текущего класса. Огр ходит 2 раза, после атаки
     * отдыхает 1 ход. Если огр отдыхал, то он пробуждается.
     * @param character наш персонаж
     * @param field поле со структурами
     */
    @Override
    public void act(Character character, char[][] field) {
        int i = 1;
        while (i <= steps && !sleep) {
            if (doISeeCharacter(character.getPosition())) {
                if (canFight(character.getPosition())) {
                    fightClub.getFight(character);
                    sleep = true;
                    i++;
                } else if (canReachCharacter(character.getPosition(), field)) {
                    pursueCharacter(character.getPosition(), field);
                } else {
                    moveInPattern(field);
                }
            } else {
                moveInPattern(field);
            }
            i++;
        }
        if (i == 1) {
            wakeUp();
        }
    }

    /**
     * Пробуждение огра, включается способность к контратаке.
     */
    private void wakeUp() {
        sleep = false;
        counterattack = true;
    }

    /**
     * Если огр может контратаковать, возвращаем эффект контратаки, если нет - отсутствие эффекта.
     * @return код эффекта
     */
    @Override
    public int getDefEffect() {
        int effect;
        if (counterattack) {
            effect = Effect.COUNTERATTACK.ordinal();
            counterattack = false;
        } else {
            effect = Effect.NO_EFFECT.ordinal();
        }
        return effect;
    }

    public boolean isSleep() {
        return sleep;
    }

    public boolean isCounterattack() {
        return counterattack;
    }
}
