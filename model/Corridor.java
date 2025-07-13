package model;

import java.util.ArrayList;
import java.util.List;

/**
 * По сути класс хранит координаты пикселей, которые являются корридором
 */
public class Corridor {
    private List<Coordinate> points;

    /**
     * Конструктор
     */
    public Corridor() {
        points = new ArrayList<>();
    }

    /**
     * Метод добавляет новую точку в основной список. Для сокращения кода
     * @param point координаты новой точки
     */
    public void addPoint(Coordinate point) {
        points.add(point);
    }

    /**
     * Геттер
     */
    public List<Coordinate> getPoints() {
        return points;
    }
}
