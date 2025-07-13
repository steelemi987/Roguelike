package model;

import controller.Controller;
import view.View;

import static model.Support.*;

/**
 * Класс обеспечивающий независимость компонентов MVC (по задумке)
 */
public class Facade {
    private GameSession newGame;
    private Controller con_;
    private View view_;
//    private char[][] field;

    /**
     * Конструктор
     */
    public Facade() {
        this.newGame = new GameSession();
        this.con_ = new Controller();
        this.view_ = new View();
//        this.field = this.newGame.getField();
    }

    /**
     * Основной игровой процесс объединяющий ввод, работу модели и отрисовку
     * Позволяет начать игру заново не выходя из программы.
     */
    public void start() {
        view_.printStart();
        while (newGame.getStatusGame() != EXIT_GAME) {
            con_.input();
            newGame.processUserInput(con_.getUserInput());
            if (newGame.getStatusGame() == END_GAME) {
                view_.printEnd();
            } else if (newGame.getStatusGame() == EXIT_GAME) {

            } else if (newGame.getStatusGame() != START_GAME) {
                view_.printField(newGame.getFieldForView()); // newGame.getFieldForView()
                view_.printStatusBar(newGame.getStatusBar());
                view_.printDebug(newGame.debug);
            }
        }
        con_.input();
    }
}
