# Analisi tecnica dettagliata e approfondita: Scoreboard.java

## Descrizione generale
Classe thread-safe per la gestione di una classifica di punteggi. Permette l’aggiunta, aggiornamento, rimozione e ordinamento di record, mantenendo solo il miglior punteggio per ogni giocatore.

## Enum
### `SortOrder`
- `DESCENDING`: punteggi dal più alto al più basso (default)
- `ASCENDING`: punteggi dal più basso al più alto

## Attributi principali
- `maxSize` (int): dimensione massima della classifica
- `entries` (ArrayList<ScoreEntry>): lista dei record
- `sortOrder` (SortOrder): ordinamento attuale

## Costruttori
- `Scoreboard(int maxSize)`: crea una classifica con ordinamento decrescente
- `Scoreboard(int maxSize, SortOrder sortOrder)`: specifica ordinamento

## Metodi principali
### `synchronized void addScore(String name, int score)`
Aggiunge o aggiorna il punteggio di un giocatore. Se il nome esiste, aggiorna solo se il nuovo punteggio è migliore.

### `synchronized boolean addOrUpdateBest(String name, int score)`
Come sopra, ma restituisce true se la classifica è cambiata.
- Parametri: `name` (String), `score` (int)
- Edge case: nomi null o vuoti ignorati.

### `synchronized boolean removeByName(String name)`
Rimuove il record associato al nome (case-insensitive).

### `synchronized List<ScoreEntry> getEntries()`
Restituisce una copia della lista dei record.

## Metodi privati
- `isBetter(int newScore, int oldScore)`: verifica se il nuovo punteggio è migliore secondo l’ordinamento
- `sortAndTrim()`: ordina e rimuove l’ultimo se la lista supera `maxSize`

## Inner class
### `ScoreEntry`
- Attributi: `name` (String), `score` (int)
- Metodi: costruttore, `getName()`, `getScore()`

## Thread safety
Tutti i metodi pubblici che modificano la classifica sono `synchronized`.

## Esempio d’uso
```java
Scoreboard sb = new Scoreboard(10);
sb.addScore("Mario", 100);
sb.addScore("Luigi", 80);
for (Scoreboard.ScoreEntry e : sb.getEntries()) {
    System.out.println(e.getName() + ": " + e.getScore());
}
```


## Package e import
```java
package com.retroroom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
```
**Cosa fa:**
- Definisce il namespace e importa le classi per la gestione di liste e ordinamenti.

---

## Enum SortOrder
```java
public enum SortOrder {
    DESCENDING,
    ASCENDING
}
```
**Cosa fa:**
- Definisce l'ordinamento della classifica: crescente o decrescente.

---

## Attributi principali
```java
private int maxSize;
private ArrayList<ScoreEntry> entries;
private SortOrder sortOrder;
```
**Cosa fa:**
- `maxSize`: dimensione massima della classifica.
- `entries`: lista delle voci della classifica.
- `sortOrder`: ordinamento attuale.
- La lista è thread-safe grazie ai metodi synchronized.

---

## Costruttori
```java
public Scoreboard(int maxSize) { ... }
public Scoreboard(int maxSize, SortOrder sortOrder) { ... }
```
**Cosa fa:**
- Inizializzano la classifica con dimensione massima e ordinamento.

---

## Aggiunta e aggiornamento punteggi
```java
public synchronized void addScore(String name, int score) { ... }
public synchronized boolean addOrUpdateBest(String name, int score) { ... }
```
**Cosa fa:**
- Aggiungono o aggiornano il punteggio di un giocatore.
- Solo il miglior punteggio viene mantenuto per ogni nome.
- Sincronizzati per thread safety.

---

## Rimozione punteggi
```java
public synchronized boolean removeByName(String name) { ... }
```
**Cosa fa:**
- Rimuove un giocatore dalla classifica.

---

## Ordinamento e trimming
```java
private boolean isBetter(int newScore, int oldScore) { ... }
private void sortAndTrim() { ... }
```
**Cosa fa:**
- Determinano se un nuovo punteggio è migliore e ordinano la classifica, rimuovendo l'ultimo se necessario.

---

## Accesso ai punteggi
```java
public synchronized List<ScoreEntry> getEntries() { ... }
```
**Cosa fa:**
- Restituisce una copia della lista dei punteggi.

---

## Classe interna ScoreEntry
```java
public static class ScoreEntry {
    private String name;
    private int score;
    public ScoreEntry(String name, int score) { ... }
    public String getName() { ... }
    public int getScore() { ... }
}
```
**Cosa fa:**
- Rappresenta una voce della classifica: nome e punteggio.

---

## Conclusione
Classe thread-safe che gestisce una classifica ordinata, mantenendo solo il miglior punteggio per ogni giocatore.