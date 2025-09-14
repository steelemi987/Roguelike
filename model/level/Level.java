package model.level;

import java.util.*;

import model.GameSession;
import model.items.*;
import model.enemies.*;

import static model.Support.*;

public class Level {
    private final List<Room> rooms;
    private final List<Corridor> corridors;
    private ArrayList<Enemy> enemies;
    private ArrayList<Item> items;
    /// Стартовая позиция персонажа
    private Coordinate startPosition;
    /// Точка выхода с уровня
    private Coordinate exitPosition;
    /// Номер комнаты, где появляется персонаж
    private int startRoom;
    private static final int SECTIONS = 3;
    private static final int SECTION_WIDTH = LEVEL_WIDTH / SECTIONS;
    private static final int SECTION_HEIGHT = LEVEL_HEIGHT / SECTIONS;
    private final Random random = new Random();

    /**
     * Конструктор
     */
    public Level() {
        rooms = new ArrayList<>();
        corridors = new ArrayList<>();
        enemies = new ArrayList<>();
        items = new ArrayList<>();
    }

    /**
     * Заполняет уровень комнатами до 9.
     * @return сам экземпляр для вызова методов по цепочке.
     */
    public Level createRooms() {
        for (int row = 0; row < SECTIONS; row++) {
            for (int col = 0; col < SECTIONS; col++) {
                int sx = col * SECTION_WIDTH;
                int sy = row * SECTION_HEIGHT;

                int rw = random.nextInt(SECTION_WIDTH - 6) + 4; // Мин 4, макс SECTION_WIDTH-2
                int rh = random.nextInt(SECTION_HEIGHT - 6) + 4;

                int rx = sx + random.nextInt(SECTION_WIDTH - rw - 2) + 1;
                int ry = sy + random.nextInt(SECTION_HEIGHT - rh - 2) + 1;

                Room room = new Room(rx, ry, rw, rh, rooms.size());
                rooms.add(room);
            }
        }
        return this;
    }

    /**
     * Рандомит стартовую и конечную позицию уровня.
     * @return сам экземпляр для вызова методов по цепочке.
     */
    public Level createStartAndExitPosition() {
        startRoom = random.nextInt(rooms.size());
        int exitRoom = random.nextInt(rooms.size());
        while (exitRoom == startRoom) exitRoom = random.nextInt(rooms.size());

        startPosition = getRandCoordinateInRoom(rooms.get(startRoom));
        exitPosition = getRandCoordinateInRoom(rooms.get(exitRoom));
        return this;
    }

    /**
     * Создает коридоры между комнатами, обеспечивая связность графа (MST).
     * @return сам экземпляр для вызова методов по цепочке.
     */
    public Level createCorridors() {
        List<int[]> edges = Arrays.asList(
                new int[]{0,1}, new int[]{1,2}, new int[]{3,4}, new int[]{4,5},
                new int[]{6,7}, new int[]{7,8}, new int[]{0,3}, new int[]{3,6},
                new int[]{1,4}, new int[]{4,7}, new int[]{2,5}, new int[]{5,8}
        );
        List<int[]> shuffledEdges = new ArrayList<>(edges);
        Collections.shuffle(shuffledEdges);

        UnionFind uf = new UnionFind(rooms.size());
        for (int[] edge : shuffledEdges) {
            int min = Math.min(edge[0], edge[1]);
            int max = Math.max(edge[0], edge[1]);
            if (uf.find(min) != uf.find(max)) {
                Corridor corridor = createCorridor(rooms.get(min), rooms.get(max));
                corridors.add(corridor);
                uf.union(min, max);
            }
        }
        return this;
    }

    /**
     * Создает L-образный коридор между двумя комнатами.
     * @param roomA первая комната
     * @param roomB вторая комната
     * @return объект Corridor с точками.
     */
    private Corridor createCorridor(Room roomA, Room roomB) {
        Coordinate doorA, doorB;

        if (roomA.getCornerRight().getX() < roomB.getCornerLeft().getX()) { // roomB справа
            doorA = new Coordinate(roomA.getCornerRight().getX(), random.nextInt(roomA.getCornerRight().getY() - roomA.getCornerLeft().getY() - 1) + roomA.getCornerLeft().getY() + 1);
            doorB = new Coordinate(roomB.getCornerLeft().getX(), random.nextInt(roomB.getCornerRight().getY() - roomB.getCornerLeft().getY() - 1) + roomB.getCornerLeft().getY() + 1);
            roomA.addDoor(doorA);
            roomB.addDoor(doorB);
            Corridor corridor = new Corridor();
            int x1 = doorA.getX() + 1;
            int y1 = doorA.getY();
            int x2 = doorB.getX() - 1;
            int y2 = doorB.getY();
            if (random.nextBoolean()) {
                for (int x = x1; x <= x2; x++) {
                    corridor.addPoint(new Coordinate(x, y1));
                }
                for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                    corridor.addPoint(new Coordinate(x2, y));
                }
            } else {
                for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                    corridor.addPoint(new Coordinate(x1, y));
                }
                for (int x = x1; x <= x2; x++) {
                    corridor.addPoint(new Coordinate(x, y2));
                }
            }
            return corridor;
        } else if (roomA.getCornerRight().getY() < roomB.getCornerLeft().getY()) { // roomB снизу
            doorA = new Coordinate(random.nextInt(roomA.getCornerRight().getX() - roomA.getCornerLeft().getX() - 1) + roomA.getCornerLeft().getX() + 1, roomA.getCornerRight().getY());
            doorB = new Coordinate(random.nextInt(roomB.getCornerRight().getX() - roomB.getCornerLeft().getX() - 1) + roomB.getCornerLeft().getX() + 1, roomB.getCornerLeft().getY());
            roomA.addDoor(doorA);
            roomB.addDoor(doorB);
            Corridor corridor = new Corridor();
            int x1 = doorA.getX();
            int y1 = doorA.getY() + 1;
            int x2 = doorB.getX();
            int y2 = doorB.getY() - 1;
            if (random.nextBoolean()) {
                for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                    corridor.addPoint(new Coordinate(x, y1));
                }
                for (int y = y1; y <= y2; y++) {
                    corridor.addPoint(new Coordinate(x2, y));
                }
            } else {
                for (int y = y1; y <= y2; y++) {
                    corridor.addPoint(new Coordinate(x1, y));
                }
                for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                    corridor.addPoint(new Coordinate(x, y2));
                }
            }
            return corridor;
        } else if (roomA.getCornerLeft().getX() > roomB.getCornerRight().getX()) { // roomB слева
            doorA = new Coordinate(roomA.getCornerLeft().getX(), random.nextInt(roomA.getCornerRight().getY() - roomA.getCornerLeft().getY() - 1) + roomA.getCornerLeft().getY() + 1);
            doorB = new Coordinate(roomB.getCornerRight().getX(), random.nextInt(roomB.getCornerRight().getY() - roomB.getCornerLeft().getY() - 1) + roomB.getCornerLeft().getY() + 1);
            roomA.addDoor(doorA);
            roomB.addDoor(doorB);
            Corridor corridor = new Corridor();
            int x1 = doorA.getX() - 1;
            int y1 = doorA.getY();
            int x2 = doorB.getX() + 1;
            int y2 = doorB.getY();
            if (random.nextBoolean()) {
                for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                    corridor.addPoint(new Coordinate(x, y1));
                }
                for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                    corridor.addPoint(new Coordinate(x2, y));
                }
            } else {
                for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                    corridor.addPoint(new Coordinate(x1, y));
                }
                for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                    corridor.addPoint(new Coordinate(x, y2));
                }
            }
            return corridor;
        } else if (roomA.getCornerLeft().getY() > roomB.getCornerRight().getY()) { // roomB сверху
            doorA = new Coordinate(random.nextInt(roomA.getCornerRight().getX() - roomA.getCornerLeft().getX() - 1) + roomA.getCornerLeft().getX() + 1, roomA.getCornerLeft().getY());
            doorB = new Coordinate(random.nextInt(roomB.getCornerRight().getX() - roomB.getCornerLeft().getX() - 1) + roomB.getCornerLeft().getX() + 1, roomB.getCornerRight().getY());
            roomA.addDoor(doorA);
            roomB.addDoor(doorB);
            Corridor corridor = new Corridor();
            int x1 = doorA.getX();
            int y1 = doorA.getY() - 1;
            int x2 = doorB.getX();
            int y2 = doorB.getY() + 1;
            if (random.nextBoolean()) {
                for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                    corridor.addPoint(new Coordinate(x, y1));
                }
                for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                    corridor.addPoint(new Coordinate(x2, y));
                }
            } else {
                for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                    corridor.addPoint(new Coordinate(x1, y));
                }
                for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                    corridor.addPoint(new Coordinate(x, y2));
                }
            }
            return corridor;
        } else {
            return new Corridor();
        }
    }

    /**
     * Заполняет уровень врагами. В каждой комнате, кроме стартовой, появляется от 0 до 3 врагов в зависимости от номера уровня.
     * @param currentLevel номер уровня
     * @param game экземпляр GameSession для регистрации наблюдателей
     * @return сам экземпляр для вызова методов по цепочке.
     */
    public Level createEnemies(int currentLevel, GameSession game) {
        int maxCountEnemiesInRoom = currentLevel / 7 + 1;
        for (int i = 0; i < rooms.size(); i++) {
            if (i != startRoom) {
                int countEnemiesInRoom = randInDiapason(0, maxCountEnemiesInRoom);
                ArrayList<Coordinate> enemyPositions = new ArrayList<>();
                for (int j = 0; j < countEnemiesInRoom; j++) {
                    Coordinate newCoordinate;
                    do {
                        newCoordinate = getRandCoordinateInRoom(rooms.get(i));
                    } while (enemyPositions.contains(newCoordinate));
                    Enemy enemy = randEnemy(newCoordinate, currentLevel);
                    enemy.registerObserver(game);
                    enemies.add(enemy);
                    enemyPositions.add(newCoordinate);
                }
            }
        }
        return this;
    }

    /**
     * Создает врага со случайным типом, с заданными координатами.
     * @param newCoordinate координаты нового врага
     * @param currentLevel текущий уровень
     * @return созданного врага
     */
    private Enemy randEnemy(Coordinate newCoordinate, int currentLevel) {
        int type = randInDiapason(0, 4);
        return switch (type) {
            case 0 -> new Ghost(newCoordinate, currentLevel);
            case 1 -> new Ogre(newCoordinate, currentLevel);
            case 2 -> new SnakeMage(newCoordinate, currentLevel);
            case 3 -> new Vampire(newCoordinate, currentLevel);
            case 4 -> new Zombie(newCoordinate, currentLevel);
            default -> new Enemy(newCoordinate);
        };
    }

    /**
     * Создает случайные предметы в комнатах с шансом, зависящем от номера уровня. Чем выше уровень, тем ниже шанс
     * появления предмета.
     * @param currentLevel номер уровня
     * @return текущий уровень
     */
    public Level createItems(int currentLevel) {
        for (int i = 0; i < rooms.size(); i++) {
            if (i != startRoom) {
                int chance = randInDiapason(MIN_PERCENTAGE, MAX_PERCENTAGE);
                boolean isSpawnItem = chance <= BASE_HIT_CHANCE - currentLevel * 2;
                if(isSpawnItem) {
                    Coordinate newCoordinate;
                    do {
                        newCoordinate = getRandCoordinateInRoom(rooms.get(i));
                    } while (newCoordinate.equals(exitPosition));
                    items.add(randItem(newCoordinate));
                }
            }
        }
        return this;
    }

    /**
     * Создает предмет со случайным типом и заданными координатами.
     * @param newCoordinate координаты предмета
     * @return созданный предмет
     */
    public Item randItem(Coordinate newCoordinate) {
        int type = randInDiapason(0, 3);
        return switch (type) {
            case 1 -> new Food(newCoordinate);
            case 2 -> new Scroll(newCoordinate);
            case 3 -> new Weapon(newCoordinate);
            default -> new Elixir(newCoordinate);
        };
    }

    /**
     * Метод генерит рандомную точку внутри комнаты.
     * @param room комната внутри которой получаем точку.
     * @return точку с полученными координатами.
     */
    private Coordinate getRandCoordinateInRoom(Room room) {
        Coordinate position = new Coordinate();
        do {
            position.setX(randInDiapason(room.getCornerLeft().getX() + 1, room.getCornerRight().getX() - 1));
            position.setY(randInDiapason(room.getCornerLeft().getY() + 1, room.getCornerRight().getY() - 1));
        } while(position.equals(exitPosition));
        return position;
    }

    /**
     * Получает список комнат уровня.
     * @return список комнат.
     */
    public List<Room> getRooms() {
        return rooms;
    }

    /**
     * Получает список коридоров уровня.
     * @return список коридоров.
     */
    public List<Corridor> getCorridors() {
        return corridors;
    }

    /**
     * Получает стартовую позицию персонажа.
     * @return стартовую позицию.
     */
    public Coordinate getStartPosition() {
        return startPosition;
    }

    /**
     * Получает позицию выхода с уровня.
     * @return позицию выхода.
     */
    public Coordinate getExitPosition() {
        return exitPosition;
    }

    /**
     * Получает список врагов уровня.
     * @return список врагов.
     */
    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    /**
     * Получает список предметов уровня.
     * @return список предметов.
     */
    public ArrayList<Item> getItems() {
        return items;
    }

    public void setEnemies(ArrayList<Enemy> enemies) {
        this.enemies = enemies;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public void setStartPosition(Coordinate startPosition) {
        this.startPosition = startPosition;
    }

    public void setExitPosition(Coordinate exitPosition) {
        this.exitPosition = exitPosition;
    }

    public void setStartRoom(int startRoom) {
        this.startRoom = startRoom;
    }

    public int getStartRoom() {
        return startRoom;
    }

    static class UnionFind {
        private final int[] parent;

        /**
         * Конструктор UnionFind.
         * @param size размер множества.
         */
        UnionFind(int size) {
            parent = new int[size];
            for (int i = 0; i < size; i++) parent[i] = i;
        }

        /**
         * Находит корень узла со сжатием пути.
         * @param p индекс узла.
         * @return индекс корня.
         */
        int find(int p) {
            if (parent[p] != p) parent[p] = find(parent[p]);
            return parent[p];
        }

        /**
         * Объединяет два множества.
         * @param p первый узел.
         * @param q второй узел.
         */
        void union(int p, int q) {
            int rootP = find(p);
            int rootQ = find(q);
            if (rootP != rootQ) parent[rootP] = rootQ;
        }
    }
}