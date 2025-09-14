package model.level;

import java.util.ArrayList;
import java.util.List;

public class Room {
    /// верхний левый угол
    private final Coordinate cornerLeft;
    /// нижний правый угол
    private final Coordinate cornerRight;
    /// массив дверей
    private final List<Coordinate> doors;
    /// номер комнаты
    private final int numb;

    /**
     * Конструктор
     * @param x горизонтальная координата левого угла
     * @param y вертикальная координата левого угла
     * @param width ширина комнаты
     * @param height высота комнаты
     * @param numb номер комнаты
     */
    public Room(int x, int y, int width, int height, int numb) {
        cornerLeft = new Coordinate(x, y);
        cornerRight = new Coordinate(x + width - 1, y + height - 1);
        doors = new ArrayList<>();
        this.numb = numb;
    }

    public Coordinate getCornerLeft() {
        return cornerLeft;
    }

    public Coordinate getCornerRight() {
        return cornerRight;
    }

    public int getNumb() {
        return numb;
    }

    /**
     * Добавляет дверь в массив.
     */
    public void addDoor(Coordinate door) {
        doors.add(door);
    }

    public List<Coordinate> getDoors() {
        return doors;
    }
}