# Analisi tecnica dettagliata e approfondita: App.java

## Descrizione generale
Classe principale dell’applicazione JavaFX. Gestisce l’avvio, la navigazione tra giochi, la visualizzazione delle schermate e l’integrazione delle componenti (scoreboard, binding input, giochi).

## Attributi principali
- `primaryStage` (Stage): finestra principale dell’applicazione
- `scoreboard` (Scoreboard): classifica globale
- `inputBindings` (InputBindings): configurazione dei controlli
- `currentGame` (Object): riferimento al gioco attivo

## Metodi principali
### `void start(Stage primaryStage)`
Metodo di ingresso JavaFX. Inizializza la finestra, carica la schermata principale e gestisce la navigazione.

### `void mostraMenu()`
Visualizza il menu principale con la selezione dei giochi.

### `void avviaGioco(String nomeGioco)`
Avvia il gioco selezionato, istanziando la classe corrispondente e integrando scoreboard e inputBindings.
- Parametri: `nomeGioco` (String) — nome identificativo del gioco
- Edge case: nomi non validi mostrano errore.

### `void mostraScoreboard()`
Visualizza la classifica globale.

### `void mostraImpostazioni()`
Permette la configurazione dei controlli tramite InputBindings.

## Edge case e note
- La classe non gestisce la persistenza dei dati tra sessioni.
- Non thread-safe: uso single-thread.
- L’integrazione di nuovi giochi richiede la modifica di `avviaGioco`.

## Esempio d’uso
```java
// Avvio applicazione JavaFX
double main(String[] args) {
    Application.launch(App.class, args);
}
```


## Import e dichiarazione di package

```java
package com.retroroom;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
```
**Cosa fa:**
- Importa tutte le classi necessarie per la GUI JavaFX, la gestione degli input, la manipolazione di layout e componenti grafici, e alcune utility Java standard.
- Il package `com.retroroom` identifica il namespace del progetto.

---

## Dichiarazione della classe principale

```java
public class App extends Application {
```
**Cosa fa:**
- Definisce la classe principale dell'applicazione, che estende `Application` di JavaFX, rendendola avviabile come app grafica.

---

## Enumerazioni interne

```java
private enum CabinetGame { MENU, DUNGEON, SPACE, FORZA4 }
private enum GridTheme { ARCADE, CONNECT4 }
```
**Cosa fa:**
- `CabinetGame` rappresenta lo stato attivo del cabinato (menu o uno dei tre giochi).
- `GridTheme` serve per cambiare lo stile grafico della griglia centrale (arcade generico o stile Forza 4).

---

## Variabili di stato e componenti GUI

```java
private Scoreboard dungeonScoreboard;
private Scoreboard spaceScoreboard;
private VBox dungeonTableBox;
private VBox spaceTableBox;
private Label screenText;
private Label controlsHintLabel;
private GridPane monitorGrid;
private VBox monitorGridBox;
private Label monitorGridInfo;
private GridTheme currentGridTheme = GridTheme.ARCADE;
private Circle joystickStick;
private Button btnA;
private Button btnB;
private CabinetGame activeGame = CabinetGame.MENU;
private int menuIndex = 0;
private final String[] menuEntries = {"Dungeon Master", "Space Invaders", "Forza 4"};
private DungeonMaster.GameState dungeonGame;
private SpaceInvaders spaceGame;
private Forza4 forza4Game;
private int forza4SelectedColumn = 0;
private boolean forza4TurnPlayerOne = true;
private boolean forza4Finished = false;
private String statusLine = "";
private final List<KeyCode> xInputStack = new ArrayList<>();
private final List<KeyCode> yInputStack = new ArrayList<>();
private final String BUTTON_STYLE_NORMAL = "...";
private final String BUTTON_STYLE_PRESSED = "...";
```
**Cosa fa:**
- Definisce tutte le variabili di stato necessarie per la gestione della GUI, delle classifiche, dello stato dei giochi, dei controlli virtuali e degli input.
- Gli stack x/y gestiscono la priorità degli input direzionali (simulazione joystick).
- Le costanti di stile definiscono l'aspetto dei pulsanti virtuali.

---

## Metodo start(Stage stage)

```java
@Override
public void start(Stage stage) {
    // ...
}
```
**Cosa fa:**
- Metodo entrypoint di JavaFX: inizializza le classifiche, costruisce il layout principale, imposta gli handler per l'input, mostra la scena e avvia il menu.
- Chiama metodi di supporto per creare i pannelli laterali, il cabinato centrale, e per gestire l'input da tastiera.

---

## Gestione input tastiera

```java
private void setupInputHandlers(Scene scene) {
    // ...
}
```
**Cosa fa:**
- Registra gli handler per la pressione e il rilascio dei tasti.
- Aggiorna gli stack di input, gestisce la priorità degli input, sincronizza i pulsanti virtuali e smista l'input ai giochi attivi.

---

## Gestione logica input per ogni gioco

```java
private void handleGameplayKey(KeyCode code) { ... }
private void handleDungeonInput(KeyCode code) { ... }
private void handleSpaceInput(KeyCode code) { ... }
private void handleForza4Input(KeyCode code) { ... }
```
**Cosa fa:**
- Smista l'input in base al gioco attivo.
- Ogni metodo gestisce la logica di input specifica del gioco: movimento, azioni, gestione turni, aggiornamento stato e classifica.

---

## Rendering e aggiornamento GUI

```java
private void renderMenu() { ... }
private void renderDungeon() { ... }
private void renderSpace() { ... }
private void renderForza4() { ... }
private void showTextMode(String text) { ... }
private void showGridMode(String[][] grid, String info, GridTheme theme) { ... }
private void renderEmojiGrid(String[][] grid) { ... }
```
**Cosa fa:**
- Aggiorna la visualizzazione centrale in base al gioco attivo.
- `renderMenu` mostra il menu testuale, gli altri metodi disegnano la griglia di gioco con emoji e informazioni di stato.
- `renderEmojiGrid` costruisce la griglia grafica cella per cella.

---

## Stile delle celle e gestione colori

```java
private String getArcadeCellStyle(...)
private String getConnect4CellStyle(...)
private boolean isPlayerOneToken(...)
```
**Cosa fa:**
- Definisce lo stile CSS delle celle della griglia in base al tema e al contenuto (giocatore, nemico, pedina).
- Permette di distinguere visivamente i diversi elementi di gioco.

---

## Utility e supporto

```java
private String formatSeconds(int seconds) { ... }
private String askPlayerName(String gameName) { ... }
private void startConsoleListener() { ... }
private void updateControlsHint() { ... }
private VBox createCabinato() { ... }
private VBox createSidePanel(String title, Scoreboard scoreboard) { ... }
private void updateTable(VBox tableBox, Scoreboard scoreboard) { ... }
```
**Cosa fa:**
- Metodi di supporto per formattare il tempo, chiedere il nome del giocatore, gestire la console per la rimozione dei record, aggiornare i suggerimenti dei controlli, costruire i pannelli e aggiornare le classifiche.
- `startConsoleListener` avvia un thread separato per ascoltare comandi da terminale.

---

## Metodo main

```java
public static void main(String[] args) {
    launch();
}
```
**Cosa fa:**
- Entry point statico dell'applicazione: avvia JavaFX.

---

## Conclusione
Questa classe funge da orchestratore dell'intera applicazione arcade, gestendo la GUI, la logica di selezione e avvio giochi, la gestione delle classifiche e l'interazione utente, con una struttura modulare e facilmente estendibile.