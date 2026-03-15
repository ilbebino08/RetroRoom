package com.retroroom;

import java.util.ArrayList;
import java.util.Comparator; // Per ordinare
import java.util.List;

public class Scoreboard {
    public enum SortOrder {
        DESCENDING,
        ASCENDING
    }

    // Implementazione logica della classifica
    private int maxSize; // Dimensione massima data dal costruttore
    private ArrayList<ScoreEntry> entries;
    private SortOrder sortOrder;

    public Scoreboard(int maxSize) {
        this(maxSize, SortOrder.DESCENDING);
    }

    public Scoreboard(int maxSize, SortOrder sortOrder) {
        this.maxSize = maxSize;
        this.entries = new ArrayList<>();
        this.sortOrder = sortOrder;
    }

    public synchronized void addScore(String name, int score) {
        addOrUpdateBest(name, score);
    }

    public synchronized boolean addOrUpdateBest(String name, int score) {
        String normalizedName = name == null ? "" : name.trim();
        if (normalizedName.isEmpty()) {
            return false;
        }

        ScoreEntry existing = null;
        for (ScoreEntry entry : entries) {
            if (entry.getName().equalsIgnoreCase(normalizedName)) {
                existing = entry;
                break;
            }
        }

        boolean changed = false;
        if (existing == null) {
            entries.add(new ScoreEntry(normalizedName, score));
            changed = true;
        } else if (isBetter(score, existing.getScore())) {
            existing.score = score;
            changed = true;
        }

        sortAndTrim();
        return changed;
    }

    public synchronized boolean removeByName(String name) {
        String normalizedName = name == null ? "" : name.trim();
        if (normalizedName.isEmpty()) {
            return false;
        }
        return entries.removeIf(entry -> entry.getName().equalsIgnoreCase(normalizedName));
    }

    private boolean isBetter(int newScore, int oldScore) {
        return sortOrder == SortOrder.ASCENDING ? newScore < oldScore : newScore > oldScore;
    }

    private void sortAndTrim() {

        if (sortOrder == SortOrder.ASCENDING) {
            entries.sort(Comparator.comparingInt(ScoreEntry::getScore));
        } else {
            entries.sort(Comparator.comparingInt(ScoreEntry::getScore).reversed());
        }

        // Se superiamo la dimensione massima, rimuoviamo l'ultimo
        if (entries.size() > maxSize) {
            entries.remove(entries.size() - 1);
        }
    }

    public synchronized List<ScoreEntry> getEntries() {
        return new ArrayList<>(entries);
    }

    // Classe interna statico per i dati del punteggio
    public static class ScoreEntry {
        private String name;
        private int score;

        public ScoreEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }
    }
}
