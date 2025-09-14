package app;

import controller.Controller;
import model.GameSession;
import view.View;
import static dataLayer.DataLayer.loadCurrentSession;
import static model.Support.*;

/**
 * Класс обеспечивающий независимость компонентов MVC (по задумке)
 */
public class Facade {
    private GameSession newGame;
    private final Controller con_;
    private final View view_;

    /**
     * Конструктор
     */
    public Facade() {
        this.newGame = new GameSession();
        this.con_ = new Controller();
        this.view_ = new View();
    }

    /**
     * Основной игровой процесс объединяющий ввод, работу модели и отрисовку.
     * Позволяет начать игру заново не выходя из программы.
     */
    public void start() {
        view_.printStart();
        while (newGame.getStatusGame() != EXIT_GAME) {
            con_.input();
            newGame.processUserInput(con_.getUserInput());
            if (newGame.getStatusGame() == END_GAME) {
                view_.printEnd(newGame.getCurrentStatistics(), newGame.isWin());
                newGame.saveStatistics();
            } else if (newGame.getStatusGame() == EXIT_GAME) {

            } else if (newGame.getStatusGame() == INVENTORY_GAME) {
                processInventoryGame();
            } else if (newGame.getStatusGame() == STATISTIC_GAME) {
                view_.printStatistic(newGame.getStatisticsForView());
            } else if (newGame.getStatusGame() == OPTIONS_GAME) {
                processOptionGame();
            } else if (newGame.getStatusGame() == CREDITS_GAME) {
                view_.showCredits();
            } else if (newGame.getStatusGame() == START_GAME) {
                view_.printStart();
            } else if (newGame.getStatusGame() == LOAD_GAME) {
                processLoadGame();
            } else if (newGame.getStatusGame() != START_GAME) {
                view_.printField(newGame.getFieldForView());
                view_.printStatusBar(newGame.getStatusBar());
            }
        }

        con_.input();
    }

    /**
     * Метод отвечает за взаимодействия игрока с инвентарем.
     */
    private void processInventoryGame() {
        con_.defaultItemNumber();
        view_.printInventory(newGame.getInventoryForView(), con_.getItemNumber());
        while (newGame.getStatusGame() == INVENTORY_GAME) {
            con_.inputInventory(newGame.getInventoryForView().size());
            newGame.processInventory(con_.getUserInput(), con_.getItemNumber());
            view_.printInventory(newGame.getInventoryForView(), con_.getItemNumber());
        }
        view_.printField(newGame.getFieldForView());
        view_.printStatusBar(newGame.getStatusBar());
    }

    /**
     * Метод отвечает за взаимодействия игрока и подменю Опции в стартовом экране.
     */
    private void processOptionGame() {
        while (con_.getInputOption() != ESC_KEY) {
            view_.showSelectedOptions(con_.getInputOption());
            con_.inputOptions();
        }
        con_.setDefaultInputOption();
        newGame.processUserInput(con_.getUserInput());
        view_.printStart();
    }

    private void processLoadGame() {
        GameSession loadGame = loadCurrentSession();
        if (loadGame != null) {
            setNewGame(loadGame);
            newGame.loadGame();
            view_.printField(newGame.getFieldForView());
            view_.printStatusBar(newGame.getStatusBar());
        } else {
            view_.printStart();
            newGame.gameIsNotLoaded();
        }

    }

    private void setNewGame(GameSession newGame) {
        this.newGame = newGame;
    }
}
