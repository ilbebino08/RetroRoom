package com.retroroom;

import java.util.Random;

public class DungeonMaster {

    // Costanti
    public static final int DIMENSIONE = 10;
    public static final int NUMERO_MOSTRI = 6;
    public static final int DANNO_SPADA = 20;
    public static final int VITA_MOSTRO = 20;

    private static Random rand = new Random();

    // Classe Giocatore
    static class Giocatore {
        int x, y;
        int salute;
        int oro;
        boolean haSpada;
        boolean haChiave;
        int mostriSconfitti;
        boolean haVinto;

        Giocatore() {
            x = 0;
            y = 0;
            salute = 100;
            oro = 0;
            haSpada = false;
            haChiave = false;
            mostriSconfitti = 0;
            haVinto = false;
        }
    }

    public static class GameState {
        private final char[][] dungeon = new char[DIMENSIONE][DIMENSIONE];
        private final int[][] vitaMostri = new int[DIMENSIONE][DIMENSIONE];
        private final boolean[] chiavePosizionata = {false};
        private final boolean[] uscitaPosizionata = {false};
        private final boolean[] finito = {false};
        private final int[] mostriRimasti = {NUMERO_MOSTRI};
        private final Giocatore giocatore = new Giocatore();
        private long startMillis;
        private long endMillis;
    }

    public static GameState newGame() {
        GameState state = new GameState();
        generaDungeon(state.dungeon, state.chiavePosizionata, state.mostriRimasti, state.uscitaPosizionata, state.vitaMostri);
        state.startMillis = System.currentTimeMillis();
        return state;
    }

    public static boolean isFinished(GameState state) {
        return state.finito[0] || state.giocatore.salute <= 0;
    }

    public static boolean isWon(GameState state) {
        return state.giocatore.haVinto;
    }

    public static int getGold(GameState state) {
        return state.giocatore.oro;
    }

    public static int getHealth(GameState state) {
        return state.giocatore.salute;
    }

    public static long getElapsedSeconds(GameState state) {
        long end = state.endMillis > 0 ? state.endMillis : System.currentTimeMillis();
        return Math.max(0, (end - state.startMillis) / 1000);
    }

    public static String move(GameState state, char movimento) {
        if (isFinished(state)) {
            return "Partita conclusa";
        }

        int nuovaX = state.giocatore.x;
        int nuovaY = state.giocatore.y;

        switch (movimento) {
            case 'w': nuovaX--; break;
            case 's': nuovaX++; break;
            case 'a': nuovaY--; break;
            case 'd': nuovaY++; break;
            default: return "Comando non valido";
        }

        if (nuovaX < 0 || nuovaX >= DIMENSIONE || nuovaY < 0 || nuovaY >= DIMENSIONE) {
            return "Muro!";
        }

        state.giocatore.x = nuovaX;
        state.giocatore.y = nuovaY;

        char cella = state.dungeon[state.giocatore.x][state.giocatore.y];
        String evento = "";

        switch (cella) {
            case 'M':
                if (state.giocatore.haSpada) {
                    state.vitaMostri[state.giocatore.x][state.giocatore.y] -= DANNO_SPADA;
                    int dannoSubito = 10 + rand.nextInt(9);
                    state.giocatore.salute -= dannoSubito;
                    evento = "Mostro colpito (-" + DANNO_SPADA + "), danno subito " + dannoSubito;

                    if (state.vitaMostri[state.giocatore.x][state.giocatore.y] <= 0) {
                        state.dungeon[state.giocatore.x][state.giocatore.y] = '.';
                        state.giocatore.mostriSconfitti++;
                        state.mostriRimasti[0]--;
                        state.giocatore.oro += 20;
                        evento = "Mostro sconfitto! +20 oro";
                    }
                } else {
                    state.giocatore.salute = 0;
                    state.finito[0] = true;
                    state.endMillis = System.currentTimeMillis();
                    return "GAME OVER: mostro senza spada";
                }
                break;
            case 'S':
                state.giocatore.haSpada = true;
                state.dungeon[state.giocatore.x][state.giocatore.y] = '.';
                evento = "Spada raccolta";
                break;
            case 'K':
                state.giocatore.haChiave = true;
                state.dungeon[state.giocatore.x][state.giocatore.y] = '.';
                evento = "Chiave raccolta";
                break;
            case 'E':
                if (state.giocatore.haChiave) {
                    state.finito[0] = true;
                    state.giocatore.haVinto = true;
                    state.endMillis = System.currentTimeMillis();
                    return "HAI VINTO";
                }
                break;
            default:
                break;
        }

        if (state.giocatore.salute <= 0) {
            state.finito[0] = true;
            state.endMillis = System.currentTimeMillis();
            return "GAME OVER";
        }

        posizionaChiaveEUscitaSilenziosa(state.dungeon, state.giocatore, state.chiavePosizionata, state.uscitaPosizionata);
        return evento;
    }

    public static String render(GameState state) {
        StringBuilder out = new StringBuilder();
        out.append("DUNGEON MASTER\n");
        for (int i = 0; i < DIMENSIONE; i++) {
            for (int j = 0; j < DIMENSIONE; j++) {
                if (i == state.giocatore.x && j == state.giocatore.y) {
                    out.append("🟦 ");
                } else {
                    out.append(tileToEmoji(state.dungeon[i][j])).append(' ');
                }
            }
            out.append('\n');
        }
        out.append(getHudLine(state));
        return out.toString();
    }

    public static String[][] getEmojiGrid(GameState state) {
        String[][] grid = new String[DIMENSIONE][DIMENSIONE];
        for (int i = 0; i < DIMENSIONE; i++) {
            for (int j = 0; j < DIMENSIONE; j++) {
                grid[i][j] = (i == state.giocatore.x && j == state.giocatore.y)
                        ? "😀"
                        : tileToEmoji(state.dungeon[i][j]);
            }
        }
        return grid;
    }

    public static String getHudLine(GameState state) {
        return "HP:" + state.giocatore.salute
                + " Oro:" + state.giocatore.oro
                + " Mostri:" + state.giocatore.mostriSconfitti + "/" + NUMERO_MOSTRI
                + " Tempo:" + getElapsedSeconds(state) + "s";
    }

    private static String tileToEmoji(char tile) {
        switch (tile) {
            case 'M':
                return "👹";
            case 'S':
                return "🔪";
            case 'K':
                return "🔑";
            case 'E':
                return "🚪";
            default:
                return "";
        }
    }

    private static void posizionaChiaveEUscitaSilenziosa(char[][] dungeon, Giocatore g,
                                                         boolean[] chiavePosizionata,
                                                         boolean[] uscitaPosizionata) {
        if (g.mostriSconfitti >= NUMERO_MOSTRI && !chiavePosizionata[0]) {
            int x = rand.nextInt(DIMENSIONE);
            int y = rand.nextInt(DIMENSIONE);
            dungeon[x][y] = 'K';
            chiavePosizionata[0] = true;
        }

        if (g.haChiave && !uscitaPosizionata[0]) {
            int x = rand.nextInt(DIMENSIONE);
            int y = rand.nextInt(DIMENSIONE);
            dungeon[x][y] = 'E';
            uscitaPosizionata[0] = true;
        }
    }

    // Genera il dungeon
    public static void generaDungeon(char[][] dungeon, boolean[] chiavePosizionata,
                                     int[] mostriRimasti, boolean[] uscitaPosizionata,
                                     int[][] vitaMostri) {

        for (int i = 0; i < DIMENSIONE; i++) {
            for (int j = 0; j < DIMENSIONE; j++) {
                dungeon[i][j] = '.';
                vitaMostri[i][j] = 0;
            }
        }

        mostriRimasti[0] = NUMERO_MOSTRI;

        for (int i = 0; i < NUMERO_MOSTRI; i++) {
            int x = rand.nextInt(DIMENSIONE);
            int y = rand.nextInt(DIMENSIONE);
            dungeon[x][y] = 'M';
            vitaMostri[x][y] = VITA_MOSTRO;
        }

        int xSpada = rand.nextInt(DIMENSIONE);
        int ySpada = rand.nextInt(DIMENSIONE);
        dungeon[xSpada][ySpada] = 'S';

        chiavePosizionata[0] = false;
        uscitaPosizionata[0] = false;
    }

}