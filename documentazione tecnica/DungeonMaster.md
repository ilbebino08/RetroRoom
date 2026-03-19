# Documentazione tecnica approfondita: DungeonMaster.java

Percorso sorgente: `src/main/java/com/retroroom/DungeonMaster.java`

Obiettivo del documento
----------------------
Fornire una descrizione esaustiva della classe `DungeonMaster`, che implementa il motore di gioco per la modalità Dungeon Master: generazione mappa, stato partita, interazione giocatore/mostri/oggetti, misurazione del tempo e interfacce per la UI.

Sommario
--------
- Tipo: classe procedurale con inner classes per rappresentare `Giocatore` e `GameState`.
- Principali responsabilità:
  - generazione casuale del dungeon;
  - gestione dello stato di gioco e delle entità;
  - risposte agli input di movimento;
  - esposizione di metodi di rendering (testuale e emoji grid) e di HUD.

Struttura dati e invarianti
--------------------------
1. Costanti
   - `DIMENSIONE = 10` — dimensione quadrata del dungeon. Invariante: `DIMENSIONE > 0`.
   - `NUMERO_MOSTRI = 6` — numero di mostri da posizionare inizialmente.
   - `DANNO_SPADA = 20`, `VITA_MOSTRO = 20` — parametri di combattimento.

2. Inner classes
   - `static class Giocatore` — campi: `int x, y; int salute; int oro; boolean haSpada; boolean haChiave; int mostriSconfitti; boolean haVinto;`.
   - `public static class GameState` — campi:
     - `char[][] dungeon` (DIMENSIONE x DIMENSIONE)
     - `int[][] vitaMostri` (DIMENSIONE x DIMENSIONE)
     - `boolean[] chiavePosizionata`, `boolean[] uscitaPosizionata`, `boolean[] finito` (usati come wrapper mutabili)
     - `int[] mostriRimasti`
     - `Giocatore giocatore`
     - `long startMillis`, `long endMillis`

   - Motivazione per array wrapper: semplicità di passaggio per reference mutabile senza introdurre classi wrapper ulteriori (scelta pragmatica, meno verbosa, ma meno leggibile).

Generazione del dungeon (`generaDungeon`)
----------------------------------------
- Inizializza tutte le celle come '.' e `vitaMostri` a zero.
- Posiziona `NUMERO_MOSTRI` con coordinate casuali: `dungeon[x][y] = 'M'` e `vitaMostri[x][y] = VITA_MOSTRO`.
- Posiziona una spada 'S' in una cella random.
- Non c'è controllo per collisioni: la probabilità di sovrapposizione esiste (mostro e spada nella stessa posizione). Se desiderato, implementare un loop che cerca una cella vuota.

Creazione di una nuova partita (`newGame`)
-----------------------------------------
- Crea `GameState state = new GameState()` e chiama `generaDungeon`. Imposta `state.startMillis = System.currentTimeMillis()`.
- Ritorna lo stato.

Movimento del giocatore e interazioni (`move`)
----------------------------------------------
Signature: `public static String move(GameState state, char movimento)`
- Controlli iniziali: se `isFinished(state)` ritorna "Partita conclusa".
- Calcolo delle coordinate nuove (`nuovaX`, `nuovaY`) in base a `'w','a','s','d'`.
- Controllo bounds: se fuori mappa ritorna "Muro!".
- Aggiorna la posizione del giocatore nello stato.
- Valuta il contenuto della cella `cella = state.dungeon[x][y]` e comportamenti:
  - 'M' (mostro):
    - se `giocatore.haSpada`: decrementa `vitaMostri[x][y]` di `DANNO_SPADA`, decrementa la `salute` del giocatore di un valore casuale `[10..18]`;
      - se vitaMostri <= 0: rimuove 'M' -> '.', aumenta oro di 20, incrementa `mostriSconfitti` e decrementa `mostriRimasti`.
    - se non ha spada: `giocatore.salute = 0`, `finito[0] = true`, `endMillis = now` -> ritorna "GAME OVER: mostro senza spada".
  - 'S' (spada): assegna `haSpada=true` e rimuove 'S'.
  - 'K' (chiave): assegna `haChiave=true` e rimuove 'K'.
  - 'E' (uscita): se `haChiave` true, imposta `finito` e `haVinto` e `endMillis = now` e ritorna "HAI VINTO".
- Dopo ogni movimento, verifica `salute <= 0` per impostare game over.
- Alla fine chiama `posizionaChiaveEUscitaSilenziosa(...)` per piazzare key/exit quando condizioni soddisfatte.

Dettagli di combattimento e bilanciamento
----------------------------------------
- Il danno inflitto dalla spada è costante (`DANNO_SPADA`) mentre il danno subito dal giocatore è casuale tra 10 e 18.
- Quando il giocatore colpisce un mostro, lo stato del mostro è persistito in `vitaMostri` — utile per combattimenti multipli su cella.
- Possibile miglioramento: normalizzare RNG tramite seed e parametri configurabili per test.

Gestione tempo e HUD
--------------------
- `startMillis` e `endMillis` sono usati per calcolare `getElapsedMillis`: `end > 0 ? end - start : now - start`.
- `getHudLine(GameState)` formatta la stringa HUD: `HP:... Oro:... Mostri:... Tempo: s.mmm` — importante: restituisce secondi e millisecondi come richiesto.

Rendering: `getEmojiGrid` e `render`
-----------------------------------
- `getEmojiGrid` mappa ogni tile a emoji:
  - Giocatore: "😀" (ma vedi note su supporto font)
  - 'M' -> "👹"; 'S' -> "🔪"; 'K' -> "🔑"; 'E' -> "🚪"; '.' -> "".
- `render(GameState)` costruisce una rappresentazione testuale con `🟦` per giocatore nella versione testuale.

Problemi con emoji e font
-------------------------
- Se alcune emoji appaiono come rettangoli vuoti significa che la font di default JavaFX non contiene quei glyph. Alternative:
  - usare immagini (sprite) per ciascuna tile e `ImageView` nelle celle;
  - usare caratteri Unicode più comuni o ASCII fallback;
  - includere e forzare il caricamento di una font che supporti emoji (può non essere portabile).

Posizionamento della chiave e dell'uscita
----------------------------------------
- `posizionaChiaveEUscitaSilenziosa` posiziona la chiave casualmente quando `mostriSconfitti >= NUMERO_MOSTRI` e poi posiziona l'uscita quando il giocatore ha la chiave.
- Nota: posizionamento casuale potrebbe mettere la chiave/uscita sopra il giocatore o su una cella già occupata; migliorare con ricerca di cella libera.

Robustezza, error handling e logging
------------------------------------
- Il codice si basa su ritorni stringa per reportare eventi (`evento`), che `App` usa per mostrare `statusLine`.
- Le eccezioni non sono propagate: la classe preferisce gestire internamente e lasciare ad `App` la gestione dell'UI in caso di stato anomalo.
- Per debug/aggressività maggiore, aggiungere logger (es. `java.util.logging.Logger`) per loggare eventi critici e condizioni anomale (es. sovrapposizioni di posizionamento).

Test consigliati
----------------
1. Unit test per `generaDungeon`:
   - verificare che la griglia sia piena di '.' o con i simboli previsti e che `vitaMostri` sia settata correttamente per le celle dei mostri.
   - testare che `mostriRimasti[0] == NUMERO_MOSTRI` dopo generazione.
2. Unit test per `move`:
   - testing movement bounds (muro), pickup spada/chiave, combattimento con e senza spada, vittoria per uscita.
   - usare `Random` con seed (necessaria modifica) per rendere il test deterministico.
3. Edge-case: test per posizionamento repetuto (assicurarsi che `generaDungeon` non sbagli con array index out-of-bound).

Refactor consigliati
--------------------
- Sostituire gli array di wrapper booleani (`boolean[] chiavePosizionata`) con campi boolean in `GameState` per chiarezza.
- Introdurre una classe `Tile` o `Entity` per rappresentare meglio cosa c'è in ogni cella (mostro con hp, spada, ecc.).
- Separare la logica di generazione in una `DungeonGenerator` per facilitare test e parametrizzazione.
- Implementare Random injection per test deterministici (`newGame(Random rand)`).

API pubbliche (riassunto)
-------------------------
- `GameState newGame()`
- `boolean isFinished(GameState)`
- `boolean isWon(GameState)`
- `long getElapsedMillis(GameState)`
- `String[][] getEmojiGrid(GameState)`
- `String getHudLine(GameState)`
- `String move(GameState, char movimento)`

Note finali
-----------
`DungeonMaster` è una buona base per un gioco a griglia. Per un progetto più ampio considerare di usare classi più descrittive per le entità, dependency injection per Random e separare generazione, logica e rendering. Posso applicare questi refactor e aggiungere unit test se vuoi.

Fine del documento.
