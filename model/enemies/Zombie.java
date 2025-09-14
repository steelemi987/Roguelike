package model.enemies;

import model.level.Coordinate;
import static model.Support.*;

public class Zombie extends Enemy {
    public Zombie(Coordinate position, int currentLevel) {
        super(position);
        type = ZOMBIE;
        this.maxHealth = 20 + currentLevel;
        this.health = maxHealth;
        this.agility = 10 + currentLevel;
        this.strength = 2 + currentLevel;
        this.hostility = 4;
    }

    public Zombie(Coordinate position, int maxHealth, int health, int agility, int strength) {
        super(position);
        type = ZOMBIE;
        this.maxHealth = maxHealth;
        this.health = health;
        this.agility = agility;
        this.strength = strength;
        this.hostility = 4;
    }
}
