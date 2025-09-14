package model.enemies;

import java.util.ArrayList;
import java.util.Arrays;

import model.level.Coordinate;
import static model.Support.*;

public class SnakeMage extends Enemy {
    public static final int LEFT_UP = 0;
    public static final int RIGHT_UP = 1;
    public static final int LEFT_DOWN = 2;
    public static final int RIGHT_DOWN = 3;

    public SnakeMage(Coordinate position, int currentLevel) {
        super(position);
        this.type = SNAKE_MAGE;
        this.maxHealth = 10 + currentLevel;
        this.health = maxHealth;
        this.agility = 25 + currentLevel;
        this.strength = 2 + currentLevel;
        this.hostility = 5;
    }

    public SnakeMage(Coordinate position, int maxHealth, int health, int agility, int strength) {
        super(position);
        this.type = SNAKE_MAGE;
        this.maxHealth = maxHealth;
        this.health = health;
        this.agility = agility;
        this.strength = strength;
        this.hostility = 5;
    }

    /**
     * Маг атакует персонажа, если тот находится на соседней клетке (включая диагональ)
     * @param characterPosition координаты персонажа
     * @return true - если персонаж находится на соседней клетке, false - если нет.
     */
    @Override
    protected boolean canFight(Coordinate characterPosition) {
        ArrayList<Coordinate> hitRange = new ArrayList<>();
        int y = position.getY() - 1;
        int x = position.getX() - 1;
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                hitRange.add(new Coordinate(x + j, y + i));
            }
        }
        return hitRange.contains(characterPosition);
    }

    /**
     * Маг перемещается по диагонали.
     * @return список направлений.
     */
    @Override
    protected ArrayList<Integer> getDirectionList() {
        return new ArrayList<>(Arrays.asList(LEFT_UP, RIGHT_UP, LEFT_DOWN, RIGHT_DOWN));
    }

    /**
     * Определяет координаты точки в зависимости от направления и текущего положения мага.
     * @param direction направление следующей точки
     * @return координаты точки
     */
    @Override
    protected Coordinate getCoordinateWithShift(int direction) {
        Coordinate next = new Coordinate();
        switch (direction) {
            case LEFT_UP : next.setCoordinate(position.getX() - 1, position.getY() - 1);
                break;
            case RIGHT_UP : next.setCoordinate(position.getX() + 1, position.getY() - 1);
                break;
            case LEFT_DOWN : next.setCoordinate(position.getX() - 1, position.getY() + 1);
                break;
            case RIGHT_DOWN : next.setCoordinate(position.getX() + 1, position.getY() + 1);
                break;
        }
        return next;
    }

    /**
     * Маг имеет возможность усыплять персонажа с вероятностью при удачной атаке.
     * @return соответствующий эффект
     */
    @Override
    public int getAttackEffect() {
        int effect = Effect.NO_EFFECT.ordinal();
        if (randInDiapason(0, 3) == 0) {
            effect = Effect.SLEEP.ordinal();
        }
        return effect;
    }
}
