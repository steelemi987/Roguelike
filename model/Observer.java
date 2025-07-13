package model;

public interface Observer {
    void updateCoordinate(Observable o, Coordinate curr);
    void updateDead(Observable o);
}
