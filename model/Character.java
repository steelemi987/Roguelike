package model;

import model.enemies.Enemy;
import model.enemies.Fighter;

import java.util.List;

import static model.Support.*;

public class Character implements Fighter, Observable{
    private Fight fightClub;
    private int maxHealth;
    private int health;
    private int agility;
    private int strength;
    private boolean sleep;
    private Coordinate position;
    private Observer observer;

    /**
     * Конструктор.
     * @param position координаты положения на карте.
     */
    public Character(Coordinate position, Observer observer) {
        this.fightClub = new Fight(this);
        this.sleep = false;
        this.maxHealth = 10000;
        this.health = 10000;
        this.agility = 20;
        this.strength = 10;
        this.position = position;
        this.observer = observer;
    }

    @Override
    public void registerObserver(Observer o){}

    @Override
    public void removeObserver(Observer o){}

    @Override
    public void notifyObserversCoordinate(Coordinate curr){
        observer.updateCoordinate(this, curr);
    }

    @Override
    public void notifyObserversDead() {
        observer.updateDead(this);
    }

    /**
     * Инициализирует бой с противником. Поиск противника по координатам в списке врагов.
     * @param position координаты положения предполагаемого врага.
     * @param defenders список врагов
     */
    public void fight(Coordinate position, List<Enemy> defenders) {
//        Fighter defender = null;
        for (Enemy c : defenders) {
            if (position.equals(c.getPosition())){
                fightClub.getFight(c);
                break;
//                defender = c;
            }
        }
//        if (defender != null) {
//            fightClub.getFight(defender);
//        }
//        if (defender.isDead()) {
//            defenders.remove(defender);
//            defender = null;
//        }
    }

    @Override
    public int makeAttackRoll() {
        return agility;
    }

    @Override
    public int makeDefenseRoll() {
        return agility;
    }

    @Override
    public int getDamage() {
        return strength;
    }

    @Override
    public void takeDamage(int damage) {
//        int hit = damage - armor;
//        if (hit <= 0) {
//            health = health - 1;
//        } else {
//            health = health - hit;
//        }
        health = health - damage;
        if (isDead()) {
            notifyObserversDead();
        }
    }

    @Override
    public int getAttackEffect() {
        return Effect.NO_EFFECT.ordinal();
    }

    @Override
    public int getDefEffect() {
        return Effect.NO_EFFECT.ordinal();
    }

    @Override
    public void applyNegativeEffect(int effect) {
        if (effect == Effect.VAMPIRE.ordinal()) {
            if (maxHealth > 1) {
                maxHealth = maxHealth - 1;
                if (health > maxHealth) {
                    health = maxHealth;
                }
            }
        } else if (effect == Effect.SLEEP.ordinal()) {
            sleep = true;
        }
    }

    @Override
    public boolean isDead() {
        return health <= 0;
    }

    /**
     * Сеттер. Устанавливает координаты позиции персонажа
     * @param position координаты нового положения
     */
    public void setPosition(Coordinate position) {
        Coordinate curr = this.position;
        this.position = position;
        notifyObserversCoordinate(curr);
    }

    /**
     * Геттер. Возвращает координаты позиции персонажа
     * @return координаты позиции персонажа (как понимаю это адрес экземпляра, возможно нужно поменять метод)
     */
    public Coordinate getPosition() {
        return position;
    }

    public boolean isSleep() {
        return sleep;
    }

    public void setSleep(boolean sleep) {
        this.sleep = sleep;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getHealth() {
        return health;
    }

    public int getAgility() {
        return agility;
    }

    public int getStrength() {
        return strength;
    }
}
