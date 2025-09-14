package model.items;

import model.character.Character;
import model.level.Coordinate;

import static model.Support.*;

public class Scroll extends Item{
    /**
     * Конструктор с рандомом типа свитка и бонусных очков характеристик
     * @param newCoordinate позиция на карте
     */
    public Scroll(Coordinate newCoordinate){
        this.type = SCROLLS;
        this.usable = true;
        this.position = newCoordinate;
        this.subtype = randInDiapason(SCROLLS_HEAL, SCROLLS_STR);
        switch (this.subtype){
            case SCROLLS_HEAL: this.maxHealth = randInDiapason(SCROLLS_HEALTH_MIN, SCROLLS_HEALTH_MAX);
                break;
            case SCROLLS_AGI: this.agility = randInDiapason(SCROLLS_AGI_MIN, SCROLLS_AGI_MAX);
                break;
            case SCROLLS_STR: this.strength = randInDiapason(SCROLLS_STR_MIN, SCROLLS_STR_MAX);
                break;
        }
    }

    public Scroll(Coordinate newCoordinate, int bonus, int subtype){
        this.type = SCROLLS;
        this.usable = true;
        this.position = newCoordinate;
        this.subtype = subtype;
        switch (this.subtype){
            case SCROLLS_HEAL: this.maxHealth = bonus;
                break;
            case SCROLLS_AGI: this.agility = bonus;
                break;
            case SCROLLS_STR: this.strength = bonus;
                break;
        }
    }

    /**
     * Создает имя предмета, исходя из его типа и бонусных характеристик
     * @return строку с именем предмета
     */
    @Override
    public String getNameItem(){
        String type = "Scroll of ";
        String suffix;
        String numb = switch (this.subtype) {
            case SCROLLS_HEAL -> {
                suffix = "Health";
                yield "(" + this.maxHealth + ")";
            }
            case SCROLLS_AGI -> {
                suffix = "Agility";
                yield "(" + this.agility + ")";
            }
            case SCROLLS_STR -> {
                suffix = "Strength";
                yield "(" + this.strength + ")";
            }
            default -> {
                suffix = "None";
                yield "()";
            }
        };
        return type + suffix + numb;
    }

    /**
     * Применяет свой эффект на персонажа
     * @param character персонаж
     */
    @Override
    public void useOnCharacter(Character character) {
        switch (this.subtype){
            case SCROLLS_HEAL: character.increaseMaxHealth(maxHealth);
                break;
            case SCROLLS_AGI: character.increaseAgility(agility);
                break;
            case SCROLLS_STR: character.increaseStrength(strength);
                break;
        }
    }

    public int getBonus() {
        return switch (this.subtype) {
            case SCROLLS_HEAL -> maxHealth;
            case SCROLLS_AGI -> agility;
            case SCROLLS_STR -> strength;
            default -> 0;
        };
    }
}
