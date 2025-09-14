package model.items;

import model.character.Character;
import model.level.Coordinate;
import model.enemies.Enemy;
import static model.Support.*;

public class Treasure extends Item {
    /**
     * Конструктор без параметров. value = 0
     */
    public Treasure() {
        this.type = TREASURES;
        this.subtype = TREASURES;
        this.usable = false;
        this.value = 0;
    }

    public Treasure(int value) {
        this.type = TREASURES;
        this.subtype = TREASURES;
        this.usable = false;
        this.value = value;
    }

    public Treasure(Coordinate newCoordinate, int value) {
        this.type = TREASURES;
        this.subtype = TREASURES;
        this.usable = false;
        this.value = value;
        this.position = newCoordinate;
    }

    /**
     * Конструктор с параметром. value расчитывается исходя из характеристик противника, за которого начисляется
     * @param enemy противник за которого выдают сокровище
     */
    public Treasure(Enemy enemy) {
        this.type = TREASURES;
        this.subtype = TREASURES;
        this.usable = false;
        this.value = enemy.getMaxHealth() + enemy.getAgility() + enemy.getStrength();
    }

    /**
     * Создает имя предмета, исходя из его типа и бонусных характеристик
     * @return строку с именем предмета
     */
    @Override
    public String getNameItem(){
        String type = "Treasure";
        String numb = "(" + this.value + ")";
        return type + numb;
    }

    @Override
    public void useOnCharacter(Character character){}

    /**
     * Увеличивает количество сокровищ на величину добавочного сокровища.
     * @param treasure еще одно сокровище
     */
    public void addTreasureValue(Treasure treasure) {
        value = value + treasure.getValue();
    }
}
