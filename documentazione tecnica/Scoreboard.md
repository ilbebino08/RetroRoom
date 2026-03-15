# Analisi tecnica dettagliata: Scoreboard.java

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