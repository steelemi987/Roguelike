package model.items;

import model.character.Character;
import model.level.Coordinate;

import static model.Support.*;

public class Elixir extends Item {
    /// Продолжительность эффекта
    private static final int EFFECT_DURATION = 5;
    /// Сколько ходов осталось до окончания эффекта
    private int timeLeft;

    /**
     * Конструктор с рандомом типа зелья и бонусных очков характеристик
     * @param newCoordinate позиция на карте
     */
    public Elixir(Coordinate newCoordinate){
        this.type = ELIXIRS;
        this.usable = true;
        this.position = newCoordinate;
        this.subtype = randInDiapason(ELIXIRS_HEAL, ELIXIRS_STR);
        switch (this.subtype){
            case ELIXIRS_HEAL: this.maxHealth = randInDiapason(ELIXIRS_HEALTH_MIN, ELIXIRS_HEALTH_MAX);
                break;
            case ELIXIRS_AGI: this.agility = randInDiapason(ELIXIRS_AGI_MIN, ELIXIRS_AGI_MAX);
                break;
            case ELIXIRS_STR: this.strength = randInDiapason(ELIXIRS_STR_MIN, ELIXIRS_STR_MAX);
                break;
        }
    }

    public Elixir(Coordinate newCoordinate, int bonus, int timeLeft, int subtype){
        this.type = ELIXIRS;
        this.usable = true;
        this.position = newCoordinate;
        this.timeLeft = timeLeft;
        this.subtype = subtype;
        switch (this.subtype){
            case ELIXIRS_HEAL: this.maxHealth = bonus;
                break;
            case ELIXIRS_AGI: this.agility = bonus;
                break;
            case ELIXIRS_STR: this.strength = bonus;
                break;
        }
    }

    /**
     * Создает имя предмета, исходя из его типа и бонусных характеристик
     * @return строку с именем предмета
     */
    @Override
    public String getNameItem(){
        String type = "Elixir of ";
        String suffix;
        String numb = switch (this.subtype) {
            case ELIXIRS_HEAL -> {
                suffix = "Health";
                yield "(" + this.maxHealth + ")";
            }
            case ELIXIRS_AGI -> {
                suffix = "Agility";
                yield "(" + this.agility + ")";
            }
            case ELIXIRS_STR -> {
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
        this.timeLeft = EFFECT_DURATION;
        character.addBonusElixir(this);
        switch (this.subtype){
            case ELIXIRS_HEAL: character.increaseMaxHealth(maxHealth);
                break;
            case ELIXIRS_AGI: character.increaseAgility(agility);
                break;
            case ELIXIRS_STR: character.increaseStrength(strength);
                break;
        }
    }

    /**
     * Уменьшает длительность эффекта на один ход и сообщает если время вышло.
     * @return правду - если время вышло, ложь - обратное
     */
    public boolean decreaseTimeLeft() {
        this.timeLeft--;
        return this.timeLeft <= 0;
    }

    /**
     * Снимает временный эффект с персонажа в зависимости от типа эликсира
     * @param character персонаж
     */
    public void removeBonusEffect(Character character) {
        switch (this.subtype){
            case ELIXIRS_HEAL: character.decreaseMaxHealth(maxHealth);
                break;
            case ELIXIRS_AGI: character.decreaseAgility(agility);
                break;
            case ELIXIRS_STR: character.decreaseStrength(strength);
                break;
        }
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public int getBonus() {
        return switch (this.subtype) {
            case ELIXIRS_HEAL -> maxHealth;
            case ELIXIRS_AGI -> agility;
            case ELIXIRS_STR -> strength;
            default -> 0;
        };
    }
}
