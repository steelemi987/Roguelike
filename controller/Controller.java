package controller;

import jcurses.system.InputChar;
import jcurses.system.Toolkit;


public class Controller {
    private int userInput;

    public void input() {
        InputChar inputChar = Toolkit.readCharacter();
        userInput = inputChar.getCode();
    }

    public int getUserInput() {
        return userInput;
    }
}
