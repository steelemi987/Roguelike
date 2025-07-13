package model.enemies;

import model.Character;
import model.Coordinate;
import model.GameSession;
import model.Observable;
import model.Observer;

import java.util.*;
import java.util.stream.Collectors;

import static model.Support.*;

/**
 * Общий класс для всех врагов. Если нужно поменять поведение конкретного типа врага, переписываем методы в
 * соответствующем классе.
 */
public class Enemy implements Fighter, Observable {
    protected Fight fightClub;
    protected int type = ENEMY;
//    protected int maxHealth;
    protected int health;
    protected int agility;
    protected int strength;
    protected int hostility;
    protected Coordinate position;
    protected Observer observer;

    protected GameSession game;

    /**
     * Конструктор.
     * @param position координаты начальной позиции
     */
    public Enemy(Coordinate position) {
        this.fightClub = new Fight(this);
        this.position = position;
    }

    /**
     * Отражает ход противника, у которого 3 типа действия: атаковать персонажа, преследовать персонажа,
     * двигаться произвольно.
     * @param character наш персонаж
     * @param field поле со структурами
     */
    public void act(Character character, char[][] field) {
        if (doISeeCharacter(character.getPosition())) {
            if (canFight(character.getPosition())) {
                fightClub.getFight(character);
            } else if (canReachCharacter(character.getPosition(), field)) {
                pursueCharacter(character.getPosition(), field);
            } else {
                moveInPattern(field);
            }
        } else {
            moveInPattern(field);
        }
    }

    /**
     * Метод определяет видит ли враг персонажа. Определяем "радиус обзора" с помощью создания точек, определяющие
     * границы интервалов по осям(Х, Y), используя показатель враждебности врага.
     * @param characterPosition координата позиции персонажа
     * @return true - если персонаж находиться внутри "радиуса обзора", false - если нет.
     */
    public boolean doISeeCharacter(Coordinate characterPosition) {
        int minX = position.getX() - hostility;
        int maxX = position.getX() + hostility;
        int minY = position.getY() - hostility;
        int maxY = position.getY() + hostility;
        return characterPosition.getY() <= maxY && characterPosition.getY() >= minY && characterPosition.getX() <= maxX && characterPosition.getX() >= minX;
    }

    /**
     * Определяет может ли враг атаковать персонажа. Создаем массив координат вокруг врага по горизонтали и вертикали.
     * Сравниваем есть ли координаты персонажа в массиве.
     * @param characterPosition координаты персонажа
     * @return true - если персонаж находиться в радиусе поражения атаки врага, false - если нет.
     */
    public boolean canFight(Coordinate characterPosition) {
        ArrayList<Coordinate> hitRange = new ArrayList<>();
        hitRange.add(new Coordinate(position.getX(), position.getY() - 1));
        hitRange.add(new Coordinate(position.getX() + 1, position.getY()));
        hitRange.add(new Coordinate(position.getX(), position.getY() + 1));
        hitRange.add(new Coordinate(position.getX() - 1, position.getY()));
        return hitRange.contains(characterPosition);
    }

    /**
     * Определяет может ли враг дойти до персонажа. Мы строим прямоугольник, вершины которого координаты персонажа и
     * врага. Исходим из логики: враг не выходит из комнаты, соответственно если между врагом и персонажем есть
     * стена/дверь, значит персонаж вне комнаты и дойти до него нет возможности. Если персонаж в комнате то проверяем
     * есть ли на соседних с ним клетках свободное место. Если да, то значит дойти до персонажа можно.
     * @param characterPosition координаты персонажа
     * @param field поле со структурами
     * @return true - если персонаж достижим, false - если нет.
     */
    public boolean canReachCharacter(Coordinate characterPosition, char[][] field) {
        int minY, minX, maxX, maxY;
        if (characterPosition.getY() < position.getY()) {
            minY = characterPosition.getY();
            maxY = position.getY();
        } else {
            minY = position.getY();
            maxY = characterPosition.getY();
        }
        if (characterPosition.getX() < position.getX()) {
            minX = characterPosition.getX();
            maxX = position.getX();
        } else {
            minX = position.getX();
            maxX = characterPosition.getX();
        }
        boolean res = true;
        for(int i = minY; i <= maxY; i++) {
            for(int j = minX; j <= maxX; j++) {
                if (field[i][j] == WALL || field[i][j] == DOOR) {
                    res = false;
                    break;
                }
            }
        }
        if (res) {
            minY = characterPosition.getY() - 1;
            maxY = minY + 2;
            minX = characterPosition.getX() - 1;
            maxX = minX + 2;
            res = false;
            for (int i = minY; i <= maxY; i++) {
                for (int j = minX; j <= maxX; j++) {
                    if (field[i][j] == FLOOR || field[i][j] == ITEM) { // Заменить на canMove()
                        res = true;
                        break;
                    }
                }
            }
        }
        return res;
    }

    /**
     * Преследование персонажа. Результатом работы метода является изменение позиции врага на ближайщую к персонажу из
     * возможных. Поэтапно:
     * - создаем стрим из списка направлений (просто инты);
     * - получаем стрим координат на основе сдвига координат врага по направлению (эти координаты отражают движение
     *   врага на 1 клетку);
     * - получем координаты свободные для перемещения;
     * - получаем стрим словарей, ключ - координата движения, значение - "стоимость передвижения" до персонажа;
     * - сортируем по "стоимости передвижения" по возрастающей;
     * - собираем словарь
     * Если словарь не пустой устанавливаем позицию врага на первый элемент в словаре (с минимальной "стоимостью
     * передвижения").
     * @param characterPosition координаты персонажа
     * @param field поле со структурами
     */
    public void pursueCharacter(Coordinate characterPosition, char[][] field) {
        ArrayList<Integer> directions = getDirectionList();
        LinkedHashMap<Coordinate, Integer> positions = directions.stream()
                .map(this::getCoordinateWithShift)
                .filter(i -> canMove(i, field))
                .map(i -> new AbstractMap.SimpleEntry<>(i, getCostMove(i, characterPosition)))
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
        if(!positions.isEmpty()) {
            setPosition(positions.entrySet().iterator().next().getKey());
        }
    }

    /**
     * Создает массив из интов (направлений). Нужна для переопределения метода в некоторых классах наследуемых от
     * данного.
     * @return возвращает этот массив.
     */
    public ArrayList<Integer> getDirectionList() {
        return new ArrayList<>(Arrays.asList(UP, RIGHT, DOWN, LEFT));
    }

    /**
     * Определяет можно ли врагу переместиться на данную клетку. Враг может стоять на полу и предмете.
     * @param next координаты точки, на которую планируем переместиться
     * @param field поле со структурами
     * @return true - если клетка свободна, false - если нет.
     */
    public boolean canMove(Coordinate next, char[][] field) {
        return field[next.getY()][next.getX()] == FLOOR || field[next.getY()][next.getX()] == ITEM;
    }

    /**
     * Определяет координаты точки исходя из положения врага и заданного направления. Производим сдвиг по направлению
     * на одну клетку.
     * @param direction направление следующей точки
     * @return координаты сдвинутой точки.
     */
    public Coordinate getCoordinateWithShift(int direction) { // Coordinate src,
        Coordinate next = new Coordinate();
        switch (direction) {
            case UP : next.setCoordinate(position.getX(), position.getY() - 1);
            break;
            case RIGHT : next.setCoordinate(position.getX() + 1, position.getY());
            break;
            case DOWN : next.setCoordinate(position.getX(), position.getY() + 1);
            break;
            case LEFT : next.setCoordinate(position.getX() - 1, position.getY());
            break;
        }
        return next;
    }

    /**
     * Расчет стоимости кратчайшего пути передвижения из одной точки в другую. Кратчайшего путь можно определить как
     * сумму разницы между координатами по оси х и по оси у.
     * @param begin начальная точка
     * @param end конечая точка
     * @return количество шагов до конечной точки.
     */
    public  int getCostMove(Coordinate begin, Coordinate end) {
        int stepY = Math.abs(end.getY() - begin.getY());
        int stepX = Math.abs(end.getX() - begin.getX());
        return stepY + stepX;
    }

    /**
     * Передвижение на 1 клетку в случайном направлении. Этапы:
     * - создание стрима из массива направлений;
     * - создание стрима из координат точек по каждому из направлений;
     * - выборка точек, на которые враг может переместиться;
     * - сборка координат в массив.
     * Если массив не пустой, то выбираем случайное направоение и меняем координаты врага.
     * @param field поле со структурами
     */
    public void moveInPattern(char[][] field) {
        ArrayList<Integer> directions = getDirectionList();
        ArrayList<Coordinate> positions = directions.stream()
                .map(this::getCoordinateWithShift)
                .filter(i -> canMove(i, field))
                .collect(Collectors.toCollection(ArrayList::new));
        if(!positions.isEmpty()) {
            int randDirection = randInDiaposone(0, positions.size() - 1);
            setPosition(positions.get(randDirection));
        }
    }

    @Override
    public int makeAttackRoll() {
        return agility;
    }

    @Override
    public int makeDefenseRoll() {
        return agility;
    }

    @Override
    public int getDamage() {
        return strength;
    }

    @Override
    public void takeDamage(int damage) {
        health = health - damage;
        game.debug = "Health " + health;
        if (isDead()) {
            notifyObserversDead();
        }
    }

    @Override
    public int getAttackEffect() {
        return Effect.NO_EFFECT.ordinal();
    }

    @Override
    public int getDefEffect() {
        return Effect.NO_EFFECT.ordinal();
    }

    @Override
    public void applyNegativeEffect(int effect){} // НЕОБХОДИМ????????????????????????????

    @Override
    public boolean isDead() {
        return health <= 0;
    }

    public int getType() {
        return type;
    }

    public Coordinate getPosition() {
        return position;
    }

    public void setPosition(Coordinate position) {
        Coordinate curr = this.position;
        this.position = position;
        notifyObserversCoordinate(curr);
    }

    /**
     * DEBUG
     */
    public void setGame(GameSession game) {
        this.game = game;
    }

    @Override
    public void registerObserver(Observer o) {
        this.observer = o;
    }

    @Override
    public void removeObserver(Observer o) {

    }

    @Override
    public void notifyObserversCoordinate(Coordinate curr) {
        observer.updateCoordinate(this, curr);
    }

    @Override
    public void notifyObserversDead() {
        observer.updateDead(this);
    }
}
