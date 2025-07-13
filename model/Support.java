package model;

import java.util.Random;

/**
 * Класс для макросов и дополнительных функций.
 */
public class Support {
    public static final int START_GAME = 0;
    public static final int NEW_GAME = 1;
    public static final int END_GAME = 4;
    public static final int EXIT_GAME = 5;
    public static final int MAXCOUNTROOMS = 9;
    public static final int MAXCOUNTLEVELS = 21;
    public static final int LEVEL_WIDTH = 32;
    public static final int LEVEL_HEIGHT = 32;
    public static final int MAX_WIDTH_ROOM = 6; //10
    public static final int MAX_HEIGHT_ROOM = 6; //10
    public static final int ESC_KEY = 27;
    public static final int RIGHT = 1;
    public static final int DOWN = 3;
    public static final int LEFT = -1;
    public static final int UP = -3;
    public static final int UP_KEY = 119;
    public static final int DOWN_KEY = 115;
    public static final int LEFT_KEY = 97;
    public static final int RIGHT_KEY = 100;
    public static final int MAX_HIT_CHANCE = 95;
    public static final int MIN_HIT_CHANCE = 5;
    public static final int BASE_HIT_CHANCE = 50;
    public static final int HIT_CHANCE_MULTIPLIER  = 2;
    public static final int MIN_PERCENTAGE   = 0;
    public static final int MAX_PERCENTAGE    = 100;
    public static final int ENEMY = 'X';
    public static final int ZOMBIE = 'Z';
    public static final int VAMPIRE = 'V';
    public static final int GHOST = 'G';
    public static final int OGRE = 'O';
    public static final int SNAKE_MAGE = 'S';
    public static final int WALL = 'I';
    public static final int FLOOR = '_';
    public static final int DOOR = 'D';
    public static final int CORRIDOR = 'C';
    public static final int EXIT = 'E';
    public static final int START = 'U';
    public static final int CHARACTER = 'P';
    public static final int ITEM = 'Q';
    
    /**
     * Рандомит число в заданном диапозоне включая крайние числа.
     * @param max максимально возможное число(включительно)
     * @param min минимально возможное число(включительно)
     * @return случайное число в диапозоне
     */
    public static int randInDiaposone(int min, int max) {
        Random rn = new Random();
        return rn.nextInt(max - min + 1) + min;
    }
}
