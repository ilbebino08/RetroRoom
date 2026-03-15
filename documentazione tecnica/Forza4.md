# Analisi tecnica dettagliata: Forza4.java

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