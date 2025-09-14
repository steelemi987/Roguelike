package view;

import jcurses.system.CharColor;

public class Defines {
    public static final int LEVEL_WIDTH = 32;
    public static final int LEVEL_HEIGHT = 32;
    public static final int FIELD_STRING = 3;
    public static final int STATUS_BAR_STRING = 36;
    public static final CharColor YELLOW = new CharColor(CharColor.BLACK, CharColor.YELLOW);
    public static final CharColor GREEN = new CharColor(CharColor.BLACK, CharColor.GREEN);
    public static final CharColor RED = new CharColor(CharColor.BLACK, CharColor.RED);
    public static final CharColor WHITE = new CharColor(CharColor.BLACK, CharColor.WHITE);
    public static final CharColor BLACK = new CharColor(CharColor.BLACK, CharColor.BLACK);
    public static final CharColor BLUE = new CharColor(CharColor.BLACK, CharColor.BLUE);
    public static final int ZOMBIE = 'Z';
    public static final int VAMPIRE = 'V';
    public static final int OGRE = 'O';
    public static final int WALL = 'I';
    public static final int DOOR = 'D';
    public static final int EXIT = 'E';
    public static final int START = 'U';
    public static final int CHARACTER = 'P';
    public static final int ITEM = 'Q';
    public static final int CORRIDOR = 'C';
    public static final int FLOOR = '_';
    public static final int WALL_SYMB = 8;
    public static final int ITEM_SYMB = 15;
    public static final int EXIT_SYMB = 20;
    public static final int CHARACTER_SYMB = 2;
    public static final int DOOR_SYMB = 127;
    public static final int CORRIDOR_SYMB = 168;
    public static final int FLOOR_SYMB = 46;
    public static final int GAME_CONTROL_OPTION = '1';
    public static final int ENEMIES_OPTION = '2';
    public static final int ITEMS_OPTION = '3';
    public static final int MENU_OPTION = '4';
}
