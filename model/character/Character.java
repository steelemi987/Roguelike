package model.character;

import model.interfaces.Observable;
import model.interfaces.Observer;
import model.enemies.Enemy;
import model.interfaces.Fighter;
import model.items.*;
import model.level.Coordinate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Character implements Fighter, Observable {
    private final Fight fightClub;
    private int maxHealth;
    private int health;
    private int agility;
    private int strength;
    private boolean sleep;
    private Coordinate position;
    private Backpack backpack;
    private final ArrayList<Elixir> bonusElixir;
    private Weapon usedWeapon;
    private Observer observer;
    private int defeatedEnemies = 0;
    private int eatenFood = 0;
    private int drunkElixirs = 0;
    private int readScrolls = 0;
    private int dealtHits = 0;
    private int receivedHits = 0;
    private int stepsTaken = 0;

    /**
     * Конструктор.
     * @param position координаты положения на карте.
     */
    public Character(Coordinate position, Observer observer) {
        this.fightClub = new Fight(this);
        this.sleep = false;
        this.maxHealth = 100;
        this.health = 100;
        this.agility = 20;
        this.strength = 10;
        this.position = position;
        this.observer = observer;
        this.backpack = new Backpack(observer);
        this.bonusElixir = new ArrayList<>();
    }

    /**
     * Использование предмета из рюкзака
     * @param itemNumb номер предмета из списка
     * @param inventoryType тип списка
     */
    public void useItem(int itemNumb, int inventoryType) {
        Item item = backpack.useItem(itemNumb, this, inventoryType);
        if (item instanceof Food) eatenFood++;
        if (item instanceof Elixir) drunkElixirs++;
        if (item instanceof Scroll) readScrolls++;
    }

    /**
     * Выкинуть предмет из рюкзака
     * @param numberItem номер предмета из списка
     * @param inventoryType тип списка
     */
    public void dropItem(int numberItem, int inventoryType) {
        backpack.dropItem(numberItem, inventoryType);
    }

    /**
     * Удаляет предмет из рюкзака. Если предмет используется персонажем, ячейка становится пустой.
     * @param item удаляемый предмет
     */
    public void removeItemFromBackpack(Item item) {
        if(usedWeapon == item) {
            usedWeapon = null;
        }
        backpack.removeItem(item);
    }

    /**
     * Поднять предмет и положить в рюкзак.
     * @param item поднятый предмет
     */
    public void pickItemIfCan(Item item) {
        backpack.putItem(item);
    }

    /**
     * Получить сокровища.
     * @param treasure новое сокровище
     */
    public void pickTreasure(Treasure treasure) {
        backpack.putTreasure(treasure);
    }

    /**
     * Получить количество сокровищ у игрока.
     * @return количество сокровищ
     */
    public int getTreasureValue() {
        return backpack.findTreasure().getValue();
    }

    /**
     * Обновляет время бонуса от эликсиров, уменьшая его. Убирает ранее полученный временный бонус при истекшем времени
     * действия.
     */
    public void updateBonus() {
        Iterator<Elixir> iterator = bonusElixir.iterator();
        while (iterator.hasNext()) {
            Elixir e = iterator.next();
            if (e.decreaseTimeLeft()) {
                e.removeBonusEffect(this);
                iterator.remove();
            }
        }
    }

    @Override
    public void registerObserver(Observer o){
        this.observer = o;
    }

    /**
     * Сообщает наблюдателю об изменении координат персонажа.
     * @param curr координаты до изменения
     */
    @Override
    public void notifyObserversCoordinate(Coordinate curr){
        stepsTaken++;
        observer.updateCoordinate(this, curr);
    }

    /**
     * Сообщает наблюдателю о смерти персонажа.
     */
    @Override
    public void notifyObserversDead() {
        observer.updateDead(this);
    }

    @Override
    public void notifyObserversItem(Item item, int action){}

    /**
     * Инициализирует бой с противником. Поиск противника по координатам в списке врагов.
     * @param position координаты положения предполагаемого врага.
     * @param defenders список врагов
     */
    public void fight(Coordinate position, List<Enemy> defenders) {
        for (Enemy c : defenders) {
            if (position.equals(c.getPosition())){
                fightClub.getFight(c);
                break;
            }
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

    /**
     * Возвращает урон персонажа с учетом оружия
     * @return урон
     */
    @Override
    public int getDamage() {
        dealtHits++;
        return strength + getStrengthFromWeapon();
    }

    /**
     * Персонаж получает урон, если здоровье меньше нуля то персонаж умирает, сообщает об этом наблюдателю
     * @param damage изначальный урон
     */
    @Override
    public void takeDamage(int damage) {
        receivedHits++;
        health = health - damage;
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
    public void applyNegativeEffect(int effect) {
        if (effect == Effect.VAMPIRE.ordinal()) {
            if (maxHealth > 1) {
                maxHealth = maxHealth - 1;
                if (health > maxHealth) {
                    health = maxHealth;
                }
            }
        } else if (effect == Effect.SLEEP.ordinal()) {
            sleep = true;
        }
    }

    @Override
    public boolean isDead() {
        return health <= 0;
    }

    /**
     * Сеттер. Устанавливает координаты позиции персонажа
     * @param position координаты нового положения
     */
    public void setPosition(Coordinate position) {
        Coordinate curr = this.position;
        this.position = position;
        notifyObserversCoordinate(curr);
    }

    /**
     * Геттер. Возвращает координаты позиции персонажа
     * @return координаты позиции персонажа (как понимаю это адрес экземпляра, возможно нужно поменять метод)
     */
    public Coordinate getPosition() {
        return position;
    }

    public boolean isSleep() {
        return sleep;
    }

    public void setSleep(boolean sleep) {
        this.sleep = sleep;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getHealth() {
        return health;
    }

    public int getAgility() {
        return agility;
    }

    public int getStrength() {
        return strength;
    }

    public int getStrengthWithWeapon() {
        return strength + getStrengthFromWeapon();
    }

    /**
     * Добавляет использованный эликсир к списку бонусных. Сам бонус от эликсира уже применен к персонажу, нам лишь
     * нужно отслеживать время окончания действия его бонуса.
     * @param bonus бонус
     */
    public void addBonusElixir(Elixir bonus) {
        bonusElixir.add(bonus);
    }

    /**
     * Реализация применений бонусных свойств предметов. Увеличивает максимальное здоровье персонажа на величину бонуса.
     * @param bonus значение бонусной характеристики
     */
    public void increaseMaxHealth(int bonus) {
        maxHealth = maxHealth + bonus;
        health = health + bonus;
    }

    /**
     * Реализация применений бонусных свойств предметов. Увеличивает ловкость персонажа на величину бонуса.
     * @param bonus значение бонусной характеристики
     */
    public void increaseAgility(int bonus) {
        agility = agility + bonus;
    }

    /**
     * Реализация применений бонусных свойств предметов. Увеличивает силу персонажа на величину бонуса.
     * @param bonus значение бонусной характеристики
     */
    public void increaseStrength(int bonus) {
        strength = strength + bonus;
    }

    /**
     * Реализация снятия бонусных свойств предметов. Уменьшает максимальное здоровье персонажа на величину бонуса.
     * @param bonus значение бонусной характеристики
     */
    public void decreaseMaxHealth(int bonus) {
        maxHealth = maxHealth - bonus;
        if(maxHealth <= 0) {
            maxHealth = 1;
        }
        if(health > maxHealth) {
            health = maxHealth;
        }
    }

    /**
     * Реализация снятия бонусных свойств предметов. Уменьшает ловкость персонажа на величину бонуса.
     * @param bonus значение бонусной характеристики
     */
    public void decreaseAgility(int bonus) {
        agility = agility - bonus;
        if(agility <= 0) {
            agility = 1;
        }
    }

    /**
     * Реализация снятия бонусных свойств предметов. Уменьшает силу персонажа на величину бонуса.
     * @param bonus значение бонусной характеристики
     */
    public void decreaseStrength(int bonus) {
        strength = strength - bonus;
        if(strength <= 0) {
            strength = 1;
        }
    }

    /**
     * Реализация применений бонусных свойств предметов. Увеличивает здоровье персонажа на величину бонуса, но не больше
     * максимального здоровья.
     * @param bonus значение бонусной характеристики
     */
    public void restoreHealth(int bonus) {
        health = health + bonus;
        if(health > maxHealth) {
            health = maxHealth;
        }
    }

    /**
     * Использование оружия. Есть возможность снять текущее оружие или поменять на новое.
     * @param newWeapon оружие для использования
     */
    public void changeWeapon(Weapon newWeapon) {
        if (usedWeapon == newWeapon) {
            usedWeapon = null;
        } else {
            usedWeapon = newWeapon;
        }
    }

    public void setUsedWeaponFromData(Weapon newWeapon) {
        ArrayList<Weapon> weaponList = backpack.getWeaponList();
        if (!weaponList.isEmpty()) {
            for (Weapon w : weaponList) {
                if (w.equals(newWeapon)) {
                    usedWeapon = w;
                    break;
                }
            }
        }
    }

    public void increaseDefeatedEnemies() {
        defeatedEnemies++;
    }

    /**
     * Получение бонуса силы от текущего оружия, если оружие не надето - бонус ноль.
     * @return бонус силы от оружия
     */
    public int getStrengthFromWeapon() {
        int bonus = 0;
        if(usedWeapon != null) {
            bonus = usedWeapon.getStrength();
        }
        return bonus;
    }

    public Backpack getBackpack() {
        return backpack;
    }

    public Weapon getUsedWeapon() {
        return usedWeapon;
    }

    public int getDefeatedEnemies() {
        return defeatedEnemies;
    }

    public int getEatenFood() {
        return eatenFood;
    }

    public int getDrunkElixirs() {
        return drunkElixirs;
    }

    public int getReadScrolls() {
        return readScrolls;
    }

    public int getDealtHits() {
        return dealtHits;
    }

    public int getReceivedHits() {
        return receivedHits;
    }

    public int getStepsTaken() {
        return stepsTaken;
    }

    public ArrayList<Elixir> getBonusElixir() {
        return bonusElixir;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public void setBackpack(Backpack backpack) {
        this.backpack = backpack;
    }

    public void setDefeatedEnemies(int defeatedEnemies) {
        this.defeatedEnemies = defeatedEnemies;
    }

    public void setEatenFood(int eatenFood) {
        this.eatenFood = eatenFood;
    }

    public void setDrunkElixirs(int drunkElixirs) {
        this.drunkElixirs = drunkElixirs;
    }

    public void setReadScrolls(int readScrolls) {
        this.readScrolls = readScrolls;
    }

    public void setDealtHits(int dealtHits) {
        this.dealtHits = dealtHits;
    }

    public void setReceivedHits(int receivedHits) {
        this.receivedHits = receivedHits;
    }

    public void setStepsTaken(int stepsTaken) {
        this.stepsTaken = stepsTaken;
    }
}