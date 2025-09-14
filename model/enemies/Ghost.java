package model.enemies;

import model.character.Character;
import model.level.Coordinate;
import static model.Support.*;

public class Ghost extends Enemy{
    private boolean visible = true;

    public Ghost(Coordinate position, int currentLevel) {
        super(position);
        this.type = GHOST;
        this.maxHealth = 10 + currentLevel;
        this.health = maxHealth;
        this.agility = 20 + currentLevel;
        this.strength = 1 + currentLevel;
        this.hostility = 3;
    }

    public Ghost(Coordinate position, int maxHealth, int health, int agility, int strength, boolean visible) {
        super(position);
        this.type = GHOST;
        this.maxHealth = maxHealth;
        this.health = health;
        this.agility = agility;
        this.strength = strength;
        this.hostility = 3;
        this.visible = visible;
    }

    /**
     * Добавлена мехника невидимости.
     * @param character наш персонаж
     * @param field поле со структурами
     */
    @Override
    public void act(Character character, char[][] field) {
        if (doISeeCharacter(character.getPosition())) {
            if (canFight(character.getPosition())) {
                fightClub.getFight(character);
                becomeVisible();
            } else if (canReachCharacter(character.getPosition(), field)) {
                pursueCharacter(character.getPosition(), field);
                randVisible();
            } else {
                moveInPattern(field);
            }
        } else {
            moveInPattern(field);
        }
    }

    /**
     * Призрак перемещается по всей комнате с помощью телепортации. Так как у нас есть только поле со структурами,
     * определяем область возможного перемещения внутри комнаты. Получаем случайные координаты точки, свободной для
     * перемещения. Меняем координаты призрака на новые. Устанавливаем видимость.
     * @param field поле со структурами
     */
    @Override
    protected void moveInPattern(char[][] field) {
        int up = 0;
        for(int i = -1; isNotWallOrDoor(position.getY() + i, position.getX(), field); i--){
            up++;
        }
        int right = 0;
        for(int i = 1; isNotWallOrDoor(position.getY(), position.getX() + i, field); i++){
            right++;
        }
        int down = 0;
        for(int i = 1; isNotWallOrDoor(position.getY() + i, position.getX(), field); i++){
            down++;
        }
        int left = 0;
        for(int i = -1; isNotWallOrDoor(position.getY(), position.getX() + i, field); i--){
            left++;
        }
        int randY;
        int randX;

        do {
            randY = randInDiapason(position.getY() - up, position.getY() + down);
            randX = randInDiapason(position.getX() - left, position.getX() + right);
        } while (field[randY][randX] != FLOOR && field[randY][randX] != ITEM);

        setPosition(new Coordinate(randX, randY));
        randVisible();
    }

    /**
     * Определяем является ли точка на поле стеной или дверью.
     * @param y координата по оси у (строка в массиве)
     * @param x координата по оси х (столбец в массиве)
     * @param field поле со структурами
     * @return true - если точка на поле не является стеной и не является дверью, false - в других случаях
     */
    private boolean isNotWallOrDoor(int y, int x, char[][] field) {
        char obj = field[y][x];
        return obj != WALL && obj != DOOR && obj != CORRIDOR;
    }

    /**
     * Устанавливает видимость призрака случайно.
     */
    private void randVisible() {
        visible = randInDiapason(0, 2) != 0;
    }

    private void becomeVisible() {
        visible = true;
    }

    public boolean isVisible() {
        return visible;
    }
}
