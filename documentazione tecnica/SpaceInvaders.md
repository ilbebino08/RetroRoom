# Analisi tecnica dettagliata e approfondita: SpaceInvaders.java

## Descrizione generale
Classe che implementa la logica di un mini Space Invaders a griglia. Gestisce il movimento del giocatore, la generazione e gestione dei nemici, il conteggio dei nemici eliminati e la condizione di vittoria. Tutte le operazioni sono eseguite in modalità testuale.

## Costanti di gioco
- `larghezza` (int): larghezza della griglia di gioco (default 10).
- `altezza` (int): altezza della griglia di gioco (default 10).
- `nemiciDaEliminare` (int): numero di nemici da eliminare per vincere (default 10).

## Variabili di stato
- `posizioneGiocatoreX`, `posizioneGiocatoreY` (int): coordinate del giocatore.
- `posizioneNemicoX`, `posizioneNemicoY` (int[]): array di coordinate dei 3 nemici attivi.
- `nemiciEliminati` (int): contatore dei nemici eliminati.
- `startMillis`, `endMillis` (long): gestione del tempo di gioco.
- `rand` (Random): generatore di numeri casuali per spawn nemici.

## Costruttore
### `SpaceInvaders()`
Inizializza le posizioni del giocatore e dei nemici, azzera i timer. Nessun parametro.

## Metodi principali
### `void mostra()`
Stampa su console la griglia di gioco, mostrando:
- Il giocatore (`[^]`)
- I nemici (`(X)`)
- Celle vuote (` . `)
- Stato attuale dei nemici eliminati.

### `void muovi(char input)`
Gestisce il movimento e l’attacco:
- `'a'`: sposta il giocatore a sinistra (se possibile)
- `'d'`: sposta il giocatore a destra (se possibile)
- `'s'`: attacca nella colonna corrente; se c’è un nemico, lo elimina e lo respawna in posizione casuale. Aggiorna il contatore e il timer di fine partita se necessario.
- Parametri: `input` (char) — carattere di comando.
- Edge case: input non valido viene ignorato.

### `boolean vittoria()`
Restituisce true se il numero di nemici eliminati è maggiore o uguale a `nemiciDaEliminare`.

### Getter
- `getNemiciEliminati()`, `getLarghezza()`, `getAltezza()`, `getNemiciDaEliminare()`, `getPosizioneGiocatoreX()`, `getPosizioneGiocatoreY()`, `getPosizioneNemicoX()`, `getPosizioneNemicoY()`, `getElapsedMillis()`, `getElapsedSeconds()`
- Forniscono accesso allo stato interno. Nessun effetto collaterale.
- **Nota**: `getElapsedMillis()` ora restituisce i millisecondi trascorsi; `getElapsedSeconds()` è mantenuto per compatibilità e calcola i secondi dividendo i millisecondi.

## Edge case e note
- I nemici vengono sempre respawnati in posizioni casuali, ma potrebbero sovrapporsi.
- Il metodo `muovi` non gestisce input diversi da quelli previsti.
- Nessuna gestione di thread safety: la classe è pensata per uso single-thread.

## Esempio d’uso
```java
SpaceInvaders game = new SpaceInvaders();
game.mostra();
game.muovi('a');
game.muovi('s');
if (game.vittoria()) {
    System.out.println("Hai vinto!");
}
```


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