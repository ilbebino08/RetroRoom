# Analisi tecnica dettagliata: SpaceInvaders.java

## Package e import
```java
package com.retroroom;
import java.util.Random;
```
**Cosa fa:**
- Definisce il namespace e importa la classe Random per la generazione di numeri casuali.

---

## Costanti di gioco
```java
private static final int larghezza = 10;
private static final int altezza = 10;
private static final int nemiciDaEliminare = 10;
```
**Cosa fa:**
- Definisce le dimensioni del campo di gioco e il numero di nemici da eliminare per vincere.

---

## Variabili di stato
```java
private int posizioneGiocatoreX;
private int posizioneGiocatoreY;
private int[] posizioneNemicoX = new int[3];
private int[] posizioneNemicoY = new int[3];
private int nemiciEliminati = 0;
private long startMillis;
private long endMillis;
private Random rand = new Random();
```
**Cosa fa:**
- Gestisce la posizione del giocatore, dei nemici, il conteggio dei nemici eliminati e i timer.
- I nemici sono sempre 3 contemporaneamente.

---

## Costruttore
```java
public SpaceInvaders() { ... }
```
**Cosa fa:**
- Inizializza le posizioni del giocatore e dei nemici, e i timer.

---

## Visualizzazione campo di gioco
```java
public void mostra() { ... }
```
**Cosa fa:**
- Stampa il campo di gioco su console, mostrando giocatore, nemici e celle vuote.

---

## Movimento e attacco
```java
public void muovi(char input) { ... }
```
**Cosa fa:**
- Gestisce il movimento laterale del giocatore e l'attacco (fuoco) per eliminare nemici nella stessa colonna.
- Riposiziona i nemici eliminati e aggiorna il conteggio.
- Aggiorna il timer di fine partita quando tutti i nemici sono stati eliminati.

---

## Controllo vittoria
```java
public boolean vittoria() { ... }
```
**Cosa fa:**
- Restituisce true se sono stati eliminati tutti i nemici richiesti.

---

## Getter di stato
```java
public int getNemiciEliminati() { ... }
public int getLarghezza() { ... }
public int getAltezza() { ... }
public int getNemiciDaEliminare() { ... }
public int getPosizioneGiocatoreX() { ... }
public int getPosizioneGiocatoreY() { ... }
public int[] getPosizioneNemicoX() { ... }
public int[] getPosizioneNemicoY() { ... }
public long getElapsedSeconds() { ... }
```
**Cosa fa:**
- Forniscono accesso allo stato interno del gioco e al tempo trascorso.

---

## Conclusione
Classe che incapsula la logica di un mini Space Invaders: movimento, attacco, gestione nemici e condizioni di vittoria.