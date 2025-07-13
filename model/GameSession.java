package model;

import model.enemies.Enemy;
import model.enemies.Ghost;
import model.render.RenderForView;

import java.util.*;
import java.util.stream.Collectors;

import static model.Support.*;

/**
 * Класс на данном этапе объединяет все остальные классы для их взаимодействия. Похож на фасад, тоько есть собственные методы.
 * Может получиться громоздким, возможно стоит декопозировать(создать доп. классы).
 */
public class GameSession implements Observer{
    private List<Level> levels; // список уровней
//    private Fight fightClub;
    private char[][] field; // текущее игровое поле для отрисовки
    private int currentLevel; // текущий номер уровня
    private int status; // состояние игры
    private Character character; // перс
    private RenderForView render;

    public String debug = "NO HIT"; // ДЕБАГ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    /**
     * Конструктор
     */
    public GameSession() {
        levels = new ArrayList<>();
//        fightClub = new Fight();
        field = new char[LEVEL_HEIGHT][LEVEL_WIDTH];
        status = START_GAME;
        render = new RenderForView(field);
    }

    @Override
    public void updateCoordinate(Observable o, Coordinate curr){
        if (o instanceof Character) {
            Coordinate next = character.getPosition();
            char obj = getObjectOnCoordinate(curr);
            field[curr.getY()][curr.getX()] = obj;
            field[next.getY()][next.getX()] = CHARACTER;
        } else if (o instanceof Enemy) {
            Enemy enemy = (Enemy) o;
            Coordinate next = enemy.getPosition();
            char obj = ' ';
//            obj = getItemIfExist(curr);
            if (obj == ' ') {
                obj = FLOOR;
            }
            field[curr.getY()][curr.getX()] = obj;
            field[next.getY()][next.getX()] = (char) enemy.getType();
        }
    }

    public char getObjectOnCoordinate(Coordinate curr) {
        char obj = getCorridorIfExist(curr);
        if (obj == ' ') {
            obj = getDoorIfExist(curr);
        }
        if(obj == ' '){
            // obj = getItemIfExist(curr);
        }
        if(obj == ' '){
            obj = FLOOR;
        }
        return obj;
    }

    public char getCorridorIfExist(Coordinate curr) {
        char obj = ' ';
        for(Corridor c : levels.get(currentLevel).getCorridors()){
            for(Coordinate co : c.getPoints()){
                if(curr.equals(co)){
                    obj = CORRIDOR;
                    break;
                }
            }
            if(c.getPoints().contains(curr)) {
                obj = CORRIDOR;
                break;
            }
        }
        return obj;
    }

    public char getDoorIfExist(Coordinate curr) {
        char obj = ' ';
        for(Room r : levels.get(currentLevel).getRooms()) {
            for(Coordinate co : r.getDoors()) {
                if(curr.equals(co)){
                    obj = DOOR;
                    break;
                }
            }
        }
        return obj;
    }

    @Override
    public void updateDead(Observable o) {
        if (o instanceof Character) {
            status = END_GAME;
        } else if (o instanceof Enemy) {
            Enemy enemy = (Enemy) o;
            Coordinate curr = enemy.getPosition();
            char obj = ' ';
//            obj = getItemIfExist(curr);
            if (obj == ' ') {
                obj = FLOOR;
            }
            field[curr.getY()][curr.getX()] = obj;
            levels.get(currentLevel).getEnemies().remove(enemy);
        }
    }

    /**
     * Генерация уровней. Если игра не первая в этой сессии список уровней сначала нужно очистить.
     * Поочередно создаем сам уровень, комнаты, начальную и конечную позиции, объединяем комнаты корридорами.
     */
    public void createLevels() {
        if (!levels.isEmpty()) {
            levels.clear();
        }
        for(int i = 0; i < MAXCOUNTLEVELS; i++){
            levels.add(new Level()
                    .createRooms()
                    .createStartAndExitPosition()
                    .createCorridors()
                    .createEnemies(i, this)
            );
            /**
             * DEBUG
             */
//            for(Enemy e : levels.get(i).getEnemies()) {
//                e.setGame(this);
//            }
        }
    }

    /**
     * Настройка новой игры. Можно использовать несколько раз за запуск программы.
     */
    public void startNewGame() {
        status = NEW_GAME;
        currentLevel = 0;
        createLevels();
        character = new Character(levels.get(currentLevel).getStartPosition(), this);
        render.setCharacter(character);
        render.clearFieldsForNewLevel();
        render.setEnemies(getEnemyList());
//        render.setItems(getItemList());
        render.setExit(getExitCoordinate());
        renderField();
    }

    /**
     * Обработка клавиши получаемой от контроллера.
     * Список keyMove содержит клавиши движения для сокращенного сравнения.
     * @param userInput код клавиши в формате ASCI
     */
    public void processUserInput(int userInput) {
        List<Integer> keyMove = new ArrayList<Integer>(Arrays.asList(UP_KEY, DOWN_KEY, LEFT_KEY, RIGHT_KEY));
        if((status == START_GAME || status == END_GAME) && userInput == 's') {
            startNewGame();
        } else if(status == NEW_GAME && keyMove.contains(userInput)) {
//            nextLevel_test();
            moveChar(userInput);
            enemyTurn();
//            if (character.isDead()) { // Не учитывает смерть от контратаки огра
//                status = END_GAME;
//            }
        } else if (userInput == ESC_KEY) {
            status = EXIT_GAME;
        }
    }

    /**
     * Метод отвечает за передвижения игрока. Сначало получаем координаты предполагаемой точки перемещения,
     * отталкиваясь от позиции персонажа. Если можно переместиться на данную точку, изменяем координаты персонажа.
     * Если точка совпадает с позицией выхода с уровня, переходим на следующий уровень.
     * @param userInput код клавиши в формате ASCI
     */
    public void moveChar(int userInput) {
        if (!character.isSleep()) {
            Coordinate nextPos = new Coordinate(character.getPosition().getX(), character.getPosition().getY());
            switch (userInput) {
                case UP_KEY:
                    nextPos.setY(nextPos.getY() - 1);
                    break;
                case DOWN_KEY:
                    nextPos.setY(nextPos.getY() + 1);
                    break;
                case LEFT_KEY:
                    nextPos.setX(nextPos.getX() - 1);
                    break;
                case RIGHT_KEY:
                    nextPos.setX(nextPos.getX() + 1);
                    break;
            }
            if (isPointEnemy(nextPos)) {
                character.fight(nextPos, levels.get(currentLevel).getEnemies());
//                levels.get(currentLevel).getEnemies().removeIf(Enemy::isDead);
//                renderField();
            } else if (isPointFree(nextPos)) {
                character.setPosition(nextPos);
//                renderField();
            } else if (isPointExit(nextPos)) {
                nextLevel_test();
//            renderField();
            }
        }
        character.setSleep(false);
    }

    /**
     * Проверка на наличие врага на точке. Смотрим символ на игровом поле по координатам.
     * @param point точка с координатами поля, в котором находиться объект сравнения.
     */
    public boolean isPointEnemy(Coordinate point) {
        int obj = field[point.getY()][point.getX()];
        List<Integer> enemy = new ArrayList<>(Arrays.asList(ZOMBIE, VAMPIRE, GHOST, OGRE, SNAKE_MAGE));
        return enemy.contains(obj);
    }

    /**
     * Проверка на возможность передвижения. Смотрим символ на игровом поле по координатам.
     * Объекты по которым можно двигаться: FLOOR - пол, DOOR - дверь, CORRIDOR - корридор. (Запихнуть в макросы)
     * @param point точка с координатами поля, в котором находиться объект сравнения.
     */
    public boolean isPointFree(Coordinate point) {
        char obj = field[point.getY()][point.getX()];
        return obj == FLOOR || obj == DOOR || obj == CORRIDOR;
    }

    /**
     * Проверка на выход с уровня. Смотрим символ на игровом поле по координатам.
     * 'Е' - выход. (Запихнуть в макросы)
     * @param point точка с координатами поля, в котором находиться объект сравнения.
     */
    public boolean isPointExit(Coordinate point) {
//        char obj = field[point.getY()][point.getX()];
        return point.equals(levels.get(currentLevel).getExitPosition());
    }

    /**
     * Ход противников. Вызываем соответствующий метод у врагов из списка текущего уровня и применяем изменения методом
     * renderField().
     */
    public void enemyTurn() {
        for(Enemy e : levels.get(currentLevel).getEnemies()) {
            if (status != END_GAME) {
                e.act(character, field);
            }
//            renderField();
        }
    }

    /**
     * Метод отвечает за отражение состояния модели для дальнейшей отрисовки. Результатом является массив символов field,
     * в котором каждый символ является тем или иным объектом. Также используется в некоторых методах модели.
     * Очистка поля. Заполнение комнатами, корридорами и т. д.
     */
    public void renderField() {
        clearField();
        for(Room room : levels.get(currentLevel).getRooms()){
            renderRoom(room);
        }
        for(Corridor corridor : levels.get(currentLevel).getCorridors()){
            renderCorridor(corridor);
        }
//        field[levels.get(currentLevel).getStartPosition().getY()][levels.get(currentLevel).getStartPosition().getX()] = START; // remove?
        field[levels.get(currentLevel).getExitPosition().getY()][levels.get(currentLevel).getExitPosition().getX()] = EXIT;
        field[character.getPosition().getY()][character.getPosition().getX()] = CHARACTER;
        renderEnemies();
    }

    /**
     * Метод заполняет все поле пробелами, символом с которым не ассоциируется ни один из объектов.
     */
    public void clearField() {
        for (int i = 0; i < LEVEL_HEIGHT; i++) {
            for (int j = 0; j < LEVEL_WIDTH; j++) {
                field[i][j] = ' ';
            }
        }
    }

    /**
     * Метод заполняет поле
     * WALL - стенами, FLOOR - полами, DOOR - дверьми.
     */
    public void renderRoom(Room room) {
        int width = room.getCornerRight().getX() - room.getCornerLeft().getX();
        int height = room.getCornerRight().getY() - room.getCornerLeft().getY();
        for(int i = 0; i <= width; i++) {
            field[room.getCornerLeft().getY()][i + room.getCornerLeft().getX()] = WALL;
            field[room.getCornerRight().getY()][i + room.getCornerLeft().getX()] = WALL;
        }
        for(int i = 0; i <= height; i++) {
            field[i + room.getCornerLeft().getY()][room.getCornerLeft().getX()] = WALL;
            field[i + room.getCornerLeft().getY()][room.getCornerRight().getX()] = WALL;
        }

        for(int i = room.getCornerLeft().getY() + 1; i < room.getCornerLeft().getY() + height; i++){
            for(int j = room.getCornerLeft().getX() + 1; j < room.getCornerLeft().getX() + width; j++){
                field[i][j] = FLOOR;
            }
        }

        for(Coordinate d : room.getDoors()) {
            field[d.getY()][d.getX()] = DOOR;
        }
    }

    /**
     * Метод заполняет поле
     * CORRIDOR - коридорами.
     */
    public void renderCorridor(Corridor corridor) {
        for(Coordinate c : corridor.getPoints()) {
            field[c.getY()][c.getX()] = CORRIDOR;
        }
    }

    /**
     * Метод добавляет врагов на поле
     */
    public void renderEnemies() {
        for(Enemy e : levels.get(currentLevel).getEnemies()){
            field[e.getPosition().getY()][e.getPosition().getX()] = (char) e.getType(); // change to 'X'
        }
    }

    /**
     * Передает данные персонажа в виде массива целых чисел для использования в представлении.
     * @return массив с показателями персонажа
     */
    public int[] getStatusBar() {
        int[] charStats = new int[5];
        charStats[0] = character.getHealth();
        charStats[1] = character.getMaxHealth();
        charStats[2] = character.getAgility();
        charStats[3] = character.getStrength();
        if (character.isSleep()) {
            charStats[4] = 1;
        } else {
            charStats[4] = 0;
        }
        return charStats;
    }

    public char[][] getField() {
        return field;
    }

    public int getStatusGame() {
        return status;
    }

    /**
     * Метод увеличивает текущий уровень на один. Выдает ошибку если уровень выше максимального.
     * CORRIDOR - коридорами.
     */
    public void eraseCurrentLevel() throws Exception {
        if (currentLevel < 20) {
            this.currentLevel++;
        } else {
            throw new Exception("Level >= 21");
        }
    }

    /**
     * Метод отвечает за переход на новый уровень. Если уровень превысил максимальный меняет статус игры на окончание.
     * CORRIDOR - коридорами.
     */
    public void nextLevel_test(){
        try {
            eraseCurrentLevel();
            character.setPosition(levels.get(currentLevel).getStartPosition());
            render.clearFieldsForNewLevel();
            render.setEnemies(getEnemyList());
//        render.setItems(getItemList());
            render.setExit(getExitCoordinate());
            renderField();
        } catch (Exception e) {
            status = END_GAME;
        }
    }

    public Character getCharacter() {
        return character;
    }

    public char[][] getFieldForView() {
        return render.getField(this);
    }

    public Room getRoomWithNumb(int numRoom) {
        return levels.get(currentLevel).getRooms().get(numRoom);
    }

    public boolean isGhostVisibleOnCoordinate(Coordinate position) {
        for(Enemy e : levels.get(currentLevel).getEnemies()) {
            if (e.getPosition().equals(position)) {
                if(e instanceof Ghost g) {
                    return g.isVisible();
                }
            }
        }
        return true;
    }

    public ArrayList<Enemy> getEnemyList() {
        return levels.get(currentLevel).getEnemies();
    }

    public Coordinate getExitCoordinate() {
        return levels.get(currentLevel).getExitPosition();
    }


} // end
