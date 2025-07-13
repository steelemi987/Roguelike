package view;

import jcurses.system.CharColor;

public class Defines {
    public static final int LEVEL_WIDTH = 32;
    public static final int LEVEL_HEIGHT = 32;
    public static final CharColor YELLOW = new CharColor(CharColor.BLACK, CharColor.YELLOW);
    public static final CharColor GREEN = new CharColor(CharColor.BLACK, CharColor.GREEN);
    public static final CharColor RED = new CharColor(CharColor.BLACK, CharColor.RED);
    public static final CharColor WHITE = new CharColor(CharColor.BLACK, CharColor.WHITE);
    public static final CharColor BLACK = new CharColor(CharColor.BLACK, CharColor.BLACK);
    public static final CharColor BLUE = new CharColor(CharColor.BLACK, CharColor.BLUE);
    public static final int ENEMY = 'X'; // Для дебага
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
}
