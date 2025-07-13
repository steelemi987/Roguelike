package model.render;

import static model.Support.*;
import model.Character;
import model.Coordinate;
import model.GameSession;
import model.Room;
import model.enemies.Enemy;
import model.enemies.Ghost;

import java.util.ArrayList;
import java.util.Arrays;


public class RenderOld {
    private char[][] fieldForView;
    private char[][] fieldMap;
    private char[][] fieldStaticScene;
    private Character character;
    private final static int radiusVision = 5;
    //    public static enum directionAngle {UP_RIGHT, RIGHT_UP, RIGHT_DOWN, DOWN_RIGHT, DOWN_LEFT, LEFT_DOWN, LEFT_UP, UP_LEFT}
    private static final int UP_RIGHT = 0;
    private static final int RIGHT_UP = 1;
    private static final int RIGHT_DOWN = 2;
    private static final int DOWN_RIGHT = 3;
    private static final int DOWN_LEFT = 4;
    private static final int LEFT_DOWN = 5;
    private static final int LEFT_UP = 6;
    private static final int UP_LEFT = 7;


    public RenderOld(char[][] fieldMap) {
        fieldStaticScene = new char[LEVEL_HEIGHT][LEVEL_WIDTH];
        fieldForView = new char[LEVEL_HEIGHT][LEVEL_WIDTH];
        this.fieldMap = fieldMap;
    }

    public void renderField(GameSession game) {
        renderStaticScene(game);
        clearField(fieldForView);
        renderFieldForView(game);
        fieldForView[character.getPosition().getY()][character.getPosition().getX()] = CHARACTER;
    }

    public void renderStaticScene(GameSession game) {
        int charY = character.getPosition().getY();
        int charX = character.getPosition().getX();
        for (int i = charY - radiusVision; i <= charY + radiusVision; i++) {
            for (int j = charX - radiusVision; j <= charX + radiusVision; j++) {
                if (checkOutOfBound(i, j)) {
                    if (fieldMap[i][j] == DOOR || fieldMap[i][j] == CORRIDOR || fieldMap[i][j] == WALL) { // || fieldMap[i][j] == EXIT
                        fieldStaticScene[i][j] = fieldMap[i][j];
                    }
                }
            }
        }
        char obj = game.getObjectOnCoordinate(character.getPosition());
        if (obj == FLOOR || obj == ITEM) {
            renderRoomWalls(game);
        }
    }

    public void renderRoomWalls(GameSession game) {
        Room room = getCurrentRoom(game);
        int width = room.getCornerRight().getX() - room.getCornerLeft().getX();
        int height = room.getCornerRight().getY() - room.getCornerLeft().getY();
        for(int i = 0; i <= width; i++) {
            fieldStaticScene[room.getCornerLeft().getY()][i + room.getCornerLeft().getX()] = WALL;
            fieldStaticScene[room.getCornerRight().getY()][i + room.getCornerLeft().getX()] = WALL;
        }
        for(int i = 0; i <= height; i++) {
            fieldStaticScene[i + room.getCornerLeft().getY()][room.getCornerLeft().getX()] = WALL;
            fieldStaticScene[i + room.getCornerLeft().getY()][room.getCornerRight().getX()] = WALL;
        }

        for(Coordinate d : room.getDoors()) {
            fieldStaticScene[d.getY()][d.getX()] = DOOR;
        }
    }

    public void renderFieldForView(GameSession game) {
        for (int i = 0; i < LEVEL_HEIGHT; i++) {
            System.arraycopy(fieldStaticScene[i], 0, fieldForView[i], 0, LEVEL_WIDTH);
        }
        renderDynamicSceneType(game);
    }

    public void renderDynamicSceneType(GameSession game) {
        char obj = game.getObjectOnCoordinate(character.getPosition());
        if (obj == CORRIDOR) {
            renderLineOfSight(game);
        } else if (obj == DOOR) {
            renderDoorVision(game);
        } else if (obj == FLOOR || obj == ITEM) {
            renderRoomVision(game);
        }
    }

    public void renderDoorVision(GameSession game) {
        int charY = character.getPosition().getY();
        int charX = character.getPosition().getX();
        ArrayList<Coordinate> checkCorridor = new ArrayList<>();
        checkCorridor.add(new Coordinate(charX, charY - 1));
        checkCorridor.add(new Coordinate(charX, charY + 1));
        checkCorridor.add(new Coordinate(charX - 1, charY));
        checkCorridor.add(new Coordinate(charX + 1, charY));
        int index = 0;
        for(Coordinate c : checkCorridor) {
            if(fieldMap[c.getY()][c.getX()] == CORRIDOR) {
                index = checkCorridor.indexOf(c);
            }
        }
        ArrayList<Coordinate> dynamicScene = new ArrayList<>();
        switch (index) {
            case 0: renderAngleVison(dynamicScene, DOWN_RIGHT);
                renderAngleVison(dynamicScene, DOWN_LEFT);
                renderDirectVison(dynamicScene, DOWN);
                break;
            case 1: renderAngleVison(dynamicScene, UP_LEFT);
                renderAngleVison(dynamicScene, UP_RIGHT);
                renderDirectVison(dynamicScene, UP);
                break;
            case 2: renderAngleVison(dynamicScene, RIGHT_UP);
                renderAngleVison(dynamicScene, RIGHT_DOWN);
                renderDirectVison(dynamicScene, RIGHT);
                break;
            case 3: renderAngleVison(dynamicScene, LEFT_DOWN);
                renderAngleVison(dynamicScene, LEFT_UP);
                renderDirectVison(dynamicScene, LEFT);
                break;
        }
        renderDynamicScene(dynamicScene, game);
//        int directionOfVision = switch (index) {
//            case 0 -> DOWN;
//            case 1 -> UP;
//            case 2 -> RIGHT;
//            default -> LEFT;
//        };

//            for (int i = 1; i <= radiusVision && checkOutOfBound(charY - i, charX); i++) {
//                if(fieldMap[charY - i][charX] == WALL){
//                    break;
//                }
//                dynamicScene.add(new Coordinate(charX, charY - i));
//            }
//            for (int i = 1; i <= radiusVision && checkOutOfBound(charY - i, charX + i); i++) {
//                if(fieldMap[charY - i][charX + i] == WALL){
//                    break;
//                }
//                dynamicScene.add(new Coordinate(charX + i, charY - i));
//            }
//            for (int i = 2; i <= radiusVision && checkOutOfBound(charY - i, charX + i); i++) {
//                if(fieldMap[charY - i][charX + i] == WALL){
//                    break;
//                }
//                dynamicScene.add(new Coordinate(charX + i, charY - i));
//            }
//            for (int i = 3; i <= radiusVision && checkOutOfBound(charY - i, charX + i); i++) {
//                if(fieldMap[charY - i][charX + i] == WALL){
//                    break;
//                }
//                dynamicScene.add(new Coordinate(charX + i, charY - i));
//            }
//            for (int i = 4; i <= radiusVision && checkOutOfBound(charY - i, charX + i); i++) {
//                if(fieldMap[charY - i][charX + i] == WALL){
//                    break;
//                }
//                dynamicScene.add(new Coordinate(charX + i, charY - i));
//            }
//            for (int i = 5; i <= radiusVision && checkOutOfBound(charY - i, charX + i); i++) {
//                if(fieldMap[charY - i][charX + i] == WALL){
//                    break;
//                }
//                dynamicScene.add(new Coordinate(charX + i, charY - i));
//            }
//        if(fieldMap[charY + i * multiY][charX + i * multiX] ==)
//        int x = charX / (MAX_WIDTH_ROOM + 1);
//        int y = charY / (MAX_HEIGHT_ROOM + 1);
//        int numRoom = y * 3 + x;
    }

    public void renderAngleVison(ArrayList<Coordinate> dynamicScene, int direction) {
        int charY = character.getPosition().getY();
        int charX = character.getPosition().getX();
        int signX = 1;
        int signY = 1;
        int modI = 0;
        int modJ = 0;
        ArrayList<Integer> check = new ArrayList<>(Arrays.asList(0, 3, 4, 7));
        if (check.contains(direction)) {
            modJ = 1;
        } else {
            modI = 1;
        }
        if (direction / 4 != 0) {
            signX = -1;
        }
        if (!(direction > 1 && direction < 6)) {
            signY = -1;
        }

        boolean vision = true; // 7
        boolean flag = true;
        for(int i = 1; i <= radiusVision && flag; i++) {
            int yCoord, xCoord;
            for (int j = i; (j <= radiusVision) && checkOutOfBound(
                    yCoord = charY + signY * (i * modI + j * modJ),
                    xCoord = charX + signX * (i * modJ + j * modI)
            ); j++) {
                if (fieldMap[yCoord][xCoord] == WALL || fieldMap[yCoord][xCoord] == DOOR) {
                    vision = false;
                    if (j == i) {
                        flag = false;
                    }
                }
                if (vision && fieldMap[yCoord][xCoord] != DOOR) { //  && fieldMap[charY - i][charX] != COR
                    dynamicScene.add(new Coordinate(xCoord, yCoord));
                }
            }
            vision = true;
        }

//        boolean vision = true;
//        for (int i = 1; i <= radiusVision && checkOutOfBound(charY - i, charX); i++) {
//            if (fieldMap[charY - i][charX] == WALL) {
//                vision = false;
//            }
//            if (vision && fieldMap[charY - i][charX] != DOOR) {
//                dynamicScene.add(new Coordinate(charX, charY - i));
//            }
//        }
//        vision = true; // 0
//        boolean flag = true;
//        for(int i = 1; i <= radiusVision && flag; i++) {
//            for (int j = i; j <= radiusVision && checkOutOfBound(charY - j, charX + i); j++) {
//                if (fieldMap[charY - j][charX + i] == WALL) {
//                    vision = false;
//                    if (j == i) {
//                        flag = false;
//                    }
//                }
//                if (vision && fieldMap[charY - j][charX + i] != DOOR) {
//                    dynamicScene.add(new Coordinate(charX + i, charY - j));
//                }
//            }
//            vision = true;
//
//        }
//
//        vision = true; // 1
//        flag = true;
//        for(int i = 1; i <= radiusVision && flag; i++) {
//            for (int j = i; j <= radiusVision && checkOutOfBound(charY - i, charX + j); j++) {
//                if (fieldMap[charY - i][charX + j] == WALL) {
//                    vision = false;
//                    if (j == i) {
//                        flag = false;
//                    }
//                }
//                if (vision && fieldMap[charY - i][charX + j] != DOOR) {
//                    dynamicScene.add(new Coordinate(charX + j, charY - i));
//                }
//            }
//            vision = true;
//        }
//
//        vision = true; // 2
//        flag = true;
//        for(int i = 1; i <= radiusVision && flag; i++) {
//            for (int j = i; j <= radiusVision && checkOutOfBound(charY + i, charX + j); j++) {
//                if (fieldMap[charY + i][charX + j] == WALL) {
//                    vision = false;
//                    if (j == i) {
//                        flag = false;
//                    }
//                }
//                if (vision && fieldMap[charY + i][charX + j] != DOOR) {
//                    dynamicScene.add(new Coordinate(charX + j, charY + i));
//                }
//            }
//            vision = true;
//        }
//
//        vision = true; // 3
//        flag = true;
//        for(int i = 1; i <= radiusVision && flag; i++) {
//            for (int j = i; j <= radiusVision && checkOutOfBound(charY + j, charX + i); j++) {
//                if (fieldMap[charY + j][charX + i] == WALL) {
//                    vision = false;
//                    if (j == i) {
//                        flag = false;
//                    }
//                }
//                if (vision && fieldMap[charY + j][charX + i] != DOOR) {
//                    dynamicScene.add(new Coordinate(charX + i, charY + j));
//                }
//            }
//            vision = true;
//        }
//
//        vision = true; // 4
//        flag = true;
//        for(int i = 1; i <= radiusVision && flag; i++) {
//            for (int j = i; j <= radiusVision && checkOutOfBound(charY + j, charX - i); j++) {
//                if (fieldMap[charY + j][charX - i] == WALL) {
//                    vision = false;
//                    if (j == i) {
//                        flag = false;
//                    }
//                }
//                if (vision && fieldMap[charY + j][charX - i] != DOOR) {
//                    dynamicScene.add(new Coordinate(charX - i, charY + j));
//                }
//            }
//            vision = true;
//        }
//
//        vision = true; // 5
//        flag = true;
//        for(int i = 1; i <= radiusVision && flag; i++) {
//            for (int j = i; j <= radiusVision && checkOutOfBound(charY + i, charX - j); j++) {
//                if (fieldMap[charY + i][charX - j] == WALL) {
//                    vision = false;
//                    if (j == i) {
//                        flag = false;
//                    }
//                }
//                if (vision && fieldMap[charY + i][charX - j] != DOOR) {
//                    dynamicScene.add(new Coordinate(charX - j, charY + i));
//                }
//            }
//            vision = true;
//        }
//
//        vision = true; // 6
//        flag = true;
//        for(int i = 1; i <= radiusVision && flag; i++) {
//            for (int j = i; j <= radiusVision && checkOutOfBound(charY - i, charX - j); j++) {
//                if (fieldMap[charY - i][charX - j] == WALL) {
//                    vision = false;
//                    if (j == i) {
//                        flag = false;
//                    }
//                }
//                if (vision && fieldMap[charY - i][charX - j] != DOOR) {
//                    dynamicScene.add(new Coordinate(charX - j, charY - i));
//                }
//            }
//            vision = true;
//        }
//
//        vision = true; // 7
//        flag = true;
//        for(int i = 1; i <= radiusVision && flag; i++) {
//            for (int j = i; j <= radiusVision && checkOutOfBound(charY - j, charX - i); j++) {
//                if (fieldMap[charY - j][charX - i] == WALL) {
//                    vision = false;
//                    if (j == i) {
//                        flag = false;
//                    }
//                }
//                if (vision && fieldMap[charY - j][charX - i] != DOOR) {
//                    dynamicScene.add(new Coordinate(charX - i, charY - j));
//                }
//            }
//            vision = true;
//        }
    }

    public void renderDirectVison(ArrayList<Coordinate> dynamicScene, int direction) {
        int charY = character.getPosition().getY();
        int charX = character.getPosition().getX();
        int modY = 0;
        int modX = 0;
        if (direction == UP || direction == DOWN) {
            modY = 1;
        } else {
            modX = 1;
        }
        if (direction < 0) {
            modY *= -1;
            modX *= -1;
        }

        boolean vision = true;
        for (int i = 1; i <= radiusVision && checkOutOfBound(charY + i * modY, charX + i * modX); i++) {
            if (fieldMap[charY + i * modY][charX + i * modX] == WALL || fieldMap[charY + i * modY][charX + i * modX] == DOOR) {
                vision = false;
            }
            if (vision && fieldMap[charY + i * modY][charX + i * modX] != DOOR) { //  && fieldMap[charY - i][charX] != COR
                dynamicScene.add(new Coordinate(charX + i * modX, charY + i * modY));
            }
        }

//        boolean vision = true; // UP
//        for (int i = 1; i <= radiusVision && checkOutOfBound(charY - i, charX); i++) {
//            if (fieldMap[charY - i][charX] == WALL) {
//                vision = false;
//            }
//            if (vision && fieldMap[charY - i][charX] != DOOR) { //  && fieldMap[charY - i][charX] != COR
//                dynamicScene.add(new Coordinate(charX, charY - i));
//            }
//        }
//
//        vision = true; // RIGHT
//        for (int i = 1; i <= radiusVision && checkOutOfBound(charY, charX + i); i++) {
//            if (fieldMap[charY][charX + i] == WALL) {
//                vision = false;
//            }
//            if (vision && fieldMap[charY][charX + i] != DOOR) { //  && fieldMap[charY - i][charX] != COR
//                dynamicScene.add(new Coordinate(charX + i, charY));
//            }
//        }
//
//        vision = true; // DOWN
//        for (int i = 1; i <= radiusVision && checkOutOfBound(charY + i, charX); i++) {
//            if (fieldMap[charY + i][charX] == WALL) {
//                vision = false;
//            }
//            if (vision && fieldMap[charY + i][charX] != DOOR) { //  && fieldMap[charY - i][charX] != COR
//                dynamicScene.add(new Coordinate(charX, charY + i));
//            }
//        }
//
//        vision = true; // LEFT
//        for (int i = 1; i <= radiusVision && checkOutOfBound(charY, charX - i); i++) {
//            if (fieldMap[charY][charX - i] == WALL) {
//                vision = false;
//            }
//            if (vision && fieldMap[charY][charX - i] != DOOR) { //  && fieldMap[charY - i][charX] != COR
//                dynamicScene.add(new Coordinate(charX - i, charY));
//            }
//        }
    }

    public void renderRoomVision(GameSession game) {
        Room room = getCurrentRoom(game);

        int width = room.getCornerRight().getX() - room.getCornerLeft().getX();
        int height = room.getCornerRight().getY() - room.getCornerLeft().getY();
        int endY = room.getCornerLeft().getY() + height;
        int endX = room.getCornerLeft().getX() + width;

        ArrayList<Coordinate> dynamicScene = new ArrayList<>();
        for(int i = room.getCornerLeft().getY() + 1; i < endY; i++){
            for(int j = room.getCornerLeft().getX() + 1; j < endX; j++){
                dynamicScene.add(new Coordinate(j, i));
            }
        }
        renderDynamicScene(dynamicScene, game);

    }

    public Room getCurrentRoom(GameSession game) {
        int charY = character.getPosition().getY();
        int charX = character.getPosition().getX();
        int x = charX / (MAX_WIDTH_ROOM + 1);
        int y = charY / (MAX_HEIGHT_ROOM + 1);
        int numRoom = y * 3 + x;
        return game.getRoomWithNumb(numRoom);
    }

    public void renderDynamicScene(ArrayList<Coordinate> dynamicScene, GameSession game){
        for(Coordinate c : dynamicScene) {
            fieldForView[c.getY()][c.getX()] = FLOOR;
        }
//        for(Item i : game.getItemList()){
//            fieldForView[i.getPosition().getY()][i.getPosition().getX()] = ITEM;
//        }
        for(Enemy e : game.getEnemyList()) {
            if (dynamicScene.contains(e.getPosition())) {
                if((e instanceof Ghost g && g.isVisible()) || !(e instanceof Ghost)) {
                    fieldForView[e.getPosition().getY()][e.getPosition().getX()] = (char) e.getType();
                }
            }
        }
        if(dynamicScene.contains(game.getExitCoordinate())) {
            fieldForView[game.getExitCoordinate().getY()][game.getExitCoordinate().getX()] = EXIT;
        }
    }

    public void renderLineOfSight(GameSession game) {
        ArrayList<Coordinate> dynamicScene = new ArrayList<>();
        renderLine(dynamicScene, -1, -1);
        renderLine(dynamicScene, -1, 0);
        renderLine(dynamicScene, -1, 1);
        renderLine(dynamicScene, 0, -1);
        renderLine(dynamicScene, 0, 1);
        renderLine(dynamicScene, 1, -1);
        renderLine(dynamicScene, 1, 0);
        renderLine(dynamicScene, 1, 1);
        renderDynamicScene(dynamicScene, game);
//        int charY = character.getPosition().getY();
//        int charX = character.getPosition().getX();
//
//        boolean vision = false;//
//        for (int i = 1; i <= radiusVision && checkOutOfBound(charY - i, charX - i); i++) {
//            if(fieldMap[charY - i][charX - i] == DOOR){
//                vision = true;
//            }
//            if (vision) {
//                fieldForView[charY - i][charX - i] = fieldMap[charY - i][charX - i];
//            }
//        }
//
//        vision = false;//
//        for (int i = 1; i <= radiusVision && checkOutOfBound(charY - i, charX); i++) {
//            if(fieldMap[charY - i][charX] == DOOR){
//                vision = true;
//            }
//            if (vision) {
//                fieldForView[charY - i][charX] = fieldMap[charY - i][charX];
//            }
//        }
//
//        vision = false;//
//        for (int i = 1; i <= radiusVision && checkOutOfBound(charY - i, charX + i); i++) {
//            if(fieldMap[charY - i][charX + i] == DOOR){
//                vision = true;
//            }
//            if (vision) {
//                fieldForView[charY - i][charX + i] = fieldMap[charY - i][charX + i];
//            }
//        }
//
//        vision = false;//
//        for (int i = 1; i <= radiusVision && checkOutOfBound(charY, charX - i); i++) {
//            if(fieldMap[charY][charX - i] == DOOR){
//                vision = true;
//            }
//            if (vision) {
//                fieldForView[charY][charX - i] = fieldMap[charY][charX - i];
//            }
//        }
//
//        vision = false;//
//        for (int i = 1; i <= radiusVision && checkOutOfBound(charY, charX + i); i++) {
//            if(fieldMap[charY][charX + i] == DOOR){
//                vision = true;
//            }
//            if (vision) {
//                fieldForView[charY][charX + i] = fieldMap[charY][charX + i];
//            }
//        }
//
//        vision = false;//
//        for (int i = 1; i <= radiusVision && checkOutOfBound(charY + i, charX - i); i++) {
//            if(fieldMap[charY + i][charX - i] == DOOR){
//                vision = true;
//            }
//            if (vision) {
//                fieldForView[charY + i][charX - i] = fieldMap[charY + i][charX - i];
//            }
//        }
//
//        vision = false;//
//        for (int i = 1; i <= radiusVision && checkOutOfBound(charY + i, charX); i++) {
//            if(fieldMap[charY + i][charX] == DOOR){
//                vision = true;
//            }
//            if (vision) {
//                fieldForView[charY + i][charX] = fieldMap[charY + i][charX];
//            }
//        }
//
//        vision = false;//
//        for (int i = 1; i <= radiusVision && checkOutOfBound(charY + i, charX + i); i++) {
//            if(fieldMap[charY + i][charX + i] == DOOR){
//                vision = true;
//            }
//            if (vision) {
//                fieldForView[charY + i][charX + i] = fieldMap[charY + i][charX + i];
//            }
//        }
    }

    public void renderLine(ArrayList<Coordinate> dynamicScene, int multiY, int multiX) {
        boolean vision = false;
        int charY = character.getPosition().getY();
        int charX = character.getPosition().getX();
        for (int i = 1; i <= radiusVision && checkOutOfBound(charY + i * multiY, charX + i * multiX); i++) {
            if(fieldMap[charY + i * multiY][charX + i * multiX] == DOOR){
                vision = true;
            }
            if (vision) {
//                dynamicScene.add(new Coordinate(charX + i * multiX, charY + i * multiY));
                fieldForView[charY + i * multiY][charX + i * multiX] = fieldMap[charY + i * multiY][charX + i * multiX];
            }
        }
    }

    public boolean checkOutOfBound(int y, int x) {
        return y < LEVEL_HEIGHT && y >= 0 && x < LEVEL_WIDTH && x >= 0;
    }

    public void renderRadiusVision() {
        clearField(fieldForView);
        int charY = character.getPosition().getY();
        int charX = character.getPosition().getX();
        for (int i = charY - radiusVision; i <= charY + radiusVision; i++) {
            for (int j = charX - radiusVision; j <= charX + radiusVision; j++) {
                if (i < LEVEL_HEIGHT && i >= 0 && j < LEVEL_WIDTH && j >= 0) {
                    fieldForView[i][j] = fieldMap[i][j];
                }
            }
        }
    }

    public void clearField(char[][] field) {
        for (int i = 0; i < LEVEL_HEIGHT; i++) {
            for (int j = 0; j < LEVEL_WIDTH; j++) {
                field[i][j] = ' ';
            }
        }
    }

    public void clearFieldsForNewLevel() {
//        clearField(fieldForView);
        clearField(fieldStaticScene);
    }

    public char[][] getField(GameSession game) {
        renderField(game);
        return fieldForView;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }
}
