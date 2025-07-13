package view;

import jcurses.system.CharColor;
import jcurses.system.Toolkit;

import static view.Defines.*;

public class View {
    /**
     * Печатает стартовый экран. Не конечная версия
     */
    public void printStart() {
        Toolkit.clearScreen(BLACK);
        Toolkit.printString("Rogue", 0, 0, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("Press S to start new game", 0, 1, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("Press L to load game", 0, 2, new CharColor(CharColor.BLACK, CharColor.BLUE));
    }

    /**
     * Печатает игровое поле. В зависимости от символа меняет его цвет.
     * @param field поле в котором содержиться тот или иной объект, закодированный символами. Каждый символ занимает один пиксель.
     */
    public void printField(char[][] field) {
        Toolkit.clearScreen(BLACK);
        for(int i = 0; i < LEVEL_HEIGHT; i++){
            for(int j = 0; j < LEVEL_WIDTH; j++) {
                char currentChar = field[i][j];
                CharColor color = WHITE;
                if(currentChar == WALL || currentChar == OGRE) {
                    color = YELLOW;
                } else if (currentChar == START) {
                    color = YELLOW;
                } else if (currentChar == EXIT || currentChar == ZOMBIE) {
                    color = GREEN;
                } else if (currentChar == CHARACTER) {
                    color = BLUE;
                } else if (currentChar == ENEMY || currentChar == VAMPIRE) {
                    color = RED;
                }
                Toolkit.printString(String.valueOf(currentChar), j, i, color);
            }
        }
//        printSome();
    }

    /**
     * Печатает сообщение об окончании игры. Не конечная версия
     */
    public void printEnd() {
        Toolkit.clearScreen(BLACK);
        Toolkit.printString("Press S to start new game", 0, 1, new CharColor(CharColor.BLACK, CharColor.BLUE));
        Toolkit.printString("Press ESC to exit game", 0, 2, new CharColor(CharColor.BLACK, CharColor.BLUE));
    }

    /**
     * Печатает данные персонажа.
     * @param charStats массив с данными персонажа
     */
    public void printStatusBar(int[] charStats) {
        String health = "Health " + charStats[0] + "(" + charStats[1] + ")";
        String agility = " Agility " + charStats[2];
        String strength = " Strength " + charStats[3];
        String sleep;
        if (charStats[4] == 1) {
            sleep = " Sleep: Yes";
        } else {
            sleep = " Sleep: No";
        }
        String statusBar = health + agility + strength + sleep;
        Toolkit.printString(statusBar, 0, 33, new CharColor(CharColor.BLACK, CharColor.WHITE));
    }
    /**
     * Тестовая функция
     */
    public void printDebug(String mes) {
            Toolkit.printString(mes, 0, 34, new CharColor(CharColor.BLUE, CharColor.BLACK));
    }
}
