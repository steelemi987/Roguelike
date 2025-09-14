package model.items;

import model.character.Character;
import model.level.Coordinate;
import static model.Support.*;

public class Weapon extends Item {
    public Weapon(Coordinate newCoordinate) {
        this.type = WEAPONS;
        this.usable = false;
        this.position = newCoordinate;
        this.subtype = randInDiapason(SHADOW_BLADE, THUNDER_HAMMER);
        this.strength = this.subtype % (SHADOW_BLADE - 1) * MOD_STR_WEAPON;
    }

    public Weapon(Coordinate newCoordinate, int strength, int subtype) {
        this.type = WEAPONS;
        this.usable = false;
        this.position = newCoordinate;
        this.subtype = subtype;
        this.strength = strength;
    }

    /**
     * Создает имя предмета, исходя из его типа и бонусных характеристик
     * @return строку с именем предмета
     */
    @Override
    public String getNameItem(){
        String name = switch (this.subtype) {
            case SHADOW_BLADE -> "Shadow blade";
            case STORM_CLAWS -> "Storm claws";
            case SOUL_EATER_STAFF -> "Soul eater staff";
            case ETERNAL_ICE_BLADE -> "Eternal ice blade";
            case VOID_SCYTHE -> "Void scythe";
            case STORM_AXE -> "Storm axe";
            case STAR_DUST_CROSSBOW -> "Star dust crossbow";
            case SOUL_DAGGER -> "Soul dagger";
            case THUNDER_HAMMER -> "Thunder hammer";
            default -> "None";
        };
        String numb = "(" + this.strength + ")";
        return name + numb;
    }

    /**
     * Меняет текущее оружие на новое
     * @param character персонаж
     */
    @Override
    public void useOnCharacter(Character character) {
        character.changeWeapon(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Weapon weapon)) return false;
        return strength == weapon.strength && subtype == weapon.subtype;
    }
}
