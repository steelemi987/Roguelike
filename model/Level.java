package model;

import model.enemies.*;

import java.util.*;

import static model.Support.*;

public class Level {
    private List<Room> rooms;
    private List<Corridor> corridors;
    private ArrayList<Enemy> enemies;
    private Coordinate startPosition; // стартовая позиция пресонажа
    private Coordinate exitPosition; // точка выхода с уровня
    private int startRoom; // номер комнаты, где появляется персонаж

    /**
     * Конструктор
     */
    public Level() {
        rooms = new ArrayList<>();
        corridors = new ArrayList<>();
        enemies = new ArrayList<>();
    }

    /**
     * Заполняет уровень комнатами до 9.
     * @return сам экземпляр для вызова методов по цепочке.
     */
    public Level createRooms() {
        for(int i = 0; i < MAXCOUNTROOMS; i++){
            rooms.add(new Room(i));
        }
        return this;
    }

    /**
     * Рандомит стартовую и конечную позицию уровня.
     * @return сам экземпляр для вызова методов по цепочке.
     */
    public Level createStartAndExitPosition() {
        startRoom = randInDiaposone(0, MAXCOUNTROOMS - 1);
        int exitRoom;
        do {
            exitRoom = randInDiaposone(0, MAXCOUNTROOMS - 1);
        } while (exitRoom == startRoom);
        startPosition = getRandCoordinateInRoom(rooms.get(startRoom));
        exitPosition = getRandCoordinateInRoom(rooms.get(exitRoom));
        return this;
    }

    /**
     * Заполняет урвень врагами. В каждой комнате, кроме стартовой, появляется от 0 до 3 врагов в зависимости от номера уровня.
     * @return сам экземпляр для вызова методов по цепочке.
     */
    public Level createEnemies(int currentLevel, GameSession game) { // GameSession game - используется для дебага
        int maxCountEnemiesInRoom = currentLevel / 7 + 1;
        for (int i = 0; i < MAXCOUNTROOMS; i++) {
            if (i != startRoom) {
                int countEnemiesInRoom = randInDiaposone(0, maxCountEnemiesInRoom);
                ArrayList<Coordinate> enemyPositions = new ArrayList<>();
                for (int j = 1; j <= countEnemiesInRoom; j++) {
                    Coordinate newCoordinate;
                    do {
                        newCoordinate = getRandCoordinateInRoom(rooms.get(i));
                    } while (enemyPositions.contains(newCoordinate));
//                    Enemy enemy = new Ghost(newCoordinate, currentLevel);
                    Enemy enemy = randEnemy(newCoordinate, currentLevel);
                    enemy.setGame(game); // DEBUG
                    enemy.registerObserver(game);
                    enemies.add(enemy);
                    enemyPositions.add(newCoordinate);
                }
            }
        }
        return this;
    }

    public Enemy randEnemy(Coordinate newCoordinate, int currentLevel) {
        int type = randInDiaposone(0, 4);
        Enemy enemy = switch (type) {
            case 0 -> new Ghost(newCoordinate, currentLevel);
            case 1 -> new Ogre(newCoordinate, currentLevel);
            case 2 -> new SnakeMage(newCoordinate, currentLevel);
            case 3 -> new Vampire(newCoordinate, currentLevel);
            case 4 -> new Zombie(newCoordinate, currentLevel);
            default -> new Enemy(newCoordinate);
        };
        return enemy;
    }

    /**
     * Метод генерит рандомную точку внутри комнаты.
     * @param room комната внутри которой получаем точку.
     * @return точку с полученными координатами.
     */
    public Coordinate getRandCoordinateInRoom(Room room) {
        Coordinate position = new Coordinate();
        do {
            position.setX(randInDiaposone(room.getCornerLeft().getX() + 1, room.getCornerRight().getX() - 1));
            position.setY(randInDiaposone(room.getCornerLeft().getY() + 1, room.getCornerRight().getY() - 1));
        } while(position.equals(exitPosition));
        return position;
    }

    /**
     * Создает коридоры между двумя комнатами. Соединяет все комнаты.
     * @return сам экземпляр для вызова методов по цепочке.
     */
    public Level createCorridors() {
        for (int i = 0; i < 8; i++){
            connectRooms(rooms.get(i), rooms.get(i + 1));
            if (i < 6) {
                connectRooms(rooms.get(i), rooms.get(i + 3));
            }
        }
        return this;
    }

    /**
     * Соединяет 2 комнаты коридорами, получает кратчайший путь.
     * Создаем словарь, где ключ - номер комнаты(приватное поле класса Room), а значение - массив номеров соседних комнат, с
     * которым ключ может быть соединен коридором.
     * Затем если endRoom может быть соединена с beginRoom, в зависимости от положения endRoom относительно beginRoom
     * (справа/снизу) начинаем процесс генерации координат точек (на примере блока RIGHT):
     * - создаем рандомные координаты дверей 2 комнат
     * - создаем координаты начала и конца корридора относительно координат дверей (есть только один вариант генерации)
     * - создаем корридор, добавляем в него первую точку
     * - определяем shiftX - количество шагов которые нужно сделать по оси Х(горизонтали)
     * - добавляем точки по горизонтали в наш корридор
     * - определяем shiftY - количество шагов которые нужно сделать по оси Y(вертикали)
     * - mod - в зависимости от направления корридора по вертикали (вниз, вверх), либо увеличиваем значение оси Y, либо
     *   уменьшаем, поэтому нам нужен этот крэффициент
     * - добавляем двери в комнату, получившийся корридор в список корридоров.
     *
     * @param beginRoom комната от которой прокладываем корридор
     * @param endRoom комната в которую прокладываем корридор
     */
    public void connectRooms(Room beginRoom, Room endRoom) {
        AbstractMap<Integer, ArrayList<Integer>> checkConnection = new HashMap<>(); // стоит перенести в класс Support как static
        checkConnection.put(0, new ArrayList<>(Arrays.asList(1, 3)));
        checkConnection.put(1, new ArrayList<>(Arrays.asList(0, 2, 4)));
        checkConnection.put(2, new ArrayList<>(Arrays.asList(1, 5)));
        checkConnection.put(3, new ArrayList<>(Arrays.asList(0, 4, 6)));
        checkConnection.put(4, new ArrayList<>(Arrays.asList(1, 3, 5, 7)));
        checkConnection.put(5, new ArrayList<>(Arrays.asList(2, 4, 8)));
        checkConnection.put(6, new ArrayList<>(Arrays.asList(3, 7)));
        checkConnection.put(7, new ArrayList<>(Arrays.asList(4, 6, 8)));
        checkConnection.put(8, new ArrayList<>(Arrays.asList(5, 7)));
        if (checkConnection.get(beginRoom.getNumb()).contains(endRoom.getNumb())){
            if (endRoom.getNumb() - beginRoom.getNumb() == RIGHT) {
                Coordinate beginDoor = new Coordinate();
                beginDoor.setX(beginRoom.getCornerRight().getX());
                beginDoor.setY(randInDiaposone(beginRoom.getCornerLeft().getY() + 1, beginRoom.getCornerRight().getY() - 1));
                Coordinate beginCorridor = new Coordinate(beginDoor.getX() + 1, beginDoor.getY());
                Coordinate endDoor = new Coordinate();
                endDoor.setX(endRoom.getCornerLeft().getX());
                endDoor.setY(randInDiaposone(endRoom.getCornerLeft().getY() + 1, endRoom.getCornerRight().getY() - 1));
                Coordinate endCorridor = new Coordinate(endDoor.getX() - 1, endDoor.getY());
                Corridor corridor = new Corridor();
                corridor.addPoint(beginCorridor);
                int shiftX = endCorridor.getX() - beginCorridor.getX();
                for(int i = 1; i <= shiftX; i++) {
                    corridor.addPoint(new Coordinate(beginCorridor.getX() + i, beginCorridor.getY()));
                }
                int shiftY = endCorridor.getY() - beginCorridor.getY();
                int mod = 1;
                if (shiftY < 0) {
                    mod = -1;
                    shiftY = Math.abs(shiftY);
                }
                for(int i = 1; i <= shiftY; i++) {
                    corridor.addPoint(new Coordinate(endCorridor.getX(), beginCorridor.getY() + i * mod));
                }
                beginRoom.addDoor(beginDoor);
                endRoom.addDoor(endDoor);
                corridors.add(corridor);
            } else if (endRoom.getNumb() - beginRoom.getNumb() == DOWN) {
                Coordinate beginDoor = new Coordinate();
                beginDoor.setY(beginRoom.getCornerRight().getY());
                beginDoor.setX(randInDiaposone(beginRoom.getCornerLeft().getX() + 1,beginRoom.getCornerRight().getX() - 1));
                Coordinate beginCorridor = new Coordinate(beginDoor.getX(), beginDoor.getY() + 1);
                Coordinate endDoor = new Coordinate();
                endDoor.setX(randInDiaposone(endRoom.getCornerLeft().getX() + 1, endRoom.getCornerRight().getX() - 1));
                endDoor.setY(endRoom.getCornerLeft().getY());
                Coordinate endCorridor = new Coordinate(endDoor.getX(), endDoor.getY() - 1);
                Corridor corridor = new Corridor();
                corridor.addPoint(beginCorridor);
                int shiftY = endCorridor.getY() - beginCorridor.getY();
                for(int i = 1; i <= shiftY; i++) {
                    corridor.addPoint(new Coordinate(beginCorridor.getX(), beginCorridor.getY() + i));
                }
                int shiftX = endCorridor.getX() - beginCorridor.getX();
                int mod = 1;
                if (shiftX < 0) {
                    mod = -1;
                    shiftX = Math.abs(shiftX);
                }
                for(int i = 1; i <= shiftX; i++) {
                    corridor.addPoint(new Coordinate(beginCorridor.getX() + i * mod, endCorridor.getY()));
                }
                beginRoom.addDoor(beginDoor);
                endRoom.addDoor(endDoor);
                corridors.add(corridor);
            } else if (endRoom.getNumb() - beginRoom.getNumb() == LEFT) {
                // пОКА  не делал
            } else if (endRoom.getNumb() - beginRoom.getNumb() == UP) {
                // пОКА  не делал
            }
        }
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public List<Corridor> getCorridors() {
        return corridors;
    }

    public Coordinate getStartPosition() {
        return startPosition;
    }

    public Coordinate getExitPosition() {
        return exitPosition;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }
}
