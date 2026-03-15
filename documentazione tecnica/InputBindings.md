# Analisi tecnica dettagliata: InputBindings.java

## Package e import
```java
package com.retroroom;
import javafx.scene.input.KeyCode;
```
**Cosa fa:**
- Definisce il namespace e importa la classe KeyCode per la gestione dei tasti.

---

## Classe finale e costruttore privato
```java
public final class InputBindings {
    private InputBindings() { }
```
**Cosa fa:**
- Classe non istanziabile, usata solo per contenere costanti e classi statiche.
- Tutte le classi interne sono statiche e raggruppano i controlli per modalità.

---

## Classi statiche interne per i controlli
```java
public static final class Common {
    public static final KeyCode UP = KeyCode.W;
    public static final KeyCode DOWN = KeyCode.S;
    public static final KeyCode LEFT = KeyCode.A;
    public static final KeyCode RIGHT = KeyCode.D;
}
public static final class Menu { ... }
public static final class Dungeon { ... }
public static final class Space { ... }
public static final class Forza4 { ... }
```
**Cosa fa:**
- Ogni classe contiene le associazioni tra azioni di gioco e tasti (KeyCode) per una modalità specifica.
- `Common` contiene i tasti base (WASD), le altre classi li riutilizzano o aggiungono tasti specifici.

---

## Metodi statici di aiuto
```java
public static String menuHint() { ... }
public static String dungeonHint() { ... }
public static String spaceHint() { ... }
public static String forza4Hint() { ... }
private static String keyName(KeyCode code) { ... }
```
**Cosa fa:**
- Restituiscono una stringa con i comandi disponibili per ciascuna modalità, utile per mostrare i controlli all'utente.
- `keyName` restituisce il nome del tasto associato.

---

## Conclusione
Classe di sola configurazione che centralizza la gestione dei controlli, rendendo facile il remapping dei tasti per tutto il progetto.