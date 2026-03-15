# Analisi tecnica dettagliata: DungeonMaster.java

## Package e import
```java
package com.retroroom;
import java.util.Random;
```
**Cosa fa:**
- Definisce il namespace e importa la classe Random per la generazione di numeri casuali.
- La classe è completamente statica, non serve istanziare DungeonMaster.

---

## Costanti di gioco
```java
public static final int DIMENSIONE = 10;
public static final int NUMERO_MOSTRI = 6;
public static final int DANNO_SPADA = 20;
public static final int VITA_MOSTRO = 20;
```
**Cosa fa:**
- Definisce le dimensioni della mappa, il numero di mostri, il danno inflitto dalla spada e la vita iniziale dei mostri.

---

## Random
```java
private static Random rand = new Random();
```
**Cosa fa:**
- Oggetto per generare numeri casuali, usato per posizionare elementi nella mappa.

---

## Classe interna Giocatore
```java
static class Giocatore {
    int x, y;
    int salute;
    int oro;
    boolean haSpada;
    boolean haChiave;
    int mostriSconfitti;
    boolean haVinto;
    Giocatore() { ... }
}
```
**Cosa fa:**
- Rappresenta lo stato del giocatore: posizione, salute, oro, oggetti raccolti, mostri sconfitti, stato di vittoria.
- Il costruttore inizializza tutti i valori di partenza.

---

## Classe GameState
```java
public static class GameState {
    private final char[][] dungeon = new char[DIMENSIONE][DIMENSIONE];
    private final int[][] vitaMostri = new int[DIMENSIONE][DIMENSIONE];
    private final boolean[] chiavePosizionata = {false};
    private final boolean[] uscitaPosizionata = {false};
    private final boolean[] finito = {false};
    private final int[] mostriRimasti = {NUMERO_MOSTRI};
    private final Giocatore giocatore = new Giocatore();
    private long startMillis;
    private long endMillis;
}
```
**Cosa fa:**
- Rappresenta lo stato globale della partita: mappa, vita dei mostri, flag per chiave/uscita, stato di fine partita, mostri rimasti, giocatore, timer.
- Tutti i dati sono incapsulati e gestiti tramite questa classe.

---

## Avvio partita
```java
public static GameState newGame() { ... }
```
**Cosa fa:**
- Crea un nuovo stato di gioco, genera il dungeon, imposta il timer di inizio.

---

## Query di stato
```java
public static boolean isFinished(GameState state) { ... }
public static boolean isWon(GameState state) { ... }
public static int getGold(GameState state) { ... }
public static int getHealth(GameState state) { ... }
public static long getElapsedSeconds(GameState state) { ... }
```
**Cosa fa:**
- Metodi di utilità per interrogare lo stato della partita (fine, vittoria, oro, salute, tempo trascorso).

---

## Movimento e logica di gioco
```java
public static String move(GameState state, char movimento) { ... }
```
**Cosa fa:**
- Gestisce il movimento del giocatore sulla mappa.
- Controlla collisioni con muri, mostri, spada, chiave, uscita.
- Gestisce il combattimento: se il giocatore ha la spada può colpire i mostri, altrimenti perde subito.
- Aggiorna salute, oro, stato di vittoria/sconfitta, posiziona chiave e uscita quando necessario.
- Restituisce una stringa che descrive l'evento accaduto.
- Aggiorna i timer di fine partita.

---

## Rendering e HUD
```java
public static String render(GameState state) { ... }
public static String[][] getEmojiGrid(GameState state) { ... }
public static String getHudLine(GameState state) { ... }
```
**Cosa fa:**
- Genera una rappresentazione testuale o grafica (emoji) della mappa e dell'HUD (salute, oro, mostri sconfitti, tempo).
- Il giocatore è rappresentato con emoji diverse rispetto agli altri elementi.

---

## Conversione simboli in emoji
```java
private static String tileToEmoji(char tile) { ... }
```
**Cosa fa:**
- Converte i simboli della mappa in emoji per la visualizzazione grafica.

---

## Posizionamento chiave e uscita
```java
private static void posizionaChiaveEUscitaSilenziosa(...)
```
**Cosa fa:**
- Posiziona la chiave quando tutti i mostri sono sconfitti.
- Posiziona l'uscita quando il giocatore raccoglie la chiave.

---

## Generazione dungeon
```java
public static void generaDungeon(char[][] dungeon, boolean[] chiavePosizionata, int[] mostriRimasti, boolean[] uscitaPosizionata, int[][] vitaMostri) { ... }
```
**Cosa fa:**
- Inizializza la mappa, posiziona mostri, spada, resetta chiave e uscita.
- Usa numeri casuali per la posizione degli elementi.

---

## Conclusione
Questa classe incapsula tutta la logica di un dungeon crawler a turni, gestendo stato, movimento, combattimento, raccolta oggetti e condizioni di vittoria/sconfitta.