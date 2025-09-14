package model.items;

import java.util.ArrayList;

import model.character.Character;
import model.level.Coordinate;
import model.interfaces.Observable;
import model.interfaces.Observer;
import static model.Support.*;

public class Backpack implements Observable {
    private final ArrayList<Item> items;
    private Observer observer;

    /**
     * Конструктор класса.
     * @param observer передается единственный наблюдатель в нашем приложении GameSession, для реализации шаблона Наблюдатель
     */
    public Backpack(Observer observer) {
        items = new ArrayList<>(9);
        items.add(new Treasure());
        this.observer = observer;
    }

    /**
     * Использует предмет в зависимости от типа списка предметов, для корректной обработки номера предмета.
     * У нас есть несколько типов списков предметов, с которыми взаимодействует игрок:
     * - общий список - отражает все типы предметов (включая сокровища, с которыми игрок напрямую не взаимодействует);
     * - список оружия - оружие не тратиться;
     * - список еды - тратиться при использовании;
     * - список эликсиров - тратиться при использовании;
     * - список свитков - тратиться при использовании;
     * @param itemNumb порядковый номер предмета в конкретном списке для использования
     * @param character персонаж один на игру
     * @param inventoryType тип списка предметов
     */
    public Item useItem(int itemNumb, Character character, int inventoryType){
        Item item = null;
        switch (inventoryType) {
            case INVENTORY_KEY:
                if (itemNumb >= 0 && itemNumb < items.size()) {
                    item = items.get(itemNumb);
                    items.get(itemNumb).useOnCharacter(character);
                    if(items.get(itemNumb).isUsable()) {
                        items.remove(itemNumb);
                    }
                }
                break;
            case WEAPON_KEY:
                ArrayList<Weapon> weaponList = getWeaponList();
                if(itemNumb >= 0 && itemNumb < weaponList.size()) {
                    weaponList.get(itemNumb).useOnCharacter(character);
                    item = weaponList.get(itemNumb);
                }
                break;
            case FOOD_KEY:
                ArrayList<Food> foodList = getFoodList();
                if(itemNumb >= 0 && itemNumb < foodList.size()) {
                    item = foodList.get(itemNumb);
                    foodList.get(itemNumb).useOnCharacter(character);
                    items.remove(foodList.get(itemNumb));
                }
                break;
            case ELIXIR_KEY:
                ArrayList<Elixir> elixirList = getElixirList();
                if(itemNumb >= 0 && itemNumb < elixirList.size()) {
                    item = elixirList.get(itemNumb);
                    elixirList.get(itemNumb).useOnCharacter(character);
                    items.remove(elixirList.get(itemNumb));
                }
                break;
            case SCROLL_KEY:
                ArrayList<Scroll> scrollList = getScrollList();
                if(itemNumb >= 0 && itemNumb < scrollList.size()) {
                    item = scrollList.get(itemNumb);
                    scrollList.get(itemNumb).useOnCharacter(character);
                    items.remove(scrollList.get(itemNumb));
                }
                break;
        }
        return item;
    }

/**
 * Выбрасывает предмет в зависимости от типа списка предметов, для корректной обработки номера предмета.
 * У нас есть несколько типов списков предметов, с которыми взаимодействует игрок:
 * - общий список - отражает все типы предметов (включая сокровища, с которыми игрок напрямую не взаимодействует);
 * - список оружия - оружие не тратиться;
 * - список еды - тратиться при использовании;
 * - список эликсиров - тратиться при использовании;
 * - список свитков - тратиться при использовании;
 * Выбросить сокровище нельзя.
 * @param numberItem порядковый номер предмета в конкретном списке для использования
 * @param inventoryType тип списка предметов
 */
    public void dropItem(int numberItem, int inventoryType) {
        switch (inventoryType) {
            case INVENTORY_KEY:
                if(!(items.get(numberItem) instanceof Treasure)) {
                    notifyObserversItem(items.get(numberItem), ADD_ITEM);
                }
                break;
            case WEAPON_KEY:
                ArrayList<Weapon> weaponList = getWeaponList();
                if(numberItem >= 0 && numberItem < weaponList.size()) {
                    notifyObserversItem(weaponList.get(numberItem), ADD_ITEM);
                }
                break;
            case FOOD_KEY:
                ArrayList<Food> foodList = getFoodList();
                if(numberItem >= 0 && numberItem < foodList.size()) {
                    notifyObserversItem(foodList.get(numberItem), ADD_ITEM);
                }
                break;
            case ELIXIR_KEY:
                ArrayList<Elixir> elixirList = getElixirList();
                if(numberItem >= 0 && numberItem < elixirList.size()) {
                    notifyObserversItem(elixirList.get(numberItem), ADD_ITEM);
                }
                break;
            case SCROLL_KEY:
                ArrayList<Scroll> scrollList = getScrollList();
                if(numberItem >= 0 && numberItem < scrollList.size()) {
                    notifyObserversItem(scrollList.get(numberItem), ADD_ITEM);
                }
                break;
        }
    }

    /**
     * Удаление предмета из рюкзака
     * @param item ссылка на удаляемый предмет
     */
    public void removeItem(Item item) {
        items.remove(item);
    }

    /**
     * Складывает сокровища в одну ячейку, меняя поле Value
     * @param treasure сокровище которое складываем
     */
    public void putTreasure(Treasure treasure) {
        for(Item i : items) {
            if(i instanceof Treasure) {
                ((Treasure) i).addTreasureValue(treasure);
                break;
            }
        }
    }

    /**
     * Кладем предмет в инвентарь
     * @param item новый предмет
     */
    public void putItem(Item item) {
        if(items.size() < BACKPACK_SIZE) {
            items.add(item);
            notifyObserversItem(item, REMOVE_ITEM);
        }
    }

    public void setItem(Item item) {
        if(items.size() < BACKPACK_SIZE) {
            if (item instanceof Treasure) {
                putTreasure((Treasure) item);
            } else {
                items.add(item);
            }
        }
    }

    /**
     * Создает нужный список имен предметов, которые есть в наличие у игрока, в зависимости от требуемого типа, для
     * отображения в интерфейсе.
     * @param usedWeapon оружие, которое сейчас использует персонаж
     * @param inventoryType тип нужного списка
     * @return список имен предметов
     */
    public ArrayList<String> getInventoryForView(Weapon usedWeapon, int inventoryType) {
        return switch (inventoryType) {
            case INVENTORY_KEY -> getGeneralInventoryList(usedWeapon);
            case WEAPON_KEY -> getWeaponNamesList(usedWeapon);
            case FOOD_KEY -> getFoodNamesList();
            case ELIXIR_KEY -> getElixirNamesList();
            case SCROLL_KEY -> getScrollNamesList();
            default -> new ArrayList<>();
        };
    }

    /**
     * Создает список имен предметов, которые есть в наличие у игрока
     * @param usedWeapon оружие, которое сейчас использует персонаж
     * @return список имен оружий
     */
    private ArrayList<String> getGeneralInventoryList(Weapon usedWeapon) {
        ArrayList<String> list = new ArrayList<>();
        for(Item i : items) {
            String str;
            if (i == usedWeapon) {
                str = i.getNameItem() + USED_FOR_VIEW;
            } else {
                str = i.getNameItem();
            }
            list.add(str);
        }
        return list;
    }

    /**
     * Создает список имен оружий, которые есть в наличие у игрока
     * @param usedWeapon оружие, которое сейчас использует персонаж
     * @return список имен оружий
     */
    private ArrayList<String> getWeaponNamesList(Weapon usedWeapon) {
        ArrayList<String> weaponNamesList = new ArrayList<>();
        ArrayList<Weapon> weaponList = getWeaponList();
        for(Weapon i : weaponList) {
            String str;
            if (i == usedWeapon) {
                str = i.getNameItem() + USED_FOR_VIEW;
            } else {
                str = i.getNameItem();
            }
            weaponNamesList.add(str);
        }
        return weaponNamesList;
    }

    /**
     * Создает список оружия в рюкзаке персонажа
     * @return список оружия
     */
    public ArrayList<Weapon> getWeaponList() {
        ArrayList<Weapon> weaponList = new ArrayList<>();
        for(Item i : items) {
            if (i instanceof Weapon) {
                weaponList.add((Weapon) i);
            }
        }
        return weaponList;
    }

    /**
     * Создает список имен еды, которые есть в наличие у игрока
     * @return список имен еды
     */
    private ArrayList<String> getFoodNamesList() {
        ArrayList<String> foodNamesList = new ArrayList<>();
        ArrayList<Food> foodList = getFoodList();
        for(Food i : foodList) {
            foodNamesList.add(i.getNameItem());
        }
        return foodNamesList;
    }

    /**
     * Создает список еды в рюкзаке персонажа
     * @return список еды
     */
    private ArrayList<Food> getFoodList() {
        ArrayList<Food> foodList = new ArrayList<>();
        for(Item i : items) {
            if (i instanceof Food) {
                foodList.add((Food) i);
            }
        }
        return foodList;
    }

    /**
     * Создает список имен эликсиров, которые есть в наличие у игрока
     * @return список имен эликсиров
     */
    private ArrayList<String> getElixirNamesList() {
        ArrayList<String> elixirNamesList = new ArrayList<>();
        ArrayList<Elixir> elixirList = getElixirList();
        for(Elixir i : elixirList) {
            elixirNamesList.add(i.getNameItem());
        }
        return elixirNamesList;
    }

    /**
     * Создает список эликсиров в рюкзаке персонажа
     * @return список эликсиров
     */
    private ArrayList<Elixir> getElixirList() {
        ArrayList<Elixir> elixirList = new ArrayList<>();
        for(Item i : items) {
            if (i instanceof Elixir) {
                elixirList.add((Elixir) i);
            }
        }
        return elixirList;
    }

    /**
     * Создает список имен свитков, которые есть в наличие у игрока
     * @return список имен свитков
     */
    private ArrayList<String> getScrollNamesList() {
        ArrayList<String> scrollNamesList = new ArrayList<>();
        ArrayList<Scroll> scrollList = getScrollList();
        for(Scroll i : scrollList) {
            scrollNamesList.add(i.getNameItem());
        }
        return scrollNamesList;
    }

    /**
     * Создает список свитков в рюкзаке персонажа
     * @return список свитков
     */
    private ArrayList<Scroll> getScrollList() {
        ArrayList<Scroll> scrollList = new ArrayList<>();
        for(Item i : items) {
            if (i instanceof Scroll) {
                scrollList.add((Scroll) i);
            }
        }
        return scrollList;
    }

    public Item findTreasure() {
        for (Item i : items) {
            if (i instanceof Treasure) {
                return i;
            }
        }
        return items.get(0);
    }
    @Override
    public void registerObserver(Observer o){
        this.observer = o;
    }

    @Override
    public void notifyObserversCoordinate(Coordinate curr){}

    @Override
    public void notifyObserversDead(){}

    /**
     * Сообщает наблюдателю о событии. ADD_ITEM - предмет выложен из рюкзака на уровень, следует добавить предмет в
     * список предметов, расположенных на текущем уровне, REMOVE_ITEM - предмет поднят с "пола", следует убоать с уровня.
     * @param item ссылка на предмет, с которым производиться действие
     * @param action тип действия.
     */
    @Override
    public void notifyObserversItem(Item item, int action) {
        observer.updateItem(item, action);
    }

    public ArrayList<Item> getItems() {
        return items;
    }
}
