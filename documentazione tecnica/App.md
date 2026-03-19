# Documentazione tecnica approfondita: App.java

Percorso sorgente: `src/main/java/com/retroroom/App.java`

Scopo del documento
-------------------
Questa documentazione descrive in modo esaustivo la classe `App` che funge da orchestratore e interfaccia grafica per il progetto RetroRoom. L'obiettivo è documentare ogni campo, metodo e comportamento osservabile, le ragioni delle scelte implementative, i casi limite e testing consigliato.

Panoramica funzionale
---------------------
`App` è l'entry-point JavaFX dell'applicazione. Si occupa di:
- costruzione dell'interfaccia grafica (menu, cabinato, pannelli leaderboard);
- gestione input utente (mappatura tasti, gestione key press/release, ripetizione controllata — autorepeat);
- orchestrazione giochi (avvio, stato, rendering): `DungeonMaster`, `SpaceInvaders`, `Forza4`;
- gestione delle classifiche tramite `Scoreboard` e dei file CSV;
- gestione delle splash-screen per vittorie e dell'inserimento nome per leaderboard;
- loghi dinamici in header e aggiornamento grafico del monitor.

Livello di responsabilità
-------------------------
`App` concentra molte responsabilità pratiche. Per la manutenzione e il testing è consigliabile estrarre alcune competenze in moduli: `InputManager`, `GridRenderer`, `LogoManager`, `LeaderboardController`.

Struttura: campi principali e loro ruolo
--------------------------------------
1. Stato della UI e dei giochi
   - `private enum CabinetGame { MENU, DUNGEON, SPACE, FORZA4 }` — stato corrente del cabinato.
   - `private enum GridTheme { ARCADE, CONNECT4 }` — tipo di stile per la griglia.
   - `private CabinetGame activeGame` — tracking dello stato attivo.
   - `menuIndex`, `menuEntries` — navigazione del menu.
   - Riferimenti ai giochi:
     - `DungeonMaster.GameState dungeonGame`
     - `SpaceInvaders spaceGame`
     - `Forza4 forza4Game`
   - Stato specifico Forza4: `forza4SelectedColumn`, `forza4TurnPlayerOne`, `forza4Finished`.

2. Scoreboards e pannelli laterali
   - `Scoreboard dungeonScoreboard`, `spaceScoreboard` — istanze che gestiscono i file `scores/dungeon.csv` e `scores/space.csv`.
   - `VBox dungeonTableBox`, `spaceTableBox` — contenitori UI per le classifiche.

3. UI nodes
   - `GridPane monitorGrid`, `VBox monitorGridBox`, `Label screenText`, `Label monitorGridInfo` — area di visualizzazione principale del cabinato (testo o griglia).
   - `ImageView gameLogoView` — logo dinamico.
   - `StackPane splashOverlay`, `TextField splashNameField`, `Label splashTitleLabel`, `splashMessageLabel`, `splashHintLabel` — overlay per splash/leaderboard.
   - Controlli fisici: `Circle joystickStick`, `Button btnA`, `Button btnB`.

4. Input management (critico)
   - `List<KeyCode> xInputStack`, `yInputStack` — mantengono ordine di press delle direzioni (last-input priority).
   - `List<KeyCode> heldInputOrder` — ordine globale dei tasti tenuti premuti (per autorepeat cycling).
   - `Map<KeyCode, Long> heldInputNextFireNanos` — timestamp (nanosecondi) per la prossima azione di ripetizione per ogni tasto.
   - Costanti temporali:
     - `HOLD_REPEAT_INITIAL_DELAY_NS = 220_000_000L` (~220ms iniziale)
     - `HOLD_REPEAT_INTERVAL_NS = 85_000_000L` (~85ms intervallo)
     - `UI_REFRESH_INTERVAL_NS = 100_000_000L` (~100ms refresh UI)
   - `AnimationTimer heldInputRepeater` — timer che gestisce autorepeat e refresh UI.

5. Stili e colori
   - `BUTTON_STYLE_NORMAL` e `BUTTON_STYLE_PRESSED` — CSS inline per i pulsanti A/B.
   - Border e colore cabinato: `-fx-border-color: #581ba7` (colore viola richiesto dall'utente) e altre regole CSS inline.

6. File e risorse
   - `pathScores = new File("scores")` — cartella di salvataggio.
   - `dungeonScores`, `spaceScores` — file CSV.
   - `loadLogo(...)` cerca immagini in `/com/retroroom/` o `/com/retroroom/logos/`.

Analisi dettagliata dei metodi principali
---------------------------------------
Per ogni metodo chiave, descriverò l'intento, il comportamento, i parametri e i casi limite.

1) `start(Stage stage)`
- Intento: inizializzare lo stato dell'app e costruire la scena JavaFX.
- Operazioni principali (ordine di esecuzione):
  1. Istanzia `dungeonScoreboard` e `spaceScoreboard` con `SortOrder.ASCENDING` (per misurare il tempo: valore più basso migliore).
  2. Costruisce il `BorderPane` principale e setta lo stile di sfondo.
  3. Crea i pannelli laterali (chiamando `createSidePanel`) e il cabinato centrale (`createCabinato`).
  4. Crea la `Scene`, registra gli handler degli input (`setupInputHandlers`) e l'`AnimationTimer` per autorepeat (`setupHeldInputRepeater`).
  5. Mostra lo stage, chiede focus al root e renderizza il menu (`renderMenu`).
  6. Avvia un listener console (`startConsoleListener`) in thread daemon per comandi `remove` via stdin.

- Casi limite e attenzione:
  - Creazione `Scoreboard` può fallire per permessi I/O; la classe `Scoreboard` gestisce la creazione del file ma `App` non fallisce in caso di eccezioni — suggerisco loggare e mostrare avviso.
  - Focus: `root.requestFocus()` importante perché la scena riceva eventi tastiera.

2) `setupInputHandlers(Scene scene)`
- Intento: registrare `setOnKeyPressed` e `setOnKeyReleased` con logica per non dipendere dall'autorepeat OS e per gestire stack direzionali.
- Dettaglio press:
  - Se il tasto è già in `heldInputNextFireNanos` viene ignorato (questo previene le doppie chiamate dovute all'autorepeat del sistema).
  - Se il tasto è verticale/horizontale (usando `isVerticalKey`/`isHorizontalKey`) lo registra nei rispettivi stack (`registerAxisPress`) e chiama `handleGameplayKey`.
  - Se è tasto A o B setta lo stato visivo (`setButtonPressed`) e inoltra `handleGameplayKey`.
  - Infine `registerHeldInput(code)` valuta se registrare per autorepeat.
- Dettaglio release:
  - Rimuove il tasto dai rispettivi stack (`registerAxisRelease`) e deregistra l'autorepeat (`unregisterHeldInput`).

- Perché questa architettura:
  - Separando asse X/Y si implementa lo SNAP-TAP/Last Input Priority: quando tieni premuto left e premi right, il sistema considera right come attivo fino a che non lo rilasci, poi torna al left se è ancora premuto.
  - `heldInputNextFireNanos` impedisce doppie esecuzioni su press causate dal key repeat nativo.

3) `setupHeldInputRepeater()` e `AnimationTimer heldInputRepeater`
- Intento: realizzare autorepeat personalizzato (initial delay + interval) e refresh UI periodico.
- Meccanismo:
  - L'`AnimationTimer` esegue `handle(now)` ogni frame (~60fps tipici). Se ora >= `nextFire` per un tasto viene invocato `handleGameplayKey(code)` e `heldInputNextFireNanos` aggiornato a `now + HOLD_REPEAT_INTERVAL_NS`.
  - In Forza4 la ripetizione è disabilitata: condizione `activeGame != CabinetGame.FORZA4`.
  - Il fuoco in Space è one-shot (gestito in `registerHeldInput`).
- Inoltre effettua `tickUiRefresh(now)` che limita gli aggiornamenti di rendering a `UI_REFRESH_INTERVAL_NS` per ridurre flicker e lavori ridondanti.

4) `handleGameplayKey(KeyCode code)`
- Intento: instradare i KeyCode in base al `activeGame`.
- Nel menu: gestisce previous/next/start.
- In gioco: gestisce back-to-menu, poi delega ai metodi specifici `handleDungeonInput`, `handleSpaceInput`, `handleForza4Input`.
- Casi limite: se `splashActive` true, ogni input viene ignorato per evitare azioni mentre la splash è mostrata.

5) Metodi per giochi specifici
- `handleDungeonInput(KeyCode)`
  - Controlla `dungeonGame` non nullo e non finito.
  - Mappa i KeyCode della classe `InputBindings.Dungeon` a caratteri (`'w','a','s','d'`) e invoca `DungeonMaster.move`.
  - Se `DungeonMaster.isFinished` e `isWon`, prende il tempo (ms), chiama `showLeaderboardSplash` con la callback che aggiorna `dungeonScoreboard` e la tabella, e poi `goHomeAfterLeaderboard`.
- `handleSpaceInput(KeyCode)`
  - Mappa i KeyCode in `spaceGame.muovi('a'|'d'|'s')` e se vince mostra leaderboard, aggiorna `spaceScoreboard` e torna alla home.
- `handleForza4Input(KeyCode)`
  - Muove `forza4SelectedColumn` in base a `MOVE_LEFT`/`MOVE_RIGHT`.
  - Su `DROP_PRIMARY` o `DROP_SECONDARY` invoca `forza4Game.inserisciPedina` e aggiorna `forza4TurnPlayerOne` e `forza4Finished`.
  - Se vittoria, mostra `showSimpleSplash` e poi `goToMenu`.

6) Rendering: `renderMenu`, `renderDungeon`, `renderSpace`, `renderForza4`
- `renderMenu()` costruisce testo multilinea e chiama `showTextMode`.
- `renderDungeon()` usa `DungeonMaster.getEmojiGrid` e `showGridMode` con `GridTheme.ARCADE`.
- `renderSpace()` costruisce `String[][]` leggendo posizione giocatore e nemici e la passa a `showGridMode`.
- `renderForza4()` costruisce una griglia con una prima riga per il cursore (freccia verso il basso) e le righe successive con `●` per pedine.

7) Visualizzazione a celle: `renderEmojiGrid` e stili per cella
- `renderEmojiGrid(String[][])` crea `Label` per ogni cella con dimensione fissa 30x30 e imposta lo stile in base al `GridTheme`.
- `getArcadeCellStyle` e `getConnect4CellStyle` definiscono CSS inline per bordi, colori, font-size. Importante: la funzione `isPlayerOneToken` aiuta a colorare una pedina in Forza4.

8) Splash e leaderboard input
- `showSimpleSplash` mostra un overlay per tempo fisso usando `PauseTransition`.
- `showLeaderboardSplash` mostra overlay con `TextField` e lega `setOnAction` per ricevere l'input (ENTER). L'input viene passato come `playerName` alla callback `Consumer<String>`.
- `hideSplashOverlay` rimuove listener e nasconde overlay.

9) Gestione loghi: `createLogoView`, `updateHeaderLogos`, `loadLogo` e `logoNameFor`
- `logoNameFor` mappa `CabinetGame` in un file immagine.
- `loadLogo` prova più path di risorse e crea `Image` da `InputStream`.
- Suggerimenti: abilitare `ImageView.setSmooth(true)` e `setCache(true)` per migliorare qualità e ridurre flicker; evitare logiche dinamiche che cambiano dimensione minima/massima continuamente.

10) Console listener: `startConsoleListener()`
- Avvia un thread daemon che legge `System.in` e interpreta comandi di amministrazione (es. `remove dm Nome`).
- Quando rimuove una entry chiama `Platform.runLater` per aggiornare la UI.
- Nota: questa è una scorciatoia utile per debug, ma in produzione è meglio avere un'interfaccia di amministrazione dedicata.

Dettagli implementativi importanti e motivazioni
-----------------------------------------------
- Autorepeat personalizzato: necessario per un'esperienza arcade coerente; permette di differenziare quali comandi ripetono e con quale latenza.
- Last-input priority e SOCD handling: meccanismo con `xInputStack`/`yInputStack` e `updateJoystickVisual` per riflettere l'ultimo input su ciascun asse; evita conflitti quando si premono input opposti.
- Separazione visuale testo/griglia: facilita mostrare menu in forma testuale e giochi come griglia di emoji.
- Scoreboard in ASCENDING per giochi basati su tempo: scelta corretta perché punteggio più basso è migliore.

Casi limite, problemi noti e soluzioni proposte
------------------------------------------------
1. Flicker e scaling dei loghi: usare `ImageView.setSmooth(true)` e `ImageView.setPreserveRatio(true)`; se ancora flickerare, usare immagini precalcolate nelle risoluzioni richieste.
2. Emoji non supportate dal font: usare immagini (PNG) o scegliere simboli ASCII; per il personaggio di DungeonMaster usare immagine/segnaposto testuale invece di emoji non portabili.
3. Race conditions con `Scoreboard`: `Scoreboard` scrive su file su chiamata di `addOrUpdateBest`; assicurarsi che i metodi siano sincronizzati se più thread possono accedervi (console thread + UI thread).
4. Possibili leak di listener sul `splashNameField`: `hideSplashOverlay` rimuove `setOnAction` per evitare comportamenti ripetuti.

Testing raccomandato
--------------------
- Test unitari:
  - `Scoreboard`: test di parsing, scrittura atomica, concorrenza (multi-threaded add/remove).
  - `Forza4`: test di `inserisciPedina`, `checkWin` su orizzontali/verticali/diagonali e sul bordo.
  - `DungeonMaster`: test `generaDungeon` (nessuna eccezione, posizionamenti validi), `move` deterministic con Random seed.
  - `SpaceInvaders`: test posizione/nemici con seed (già documentato).

- Test di integrazione e UI:
  - Simulare KeyEvents in `App` e verificare che `heldInputRepeater` generi autorepeat come previsto.
  - Testare la submission della leaderboard via `showLeaderboardSplash` e che la callback venga chiamata e aggiorni la UI.

Refactor consigliati (priorità)
------------------------------
1. Estrarre `InputManager` per centralizzare la logica di autorepeat, SOCD e remapping runtime.
2. Estrarre `GridRenderer` per gestire rendering e caching dei Label/ImageView delle celle.
3. Migliorare `Scoreboard.writeScores` con atomic write via `Files.write` su file temporaneo e `Files.move` con `REPLACE_EXISTING`.
4. Rendere `App` meno responsabile: delegare leaderboard e splash a controller specifici.

Appendice: comandi e snippet utili
---------------------------------
- Per eseguire l'app da riga di comando (PowerShell) con JavaFX 13 installato via Maven local repository, un esempio di comando:
```powershell
java --module-path "C:\Users\ilbebo\.m2\repository\org\openjfx\javafx-controls\13\javafx-controls-13-win.jar;C:\Users\ilbebo\.m2\repository\org\openjfx\javafx-graphics\13\javafx-graphics-13-win.jar;C:\Users\ilbebo\.m2\repository\org\openjfx\javafx-base\13\javafx-base-13-win.jar;C:\Users\ilbebo\.m2\repository\org\openjfx\javafx-fxml\13\javafx-fxml-13-win.jar;E:\Documenti\Scuola\2025-2026\Info\Retroroom\target\classes" --add-modules javafx.controls,javafx.fxml -m com.retroroom/com.retroroom.App
```
- Maven build e run:
```powershell
mvn -DskipTests clean package
mvn javafx:run
```

Conclusione
-----------
Ho descritto `App.java` in dettaglio, incluse motivazioni progettuali, meccanismi di input, rendering, gestione delle leaderboard e suggerimenti di testing e refactor. Se desideri, applico le refactor suggerite (ad es. `InputManager` + tests) e poi aggiornare la documentazione generata. 

Fine del documento.
