# Documentazione tecnica approfondita: Forza4.java

Percorso sorgente: `src/main/java/com/retroroom/Forza4.java`

Scopo del documento
-------------------
Questa doc espande in modo esaustivo la classe `Forza4`, descrivendone struttura, API, comportamento, complessità, casi limite e test consigliati. L'obiettivo è avere una specifica chiara per manutenzione, correzione dei bug noti e aggiunta di funzionalità.

Breve panoramica
----------------
- `Forza4` rappresenta una griglia di gioco con metodi per inserire pedine e verificare vittorie locali.
- Valori nella matrice `tabella`: `0` = vuoto, `1` = pedina gioc.1, `2` = pedina gioc.2.

Campi e invarianti
------------------
- `private int[][] tabella;` — matrice `righe x colonne`.
- Invariante: `tabella != null`, `tabella.length == righe`, `tabella[0].length == colonne`, tutte le celle contengono valori >= 0.

Costruttore
-----------
- `Forza4(int righe, int colonne)` inizializza `tabella = new int[righe][colonne]` e lascia tutte le celle a 0.
- Edge case: valori di `righe` o `colonne` <= 0 non gestiti esplicitamente — si può aggiungere validazione e lancio di `IllegalArgumentException`.

Metodo `inserisciPedina` — comportamento
--------------------------------------
Signature: `public boolean inserisciPedina(int colonna, boolean giocatore)`
- Scopo: inserire la pedina del giocatore (1 o 2) nella colonna specificata, cadendo nella prima cella vuota partendo dal fondo.
- Loop: `for (int i = tabella.length - 1; i >= 0; i--)` — si cerca il primo indice `i` con `tabella[i][colonna] == 0` e lo si assegna a `pedina`.
- Ritorna `true` se inserita, `false` se la colonna è piena.
- Assunzioni: `0 <= colonna < tabella[0].length`. Se `colonna` out-of-bound il codice lancerà `ArrayIndexOutOfBoundsException`; si raccomanda validazione o gestione controllata.

Metodo `checkWin` — comportamento e bug
--------------------------------------
Signature: `public boolean checkWin(int i, int j)`
- Intento: verificare se la cella `(i,j)` contiene una pedina che è parte di una linea di 4 pedine dello stesso giocatore.
- Logica attuale: chiama `checkDirection(i, j, 0,1, pedina)` ecc. per le 4 direzioni.

Bug nella funzione `checkDirection` del sorgente attuale
- L'implementazione attuale è errata: sposta gli indici `i,j` in avanti fino a trovare uno diverso, poi conta andando all'indietro — ma questo non conta la cella di partenza correttamente e può portare a false negative.
- Ritorna `count == 3` che è fragile e poco leggibile.

Correzione consigliata
----------------------
Usare l'algoritmo standard:
1. total = 1 (conta la cella corrente)
2. per step = 1..n: if cell(i+dr*step, j+dc*step) == pedina, total++ else break
3. per step = 1..n: if cell(i-dr*step, j-dc*step) == pedina, total++ else break
4. return total >= 4

Implementazione proposta di `checkDirection` (snippet):
```java
private int countLine(int[][] board, int row, int col, int dr, int dc, int pedina) {
  int total = 1;
  int r = row + dr, c = col + dc;
  while (r >= 0 && r < board.length && c >= 0 && c < board[0].length && board[r][c] == pedina) {
    total++; r += dr; c += dc;
  }
  r = row - dr; c = col - dc;
  while (r >= 0 && r < board.length && c >= 0 && c < board[0].length && board[r][c] == pedina) {
    total++; r -= dr; c -= dc;
  }
  return total;
}

public boolean checkWin(int i, int j) {
  int pedina = tabella[i][j];
  if (pedina == 0) return false;
  return countLine(tabella, i, j, 0, 1, pedina) >= 4 ||
         countLine(tabella, i, j, 1, 0, pedina) >= 4 ||
         countLine(tabella, i, j, 1, 1, pedina) >= 4 ||
         countLine(tabella, i, j, 1, -1, pedina) >= 4;
}
```

Complessità
-----------
- `inserisciPedina`: O(righe)
- `checkWin`: O(righe + colonne) in peggiore dei casi;
- Spazio: O(righe*colonne)

Casi limite e test consigliati
-----------------------------
Unit tests da aggiungere:
1. `testInsertAndGetTable` — inserire in colonne diverse e verificare stato della matrice.
2. `testInsertFullColumn` — riempire una colonna e verificare ritorno `false` per inserimenti ulteriori.
3. `testHorizontalWin`, `testVerticalWin`, `testDiagonalWin1`, `testDiagonalWin2` — scenari di vittoria in tutti e quattro i tipi.
4. `testBorderCases` — vittoria che coinvolge la prima/ultima riga/colonna.
5. `testInvalidColumn` — check che un `colonna` out-of-range venga gestito (aggiungere throw esplicito o return false).

Suggerimenti di refactor e miglioramenti
---------------------------------------
- Validazione dei parametri nel costruttore e nei metodi pubblici: lanciare `IllegalArgumentException` per dimensioni non valide o colonne fuori range.
- Rimuovere mutabilità indesiderata esponendo copie difensive con `getTabella()` che ritorna deep copy invece di riferimento diretto.
- Estrarre la logica di conteggio in metodi riutilizzabili in modo da evitare duplicazione e facilitare test.
- Aggiungere annotazioni e JavaDoc per API pubbliche.

API pubblica (riassunto)
------------------------
- `Forza4(int righe, int colonne)`
- `boolean inserisciPedina(int colonna, boolean giocatore)`
- `int[][] getTabella()`
- `boolean checkWin(int i, int j)`

Conclusione
----------
Questa documentazione fornisce una panoramica approfondita e pratica su `Forza4.java`, individua bug critici (algoritmo di verifica vittoria) e suggerisce correzioni e test da introdurre. Posso applicare la correzione proposta direttamente nel codice sorgente e fornire test JUnit se desideri.

Fine del documento.
