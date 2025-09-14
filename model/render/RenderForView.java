package model.render;

import static model.Support.*;
import model.character.Character;
import model.level.Coordinate;
import model.enemies.Enemy;
import model.enemies.Ghost;
import model.items.Item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class RenderForView {
    private final char[][] fieldForView;
    /// поле из GameSession
    private final char[][] fieldMap;
    private final Set<Coordinate> dynamicVision;
    private Set<Coordinate> staticVision;
    private Character character;
    private ArrayList<Enemy> enemies;
    private ArrayList<Item> items;
    private Coordinate exit;
    private final static int RADIUS_VISION = 5;

    /**
     * Конструктор
     * @param fieldMap карта с объектами
     */
    public RenderForView(char[][] fieldMap) {
        fieldForView = new char[LEVEL_HEIGHT][LEVEL_WIDTH];
        this.fieldMap = fieldMap;
        dynamicVision = new HashSet<>();
        staticVision = new HashSet<>();
    }

    /**
     * Переносит информацию из модели в формат поле символов, для дальнейшей распечатки на экран консоли.
     * Работа рендера разделена на 2 сцены: динамическая и статическая. Динамическая отражает объекты, которые могут
     * менять свои координаты или же исчезать с карты. Статическая - неизменяемые объекты. Сцены представлены в виде
     * соответствущих сетов координат.
     * Создаем сцены. Переносим в матрицу для представления
     */
    private void renderField() {
        clearField(fieldForView);
        if (!dynamicVision.isEmpty()) {
            dynamicVision.clear();
        }
        createVisions();
        renderDynamic();
        renderStatic();
        fieldForView[character.getPosition().getY()][character.getPosition().getX()] = CHARACTER;
    }

    /**
     * Создает динамическую и статическую сцены. Создается окружность в виде списка координат, отражающая границы поля
     * зрения персонажа. От местоположения персонажа к каждой точке окружности пускается луч в виде списка координат.
     * Далее объекты расположенные на этих координатах распределяются по спискам в зависимости от их типа. Если обзор
     * блокируется. то распределение заканчивается.
     */
    private void createVisions() {
        ArrayList<Coordinate> coordinatesCircle = createRadiusOfVision();
        for (Coordinate point : coordinatesCircle) {
            ArrayList<Coordinate> linePoints = getLine(character.getPosition(), point);
            for(Coordinate c : linePoints) {
                if(isStaticObj(c)) {
                    staticVision.add(c);
                    if(isBlockVision(c)){
                        break;
                    }
                } else {
                    dynamicVision.add(c);
                }
            }
        }
    }

    /**
     * Проверяет - является ли объект расположенный по заданным координатам статическим.
     * @param c координаты объекта
     * @return true - статический объект, false - динамический
     */
    private boolean isStaticObj(Coordinate c) {
        char obj = fieldMap[c.getY()][c.getX()];
        return obj == WALL || obj == CORRIDOR || obj == DOOR || obj == ' ';
    }

    /**
     * Проверяет - блокирует ли объект расположенный по заданным координатам обзор персонажа.
     * @param c координаты объекта
     * @return true - объект блокирует обзор, false - нет.
     */
    private boolean isBlockVision(Coordinate c) {
        char obj = fieldMap[c.getY()][c.getX()];
        return obj == WALL || obj == ' ';
    }


    /**
     * Переносит статические объекты на матрицу для представления.
     */
    private void renderStatic() {
        for(Coordinate c : staticVision) {
            fieldForView[c.getY()][c.getX()] = fieldMap[c.getY()][c.getX()];
        }
    }

    /**
     * Переносит динамические объекты на матрицу для представления.
     */
    private void renderDynamic() {
        for(Coordinate c : dynamicVision) {
            fieldForView[c.getY()][c.getX()] = FLOOR;
        }
        for(Item i : items){
            if (dynamicVision.contains(i.getPosition())) {
                fieldForView[i.getPosition().getY()][i.getPosition().getX()] = ITEM;
            }
        }
        for(Enemy e : enemies) {
            if (dynamicVision.contains(e.getPosition())) {
                if((e instanceof Ghost g && g.isVisible()) || !(e instanceof Ghost)) {
                    fieldForView[e.getPosition().getY()][e.getPosition().getX()] = (char) e.getType();
                }
            }
        }
        if(dynamicVision.contains(exit)) {
            fieldForView[exit.getY()][exit.getX()] = EXIT;
        }
    }

    /**
     * Создает границы обзора персонажа в виде окружности по алгоритму Брезенхема.
     * @return массив координат созданной окружности
     */
    private ArrayList<Coordinate> createRadiusOfVision() {
        ArrayList<Coordinate> coordinatesCircle = new ArrayList<>();
        int x = 0;
        int y = RADIUS_VISION;
        int d = 3 - 2 * RADIUS_VISION;
        while (x <= y) {
            addSymmetricPoints(coordinatesCircle, character.getPosition().getX(), character.getPosition().getY(), x, y);
            if (d <= 0) {
                d = d + 4 * x + 6;
            } else {
                d = d + 4 * (x - y) + 10;
                y--;
            }
            x++;
        }
        addDopPoints(coordinatesCircle, character.getPosition().getX(), character.getPosition().getY());
        return coordinatesCircle;
    }

    private static void addSymmetricPoints(ArrayList<Coordinate> coordinatesCircle, int cx, int cy, int x, int y) {
        coordinatesCircle.add(new Coordinate(cx + x, cy + y));
        coordinatesCircle.add(new Coordinate(cx - x, cy + y));
        coordinatesCircle.add(new Coordinate(cx + x, cy - y));
        coordinatesCircle.add(new Coordinate(cx - x, cy - y));
        coordinatesCircle.add(new Coordinate(cx + y, cy + x));
        coordinatesCircle.add(new Coordinate(cx - y, cy + x));
        coordinatesCircle.add(new Coordinate(cx + y, cy - x));
        coordinatesCircle.add(new Coordinate(cx - y, cy - x));
    }

    private static void addDopPoints(ArrayList<Coordinate> points, int cx, int cy) {
        points.add(new Coordinate(cx - 3, cy - 3));
        points.add(new Coordinate(cx + 3, cy - 3));
        points.add(new Coordinate(cx - 3, cy + 3));
        points.add(new Coordinate(cx + 3, cy + 3));
    }

    /**
     * Строит линию между двумя точками с помощью алгоритма Брезенхема.
     * Цикл завершается при достижении точки с отрицательными координатами.
     * @param start начальная точка
     * @param end конечная точка
     * @return список координат точки линии, остановленный при отрицательных x или y
     */
    private ArrayList<Coordinate> getLine(Coordinate start, Coordinate end) {
        ArrayList<Coordinate> linePoints = new ArrayList<>();

        int x0 = start.getX();
        int y0 = start.getY();
        int x1 = end.getX();
        int y1 = end.getY();

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;

        int err = dx - dy;
        while (true) {
            if (x0 < 0 || y0 < 0) {
                break;
            }
            linePoints.add(new Coordinate(x0, y0));
            if(fieldMap[y0][x0] == WALL || fieldMap[y0][x0] == ' ') {
                break;
            }
            if (x0 == x1 && y0 == y1) {
                break;
            }
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
        return linePoints;
    }

    /**
     * Очищает статическую сцену перед новым уровнем
     */
    public void clearFieldsForNewLevel() {
        staticVision.clear();
    }

    /**
     * Очистка матрицы
     * @param field матрица
     */
    private void clearField(char[][] field) {
        for (int i = 0; i < LEVEL_HEIGHT; i++) {
            for (int j = 0; j < LEVEL_WIDTH; j++) {
                field[i][j] = ' ';
            }
        }
    }

    /**
     * Передает готовую матрицу с видимыми объектами для представления и дальнейшего вывода в консоль
     * @return матрицу
     */
    public char[][] getField() {
        renderField();
        return fieldForView;
    }

    public Set<Coordinate> getStaticVision() {
        return staticVision;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public void setEnemies(ArrayList<Enemy> enemies) {
        this.enemies = enemies;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public void setExit(Coordinate exit) {
        this.exit = exit;
    }

    public void setStaticVision(Set<Coordinate> staticVision) {
        this.staticVision = staticVision;
    }
}
