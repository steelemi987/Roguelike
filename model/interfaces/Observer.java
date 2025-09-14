package model.interfaces;

import model.items.Item;
import model.level.Coordinate;

public interface Observer {
    void updateCoordinate(Observable o, Coordinate curr);
    void updateDead(Observable o);
    void updateItem(Item item, int action);
}
