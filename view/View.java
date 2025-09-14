package view;

import jcurses.system.CharColor;
import jcurses.system.Toolkit;

import java.util.ArrayList;

import static view.Defines.*;

public class View {
    /**
     * Печатает стартовый экран.
     */
    public void printStart() {
        Toolkit.clearScreen(BLACK);
        Toolkit.printString("Welcome to JavaGame RogueLike. Please make your choice and press key:", 0, 0, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("1: Start new game", 0, 1, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("2: Load game", 0, 2, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("3: View statistics", 0, 3, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("4: Options", 0, 4, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("5: Credits", 0, 5, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("Esc: Exit", 0, 6, new CharColor(CharColor.BLACK, CharColor.BLUE));

    }

    /**
     * Печатает игровое поле. В зависимости от символа меняет его цвет.
     * @param field поле в котором содержится тот или иной объект, закодированный символами. Каждый символ занимает один пиксель.
     */
    public void printField(char[][] field) {
        Toolkit.clearScreen(BLACK);
        for(int i = 0; i < LEVEL_HEIGHT; i++){
            for(int j = 0; j < LEVEL_WIDTH; j++) {
                char currentChar = field[i][j];
                CharColor color = getColor(currentChar);
                currentChar = getPrintChar(currentChar);
                Toolkit.printString(String.valueOf(currentChar), j, i + FIELD_STRING, color);
            }
        }
    }

    /**
     * Метод определяет цвет печатаемого символа в зависимости от типа самого символа.
     * @param currentChar символ, цвет которого определяем
     * @return цвет символа в палитре библиотеки JCurses
     */
    private CharColor getColor(char currentChar) {
        CharColor color = WHITE;
        if(currentChar == WALL || currentChar == OGRE || currentChar == ITEM || currentChar == START) {
            color = YELLOW;
        } else if (currentChar == EXIT || currentChar == ZOMBIE) {
            color = GREEN;
        } else if (currentChar == CHARACTER) {
            color = BLUE;
        } else if (currentChar == VAMPIRE) {
            color = RED;
        }
        return color;
    }

    /**
     * Метод определяет вид печатаемого на экран символа в зависимости от типа самого символа.
     * @param currentChar типа символа
     * @return вид выводимого на экран символа
     */
    private char getPrintChar(char currentChar) {
        return switch (currentChar) {
            case WALL -> WALL_SYMB;
            case EXIT -> EXIT_SYMB;
            case CHARACTER -> CHARACTER_SYMB;
            case DOOR -> DOOR_SYMB;
            case ITEM -> ITEM_SYMB;
            case CORRIDOR -> CORRIDOR_SYMB;
            case FLOOR -> FLOOR_SYMB;
            default -> currentChar;
        };
    }

    /**
     * Печатает сообщение об окончании игры. Не конечная версия
     */
    public void printEnd(int[] stat, boolean win) {
        Toolkit.clearScreen(BLACK);
        int numbLine = 1;
        if (win) {
            Toolkit.printString("Congratulations! You win", 0, numbLine, new CharColor(CharColor.BLACK, CharColor.BLUE));
        } else {
            Toolkit.printString("You loose!", 0, numbLine, new CharColor(CharColor.BLACK, CharColor.BLUE));
        }
        numbLine++;
        Toolkit.printString("Your statistics:", 0, numbLine, new CharColor(CharColor.BLACK, CharColor.BLUE));
        numbLine++;
        int i = 0;
        Toolkit.printString("Treasures: " + stat[i++], 0, numbLine + i, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("Level: " + stat[i++], 0, numbLine + i, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("Defeated Enemies: " + stat[i++], 0, numbLine + i, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("Eaten Food: " + stat[i++], 0, numbLine + i, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("Drunken Elixirs: " + stat[i++], 0, numbLine + i, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("Read Scrolls: " + stat[i++], 0, numbLine + i, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("Dealt Hits: " + stat[i++], 0, numbLine + i, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("Received Hits: " + stat[i++], 0, numbLine + i, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("Steps Taken: " + stat[i++], 0, numbLine + i, new CharColor(CharColor.BLACK, CharColor.BLUE));
        numbLine = numbLine + i + 1;
        Toolkit.printString("Press any key to return in main menu", 0, numbLine, new CharColor(CharColor.BLACK, CharColor.BLUE));

    }

    /**
     * Печатает данные персонажа.
     * @param charStats массив с данными персонажа
     */
    public void printStatusBar(int[] charStats) {
        String health = "Health " + charStats[0] + "(" + charStats[1] + ")";
        String agility = " Agility " + charStats[2];
        String strength = " Strength " + charStats[3];
        String treasure = " Treasure " + charStats[4];
        String level = " Level " + charStats[5];

        String statusBar = health + agility + strength + treasure + level;
        Toolkit.printString(statusBar, 0, STATUS_BAR_STRING, new CharColor(CharColor.BLACK, CharColor.WHITE));
    }

    /**
     * Метод печатает список имен предметов. Выбранный предмет печатает зеленым цветом. Если переданный список предметов
     * пустой, печатает соответствующее сообщение.
     * @param items список имен предметов для распечатки
     * @param numberOfSelectedItem номер выбранного предмета
     */
    public void printInventory(ArrayList<String> items, int numberOfSelectedItem) {
        int i = 0;
        cleanInventory();
        if(numberOfSelectedItem >= items.size()) {
            numberOfSelectedItem--;
        }
        for(String s : items){
            if(i == numberOfSelectedItem) {
                Toolkit.printString("-" + i + "-" + s, 33, i, new CharColor(CharColor.BLACK, CharColor.GREEN));
            } else {
                Toolkit.printString("-" + i + "-" + s, 33, i, new CharColor(CharColor.BLACK, CharColor.WHITE));
            }
            i++;
        }
        if (items.isEmpty()) {
            Toolkit.printString("Empty", 33, i, new CharColor(CharColor.BLACK, CharColor.WHITE));
        }
        i = 10;
        Toolkit.printString("Press 'q' - use item", 33, i, new CharColor(CharColor.BLACK, CharColor.WHITE));
        i++;
        Toolkit.printString("Press 'r' - drop item", 33, i, new CharColor(CharColor.BLACK, CharColor.WHITE));
        i++;
        Toolkit.printString("Press 'ESC' - exit inventory", 33, i, new CharColor(CharColor.BLACK, CharColor.WHITE));
    }

    public void printStatistic(Integer[][] stat) {
        Toolkit.clearScreen(BLACK);
        int step = "DefeatedEnemies:".length();
        int j = 0;
        Toolkit.printString("Number:", j, 0, new CharColor(CharColor.BLACK, CharColor.WHITE));
        j+=step;
        Toolkit.printString("Treasures:", j, 0, new CharColor(CharColor.BLACK, CharColor.WHITE));
        j+=step;
        Toolkit.printString("Level:", j, 0, new CharColor(CharColor.BLACK, CharColor.WHITE));
        j+=step;
        Toolkit.printString("DefeatedEnemies:", j, 0, new CharColor(CharColor.BLACK, CharColor.WHITE));
        j+=step;
        Toolkit.printString("EatenFood:", j, 0, new CharColor(CharColor.BLACK, CharColor.WHITE));
        j+=step;
        Toolkit.printString("DrunkElixirs:", j, 0, new CharColor(CharColor.BLACK, CharColor.WHITE));
        j+=step;
        Toolkit.printString("ReadScrolls:", j, 0, new CharColor(CharColor.BLACK, CharColor.WHITE));
        j+=step;
        Toolkit.printString("DealtHits:", j, 0, new CharColor(CharColor.BLACK, CharColor.WHITE));
        j+=step;
        Toolkit.printString("ReceivedHits:", j, 0, new CharColor(CharColor.BLACK, CharColor.WHITE));
        j+=step;
        Toolkit.printString("StepsTaken:", j, 0, new CharColor(CharColor.BLACK, CharColor.WHITE));

        for (int i = 0; i < stat.length; i++) {
            Toolkit.printString(String.valueOf(i + 1), 0, 1 + i, new CharColor(CharColor.BLACK, CharColor.WHITE));
            for (int k = 0; k < stat[i].length; k++) {
                Toolkit.printString(String.valueOf(stat[i][k]), (k + 1) * step, 1 + i, new CharColor(CharColor.BLACK, CharColor.WHITE));
            }
        }
    }

    /**
     * Метод определяет что нужно вывести на экран в зависимости от полученной опции.
     * @param option тип сообщения
     */
    public void showSelectedOptions(int option) {
        switch (option) {
            case GAME_CONTROL_OPTION:
                showGameControlOptions();
                break;
            case ENEMIES_OPTION:
                showEnemiesOptions();
                break;
            case ITEMS_OPTION:
                showItemsOptions();
                break;
            case MENU_OPTION:
                showMenuOptions();
                break;
        }
    }

    /**
     * Метод выводит на экран информацию об кнопках управления в игре.
     */
    private void showGameControlOptions() {
        Toolkit.clearScreen(BLACK);
        Toolkit.printString("Game control is:", 0, 0, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("W - Forward", 0, 1, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("A - Left.", 0, 2, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("D - Right.", 0, 3, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("I - Inventory general.", 0, 4, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("ESC - Exit.", 0, 5, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("H - Inventory of weapons.", 0, 6, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("J - Inventory of foods.", 0, 7, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("K - Inventory of elixirs.", 0, 8, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("E - Inventory of scrolls.", 0, 8, new CharColor(CharColor.BLACK, CharColor.BLUE));
    }

    /**
     * Метод выводит на экран информацию о противниках в игре.
     */
    private void showEnemiesOptions() {
        Toolkit.clearScreen(BLACK);
        Toolkit.printString("There are 5 types of enemies:", 0, 0, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("First type: Zombie (symbol: green Z): Low Agility; medium Strength and Hostility; high Health.", 0, 1, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("Second type: Vampire (symbol: red V): High Agility, Aggression and Health; medium Strength; deducts a certain amount of the player's maximum Health on a successful attack; the first attack on a Vampire always misses.", 0, 3, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("Third type: Ghost (symbol: white G): High Agility; low Strength, Hostility, and Health; constantly teleports around the room and periodically becomes invisible until the player enters combat", 0, 3, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("Fourth type: Ogre (symbol: yellow O): Moves two cells at a time around the room; very high Strength and Health, but rests for a turn after each attack, then guarantees a counterattack; low Agility; medium Hostility.", 0, 5, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("Fifth type: Snake Mage (symbol: white s): Very high Agility; moves diagonally across the map, constantly changing direction; each successful attack has a chance to put the player to sleep for one turn; high Hostility", 0, 7, new CharColor(CharColor.BLACK, CharColor.BLUE));
    }

    /**
     * Метод выводит на экран информацию о предметах в игре.
     */
    private void showItemsOptions() {
        Toolkit.clearScreen(BLACK);
        Toolkit.printString("Items you may encounter:", 0, 0, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("Treasures: have a value, are accumulated and affect the final score", 0, 1, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("Food: restores health by a certain amount.", 0, 2, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("Elixirs: temporarily increase one of the following attributes: Agility, Strength, or maximum Health.", 0, 3, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("Scrolls: permanently increases one of: Agility, Strength, or maximum Health.", 0, 4, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("Weapons: have a Strength attribute; when switching weapons, the damage calculation formula changes.", 0, 5, new CharColor(CharColor.BLACK, CharColor.BLUE));
    }

    /**
     * Метод выводит на экран меню состоящее из типа полезной информации для игрока.
     */
    private void showMenuOptions() {
        Toolkit.clearScreen(BLACK);
        Toolkit.printString("Select your choice and press key:", 0, 0, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("1: Show game control", 0, 1, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("2: Show description of enemies", 0, 2, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("3: Show description of items", 0, 3, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("4: Turn to this menu", 0, 4, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("ESC: Turn to main menu", 0, 5, new CharColor(CharColor.BLACK, CharColor.BLUE));
    }

    /**
     * Метод выводит на экран информацию о создателях игры.
     */
    public void showCredits() {
        Toolkit.clearScreen(BLACK);
        Toolkit.printString("Java game project created by Steelemi, Dennetca and Kadabrac.", 0, 0, new CharColor(CharColor.BLACK, CharColor.BLUE));
    }

    /**
     * Очищает экран вывода предметов.
     */
    public void cleanInventory() {
        String s = "                                         ";
        for(int i = 0; i < 9; i++) {
            Toolkit.printString(s, 33, i, new CharColor(CharColor.BLACK, CharColor.BLACK));
        }
    }
}
