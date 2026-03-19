# Documentazione tecnica approfondita: SpaceInvaders.java

Percorso sorgente: `src/main/java/com/retroroom/SpaceInvaders.java`

Scopo del documento
-------------------
Questa documentazione descrive in modo esaustivo la classe `SpaceInvaders` presente nel progetto RetroRoom. Lo scopo è spiegare ogni campo, metodo, comportamento osservabile dall'esterno e le scelte implementative, evidenziare vincoli e casi limite, fornire esempi di esecuzione e suggerire test automatici e miglioramenti strutturali.

Sommario (veloce)
-----------------
- Tipo: classe Java autonoma che modella una versione semplificata di Space Invaders.
- Funzionalità: posizione giocatore, pochi nemici, movimento orizzontale, fuoco che colpisce nemici nella stessa colonna, conteggio nemici eliminati e timing partita.
- Ingressi: chiamate a `muovi(char)` con `'a'`, `'d'`, `'s'` (sinistra, destra, sparo).
- Uscite: stato mutabile interno leggibile tramite getter (posizioni, nemici eliminati, vittoria, elapsed ms).

Contesto di integrazione
------------------------
- `App` crea un'istanza di `SpaceInvaders` quando si avvia il gioco Space Invaders (vedi `startSelectedGame()` in `App.java`).
- `App` si occupa della mappatura dei tasti tramite `InputBindings` e converte `KeyCode` in char (`'a'`, `'d'`, `'s'`) quando richiama `spaceGame.muovi(...)` in `handleSpaceInput`.
- `App` esegue rendering della griglia leggendo dimensione e posizioni con i getter forniti dalla classe.

Design e scelte implementative (sommario)
-----------------------------------------
- Semplicità: la classe è stata progettata intenzionalmente semplice per scopi didattici; non ci sono thread interni né loop di gioco autonomi.
- Stato esplicito: tutte le informazioni sono campi privati accessibili tramite getter pubblici (immutabilità parziale per alcuni array che vengono clonati nei getter).
- Randomness: il posizionamento dei nemici usa `java.util.Random` senza seed; pertanto il comportamento è nondeterministico a meno che non si passi un seed (modifica suggerita).
- Il conteggio `nemiciDaEliminare` è una policy di vittoria basata su numero di hit cumulative, non sul numero iniziale di nemici.

Contenuto della classe: campi e significato
-----------------------------------------
(riporto il campo come appare nel sorgente, seguito da commento dettagliato)

- `private static final int larghezza = 10;`
  - Larghezza (numero di colonne) della griglia usata dal gioco. Valore costante.
  - Invariante: `larghezza > 0`.

- `private static final int altezza = 10;`
  - Altezza (numero di righe) della griglia.
  - Invariante: `altezza > 0`.

- `private static final int nemiciDaEliminare = 10;`
  - Numero di 'kill' necessari per dichiarare la vittoria. Non deve essere confuso con il numero di oggetti nemici attivi simultaneamente.
  - Scelta progettuale: il gioco rigenera i nemici dopo che vengono colpiti; si accumulano eliminazioni fino a raggiungere questa soglia.

- `private int posizioneGiocatoreX;`
  - Coordinata X (colonna) del giocatore. Dominio: [0, larghezza-1].
  - Invariante mantenuta: `0 <= posizioneGiocatoreX < larghezza`.

- `private int posizioneGiocatoreY;`
  - Coordinata Y (riga) del giocatore. Nel costruttore è settata generalmente a `altezza - 1` (ultima riga).
  - Invariante: `0 <= posizioneGiocatoreY < altezza`.

- `private int[] posizioneNemicoX = new int[3];`
  - Array parallelo delle X per i 3 nemici iniziali.
  - Design: la dimensione è fissa a 3 nel sorgente. Per estensione si raccomanda usare una lista dinamica.

- `private int[] posizioneNemicoY = new int[3];`
  - Array delle Y corrispondenti.

- `private int nemiciEliminati = 0;`
  - Contatore incrementato ogni volta che un fuoco colpisce un nemico (colonna uguale al giocatore).
  - Viene usato per determinare `vittoria()`.

- `private long startMillis;`
  - Timestamp (millis) di inizio partita, impostato nel costruttore con `System.currentTimeMillis()`.

- `private long endMillis;`
  - Timestamp di fine partita (quando `nemiciEliminati >= nemiciDaEliminare`), inizialmente 0.
  - Invariante: `endMillis >= startMillis` quando differente da 0.

- `private Random rand = new Random();`
  - Generatore casuale per posizionamento/rigenerazione dei nemici.
  - Non seedato: comportamento nondeterministico. Per testing usare sovraccarico con seed.

Costruttore: comportamento e invarianti
-------------------------------------
Cosa fa il costruttore:
1. Imposta `posizioneGiocatoreX = larghezza / 2;` — posiziona il giocatore al centro (floor) della griglia.
2. Imposta `posizioneGiocatoreY = altezza - 1;` — ultima riga (in basso).
3. Per `i` da 0 a 2: imposta `posizioneNemicoX[i] = rand.nextInt(larghezza);` e `posizioneNemicoY[i] = rand.nextInt(altezza - 1);` — i nemici non vengono posizionati sull'ultima riga per evitare spawn nello stesso y del giocatore.
4. Imposta `startMillis = System.currentTimeMillis();` e `endMillis = 0;`

Proprietà garantite dal costruttore:
- Ogni nemico ha coordinate valide: `0 <= posizioneNemicoX[i] < larghezza` e `0 <= posizioneNemicoY[i] <= altezza-2`.
- Giocatore e nemici possono condividere la stessa colonna o riga (è intenzionale).

Metodo `mostra()` — scopo e comportamento
---------------------------------------
- Funzione di utilità per stampa testuale su stdout (utile per debug o esecuzioni console-based).
- Stampa una griglia ASCII di `altezza` righe e `larghezza` colonne; per ogni cella:
  - se è posizione del giocatore stampare `[^]`;
  - se è la posizione di un nemico stampare `(X)`;
  - altrimenti ` . `;
- Stampa il conteggio dei nemici eliminati.

Nota: `mostra()` è indipendente dalla UI JavaFX; `App` non usa questo metodo per il rendering grafico, usa invece i getter per costruire la griglia emoji.

Metodo chiave: `muovi(char input)` — analisi dettagliata
-----------------------------------------------------
Signature: `public void muovi(char input)`

Comportamento generale:
- Accetta tre comandi:
  - `'a'` — muovi a sinistra se `posizioneGiocatoreX > 0`.
  - `'d'` — muovi a destra se `posizioneGiocatoreX < larghezza - 1`.
  - `'s'` — fuoco: controlla ogni nemico e se `posizioneGiocatoreX == posizioneNemicoX[i]` allora il nemico è colpito.
- Se il nemico è colpito:
  - viene riposizionato: `posizioneNemicoX[i] = rand.nextInt(larghezza); posizioneNemicoY[i] = rand.nextInt(altezza - 1);`
  - `nemiciEliminati++`.
  - se `nemiciEliminati >= nemiciDaEliminare && endMillis == 0` allora `endMillis = System.currentTimeMillis();` — registrazione del tempo di fine partita.

Analisi linea per linea (logica interna rilevante):
1. Controlli bounds per `'a'` e `'d'`: proteggono invariant del giocatore.
2. Per `'s'`, il codice itera su `i = 0..2`, verifica la colonna e riposiziona il nemico in posizione casuale (X in [0,larghezza-1], Y in [0,altezza-2]).
3. La funzione non restituisce valore — stato è mutato internamente; `App` legge i getter per aggiornare render.

Comportamenti e implicazioni interessanti
----------------------------------------
- Sparo colpisce se e solo se la colonna del giocatore coincide con quella del nemico, indipendentemente dalla riga (quindi è come un laser che attraversa l'intera colonna).
- Un singolo sparo può colpire più nemici contemporaneamente se più nemici condividono la stessa colonna: in quel caso il codice incrementerà `nemiciEliminati` per ciascun nemico trovato e rigenererà ciascuno singolarmente.
- Se più nemici occupano la stessa colonna, un singolo `'s'` incrementa `nemiciEliminati` multiplo volte (comportamento voluto o bug? necessita di design decision).

Condizioni di vittoria e temporizzazione
---------------------------------------
- `vittoria()` ritorna `nemiciEliminati >= nemiciDaEliminare`.
- `getElapsedMillis()` ritorna `endMillis>0 ? endMillis - startMillis : currentTime - startMillis`.
- Nota su unità: il gioco usa millisecondi per calcolo del tempo. `App` converte e formatta questi valori in `seconds.milliseconds` con `formatElapsed`.
- Precisione: `System.currentTimeMillis()` dipende dall'orologio sistema; è sufficiente per un timer di gioco semplice ma non per misurazioni ad alta precisione.

Randomness, testabilità e suggerimento di miglioramento
-------------------------------------------------------
Problema: `Random rand = new Random();` senza seed rende il comportamento non riproducibile. Per consentire test unitari deterministici, si suggerisce:
- Aggiungere un costruttore sovraccarico `public SpaceInvaders(long seed)` che imposti `rand = new Random(seed);` e probabilmente un `public SpaceInvaders(Random rand)` per dependency injection.
- In test JUnit, usare seed noto e verificare posizionamenti, conteggi e times.

Complessità temporale e spaziale
--------------------------------
- Stato in memoria: O((numero nemici)) ~ O(1) perché il numero di nemici è fisso a 3.
- `muovi`: operazioni O(n) dove n = numero di nemici (3). Quindi costante.

Interazione con `App` e `InputBindings` (dettagli pratici)
---------------------------------------------------------
- `InputBindings.Space.MOVE_LEFT` e `MOVE_RIGHT` sono mappati a `Common.LEFT`/`RIGHT` (default A/D) e `InputBindings.Space.FIRE_PRIMARY` può essere `Common.UP` o altro; `App` traduce questi `KeyCode` in char (es. `InputBindings.Space.MOVE_LEFT` -> `'a'`) perché `SpaceInvaders.muovi` utilizza char.
- Importantissimo: `App.registerHeldInput` evita la ripetizione per il fuoco (primary/secondary) in Space — questo evita che `'s'` venga inviato continuamente se si tiene premuto il tasto.
- `App.renderSpace()` legge via getter:
  - `getLarghezza()`, `getAltezza()`, `getPosizioneGiocatoreX()`, `getPosizioneGiocatoreY()`, `getPosizioneNemicoX()`, `getPosizioneNemicoY()` e costruisce la griglia `String[][]` di emoji (player 🚀, nemico 👾, vuoto "").

Getter e protezione dello stato
-------------------------------
- `getPosizioneNemicoX()` e `getPosizioneNemicoY()` ritornano `clone()` dell'array per prevenire modifiche esterne involontarie — comportamento corretto.
- `getLarghezza()` / `getAltezza()` sono costanti statiche ritornate come valori; `getNemiciEliminati()` ritorna semplice int.

Casi limite e test consigliati
-----------------------------
Elenco dei test unitari con input e asserzioni raccomandate (JUnit 5 pseudo-codice):

1) Test costruttore - dimensioni e bounds
- Setup: new SpaceInvaders(seed)
- Asserzioni:
  - `assertTrue(si.getPosizioneGiocatoreX() >= 0 && < si.getLarghezza())`
  - `assertEquals(si.getPosizioneGiocatoreY(), si.getAltezza()-1)`
  - per ogni nemico: 0 <= x < larghezza; 0 <= y <= altezza-2

2) Test movimento sinistra/destra
- Setup: set giocatore al centro
- Chiamata: `muovi('a')` e `muovi('d')`
- Asserzioni: `posizioneGiocatoreX` diminuisce o aumenta rispettivamente e non oltrepassa i bounds.

3) Test sparo singolo colpisce nemico
- Setup: usare seed per posizionare un nemico nella stessa colonna del giocatore
- Chiamata: `muovi('s')`
- Asserzioni: `nemiciEliminati` incrementa di almeno 1; la posizione di quel nemico è cambiata (diversa da prima) e rientra nei bound.

4) Test sparo colpisce più nemici nella stessa colonna
- Setup: forzare due nemici nella stessa colonna del giocatore
- Chiamata: `muovi('s')`
- Asserzioni: `nemiciEliminati` incrementa di 2 e entrambi i nemici vengono riposizionati.

5) Test vittoria e tempo
- Setup: creare SpaceInvaders con seed che porta i nemici sempre nella colonna del giocatore o chiamare `muovi('s')` ripetutamente fino a `nemiciDaEliminare`.
- Asserzioni: dopo il k-esimo sparo `vittoria()` ritorna true e `getElapsedMillis()` >= 0 e `endMillis` fissato (non zero).

6) Test non ripetizione del fuoco (integrazione con App)
- Nota: nell'unit test di `SpaceInvaders` questa proprietà non è gestita qui; il test d'integrazione con `App` deve verificare che tenendo premuto il tasto di fuoco non si invii ripetutamente `muovi('s')` (o che ciò sia esplicitamente consentito se si cambia la policy). Suggerimento: testare `App` con mocking dei KeyEvents.

Esempi di test JUnit (sintesi)
```
@Test
void testSparoColpisce() {
  SpaceInvaders si = new SpaceInvaders(12345L); // se aggiungi costruttore con seed
  // posiziona un nemico nella colonna del giocatore
  si.setEnemyPosition(0, si.getPosizioneGiocatoreX(), 0); // se esponi setter per testing
  int before = si.getNemiciEliminati();
  si.muovi('s');
  assertTrue(si.getNemiciEliminati() >= before + 1);
}
```
(implementare helper `setEnemyPosition` solo per i test o usare reflection se non vuoi modificare la classe per scopi di test)

Bug noti, ambiguità e scelte da rivedere
---------------------------------------
1. Multipli hit nello stesso sparo: se ci sono più nemici nella stessa colonna, un solo `'s'` può incrementare `nemiciEliminati` multiple volte; decidere se questo è voluto (laser attraversa) o se si desidera che lo sparo colpisca il nemico più vicino (aggiungere controllo sulla riga più piccola rispetto al giocatore o logica di distanza).
2. `nemiciDaEliminare` fisso a 10: poco intuitivo rispetto al numero di nemici; chiarire nella UI o impostarlo dinamicamente a `initialEnemies * rounds`.
3. Rigenerazione senza controllo: la posizione casuale può immediatamente posizionare il nemico sulla stessa colonna -> possibile loop di colpi ripetuti involontari. Per evitare questo, si può scegliere di rigenerare in una colonna diversa da quella del giocatore corrente.
4. Mancanza di metodo per resettare manualmente o per impostare seed/posizioni (utile per test e debug).

Refactor consigliati (priorità e descrizione)
---------------------------------------------
1. Rifattorizzare `Enemy` come classe:
   - campi: `int x, int y; int health; boolean alive;`
   - usare `List<Enemy>` invece di array paralleli, consente espansione facile.
2. Dependency injection per `Random`:
   - aggiungere costruttore che accetta `Random rand` o `long seed`.
3. Esporre operazioni di spawn/respawn:
   - `spawnEnemy(int index)` e `respawnEnemy(int index, boolean avoidPlayerColumn)` per separare logica e testabilità.
4. Modificare comportamento del fuoco per colpire il nemico più vicino:
   - quando `'s'` viene premuto, cercare il nemico con massima `posizioneNemicoY` (maggiore y = più vicino al giocatore se y cresce verso il basso) nella stessa colonna e colpirlo solo.
5. Aggiungere eventi/callback (listener) per notificare `App` su eventi importanti (enemyHit, playerMoved, victory) invece di interrogare lo stato ad ogni frame.

Esempio di API migliorata (proposta)
```
public class SpaceInvaders {
  public SpaceInvaders(int width, int height, Random rand) { ... }
  public void moveLeft();
  public void moveRight();
  public int fire(); // ritorna numero di nemici colpiti
  public boolean isVictory();
  public void addGameListener(GameListener l);
}
```

Integrazione grafica: mapping a emoji
------------------------------------
- `App.renderSpace()` attualmente costruisce una griglia `String[][]` e usa le seguenti mappature per cella:
  - giocatore: "🚀"
  - nemico: "👾"
  - vuoto: ""
- Nota: se alcune emoji non sono supportate dal font, appaiono rettangoli vuoti. Soluzioni: usare immagini PNG/tileset e `ImageView`, oppure scegliere simboli Unicode ASCII (es. 'A', 'V') per compatibilità.

Riepilogo: checklist di miglioramenti applicabili
------------------------------------------------
- [ ] Aggiungere costruttore `SpaceInvaders(long seed)` per test deterministici.
- [ ] Separare nemico in `Enemy` e usare `List<Enemy>`.
- [ ] Fornire metodi `spawnEnemy`/`respawnEnemy` e un modo per evitare rigenerazione sulla colonna del giocatore.
- [ ] Introdurre comportamento di fuoco che colpisce il nemico più vicino (opzione configurabile).
- [ ] Aggiungere eventi/callback per integrazione pulita con `App`.
- [ ] Documentare e/o modificare `nemiciDaEliminare` per essere derivato dinamicamente oppure esporre come parametro.

Conclusione
-----------
Questa documentazione ha esplorato `SpaceInvaders.java` in tutti i dettagli rilevanti per sviluppo, testing e manutenzione: struttura dati, invarianti, flusso di esecuzione, edge-case, suggerimenti di refactor e test consigliati. Se desideri, applico automaticamente uno (o più) dei miglioramenti elencati — ad esempio aggiungere un costruttore con seed e un metodo `fire()` che colpisce il nemico più vicino — e scrivo test JUnit corrispondenti.

Fine del documento.
