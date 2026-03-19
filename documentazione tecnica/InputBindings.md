# Documentazione tecnica approfondita: InputBindings.java

Percorso sorgente: `src/main/java/com/retroroom/InputBindings.java`

Scopo del documento
-------------------
`InputBindings` è il punto centrale per definire la mappatura dei tasti e produrre stringhe di help per l'utente. Questa documentazione descrive in dettaglio le scelte implementative, come estendere il sistema per il remapping runtime, e come integrare supporto per controller esterni.

Panoramica
----------
- File minimalista che contiene costanti `KeyCode` raggruppate per contesto: `Common`, `Menu`, `Dungeon`, `Space`, `Forza4`.
- Offre metodi helper `menuHint()`, `dungeonHint()`, `spaceHint()`, `forza4Hint()` che generano stringhe descrittive basate sui `KeyCode` correnti.

Motivazione progettuale
------------------------
- Centralizzare le mappature permette di cambiare i controlli in un solo file. È un approccio semplice e tipico in progetti scolastici.
- Contro: le costanti `public static final` non permettono remapping a runtime senza ricompilare.

Dettaglio della struttura
------------------------
- `public final class InputBindings` — classe non istanziabile.
- `public static final class Common` — definisce `UP, DOWN, LEFT, RIGHT` (default WASD).
- `public static final class Menu` — `PREVIOUS_GAME`, `NEXT_GAME`, `START_GAME`.
- `public static final class Dungeon` — `MOVE_UP/MOVE_DOWN/MOVE_LEFT/MOVE_RIGHT/BACK_TO_MENU`.
- `public static final class Space` — movement, `FIRE_PRIMARY`, `FIRE_SECONDARY`, `BACK_TO_MENU`.
- `public static final class Forza4` — movement, `DROP_PRIMARY`, `DROP_SECONDARY`, `BACK_TO_MENU`.

Come vengono usate le costanti
-----------------------------
- `App` usa questi campi per verificare `KeyCode` ricevuti negli handler: ad esempio `if(code == InputBindings.Common.UP) ...`.
- La generazione dei suggerimenti usa `KeyCode.getName()` per produrre label leggibili dall'utente.

Rimappatura: soluzioni possibili
--------------------------------
1. Rimappatura statica (attuale): cambiare la costante e ricompilare.
2. Rimappatura runtime semplice:
   - sostituire `public static final KeyCode X` con `public static KeyCode X` e leggere una configurazione da `properties` all'avvio (es. `config/controls.properties`).
   - fornire una UI che modifica questi valori e salva il file.
3. Architettura migliore: introdurre `enum Action { MOVE_LEFT, MOVE_RIGHT, FIRE, ... }` e una mappa `Map<Action, List<InputMapping>>` dove `InputMapping` può rappresentare `KeyCode` o `GamepadButton`.

Esempio d'implementazione runtime (bozza)
```
// config loader
Properties p = new Properties();
p.load(new FileInputStream("config/controls.properties"));
InputBindings.Common.UP = KeyCode.valueOf(p.getProperty("common.up","W"));
```

Integrare gamepad e multipli device
----------------------------------
- Definire `interface InputDevice { boolean isPressed(Action a); }` e avere implementazioni per Keyboard e Gamepad.
- `InputManager` chiederebbe agli `InputDevice` lo stato di `Action` con priorità.

Helper text generation e internazionalizzazione
----------------------------------------------
- I metodi `menuHint()` ecc. usano `KeyCode.getName()` che riflette il nome locale. Per i testi UI, considerare la localizzazione (ResourceBundle) e non concatenare stringhe direttamente.

Sicurezza e validazione
-----------------------
- Se si passa a rimappatura runtime validare che due azioni critiche non abbiano la stessa combinazione di tasti se non desiderato.
- Se si consente la pressione contemporanea di tasti, documentare le priorità (X/Y stacks nel `App`).

Test consigliati
----------------
- Testare che `InputBindings.menuHint()` produca la stringa attesa per le mappature di default.
- Se si implementa remapping runtime: testare la persistenza della config `controls.properties` e la corretta lettura al riavvio.

API pubblica (riassunto)
------------------------
- `KeyCode` pubblici per ogni contesto.
- `menuHint()`, `dungeonHint()`, `spaceHint()`, `forza4Hint()` che restituiscono stringhe ai fini della UI.

Conclusione e roadmap
---------------------
- L'attuale implementazione è corretta per uso scolastico ma limitata per estensioni runtime o supporto controller.
- Roadmap possibile:
  1. Convertire in `Action` + mappa `Map<Action, List<Input>>`.
  2. Aggiungere loader/saver di config in `JSON/PROPERTIES`.
  3. Integrare supporto Gamepad con libreria esterna o JavaFX `GameLoop`.

Fine del documento.
