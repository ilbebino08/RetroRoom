package com.retroroom;

import java.util.Random;

public class SpaceInvaders {

    // dichiarazioni costanti
    private static final int larghezza = 10;
    private static final int altezza = 10;
    private static final int nemiciDaEliminare = 10;

    private int posizioneGiocatoreX;
    private int posizioneGiocatoreY;

    private int[] posizioneNemicoX = new int[3];
    private int[] posizioneNemicoY = new int[3];

    private int nemiciEliminati = 0;
    private long startMillis;
    private long endMillis;

    private Random rand = new Random();

    // Costruttore
    public SpaceInvaders() {

        posizioneGiocatoreX = larghezza / 2;
        posizioneGiocatoreY = altezza - 1;

        for (int i = 0; i < 3; i++) {
            posizioneNemicoX[i] = rand.nextInt(larghezza);
            posizioneNemicoY[i] = rand.nextInt(altezza - 1);
        }

        startMillis = System.currentTimeMillis();
        endMillis = 0;
    }

    // Mostra il campo di gioco
    public void mostra() {

        System.out.println("======= SPACE INVADERS =======");

        for (int y = 0; y < altezza; y++) {

            for (int x = 0; x < larghezza; x++) {

                if (x == posizioneGiocatoreX && y == posizioneGiocatoreY) {
                    System.out.print("[^]"); // giocatore
                } else {

                    boolean nemico = false;

                    for (int i = 0; i < 3; i++) {
                        if (x == posizioneNemicoX[i] && y == posizioneNemicoY[i]) {
                            System.out.print("(X)"); // nemico
                            nemico = true;
                            break;
                        }
                    }

                    if (!nemico) {
                        System.out.print(" . ");
                    }
                }
            }

            System.out.println();
        }

        System.out.println("==============================");
        System.out.println("Nemici eliminati: " + nemiciEliminati + "/" + nemiciDaEliminare);
    }

    // Movimento del giocatore
    public void muovi(char input) {

        if (input == 'a' && posizioneGiocatoreX > 0) {
            posizioneGiocatoreX--;
        }

        if (input == 'd' && posizioneGiocatoreX < larghezza - 1) {
            posizioneGiocatoreX++;
        }

        if (input == 's') {

            for (int i = 0; i < 3; i++) {

                if (posizioneGiocatoreX == posizioneNemicoX[i]) {

                    posizioneNemicoX[i] = rand.nextInt(larghezza);
                    posizioneNemicoY[i] = rand.nextInt(altezza - 1);

                    nemiciEliminati++;
                    if (nemiciEliminati >= nemiciDaEliminare && endMillis == 0) {
                        endMillis = System.currentTimeMillis();
                    }
                }
            }
        }
    }

    // Controllo vittoria
    public boolean vittoria() {
        return nemiciEliminati >= nemiciDaEliminare;
    }

    // Get se vogliamo vedere i nemici eliminati
    public int getNemiciEliminati() {
        return nemiciEliminati;
    }

    public int getLarghezza() {
        return larghezza;
    }

    public int getAltezza() {
        return altezza;
    }

    public int getNemiciDaEliminare() {
        return nemiciDaEliminare;
    }

    public int getPosizioneGiocatoreX() {
        return posizioneGiocatoreX;
    }

    public int getPosizioneGiocatoreY() {
        return posizioneGiocatoreY;
    }

    public int[] getPosizioneNemicoX() {
        return posizioneNemicoX.clone();
    }

    public int[] getPosizioneNemicoY() {
        return posizioneNemicoY.clone();
    }

    public long getElapsedMillis() {
        long end = endMillis > 0 ? endMillis : System.currentTimeMillis();
        return Math.max(0, end - startMillis);
    }

    // Compatibilita con eventuale codice esistente.
    public long getElapsedSeconds() {
        return getElapsedMillis() / 1000;
    }
}