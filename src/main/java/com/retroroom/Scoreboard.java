package com.retroroom;

import java.io.File;
import java.io.FileOutputStream;
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
    private File fileScores;

    public Scoreboard(int maxSize, File fileScores) {
        this(maxSize, SortOrder.DESCENDING, fileScores);
    }

    public Scoreboard(int maxSize, SortOrder sortOrder, File fileScores) {
        this.maxSize = maxSize;
        this.entries = new ArrayList<>();
        this.sortOrder = sortOrder;
        this.fileScores = fileScores;
        loadScores();
    }

    private void loadScores() {
        if (fileScores == null) return;

        try {
            if (!fileScores.exists()) {
                if (fileScores.getParentFile() != null) {
                    fileScores.getParentFile().mkdirs();
                }
                fileScores.createNewFile();
                return;
            }

            java.util.Scanner scanner = new java.util.Scanner(fileScores);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    try {
                        entries.add(new ScoreEntry(line));
                    } catch (Exception e) {
                        System.err.println("Errore nel formato riga classifica: " + line);
                    }
                }
            }
            scanner.close();
            sortAndTrim();
        } catch (Exception e) {
            System.err.println("Errore caricamento classifica: " + e.getMessage());
        }
    }

    private void writeScores() {
        File newScores = new File(fileScores.getParent() + "/temp.csv");
        try (FileOutputStream fos = new FileOutputStream(newScores, false)) {
            for (ScoreEntry entry : entries) {
                fos.write(entry.getBytes());
            }
        } catch (Exception e) {
            System.err.println("Errore scrittura classifica: " + e.getMessage());
        }
        try {
            File oldScores = new File(fileScores.getParent() + "/old.csv");
            boolean success = fileScores.renameTo(oldScores) &&
                            newScores.renameTo(fileScores) &&
                            oldScores.delete();
        } catch (Exception e) {
            System.err.println("Errore scrittura classifica: " + e.getMessage());
        }
    }



    public void addScore(String name, int score) {
        addOrUpdateBest(name, score);
    }

    public boolean addOrUpdateBest(String name, int score) {
        String normalizedName = (name == null) ? "" : name.trim();
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
        writeScores();
        return changed;
    }

    public boolean removeByName(String name) {
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
        while (entries.size() > maxSize) {
            entries.remove(entries.size() - 1);
        }
    }

    public List<ScoreEntry> getEntries() {
        return new ArrayList<>(entries);
    }

    // Classe interna statico per i dati del punteggio
    public static class ScoreEntry {
        private String name;
        private int score;

        public ScoreEntry(String row) {
            String[] rows = row.split(";");
            name = rows[0];
            if (rows[1] != null && !rows[1].isEmpty()) {
                score = Integer.parseInt(rows[1]);
            } else {
                score = 0;
            }
        }

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

        public String toString() {
            return "%s;%d".formatted(name, score);
        }

        public byte[] getBytes() {
            return (toString()+"\n").getBytes();
        }
    }
}
