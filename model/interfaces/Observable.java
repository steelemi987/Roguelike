package model.interfaces;

import model.items.Item;
import model.level.Coordinate;

public interface Observable {
    void registerObserver(Observer o);
    void notifyObserversCoordinate(Coordinate curr);
    void notifyObserversDead();
    void notifyObserversItem(Item item, int action);
}
