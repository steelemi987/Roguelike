package model.enemies;

import model.Coordinate;

import static model.Support.*;

public class Zombie extends Enemy {
    public Zombie(Coordinate position, int currentLevel) {
        super(position);
        type = ZOMBIE;
        this.health = 20 + currentLevel;
        this.agility = 10 + currentLevel;
        this.strength = 2 + currentLevel;
        this.hostility = 4;
    }
}
