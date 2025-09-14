package dataLayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class Statistics {
    private final List<StatEntry> entries = new ArrayList<>();

    public void addEntry(int treasures, int level, int defeatedEnemies, int eatenFood, int drunkElixirs, int readScrolls, int dealtHits, int receivedHits, int stepsTaken) {
        entries.add(new StatEntry(treasures, level, defeatedEnemies, eatenFood, drunkElixirs, readScrolls, dealtHits, receivedHits, stepsTaken));
        Collections.sort(entries, Comparator.reverseOrder());
        DataLayer.saveStatistics(entries.get(0));
    }

    public void loadFromFile() {
        entries.clear();
        entries.addAll(DataLayer.loadStatistics());
        Collections.sort(entries, Comparator.reverseOrder());
    }

    public Integer[][] getEntriesAsArrayInt() {
        Integer[][] result = new Integer[entries.size()][9]; // 9 — число полей
        for (int i = 0; i < entries.size(); i++) {
            StatEntry entry = entries.get(i);
            result[i][0] = entry.getTreasures();
            result[i][1] = entry.getLevel();
            result[i][2] = entry.getDefeatedEnemies();
            result[i][3] = entry.getEatenFood();
            result[i][4] = entry.getDrunkElixirs();
            result[i][5] = entry.getReadScrolls();
            result[i][6] = entry.getDealtHits();
            result[i][7] = entry.getReceivedHits();
            result[i][8] = entry.getStepsTaken();
        }
        return result;
    }

    public static class StatEntry implements Comparable<StatEntry> {
        public final int treasures;
        public final int level;
        public final int defeatedEnemies;
        public final int eatenFood;
        public final int drunkElixirs;
        public final int readScrolls;
        public final int dealtHits;
        public final int receivedHits;
        public final int stepsTaken;

        public StatEntry(int treasures, int level, int defeatedEnemies, int eatenFood, int drunkElixirs, int readScrolls, int dealtHits, int receivedHits, int stepsTaken) {
            this.treasures = treasures;
            this.level = level;
            this.defeatedEnemies = defeatedEnemies;
            this.eatenFood = eatenFood;
            this.drunkElixirs = drunkElixirs;
            this.readScrolls = readScrolls;
            this.dealtHits = dealtHits;
            this.receivedHits = receivedHits;
            this.stepsTaken = stepsTaken;
        }

        @Override
        public int compareTo(StatEntry other) {
            return Integer.compare(this.treasures, other.treasures);
        }

        public int getStepsTaken() {
            return stepsTaken;
        }

        public int getReceivedHits() {
            return receivedHits;
        }

        public int getDealtHits() {
            return dealtHits;
        }

        public int getReadScrolls() {
            return readScrolls;
        }

        public int getDrunkElixirs() {
            return drunkElixirs;
        }

        public int getEatenFood() {
            return eatenFood;
        }

        public int getDefeatedEnemies() {
            return defeatedEnemies;
        }

        public int getLevel() {
            return level;
        }

        public int getTreasures() {
            return treasures;
        }
    }
}

