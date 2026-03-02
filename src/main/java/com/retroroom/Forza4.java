package com.retroroom;

public class Forza4 {
    private int[][] tabella;

    // Costruttore che accetta le dimensioni della tabella
    public Forza4(int righe, int colonne) {
        this.tabella = new int[righe][colonne];
    }

    public boolean inserisciPedina(int colonna, boolean giocatore) {
        int pedina = giocatore ? 1 : 2; // 1 per il primo giocatore, 2 per il secondo
        for (int i = tabella.length - 1; i >= 0; i--) {
            if (tabella[i][colonna] == 0) { // Se la cella è vuota
                tabella[i][colonna] = pedina; // Inserisce la pedina
                return true; // Pedina inserita con successo
            }
        }
        return false; // Colonna piena, non è possibile inserire la pedina
    }

    public int[][] getTabella() {
        return tabella;
    }

    public boolean checkWin(int i, int j) {
        int pedina = tabella[i][j];
        if (pedina == 0) {
            return false; // Nessuna pedina in questa posizione
        }

        // Controlla in tutte le direzioni (orizzontale, verticale, diagonale)
        return checkDirection(i, j, 0, 1, pedina) || // Orizzontale
               checkDirection(i, j, 1, 0, pedina) || // Verticale
               checkDirection(i, j, 1, 1, pedina) || // Diagonale \
               checkDirection(i, j, 1, -1, pedina);   // Diagonale /
    }

    private boolean checkDirection(int i, int j, int x, int y, int pedina) {
        while(i >= 0 && i < tabella.length && j >= 0 && j < tabella[0].length && tabella[i][j] == pedina) {
            i += x;
            j += y;
        }

        int count = 0;
        while(i >= 0 && i < tabella.length && j >= 0 && j < tabella[0].length && tabella[i][j] == pedina) {
            count++;
            i -= x;
            j -= y;
        }

        return count == 3; // Controlla se ci sono 4 pedine in fila (inclusa quella appena inserita)
    }
}
