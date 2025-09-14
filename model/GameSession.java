package model;

import java.util.*;

import dataLayer.Statistics;
import model.character.Character;
import model.enemies.Enemy;
import model.interfaces.Observable;
import model.interfaces.Observer;
import model.items.Item;
import model.items.Treasure;
import model.level.Coordinate;
import model.level.Corridor;
import model.level.Level;
import model.level.Room;
import model.render.RenderForView;
import static dataLayer.DataLayer.saveCurrentSession;
import static model.Support.*;

public class GameSession implements Observer {
    /// список уровней
    private List<Level> levels;
    /// текущее игровое поле для отрисовки
    private final char[][] field;
    /// текущий номер уровня
    private int currentLevel;
    /// состояние игры
    private int status;
    /// персонаж
    private model.character.Character character;
    private RenderForView render;
    private int inventoryType;
    private boolean win;

    public GameSession() {
        levels = new ArrayList<>();
        field = new char[LEVEL_HEIGHT][LEVEL_WIDTH];
        status = START_GAME;
        render = new RenderForView(field);
    }

    @Override
    public void updateCoordinate(model.interfaces.Observable o, Coordinate curr){
        if (o instanceof model.character.Character) {
            Coordinate next = character.getPosition();
            char obj = getObjectOnCoordinate(curr);
            field[curr.getY()][curr.getX()] = obj;
            field[next.getY()][next.getX()] = CHARACTER;
        } else if (o instanceof Enemy enemy) {
            Coordinate next = enemy.getPosition();
            field[curr.getY()][curr.getX()] = (char) FLOOR;
            field[next.getY()][next.getX()] = (char) enemy.getType();
        }
    }

    private char getObjectOnCoordinate(Coordinate curr) {
        char obj = getCorridorIfExist(curr);
        if (obj == ' ') {
            obj = getDoorIfExist(curr);
        }
        if(obj == ' '){
            obj = FLOOR;
        }
        return obj;
    }

    private char getCorridorIfExist(Coordinate curr) {
        char obj = ' ';
        for(Corridor c : levels.get(currentLevel).getCorridors()){
            if(c.getPoints().contains(curr)) {
                obj = CORRIDOR;
                break;
            }
        }
        return obj;
    }

    private char getDoorIfExist(Coordinate curr) {
        char obj = ' ';
        for(Room r : levels.get(currentLevel).getRooms()) {
            if(r.getDoors().contains(curr)){
                obj = DOOR;
                break;
            }
        }
        return obj;
    }

    @Override
    public void updateDead(Observable o) {
        if (o instanceof model.character.Character) {
            status = END_GAME;
            win = false;
        } else if (o instanceof Enemy enemy) {
            Coordinate curr = enemy.getPosition();
            field[curr.getY()][curr.getX()] = (char) FLOOR;
            character.pickTreasure(new Treasure(enemy));
            levels.get(currentLevel).getEnemies().remove(enemy);
            character.increaseDefeatedEnemies();
        }
    }

    @Override
    public void updateItem(Item item, int action) {
        if (action == REMOVE_ITEM) {
            getItemList().remove(item);
        } else if (action == ADD_ITEM) {
            ArrayList<Coordinate> neighboringPoints = new ArrayList<>();
            addNeighboringPoints(neighboringPoints);
            for (Coordinate c : neighboringPoints) {
                if(field[c.getY()][c.getX()] == FLOOR || field[c.getY()][c.getX()] == ENEMY) {
                    if(addItemInLevel(item, c)){
                        character.removeItemFromBackpack(item);
                        break;
                    }
                }
            }
        }
    }

    private void addNeighboringPoints(ArrayList<Coordinate> neighboringPoints) {
        int cordX = character.getPosition().getX() - 1;
        int cordY = character.getPosition().getY() - 1;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if ((i != 1 || j != 1) && isPointOnBound(cordX + j, cordY + i)) {
                    neighboringPoints.add(new Coordinate(cordX + j, cordY + i));
                }
            }
        }
    }

    private boolean isPointOnBound(int x, int y) {
        return x >= 0 && y >= 0 && x < LEVEL_WIDTH && y < LEVEL_HEIGHT;
    }

    private boolean addItemInLevel(Item item, Coordinate c) {
        boolean freePosition = true;
        for (Item i : getItemList()) {
            if(i.getPosition().equals(c) || isPointExit(c)){
                freePosition = false;
            }
        }
        if(freePosition) {
            item.setPosition(c);
            getItemList().add(item);
            return true;
        }
        return false;
    }

    private void createLevels() {
        if (!levels.isEmpty()) {
            levels.clear();
        }
        for(int i = 0; i < MAX_COUNT_LEVELS; i++){
            levels.add(new Level()
                    .createRooms()
                    .createStartAndExitPosition()
                    .createCorridors()
                    .createEnemies(i, this)
                    .createItems(i)
            );
        }
    }

    private void startNewGame() {
        status = NEW_GAME;
        currentLevel = 0;
        createLevels();
        character = new model.character.Character(levels.get(currentLevel).getStartPosition(), this);
        render.setCharacter(character);
        render.clearFieldsForNewLevel();
        render.setEnemies(getEnemyList());
        render.setItems(getItemList());
        render.setExit(getExitCoordinate());
        renderField();
    }

    public void loadGame() {
        status = NEW_GAME;
        character.registerObserver(this);
        for (Level level : getLevels()) {
            for (Enemy enemy : level.getEnemies()) {
                enemy.registerObserver(this);
            }
        }
        render.setCharacter(character);
        render.setEnemies(getEnemyList());
        render.setItems(getItemList());
        render.setExit(getExitCoordinate());
        renderField();
    }

    public void processUserInput(int userInput) {
        List<Integer> keyMove = new ArrayList<>(Arrays.asList(UP_KEY, DOWN_KEY, LEFT_KEY, RIGHT_KEY));
        List<Integer> keyInventory = new ArrayList<>(Arrays.asList(INVENTORY_KEY, WEAPON_KEY, FOOD_KEY, ELIXIR_KEY, SCROLL_KEY));
        if(status == START_GAME && userInput == START_KEY) {
            startNewGame();
        } else if(status == START_GAME && userInput == LOAD_KEY) {
            status = LOAD_GAME;
        } else if(status == START_GAME && userInput == STATISTICS_KEY) {
            status = STATISTIC_GAME;
        } else if(status == START_GAME && userInput == OPTIONS_KEY) {
            status = OPTIONS_GAME;
        } else if(status == START_GAME && userInput == CREDITS_KEY) {
            status = CREDITS_GAME;
        } else if((status == STATISTIC_GAME || status == OPTIONS_GAME || status == CREDITS_GAME) && userInput == ESC_KEY) {
            status = START_GAME;
        } else if(status == NEW_GAME && keyMove.contains(userInput)) {
            moveChar(userInput);
            enemyTurn();
        } else if (status == NEW_GAME && keyInventory.contains(userInput)) {
            status = INVENTORY_GAME;
            inventoryType = userInput;
        } else if (status == NEW_GAME && userInput == ESC_KEY) {
            status = EXIT_GAME;
            saveCurrentSession(this);
        } else if (status == END_GAME) {
            status = START_GAME;
        } else if (userInput == ESC_KEY) {
            status = EXIT_GAME;
        }
    }

    public void processInventory(int userInput, int numberItem) {
        if(userInput == ESC_KEY || userInput == INVENTORY_KEY) {
            status = NEW_GAME;
        } else if (userInput == USE_ITEM) {
            character.useItem(numberItem, inventoryType);
        } else if (userInput == DROP_ITEM) {
            character.dropItem(numberItem, inventoryType);
        }
    }

    private void moveChar(int userInput) {
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
            } else if (isPointFree(nextPos)) {
                character.setPosition(nextPos);
                Item item = getItemOnCoordinate(nextPos);
                if(item != null) {
                    character.pickItemIfCan(item);
                }
            } else if (isPointExit(nextPos)) {
                nextLevel();
            }
        }
        character.updateBonus();
        character.setSleep(false);
    }

    private boolean isPointEnemy(Coordinate point) {
        int obj = field[point.getY()][point.getX()];
        List<Integer> enemy = new ArrayList<>(Arrays.asList(ZOMBIE, VAMPIRE, GHOST, OGRE, SNAKE_MAGE));
        return enemy.contains(obj);
    }

    private boolean isPointFree(Coordinate point) {
        char obj = field[point.getY()][point.getX()];
        return obj == FLOOR || obj == DOOR || obj == CORRIDOR;
    }

    private Item getItemOnCoordinate(Coordinate point) {
        Item item = null;
        for(Item i : getItemList()) {
            if(i.getPosition().equals(point)) {
                item = i;
                break;
            }
        }
        return item;
    }

    private boolean isPointExit(Coordinate point) {
        return point.equals(levels.get(currentLevel).getExitPosition());
    }

    private void enemyTurn() {
        for(Enemy e : levels.get(currentLevel).getEnemies()) {
            if (status != END_GAME) {
                e.act(character, field);
            }
        }
    }

    private void renderField() {
        clearField();
        for(Room room : levels.get(currentLevel).getRooms()){
            renderRoom(room);
        }
        for(Corridor corridor : levels.get(currentLevel).getCorridors()){
            renderCorridor(corridor);
        }
        field[levels.get(currentLevel).getExitPosition().getY()][levels.get(currentLevel).getExitPosition().getX()] = EXIT;
        field[character.getPosition().getY()][character.getPosition().getX()] = CHARACTER;
        renderEnemies();
        for (Room room : levels.get(currentLevel).getRooms()) {
            for (Coordinate d : room.getDoors()) {
                field[d.getY()][d.getX()] = DOOR;
            }
        }
    }

    private void clearField() {
        for (int i = 0; i < LEVEL_HEIGHT; i++) {
            for (int j = 0; j < LEVEL_WIDTH; j++) {
                field[i][j] = ' ';
            }
        }
    }

    private void renderRoom(Room room) {
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

    private void renderCorridor(Corridor corridor) {
        for(Coordinate c : corridor.getPoints()) {
            field[c.getY()][c.getX()] = CORRIDOR;
        }
    }

    private void renderEnemies() {
        for(Enemy e : levels.get(currentLevel).getEnemies()){
            field[e.getPosition().getY()][e.getPosition().getX()] = (char) e.getType(); // change to 'X'
        }
    }

    public int[] getStatusBar() {
        int[] charStats = new int[6];
        charStats[0] = character.getHealth();
        charStats[1] = character.getMaxHealth();
        charStats[2] = character.getAgility();
        charStats[3] = character.getStrengthWithWeapon();
        charStats[4] = character.getTreasureValue();
        charStats[5] = currentLevel;
        return charStats;
    }

    public int getStatusGame() {
        return status;
    }

    private void eraseCurrentLevel() throws Exception {
        if (currentLevel < 20) {
            this.currentLevel++;
        } else {
            throw new Exception("Level >= 21");
        }
    }

    private void nextLevel(){
        try {
            eraseCurrentLevel();
            character.setPosition(levels.get(currentLevel).getStartPosition());
            render.clearFieldsForNewLevel();
            render.setEnemies(getEnemyList());
            render.setItems(getItemList());
            render.setExit(getExitCoordinate());
            renderField();
        } catch (Exception e) {
            status = END_GAME;
            win = true;
        }
    }

    public model.character.Character getCharacter() {
        return character;
    }

    public char[][] getFieldForView() {
        return render.getField();
    }

    public ArrayList<String> getInventoryForView() {
        return character.getBackpack().getInventoryForView(character.getUsedWeapon(), inventoryType);
    }

    public void saveStatistics() {
        Statistics stats = new Statistics();
        stats.addEntry(character.getTreasureValue(),
                currentLevel,
                character.getDefeatedEnemies(),
                character.getEatenFood(),
                character.getDrunkElixirs(),
                character.getReadScrolls(),
                character.getDealtHits(),
                character.getReceivedHits(),
                character.getStepsTaken());

    }

    public Integer[][] getStatisticsForView() {
        Statistics statsFromFile = new Statistics();
        statsFromFile.loadFromFile();
        return statsFromFile.getEntriesAsArrayInt();
    }

    public int[] getCurrentStatistics() {
        int[] stat = new int[9];
        stat[0] = character.getTreasureValue();
        stat[1] = currentLevel;
        stat[2] = character.getDefeatedEnemies();
        stat[3] = character.getEatenFood();
        stat[4] = character.getDrunkElixirs();
        stat[5] = character.getReadScrolls();
        stat[6] = character.getDealtHits();
        stat[7] = character.getReceivedHits();
        stat[8] = character.getStepsTaken();
        return stat;
    }

    public void gameIsNotLoaded() {
        if (status == LOAD_GAME) {
            status = START_GAME;
        }
    }

    public ArrayList<Enemy> getEnemyList() {
        return levels.get(currentLevel).getEnemies();
    }

    private ArrayList<Item> getItemList() {
        return levels.get(currentLevel).getItems();
    }

    public Coordinate getExitCoordinate() {
        return levels.get(currentLevel).getExitPosition();
    }

    public String getCurrentLevel() {
        return String.valueOf(currentLevel);
    }

    public List<Level> getLevels() {
        return levels;
    }

    public RenderForView getRender() {
        return render;
    }

    public char[][] getField() {
        return field;
    }

    public boolean isWin() {
        return win;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public void setLevels(List<Level> levels) {
        this.levels = levels;
    }

    public void setRender(RenderForView render) {
        this.render = render;
    }
}