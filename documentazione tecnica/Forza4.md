# Analisi tecnica dettagliata e approfondita: Forza4.java

## Descrizione generale
Classe che implementa la logica del gioco Forza4 su griglia 7x6. Gestisce la disposizione delle pedine, il controllo delle condizioni di vittoria e la gestione dei turni tra due giocatori.

## Attributi principali
- `griglia` (int[][]): matrice 7x6 che rappresenta la plancia di gioco (0=vuoto, 1=giocatore1, 2=giocatore2)
- `giocatoreCorrente` (int): indica il giocatore attivo (1 o 2)
- `vittoria` (boolean): true se la partita è terminata con una vittoria

## Costruttore
- `Forza4()`: inizializza la griglia vuota e imposta il giocatore corrente a 1.

## Metodi principali
### `boolean inserisci(int colonna)`
Inserisce una pedina nella colonna specificata per il giocatore corrente.
- Parametri: `colonna` (int) — indice da 0 a 6
- Restituisce true se l’inserimento ha successo, false se la colonna è piena o fuori range.
- Edge case: colonne piene o indici non validi.

### `boolean controllaVittoria()`
Verifica se il giocatore corrente ha vinto dopo l’ultima mossa.

### `void cambiaGiocatore()`
Passa il turno all’altro giocatore.

### `int getGiocatoreCorrente()`
Restituisce il numero del giocatore attivo.

### `int[][] getGriglia()`
Restituisce una copia della griglia di gioco.

## Edge case e note
- La classe non gestisce input non validi (es. colonne negative).
- Nessuna gestione di thread safety: uso single-thread.

## Esempio d’uso
```java
Forza4 f = new Forza4();
f.inserisci(3);
f.cambiaGiocatore();
if (f.controllaVittoria()) {
    System.out.println("Vittoria!");
}
```


## Package e dichiarazione
```java
package com.retroroom;
```
**Cosa fa:**
- Definisce il namespace del progetto.

---

## Attributo principale
```java
private int[][] tabella;
```
**Cosa fa:**
- Matrice che rappresenta il campo di gioco di Forza 4.

---

## Costruttore
```java
public Forza4(int righe, int colonne) { ... }
```
**Cosa fa:**
- Inizializza la matrice di gioco con il numero di righe e colonne specificato.

---

## Inserimento pedina
```java
public boolean inserisciPedina(int colonna, boolean giocatore) { ... }
```
**Cosa fa:**
- Inserisce una pedina nella colonna scelta dal giocatore (1 o 2).
- Scorre la colonna dal basso verso l'alto e inserisce la pedina nella prima cella libera.
- Restituisce true se l'inserimento ha successo, false se la colonna è piena.
- Il parametro giocatore è booleano: true per il primo giocatore, false per il secondo.

---

## Accesso alla tabella
```java
public int[][] getTabella() { ... }
```
**Cosa fa:**
- Restituisce la matrice di gioco.

---

## Controllo vittoria
```java
public boolean checkWin(int i, int j) { ... }
```
**Cosa fa:**
- Controlla se la pedina appena inserita in (i, j) ha generato una vittoria (4 in fila) in una delle direzioni.
- Chiama `checkDirection` per ogni direzione (orizzontale, verticale, diagonale).
- La logica di checkDirection conta le pedine uguali in entrambe le direzioni.

---

## Controllo direzione
```java
private boolean checkDirection(int i, int j, int x, int y, int pedina) { ... }
```
**Cosa fa:**
- Conta le pedine uguali in una direzione e nella direzione opposta.
- Restituisce true se ci sono 4 pedine consecutive (inclusa quella appena inserita).
- La funzione restituisce true se trova 4 in fila, contando sia avanti che indietro dalla posizione inserita.

---

## Conclusione
Classe compatta che incapsula tutta la logica di Forza 4: inserimento pedine, controllo vittoria e accesso alla matrice di gioco.