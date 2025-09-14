package dataLayer;

import com.google.gson.*;
import model.*;
import model.character.Character;
import model.enemies.*;
import model.interfaces.Observer;
import model.items.*;
import model.level.Coordinate;
import model.level.Corridor;
import model.level.Level;
import model.level.Room;
import model.render.RenderForView;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/// Баги
/// При загрузке сохранения нет usedWeapon

public class DataLayer {
    // System.getProperty("user.home") + File.separator + "Documents" + File.separator + "MyAppData" + File.separator + "game_progress.json";
    private static final String SAVE_FILE = "game_progress.json";

    public static void saveStatistics(Statistics.StatEntry entry) {
        JsonObject progress = loadProgress();
        JsonArray stats = progress.getAsJsonArray("statistics");
        if (stats == null) {
            stats = new JsonArray();
        }
        JsonObject statJson = new JsonObject();
        statJson.addProperty("treasures", entry.treasures);
        statJson.addProperty("level", entry.level);
        statJson.addProperty("defeatedEnemies", entry.defeatedEnemies);
        statJson.addProperty("eatenFood", entry.eatenFood);
        statJson.addProperty("drunkElixirs", entry.drunkElixirs);
        statJson.addProperty("readScrolls", entry.readScrolls);
        statJson.addProperty("dealtHits", entry.dealtHits);
        statJson.addProperty("receivedHits", entry.receivedHits);
        statJson.addProperty("stepsTaken", entry.stepsTaken);
        stats.add(statJson);
        progress.add("statistics", stats);
        writeProgress(progress);
    }

    public static List<Statistics.StatEntry> loadStatistics() {
        JsonObject progress = loadProgress();
        JsonArray stats = progress.getAsJsonArray("statistics");
        List<Statistics.StatEntry> entries = new ArrayList<>();
        if (stats != null) {
            for (JsonElement elem : stats) {
                JsonObject stat = elem.getAsJsonObject();
                entries.add(new Statistics.StatEntry(
                        stat.get("treasures").getAsInt(),
                        stat.get("level").getAsInt(),
                        stat.get("defeatedEnemies").getAsInt(),
                        stat.get("eatenFood").getAsInt(),
                        stat.get("drunkElixirs").getAsInt(),
                        stat.get("readScrolls").getAsInt(),
                        stat.get("dealtHits").getAsInt(),
                        stat.get("receivedHits").getAsInt(),
                        stat.get("stepsTaken").getAsInt()
                ));
            }
        }
        return entries;
    }

    public static void saveCurrentSession(GameSession session) {
        JsonObject progress = loadProgress();
        JsonObject sessionJson = new JsonObject();
        sessionJson.addProperty("currentLevel", session.getCurrentLevel());
        sessionJson.add("character", serializeCharacter(session.getCharacter()));
        JsonArray levelsJson = new JsonArray();
        for (Level level : session.getLevels()) {
            levelsJson.add(serializeLevel(level));
        }
        sessionJson.add("levels", levelsJson);
        sessionJson.add("render", serializeRenderForView(session.getRender()));
        progress.add("current_session", sessionJson);
        writeProgress(progress);
    }

    public static GameSession loadCurrentSession() {
        JsonObject progress = loadProgress();
        JsonObject sessionJson = progress.getAsJsonObject("current_session");
        if (sessionJson == null) return null;
        GameSession session = new GameSession();
        session.setCurrentLevel(sessionJson.get("currentLevel").getAsInt());
        Character character = deserializeCharacter(sessionJson.getAsJsonObject("character"), session);
        session.setCharacter(character);
        JsonArray levelsJson = sessionJson.getAsJsonArray("levels");
        List<Level> levels = new ArrayList<>();
        for (JsonElement elem : levelsJson) {
            Level level = deserializeLevel(elem.getAsJsonObject(), session);
            levels.add(level);
        }
        session.setLevels(levels);
        RenderForView render = deserializeRenderForView(sessionJson.getAsJsonObject("render"), session.getField());
        session.setRender(render);
        return session;
    }

    // Удаляем текущую сессию (для новой игры)
    public static void deleteCurrentSession() {
        JsonObject progress = loadProgress();
        progress.remove("current_session");
        writeProgress(progress);
    }

    private static JsonObject loadProgress() {
        try (FileReader reader = new FileReader(SAVE_FILE)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            return new JsonObject();
        }
    }

    private static void writeProgress(JsonObject progress) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(SAVE_FILE)) {
            gson.toJson(progress, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static JsonObject serializeCharacter(Character character) {
        JsonObject json = new JsonObject();
        json.addProperty("maxHealth", character.getMaxHealth());
        json.addProperty("health", character.getHealth());
        json.addProperty("agility", character.getAgility());
        json.addProperty("strength", character.getStrength());
        json.addProperty("sleep", character.isSleep());
        json.add("position", serializeCoordinate(character.getPosition()));
        json.addProperty("defeatedEnemies", character.getDefeatedEnemies());
        json.addProperty("eatenFood", character.getEatenFood());
        json.addProperty("drunkElixirs", character.getDrunkElixirs());
        json.addProperty("readScrolls", character.getReadScrolls());
        json.addProperty("dealtHits", character.getDealtHits());
        json.addProperty("receivedHits", character.getReceivedHits());
        json.addProperty("stepsTaken", character.getStepsTaken());
        JsonArray bonusElixirJson = new JsonArray();
        for (Elixir elixir : character.getBonusElixir()) {
            bonusElixirJson.add(serializeElixir(elixir));
        }
        json.add("bonusElixir", bonusElixirJson);
        if (character.getUsedWeapon() != null) {
            json.add("usedWeapon", serializeItem(character.getUsedWeapon()));
        }
        json.add("backpack", serializeBackpack(character.getBackpack()));
        return json;
    }

    private static Character deserializeCharacter(JsonObject json, GameSession gameSession) {
        Coordinate position = deserializeCoordinate(json.getAsJsonObject("position"));
        Character character = new Character(position, gameSession);
        character.setMaxHealth(json.get("maxHealth").getAsInt());
        character.setHealth(json.get("health").getAsInt());
        character.setAgility(json.get("agility").getAsInt());
        character.setStrength(json.get("strength").getAsInt());
        character.setSleep(json.get("sleep").getAsBoolean());
        character.setDefeatedEnemies(json.get("defeatedEnemies").getAsInt()); //remark
        character.setEatenFood(json.get("eatenFood").getAsInt());
        character.setDrunkElixirs(json.get("drunkElixirs").getAsInt());
        character.setReadScrolls(json.get("readScrolls").getAsInt());
        character.setDealtHits(json.get("dealtHits").getAsInt());
        character.setReceivedHits(json.get("receivedHits").getAsInt());
        character.setStepsTaken(json.get("stepsTaken").getAsInt());
        JsonArray bonusElixirJson = json.getAsJsonArray("bonusElixir");
        for (JsonElement elem : bonusElixirJson) {
            Elixir elixir = deserializeElixir(elem.getAsJsonObject());
            character.addBonusElixir(elixir);
        }
        Backpack backpack = deserializeBackpack(json.getAsJsonObject("backpack"), gameSession);
        character.setBackpack(backpack);
        if (json.has("usedWeapon")) {
            Weapon weapon = (Weapon) deserializeItem(json.getAsJsonObject("usedWeapon"));
            character.setUsedWeaponFromData(weapon);
        }
        return character;
    }

    private static JsonObject serializeCoordinate(Coordinate coord) {
        JsonObject json = new JsonObject();
        json.addProperty("x", coord.getX());
        json.addProperty("y", coord.getY());
        return json;
    }

    private static Coordinate deserializeCoordinate(JsonObject json) {
        return new Coordinate(json.get("x").getAsInt(), json.get("y").getAsInt());
    }

    private static JsonObject serializeLevel(Level level) {
        JsonObject json = new JsonObject();
        JsonArray roomsJson = new JsonArray();
        for (Room room : level.getRooms()) {
            roomsJson.add(serializeRoom(room));
        }
        json.add("rooms", roomsJson);
        JsonArray corridorsJson = new JsonArray();
        for (Corridor corridor : level.getCorridors()) {
            corridorsJson.add(serializeCorridor(corridor));
        }
        json.add("corridors", corridorsJson);
        JsonArray enemiesJson = new JsonArray();
        for (Enemy enemy : level.getEnemies()) {
            enemiesJson.add(serializeEnemy(enemy));
        }
        json.add("enemies", enemiesJson);
        JsonArray itemsJson = new JsonArray();
        for (Item item : level.getItems()) {
            itemsJson.add(serializeItem(item));
        }
        json.add("items", itemsJson);
        json.add("startPosition", serializeCoordinate(level.getStartPosition()));
        json.add("exitPosition", serializeCoordinate(level.getExitPosition()));
        json.addProperty("startRoom", level.getStartRoom());
        return json;
    }

    private static Level deserializeLevel(JsonObject json, GameSession gameSession) {
        Level level = new Level();
        JsonArray roomsJson = json.getAsJsonArray("rooms");
        for (JsonElement elem : roomsJson) {
            level.getRooms().add(deserializeRoom(elem.getAsJsonObject()));
        }
        JsonArray corridorsJson = json.getAsJsonArray("corridors");
        for (JsonElement elem : corridorsJson) {
            level.getCorridors().add(deserializeCorridor(elem.getAsJsonObject()));
        }
        JsonArray enemiesJson = json.getAsJsonArray("enemies");
        for (JsonElement elem : enemiesJson) {
            Enemy enemy = deserializeEnemy(elem.getAsJsonObject());
            enemy.registerObserver(gameSession);
            level.getEnemies().add(enemy);
        }
        JsonArray itemsJson = json.getAsJsonArray("items");
        for (JsonElement elem : itemsJson) {
            level.getItems().add(deserializeItem(elem.getAsJsonObject()));
        }
        level.setStartPosition(deserializeCoordinate(json.getAsJsonObject("startPosition")));
        level.setExitPosition(deserializeCoordinate(json.getAsJsonObject("exitPosition")));
        level.setStartRoom(json.get("startRoom").getAsInt());
        return level;
    }

    private static JsonObject serializeRoom(Room room) {
        JsonObject json = new JsonObject();
        json.add("cornerLeft", serializeCoordinate(room.getCornerLeft()));
        json.add("cornerRight", serializeCoordinate(room.getCornerRight()));
        JsonArray doorsJson = new JsonArray();
        for (Coordinate door : room.getDoors()) {
            doorsJson.add(serializeCoordinate(door));
        }
        json.add("doors", doorsJson);
        json.addProperty("numb", room.getNumb());
        return json;
    }

    private static Room deserializeRoom(JsonObject json) {
        Coordinate cornerLeft = deserializeCoordinate(json.getAsJsonObject("cornerLeft"));
        Coordinate cornerRight = deserializeCoordinate(json.getAsJsonObject("cornerRight"));
        Room room = new Room(cornerLeft.getX(), cornerLeft.getY(), cornerRight.getX() - cornerLeft.getX() + 1, cornerRight.getY() - cornerLeft.getY() + 1, json.get("numb").getAsInt());
        JsonArray doorsJson = json.getAsJsonArray("doors");
        for (JsonElement elem : doorsJson) {
            room.addDoor(deserializeCoordinate(elem.getAsJsonObject()));
        }
        return room;
    }

    private static JsonObject serializeCorridor(Corridor corridor) {
        JsonObject json = new JsonObject();
        JsonArray pointsJson = new JsonArray();
        for (Coordinate point : corridor.getPoints()) {
            pointsJson.add(serializeCoordinate(point));
        }
        json.add("points", pointsJson);
        return json;
    }

    private static Corridor deserializeCorridor(JsonObject json) {
        Corridor corridor = new Corridor();
        JsonArray pointsJson = json.getAsJsonArray("points");
        for (JsonElement elem : pointsJson) {
            corridor.addPoint(deserializeCoordinate(elem.getAsJsonObject()));
        }
        return corridor;
    }

    private static JsonObject serializeEnemy(Enemy enemy) {
        JsonObject json = new JsonObject();
        json.addProperty("type", enemy.getType());
        json.add("position", serializeCoordinate(enemy.getPosition()));
        json.addProperty("health", enemy.getHealth());
        json.addProperty("maxHealth", enemy.getMaxHealth());
        json.addProperty("agility", enemy.getAgility());
        json.addProperty("strength", enemy.getStrength());
        if (enemy instanceof Ghost) {
            json.addProperty("isVisible", ((Ghost) enemy).isVisible());
        } else if (enemy instanceof Vampire) {
            json.addProperty("dodge", ((Vampire) enemy).isDodge());
        } else if (enemy instanceof Ogre) {
            json.addProperty("sleep", ((Ogre) enemy).isSleep());
            json.addProperty("counterattack", ((Ogre) enemy).isCounterattack());
        }
        return json;
    }

    private static Enemy deserializeEnemy(JsonObject json) {
        int type = json.get("type").getAsInt();
        Coordinate position = deserializeCoordinate(json.getAsJsonObject("position"));
        int health = json.get("health").getAsInt();
        int maxHealth = json.get("maxHealth").getAsInt();
        int agility = json.get("agility").getAsInt();
        int strength = json.get("strength").getAsInt();
        Enemy enemy;
        switch (type) {
            case Support.ZOMBIE:
                enemy = new Zombie(position, maxHealth, health, agility, strength);
                break;
            case Support.VAMPIRE:
                enemy = new Vampire(position, maxHealth, health, agility, strength, json.get("dodge").getAsBoolean());
                break;
            case Support.OGRE:
                enemy = new Ogre(position, maxHealth, health, agility, strength, json.get("sleep").getAsBoolean(), json.get("counterattack").getAsBoolean());
                break;
            case Support.GHOST:
                enemy = new Ghost(position, maxHealth, health, agility, strength, json.get("isVisible").getAsBoolean());
                break;
            case Support.SNAKE_MAGE:
                enemy = new SnakeMage(position, maxHealth, health, agility, strength);
                break;
            default:
                enemy = new Enemy(position);
                enemy.setMaxHealth(maxHealth);
                enemy.setHealth(health);
                enemy.setAgility(agility);
                enemy.setStrength(strength);
        }
        return enemy;
    }

    private static JsonObject serializeItem(Item item) {
        JsonObject json = new JsonObject();
        json.addProperty("type", item.getType());
        json.add("position", serializeCoordinate(item.getPosition()));
        if (item instanceof Elixir) {
            json.addProperty("bonus", ((Elixir) item).getBonus());
            json.addProperty("timeLeft", ((Elixir) item).getTimeLeft());
            json.addProperty("subtype", item.getSubtype());
        } else if (item instanceof Scroll) {
            json.addProperty("bonus", ((Scroll) item).getBonus());
            json.addProperty("subtype", item.getSubtype());
        } else if (item instanceof Food) {
            json.addProperty("healthRestore", item.getHealth());
            json.addProperty("subtype", item.getSubtype());
        } else if (item instanceof Weapon) {
            json.addProperty("strength", item.getStrength());
            json.addProperty("subtype", item.getSubtype());
        } else if (item instanceof Treasure) {
            json.addProperty("value", item.getValue());
        }
        return json;
    }

    private static Item deserializeItem(JsonObject json) {
        int type = json.get("type").getAsInt();
        Coordinate position = deserializeCoordinate(json.getAsJsonObject("position"));
        Item item;
        switch (type) {
            case Support.FOOD:
                int healthRestore = json.get("healthRestore").getAsInt();
                int foodSubtype = json.get("subtype").getAsInt();
                item = new Food(position, healthRestore, foodSubtype);
                break;
            case Support.SCROLLS:
                int scrollBonus = json.get("bonus").getAsInt();
                int scrollSubtype = json.get("subtype").getAsInt();
                item = new Scroll(position, scrollBonus, scrollSubtype);
                break;
            case Support.ELIXIRS:
                int elixirBonus = json.get("bonus").getAsInt();
                int timeLeft = json.get("timeLeft").getAsInt();
                int elixirSubtype = json.get("subtype").getAsInt();
                item = new Elixir(position, elixirBonus, timeLeft, elixirSubtype);
                break;
            case Support.WEAPONS:
                int strength = json.get("strength").getAsInt();
                int weaponSubtype = json.get("subtype").getAsInt();
                item = new Weapon(position, strength, weaponSubtype);
                break;
            case Support.TREASURES:
                int value = json.get("value").getAsInt();
                item = new Treasure(position, value);
                break;
            default:
                item = new Treasure(position, 0);
        }
        return item;
    }

    private static JsonObject serializeBackpack(Backpack backpack) {
        JsonObject json = new JsonObject();
        JsonArray itemsJson = new JsonArray();
        for (Item item : backpack.getItems()) {
            itemsJson.add(serializeItem(item));
        }
        json.add("items", itemsJson);
        return json;
    }

    private static Backpack deserializeBackpack(JsonObject json, Observer observer) {
        Backpack backpack = new Backpack(observer);
        JsonArray itemsJson = json.getAsJsonArray("items");
        for (JsonElement elem : itemsJson) {
            backpack.setItem(deserializeItem(elem.getAsJsonObject()));
        }
        return backpack;
    }

    private static JsonObject serializeElixir(Elixir elixir) {
        JsonObject json = new JsonObject();
        json.addProperty("type", Support.ELIXIRS);
        json.add("position", serializeCoordinate(elixir.getPosition()));
        json.addProperty("bonus", elixir.getBonus());
        json.addProperty("timeLeft", elixir.getTimeLeft());
        json.addProperty("subtype", elixir.getSubtype());
        return json;
    }

    private static Elixir deserializeElixir(JsonObject json) {
        Coordinate position = deserializeCoordinate(json.getAsJsonObject("position"));
        int bonus = json.get("bonus").getAsInt();
        int timeLeft = json.get("timeLeft").getAsInt();
        int subtype = json.get("subtype").getAsInt();
        return new Elixir(position, bonus, timeLeft, subtype);
    }

    private static JsonObject serializeRenderForView(RenderForView render) {
        JsonObject json = new JsonObject();
        JsonArray staticVisionJson = new JsonArray();
        for (Coordinate coord : render.getStaticVision()) {
            staticVisionJson.add(serializeCoordinate(coord));
        }
        json.add("staticVision", staticVisionJson);
        return json;
    }

    private static RenderForView deserializeRenderForView(JsonObject json, char[][] fieldMap) {
        RenderForView render = new RenderForView(fieldMap);
        JsonArray staticVisionJson = json.getAsJsonArray("staticVision");
        Set<Coordinate> staticVision = new HashSet<>();
        for (JsonElement elem : staticVisionJson) {
            staticVision.add(deserializeCoordinate(elem.getAsJsonObject()));
        }
        render.setStaticVision(staticVision);
        return render;
    }
}