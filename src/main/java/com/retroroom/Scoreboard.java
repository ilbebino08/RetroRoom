package com.retroroom;

import java.util.ArrayList;
import java.util.Comparator; // Per ordinare
import java.util.List;

public class Scoreboard {
    // Implementazione logica della classifica
    private int maxSize; // Dimensione massima data dal costruttore
    private ArrayList<ScoreEntry> entries;

    public Scoreboard(int maxSize) {
        this.maxSize = maxSize;
        this.entries = new ArrayList<>();
    }

    public void addScore(String name, int score) {
        ScoreEntry newEntry = new ScoreEntry(name, score);
        entries.add(newEntry);

        // Ordiniamo la lista in ordine decrescente
        entries.sort(Comparator.comparingInt(ScoreEntry::getScore).reversed());

        // Se superiamo la dimensione massima, rimuoviamo l'ultimo
        if (entries.size() > maxSize) {
            entries.remove(entries.size() - 1);
        }
    }

    public List<ScoreEntry> getEntries() {
        return entries;
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
