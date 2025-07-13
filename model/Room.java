package model;

import java.util.ArrayList;
import java.util.List;

import static model.Support.*;

/**
 * Номера комнат отражают географию комнат на уровне:
 * 0 1 2
 * 3 4 5
 * 6 7 8
 */
public class Room {
    private Coordinate cornerLeft; // верхний левый угол
    private Coordinate cornerRight; // нижний правый угол
    private List<Coordinate> doors; // массив дверей
    private int numb = -1; // номер комнаты

    /**
     * Конструктор
     * @param numRoom расположение комнаты
     */
    public Room(int numRoom) {
        cornerLeft = new Coordinate();
        cornerRight = new Coordinate();
        doors = new ArrayList<>();
        randomRoom(numRoom);
    }

    /**
     * Рандомит углы комнаты в зависимости от номера комнаты.
     * @param numRoom расположение комнаты
     */
    public void randomRoom(int numRoom) {
        int width = MAX_WIDTH_ROOM;
        int height = MAX_HEIGHT_ROOM;
        int minWidthRoom = 4; //4
        int minHeightRoom = 4; //4
        int minX = numRoom % 3 * width + numRoom % 3;
        int maxX = minX + width - minWidthRoom;
        int minY = numRoom / 3 * height + numRoom / 3;
        int maxY = minY + height - minHeightRoom;
        cornerLeft.setCoordinate(randInDiaposone(minX, maxX), randInDiaposone(minY, maxY));
        cornerRight.setX(randInDiaposone(cornerLeft.getX() + minWidthRoom - 1, minX + width - 1));
        cornerRight.setY(randInDiaposone(cornerLeft.getY() + minHeightRoom - 1, minY + height - 1));
        numb = numRoom;
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
