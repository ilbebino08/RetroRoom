# Analisi tecnica dettagliata e approfondita: InputBindings.java

## Descrizione generale
Classe di utility per la gestione dei binding tra tasti e azioni. Permette di associare, rimuovere e recuperare azioni legate a specifici tasti, supportando la configurazione dinamica dei controlli.

## Attributi principali
- `bindings` (HashMap<String, String>): mappa che associa tasti (come stringhe) ad azioni (stringhe).

## Costruttore
- `InputBindings()`: inizializza la mappa vuota.

## Metodi principali
### Binding predefiniti per Menu
- `PREVIOUS_GAME`: ora associato a `Common.UP` (prima era `Common.LEFT`)
- `NEXT_GAME`: ora associato a `Common.DOWN` (prima era `Common.RIGHT`)

### `void bind(String key, String action)`
Associa un’azione a un tasto. Se il tasto è già associato, sovrascrive l’azione precedente.
- Parametri: `key` (String), `action` (String)
- Edge case: chiavi o azioni null vengono ignorate.

### `String getAction(String key)`
Restituisce l’azione associata al tasto, o null se non esiste.

### `void unbind(String key)`
Rimuove l’associazione per il tasto specificato.

### `Set<String> getBoundKeys()`
Restituisce l’insieme dei tasti attualmente associati.

## Thread safety
Non thread-safe: la classe non sincronizza l’accesso alla mappa.

## Modifiche recenti
- I binding predefiniti del menu sono ora:
  - `PREVIOUS_GAME = Common.UP` (prima era `Common.LEFT`)
  - `NEXT_GAME = Common.DOWN` (prima era `Common.RIGHT`)

## Esempio d’uso
```java
InputBindings ib = new InputBindings();
ib.bind("W", "Su");
ib.bind("S", "Giu");
System.out.println(ib.getAction("W")); // "Su"
ib.unbind("W");
```


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