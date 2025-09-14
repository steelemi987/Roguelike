package model.level;

public class Coordinate {
    private int x;
    private int y;

    /**
     * Конструктор по умолчанию
     */
    public Coordinate() {
        x = 0;
        y = 0;
    }

    /**
     * Конструктор с параметрами
     */
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    /**
     * Сеттер, только для двух параметров сразу
     * @param x горизонтальная координата
     * @param y вертикальная координата
     */
    public void setCoordinate(int x, int y) {
        this.y = y;
        this.x = x;
    }

    /**
     * Метод определяющий являются ли координаты равными по двум осям
     * @param o объект с которым сравниваем(по идее это класс Coordinate)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinate coordinate)) return false;
        return x == coordinate.x && y == coordinate.y;
    }

    /**
     * Метод нужен для использования класса в составе сета.
     * @return хэш код
     */
    @Override
    public int hashCode() {
        int result = Integer.hashCode(x);
        result = 31 * result + Integer.hashCode(y);
        return result;
    }

    public Coordinate getThis() {
        return this;
    }
}
