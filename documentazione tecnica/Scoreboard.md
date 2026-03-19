# Documentazione tecnica approfondita: Scoreboard.java

Percorso sorgente: `src/main/java/com/retroroom/Scoreboard.java`

Scopo del documento
-------------------
Fornire una documentazione completa della classe `Scoreboard` che gestisce la persistenza e l'ordinamento delle classifiche (leaderboard) usate dai giochi. Copre formato del file, API, comportamento in presenza di errori, sincronizzazione e test consigliati.

Contesto e responsabilità
-------------------------
- `Scoreboard` è responsabile di mantenere una lista ordinata di oggetti `ScoreEntry` con operazioni di lettura da file (caricamento), inserimento/aggiornamento riga per nome, rimozione e scrittura su file.
- `App` istanzia `Scoreboard` con un `File fileScores` e usa `getEntries()` per aggiornare la UI.

Formato del file
----------------
- Testuale CSV con separatore `;` e fine riga `\n`.
- Righe valide: `Nome;Punteggio` (es. `Alice;12345`).
- Caratteri speciali nei nomi: il codice attuale non fa escaping; se si vogliono supportare `;` nei nomi bisogna cambiare formato (ad es. CSV vero con quote) o usare JSON.

Caricamento (loadScores)
------------------------
- Se `fileScores` è `null` ritorna immediatamente.
- Se il file non esiste, crea la directory padre (se presente) e `fileScores.createNewFile()`.
- Se il file esiste legge linea per linea con `Scanner`, ignora righe vuote e cerca di creare `ScoreEntry` da ogni riga.
- Valori malformati vengono loggati su `System.err` e ignorati.
- Dopo il caricamento viene chiamato `sortAndTrim()`.

Scrittura (writeScores)
-----------------------
- La versione corrente scrive direttamente su `fileScores` aprendo un `FileOutputStream` in modalità sovrascrittura e scrivendo ciascuna `ScoreEntry.getBytes()`.
- Vantaggi: semplicità. Svantaggi: non è atomico; errori di I/O mentre si scrive possono corrompere il file.

Atomicità consigliata
---------------------
- Uso raccomandato: scrivere su un file temporaneo nella stessa directory (`fileScores.tmp`) usando `Files.write(tempPath, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)`, quindi `Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)` se la piattaforma lo supporta.
- In alternativa, conservare una copia di backup prima di sovrascrivere.

Sincronizzazione e concorrenza
------------------------------
- `App` avvia un thread console che può chiamare `removeByName` mentre la UI chiama `addOrUpdateBest` — possono verificarsi race condition.
- Raccomandazione: marcare i metodi che modificano `entries` e scrivono su disco come `synchronized` (es. `public synchronized boolean addOrUpdateBest(...)`) oppure usare `ReentrantReadWriteLock` per maggiore granularità.

API dettagliata
----------------
- Costruttori:
  - `Scoreboard(int maxSize, File fileScores)` — default descending order.
  - `Scoreboard(int maxSize, SortOrder sortOrder, File fileScores)`.
- `List<ScoreEntry> getEntries()` — ritorna copia difensiva delle entries.
- `boolean addOrUpdateBest(String name, int score)` — aggiunge o aggiorna un record mantenendo la lista ordinata e tronca alla dimensione massima; ritorna true se la lista è cambiata.
- `boolean removeByName(String name)` — rimuove entry case-insensitive.

Classe interna `ScoreEntry`
--------------------------
- Campi: `String name; int score;`
- Costruttore da riga: `ScoreEntry(String row)` splitta per `;` e parse int su index 1. Se parse fallisce può sollevare eccezione.
- `toString()` e `getBytes()` restituiscono la rappresentazione `Nome;Punteggio\n`.

Edge cases e come gestirli
-------------------------
1. File inesistente -> creato automaticamente.
2. File non scrivibile -> log e continuare (app non crasha). Meglio notificare l'utente.
3. Nome vuoto -> `addOrUpdateBest` rifiuta e ritorna false.
4. Valori non numerici -> riga ignorata, log su stderr.
5. Concorrenza -> aggiungere sincronizzazione.

Test consigliati
----------------
- Test parsing: file con righe valide, righe vuote, righe malformate.
- Test add/update: aggiungere nuovi nomi, aggiornare esistenti con punteggio migliore/peggiore.
- Test di scrittura atomica: simulare errore di I/O (mock Files) e verificare che il file non venga corrotto.
- Test concorrenti: spawnare più thread che fanno `addOrUpdateBest` e verificare consistenza finale.

Migrazioni e miglioramenti futuri
---------------------------------
- Migrare a JSON per più robustezza e leggibilità (usare Jackson o Gson).
- Usare DB SQLite per storicizzare le interazioni e fornire query avanzate (top N per gioco, filtri temporali).
- Aggiungere API per esportare/importare classifiche.

Snippet di scrittura atomica consigliata
---------------------------------------
```java
Path target = fileScores.toPath();
Path tmp = target.resolveSibling(target.getFileName().toString() + ".tmp");
Files.write(tmp, lines, StandardCharsets.UTF_8);
Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
```

Conclusione
-----------
`Scoreboard` è semplice e funzionale per il contesto scolastico; per produzione o per repository multiuser si consiglia di introdurre atomic write, locking e formato più robusto (JSON/DB). Posso applicare questi miglioramenti al codice e aggiungere test automatizzati.

Fine del documento.
