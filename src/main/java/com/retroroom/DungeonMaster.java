package com.retroroom;

public class DungeonMaster {
    // Implementazione del gioco Dungeon Master, deve essere un gioco che possa durare all'infinito, con un sistema di punteggio che aumenta nel tempo e una classifica per i migliori punteggi
    
    java
import java.util.Random;
import java.util.Scanner;

public class DungeonMaster {

    // Costanti
    public static final int DIMENSIONE = 10;
    public static final int NUMERO_MOSTRI = 6;
    public static final int DANNO_SPADA = 20;
    public static final int VITA_MOSTRO = 20;

    // Scanner per input
    private static Scanner input = new Scanner(System.in);
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

    // Posiziona chiave e uscita
    public static void posizionaChiaveEUscita(char[][] dungeon, Giocatore g,
                                              boolean[] chiavePosizionata,
                                              boolean[] uscitaPosizionata) {

        if (g.mostriSconfitti >= NUMERO_MOSTRI && !chiavePosizionata[0]) {
            int x = rand.nextInt(DIMENSIONE);
            int y = rand.nextInt(DIMENSIONE);
            dungeon[x][y] = 'K';
            chiavePosizionata[0] = true;
            System.out.println("\nHai sconfitto tutti i mostri! La chiave è apparsa!");
        }

        if (g.haChiave && !uscitaPosizionata[0]) {
            int x = rand.nextInt(DIMENSIONE);
            int y = rand.nextInt(DIMENSIONE);
            dungeon[x][y] = 'E';
            uscitaPosizionata[0] = true;
            System.out.println("\nLa porta di uscita è apparsa!");
        }
    }

    // Mostra il dungeon
    public static void mostraDungeon(char[][] dungeon, Giocatore g) {
        System.out.println("\n+-- D U N G E O N  W O R L D --+\n");

        for (int i = 0; i < DIMENSIONE; i++) {
            System.out.print("  ");
            for (int j = 0; j < DIMENSIONE; j++) {
                if (i == g.x && j == g.y) {
                    System.out.print("P ");
                } else {
                    System.out.print(dungeon[i][j] + " ");
                }
            }
            System.out.println();
        }

        System.out.println("\nSalute: " + g.salute + " | Oro: " + g.oro +
                " | Mostri sconfitti: " + g.mostriSconfitti);

        System.out.println("Spada: " + (g.haSpada ? "Si" : "No") +
                " | Chiave: " + (g.haChiave ? "Si" : "No"));
    }

    // Movimento del giocatore
    public static void muoviGiocatore(Giocatore g, char[][] dungeon,
                                      boolean[] chiavePosizionata,
                                      boolean[] uscitaPosizionata,
                                      int[] mostriRimasti,
                                      int[][] vitaMostri,
                                      boolean[] finito) {

        System.out.print("Muoviti (w,a,s,d): ");
        char movimento = input.next().charAt(0);

        int nuovaX = g.x;
        int nuovaY = g.y;

        switch (movimento) {
            case 'w': nuovaX--; break;
            case 's': nuovaX++; break;
            case 'a': nuovaY--; break;
            case 'd': nuovaY++; break;
            default:
                System.out.println("Comando non valido!");
                return;
        }

        if (nuovaX >= 0 && nuovaX < DIMENSIONE && nuovaY >= 0 && nuovaY < DIMENSIONE) {
            g.x = nuovaX;
            g.y = nuovaY;

            char cella = dungeon[g.x][g.y];

            switch (cella) {

                case 'M':
                    if (g.haSpada) {
                        System.out.println("\nHai attaccato il mostro!");
                        vitaMostri[g.x][g.y] -= DANNO_SPADA;

                        int dannoSubito = 10 + rand.nextInt(9);
                        g.salute -= dannoSubito;
                        System.out.println("Il mostro ti colpisce! -" + dannoSubito);

                        if (vitaMostri[g.x][g.y] <= 0) {
                            dungeon[g.x][g.y] = '.';
                            g.mostriSconfitti++;
                            mostriRimasti[0]--;
                            g.oro += 20;
                            System.out.println("Mostro sconfitto! +20 oro");
                        }

                    } else {
                        System.out.println("\nGAME OVER! Hai incontrato un mostro senza spada!");
                        g.salute = 0;
                        finito[0] = true;
                        return;
                    }
                    break;

                case 'S':
                    System.out.println("\nHai raccolto la spada!");
                    g.haSpada = true;
                    dungeon[g.x][g.y] = '.';
                    break;

                case 'K':
                    System.out.println("\nHai raccolto la chiave!");
                    g.haChiave = true;
                    dungeon[g.x][g.y] = '.';
                    break;

                case 'E':
                    if (g.haChiave) {
                        System.out.println("\nHAI VINTO! Sei fuggito dal dungeon!");
                        finito[0] = true;
                        g.haVinto = true;
                    }
                    break;
            }

            posizionaChiaveEUscita(dungeon, g, chiavePosizionata, uscitaPosizionata);
        }
    }
}
