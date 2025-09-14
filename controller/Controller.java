package controller;

import jcurses.system.InputChar;
import jcurses.system.Toolkit;

public class Controller {
    public static final int SCROLL_UP_ITEMS = 'w';
    public static final int SCROLL_DOWN_ITEMS = 's';
    public static final int USE_ITEM = 'q';
    public static final int DROP_ITEM = 'r';
    public static final int INVENTORY_KEY = 'i';
    public static final int ESC_KEY = 27;
    public static final int KEY_0 = 48;
    public static final int KEY_8 = 56;
    public static final int MENU = '4';

    /// пользовательский ввод
    private int userInput;
    /// номер предмета передаваемый с контроллера для взаимодействия с предметом в списке в модели
    private int itemNumber;
    private int inputOption = MENU;

    public void input() {
        InputChar inputChar = Toolkit.readCharacter();
        userInput = inputChar.getCode();
    }

    /**
     * Метод, который получает номер предмета от пользователя и тип взаимодействия с предметом (использовать или
     * выкинуть из рюкзака), а также тип списка предметов для передачи в модель. Поддерживает выбор предмета с помощью
     * клавиш 'w', 's' и цифр 0-8.
     * @param quantityItem количество предметов в списке предметов
     */
    public void inputInventory(int quantityItem) {
        boolean flag = true;
        if(itemNumber >= quantityItem) {
            itemNumber--;
            if(itemNumber < 0) {
                itemNumber = 0;
            }
        }
        while(flag) {
            userInput = Toolkit.readCharacter().getCode();
            if(userInput == SCROLL_UP_ITEMS) {
                itemNumber--;
                if(itemNumber < 0) {
                    itemNumber = 0;
                }
                flag = false;
            } else if (userInput == SCROLL_DOWN_ITEMS) {
                itemNumber++;
                if(itemNumber > quantityItem) {
                    itemNumber = quantityItem;
                }
                flag = false;
            } else if (userInput >= KEY_0 && userInput <= KEY_8) {
                if (userInput % KEY_0 < quantityItem) {
                    itemNumber = userInput % KEY_0;
                    flag = false;
                }
            } else if (userInput == USE_ITEM || userInput == DROP_ITEM || userInput == INVENTORY_KEY || userInput == ESC_KEY) {
                flag = false;
            }
        }
    }

    /**
     * Принимает ввод опций из стартового экрана.
     */
    public void inputOptions() {
        InputChar inputChar = Toolkit.readCharacter();
        inputOption = inputChar.getCode();
        if (inputOption == ESC_KEY) {
            userInput = inputOption;
        }
    }

    public int getUserInput() {
        return userInput;
    }

    public int getItemNumber() {
        return itemNumber;
    }

    public int getInputOption() {
        return inputOption;
    }

    /**
     * Метод для обнуления номера предметов
     */
    public void defaultItemNumber() {
        itemNumber = 0;
    }

    /**
     * Метод переводит ввод опций из меню старта в стандартное положение.
     */
    public void setDefaultInputOption() {
        inputOption = MENU;
    }
}
