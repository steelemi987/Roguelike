package model;

import java.util.Random;

/**
 * Класс для макросов и дополнительных функций.
 */
public class Support {
    public static final int START_GAME = 0;
    public static final int NEW_GAME = 1;
    public static final int INVENTORY_GAME = 2;
    public static final int STATISTIC_GAME = 3;
    public static final int END_GAME = 4;
    public static final int EXIT_GAME = 5;
    public static final int OPTIONS_GAME = 6;
    public static final int CREDITS_GAME = 7;
    public static final int LOAD_GAME = 8;
    public static final int MAX_COUNT_LEVELS = 21;
    public static final int LEVEL_WIDTH = 32;
    public static final int LEVEL_HEIGHT = 32;
    public static final int ESC_KEY = 27;
    public static final int START_KEY = '1';
    public static final int LOAD_KEY = '2';
    public static final int STATISTICS_KEY = '3';
    public static final int OPTIONS_KEY = '4';
    public static final int CREDITS_KEY = '5';
    public static final int RIGHT = 1;
    public static final int DOWN = 3;
    public static final int LEFT = -1;
    public static final int UP = -3;
    public static final int UP_KEY = 119;
    public static final int DOWN_KEY = 115;
    public static final int LEFT_KEY = 97;
    public static final int RIGHT_KEY = 100;
    public static final int INVENTORY_KEY = 'i';
    public static final int WEAPON_KEY = 'h';
    public static final int FOOD_KEY = 'j';
    public static final int ELIXIR_KEY = 'k';
    public static final int SCROLL_KEY = 'e';
    public static final int MAX_HIT_CHANCE = 95;
    public static final int MIN_HIT_CHANCE = 5;
    public static final int BASE_HIT_CHANCE = 50;
    public static final int HIT_CHANCE_MULTIPLIER  = 2;
    public static final int MIN_PERCENTAGE   = 0;
    public static final int MAX_PERCENTAGE    = 100;
    public static final int ENEMY = 'X';
    public static final int ZOMBIE = 90; //'Z';
    public static final int VAMPIRE = 86; //'V';
    public static final int GHOST = 71; //'G';
    public static final int OGRE = 79; //'O';
    public static final int SNAKE_MAGE = 83; //'S';
    public static final int WALL = 'I';
    public static final int FLOOR = '_';
    public static final int DOOR = 'D';
    public static final int CORRIDOR = 'C';
    public static final int EXIT = 'E';
    public static final int CHARACTER = 'P';
    public static final int ITEM = 'Q';
    public static final int FOOD = 0;
    public static final int SCROLLS = 1;
    public static final int ELIXIRS = 2;
    public static final int WEAPONS = 3;
    public static final int TREASURES = 4;
    public static final int FOOD_LOW = 5;
    public static final int FOOD_MED = 6;
    public static final int FOOD_LARGE = 7;
    public static final int SCROLLS_HEAL = 8;
    public static final int SCROLLS_AGI = 9;
    public static final int SCROLLS_STR = 10;
    public static final int ELIXIRS_HEAL = 11;
    public static final int ELIXIRS_AGI = 12;
    public static final int ELIXIRS_STR = 13;
    public static final int SHADOW_BLADE = 14;
    public static final int STORM_CLAWS = 15;
    public static final int SOUL_EATER_STAFF = 16;
    public static final int ETERNAL_ICE_BLADE = 17;
    public static final int VOID_SCYTHE = 18;
    public static final int STORM_AXE = 19;
    public static final int STAR_DUST_CROSSBOW = 20;
    public static final int SOUL_DAGGER = 21;
    public static final int THUNDER_HAMMER = 22;
    public static final int NO_TYPE_ITEM = 99;
    public static final int FOOD_HEALTH_MIN = 10;
    public static final int FOOD_HEALTH_LOW = 25;
    public static final int FOOD_HEALTH_MED = 50;
    public static final int FOOD_HEALTH_LARGE = 100;
    public static final int SCROLLS_HEALTH_MIN = 15;
    public static final int SCROLLS_HEALTH_MAX = 50;
    public static final int SCROLLS_AGI_MIN = 10;
    public static final int SCROLLS_AGI_MAX = 20;
    public static final int SCROLLS_STR_MIN = 10;
    public static final int SCROLLS_STR_MAX = 20;
    public static final int ELIXIRS_HEALTH_MIN = 15;
    public static final int ELIXIRS_HEALTH_MAX = 50;
    public static final int ELIXIRS_AGI_MIN = 10;
    public static final int ELIXIRS_AGI_MAX = 20;
    public static final int ELIXIRS_STR_MIN = 10;
    public static final int ELIXIRS_STR_MAX = 20;
    public static final int MOD_STR_WEAPON = 5;
    public static final int BACKPACK_SIZE = 9;
    public static final int USE_ITEM = 'q';
    public static final int DROP_ITEM = 'r';
    public static final int ADD_ITEM = 0;
    public static final int REMOVE_ITEM = 1;
    public static final String USED_FOR_VIEW = "(used)";

    /**
     * Рандомит число в заданном диапазоне включая крайние числа.
     * @param max максимально возможное число(включительно)
     * @param min минимально возможное число(включительно)
     * @return случайное число в диапазоне
     */
    public static int randInDiapason(int min, int max) {
        Random rn = new Random();
        return rn.nextInt(max - min + 1) + min;
    }
}
