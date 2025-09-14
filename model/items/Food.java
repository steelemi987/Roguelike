package model.items;

import model.character.Character;
import model.level.Coordinate;
import static model.Support.*;

public class Food extends Item{
    /**
     * Конструктор с рандомом типа еды и бонусных очков характеристик
     * @param newCoordinate позиция на карте
     */
    public Food(Coordinate newCoordinate) {
        this.type = FOOD;
        this.usable = true;
        this.position = newCoordinate;
        this.subtype = randInDiapason(FOOD_LOW, FOOD_LARGE);
        switch (this.subtype){
            case FOOD_LOW: this.health = randInDiapason(FOOD_HEALTH_MIN, FOOD_HEALTH_LOW - 1);
                break;
            case FOOD_MED: this.health = randInDiapason(FOOD_HEALTH_LOW, FOOD_HEALTH_MED - 1);
                break;
            case FOOD_LARGE: this.health = randInDiapason(FOOD_HEALTH_MED, FOOD_HEALTH_LARGE);
                break;
        }
    }

    public Food(Coordinate newCoordinate, int health, int subtype) {
        this.type = FOOD;
        this.usable = true;
        this.position = newCoordinate;
        this.subtype = subtype;
        this.health = health;
    }

    /**
     * Создает имя предмета, исходя из его типа и бонусных характеристик
     * @return строку с именем предмета
     */
    @Override
    public String getNameItem(){
        String type = "Food";
        String numb = "(" + this.health + ")";
        return type + numb;
    }

    /**
     * Восстанавливает очки здоровья персонажа не более максимума
     * @param character персонаж
     */
    @Override
    public void useOnCharacter(Character character) {
        character.restoreHealth(health);
    }
}
