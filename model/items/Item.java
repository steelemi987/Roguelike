package model.items;

import model.character.Character;
import model.level.Coordinate;

import static model.Support.*;

public abstract class Item {
    protected int type = NO_TYPE_ITEM;
    protected int subtype = NO_TYPE_ITEM;
    protected int health = 0; //restoration amount for food
    protected int maxHealth = 0; //restoration amount in specific units for scrolls and elixirs
    protected int agility = 0; //restoration amount in specific units for scrolls and elixirs
    protected int strength = 0; //restoration amount in specific units for scrolls, elixirs, and weapons
    protected int value = 0; //for treasures
    protected boolean usable;
    protected Coordinate position = new Coordinate();

    /**
     * Применяет свой эффект на персонажа
     * @param character персонаж
     */
    public abstract void useOnCharacter(Character character);

    /**
     * Создает имя предмета, исходя из его типа и бонусных характеристик
     * @return строку с именем предмета
     */
    public abstract String getNameItem();

    public boolean isUsable() {
        return usable;
    }

    public int getStrength() {
        return strength;
    }

    public Coordinate getPosition() {
        return position;
    }

    public void setPosition(Coordinate position) {
        this.position = position;
    }

    public int getValue() {
        return value;
    }

    public int getType() {
        return type;
    }

    public int getSubtype() {
        return subtype;
    }

    public int getHealth() {
        return health;
    }

}
