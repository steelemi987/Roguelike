package model;

public interface Observable {
    void registerObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObserversCoordinate(Coordinate curr);
    void notifyObserversDead();
}
