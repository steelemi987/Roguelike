package model.enemies;

import model.Character;
import model.Coordinate;

import static model.Support.*;

public class Ogre extends Enemy{
    private boolean sleep; // отдыхает ли огр
    private boolean counterattack; // возможность контраттаки
    private final int steps; // количество ходов
    public Ogre(Coordinate position, int currentLevel) {
        super(position);
        this.steps = 2;
        this.sleep = false;
        this.counterattack = false;
        this.type = OGRE;
        this.health = 25 + currentLevel;
        this.agility = 10 + currentLevel;
        this.strength = 4 + currentLevel;
        this.hostility = 4;
    }

    /**
     * Модификация одноименной функции супер класса с учетом особенностей текущего класса. Огр ходит 2 раза, после атаки
     * отдыхает 1 ход. Если огр отдыхал, то он пробуждается.
     * @param character наш персонаж
     * @param field поле со структурами
     */
    @Override
    public void act(Character character, char[][] field) { // Огр ходит 2 раза подряд, но функция renderfield вызывается единожды, на поле(field) остается координата первого хода
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
     * Пробуждение огра, включается способность к контраттаке.
     */
    public void wakeUp() {
        sleep = false;
        counterattack = true;

    }

    /**
     * Если огр может контраттаковать, возвращаем эффект контраттаки, если нет - отсутствие эффекта.
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
}
