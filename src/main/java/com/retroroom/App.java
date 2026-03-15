package com.retroroom;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * JavaFX App
 */
public class App extends Application {

    private enum CabinetGame {
        MENU,
        DUNGEON,
        SPACE,
        FORZA4
    }

    private enum GridTheme {
        ARCADE,
        CONNECT4
    }

    private Scoreboard dungeonScoreboard;
    private Scoreboard spaceScoreboard;
    private VBox dungeonTableBox;
    private VBox spaceTableBox;
    private Label screenText;
    private Label controlsHintLabel;
    private GridPane monitorGrid;
    private VBox monitorGridBox;
    private Label monitorGridInfo;
    private StackPane splashOverlay;
    private Label splashTitleLabel;
    private Label splashMessageLabel;
    private Label splashHintLabel;
    private TextField splashNameField;
    private GridTheme currentGridTheme = GridTheme.ARCADE;

    // Controlli interattivi
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
    private boolean splashActive = false;

    private String statusLine = "";

    // Snap Tap / SOCD stacks
    private final List<KeyCode> xInputStack = new ArrayList<>();
    private final List<KeyCode> yInputStack = new ArrayList<>();

    private final String BUTTON_STYLE_NORMAL = "-fx-background-color: #d2691e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 50; -fx-min-width: 50; -fx-min-height: 50;";
    private final String BUTTON_STYLE_PRESSED = "-fx-background-color: #8b4500; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 50; -fx-min-width: 50; -fx-min-height: 50;";

    @Override
    public void start(Stage stage) {
        // Inizializza le classifiche
        dungeonScoreboard = new Scoreboard(10, Scoreboard.SortOrder.ASCENDING);

        spaceScoreboard = new Scoreboard(10, Scoreboard.SortOrder.ASCENDING);

        // Layout principale
        BorderPane root = new BorderPane();
        root.setFocusTraversable(true);
        root.setStyle("-fx-background-color: #2b2b2b;"); // Sfondo scuro

        // Pannello sinistro - Dungeon Master
        VBox leftPanel = createSidePanel("Dungeon Master", dungeonScoreboard);
        root.setLeft(leftPanel);

        // Pannello destro - Space Invaders
        VBox rightPanel = createSidePanel("Space Invaders", spaceScoreboard);
        root.setRight(rightPanel);

        // Pannello centrale - Cabinato Arcade
        VBox boxCabinato = createCabinato();
        root.setCenter(boxCabinato);

        Scene scene = new Scene(root, 1280, 720); // 16:9

        // Gestione input tastiera
        setupInputHandlers(scene);

        stage.setTitle("RetroRoom Arcade");
        stage.setScene(scene);
        stage.show();
        root.requestFocus();

        renderMenu();
        startConsoleListener();
    }

    private void setupInputHandlers(Scene scene) {
        scene.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            if (code == InputBindings.Common.UP || code == InputBindings.Common.DOWN) {
                // Se non è già presente, lo aggiunge in cima (ultimo premuto = prioritario)
                if (!yInputStack.contains(code)) {
                    yInputStack.add(code);
                    updateJoystickVisual();
                }
                handleGameplayKey(code);
                return;
            }

            if (code == InputBindings.Common.LEFT || code == InputBindings.Common.RIGHT) {
                if (!xInputStack.contains(code)) {
                    xInputStack.add(code);
                    updateJoystickVisual();
                }
                handleGameplayKey(code);
                return;
            }

            if (code == InputBindings.Menu.START_GAME
                    || code == InputBindings.Space.FIRE_SECONDARY
                    || code == InputBindings.Forza4.DROP_SECONDARY) {
                btnA.arm();
                btnA.setStyle(BUTTON_STYLE_PRESSED);
                handleGameplayKey(code);
                return;
            }

            if (code == InputBindings.Dungeon.BACK_TO_MENU
                    || code == InputBindings.Space.BACK_TO_MENU
                    || code == InputBindings.Forza4.BACK_TO_MENU) {
                btnB.arm();
                btnB.setStyle(BUTTON_STYLE_PRESSED);
                handleGameplayKey(code);
                return;
            }

            handleGameplayKey(code);
        });

        scene.setOnKeyReleased(e -> {
            KeyCode code = e.getCode();
            if (code == InputBindings.Common.UP || code == InputBindings.Common.DOWN) {
                yInputStack.remove(code);
                updateJoystickVisual();
                return;
            }

            if (code == InputBindings.Common.LEFT || code == InputBindings.Common.RIGHT) {
                xInputStack.remove(code);
                updateJoystickVisual();
                return;
            }

            if (code == InputBindings.Menu.START_GAME
                    || code == InputBindings.Space.FIRE_SECONDARY
                    || code == InputBindings.Forza4.DROP_SECONDARY) {
                btnA.disarm();
                btnA.setStyle(BUTTON_STYLE_NORMAL);
                return;
            }

            if (code == InputBindings.Dungeon.BACK_TO_MENU
                    || code == InputBindings.Space.BACK_TO_MENU
                    || code == InputBindings.Forza4.BACK_TO_MENU) {
                btnB.disarm();
                btnB.setStyle(BUTTON_STYLE_NORMAL);
            }
        });
    }

    private void handleGameplayKey(KeyCode code) {
        if (splashActive) {
            return;
        }

        if (activeGame == CabinetGame.MENU) {
            if (code == InputBindings.Menu.PREVIOUS_GAME) {
                menuIndex = (menuIndex - 1 + menuEntries.length) % menuEntries.length;
                renderMenu();
            } else if (code == InputBindings.Menu.NEXT_GAME) {
                menuIndex = (menuIndex + 1) % menuEntries.length;
                renderMenu();
            } else if (code == InputBindings.Menu.START_GAME) {
                startSelectedGame();
            }
            return;
        }

        if ((activeGame == CabinetGame.DUNGEON && code == InputBindings.Dungeon.BACK_TO_MENU)
                || (activeGame == CabinetGame.SPACE && code == InputBindings.Space.BACK_TO_MENU)
                || (activeGame == CabinetGame.FORZA4 && code == InputBindings.Forza4.BACK_TO_MENU)) {
            activeGame = CabinetGame.MENU;
            statusLine = "Menu aperto";
            renderMenu();
            return;
        }

        switch (activeGame) {
            case DUNGEON:
                handleDungeonInput(code);
                break;
            case SPACE:
                handleSpaceInput(code);
                break;
            case FORZA4:
                handleForza4Input(code);
                break;
            default:
                break;
        }
    }

    private void handleDungeonInput(KeyCode code) {
        if (dungeonGame == null || DungeonMaster.isFinished(dungeonGame)) {
            return;
        }

        char move;
        if (code == InputBindings.Dungeon.MOVE_UP) {
            move = 'w';
        } else if (code == InputBindings.Dungeon.MOVE_LEFT) {
            move = 'a';
        } else if (code == InputBindings.Dungeon.MOVE_DOWN) {
            move = 's';
        } else if (code == InputBindings.Dungeon.MOVE_RIGHT) {
            move = 'd';
        } else {
            return;
        }

        String event = DungeonMaster.move(dungeonGame, move);

        if (DungeonMaster.isFinished(dungeonGame)) {
            if (DungeonMaster.isWon(dungeonGame)) {
                int elapsedSeconds = (int) DungeonMaster.getElapsedSeconds(dungeonGame);
                showLeaderboardSplash("Dungeon Master", "Hai completato il dungeon in " + formatSeconds(elapsedSeconds) + "!", playerName -> {
                    boolean updated = dungeonScoreboard.addOrUpdateBest(playerName, elapsedSeconds);
                    updateTable(dungeonTableBox, dungeonScoreboard);
                    String resultMessage = updated
                            ? "Vittoria in " + formatSeconds(elapsedSeconds) + "! Nuovo record per " + playerName
                            : "Vittoria in " + formatSeconds(elapsedSeconds) + ". Record migliore di " + playerName + " mantenuto";
                    goHomeAfterLeaderboard(resultMessage);
                });
                return;
            } else {
                statusLine = "Game over nel dungeon.";
            }
        } else {
            statusLine = event;
        }

        renderDungeon();
    }

    private void handleSpaceInput(KeyCode code) {
        if (spaceGame == null || spaceGame.vittoria()) {
            return;
        }

        if (code == InputBindings.Space.MOVE_LEFT) {
            spaceGame.muovi('a');
        } else if (code == InputBindings.Space.MOVE_RIGHT) {
            spaceGame.muovi('d');
        } else if (code == InputBindings.Space.FIRE_PRIMARY || code == InputBindings.Space.FIRE_SECONDARY) {
            spaceGame.muovi('s');
        } else {
            return;
        }

        if (spaceGame.vittoria()) {
            int elapsedSeconds = (int) spaceGame.getElapsedSeconds();
            showLeaderboardSplash("Space Invaders", "Hai vinto in " + formatSeconds(elapsedSeconds) + "!", playerName -> {
                boolean updated = spaceScoreboard.addOrUpdateBest(playerName, elapsedSeconds);
                updateTable(spaceTableBox, spaceScoreboard);
                String resultMessage = updated
                        ? "Space completato in " + formatSeconds(elapsedSeconds) + "! Nuovo record per " + playerName
                        : "Space completato in " + formatSeconds(elapsedSeconds) + ". Record migliore di " + playerName + " mantenuto";
                goHomeAfterLeaderboard(resultMessage);
            });
            return;
        } else {
            statusLine = "";
        }

        renderSpace();
    }

    private void handleForza4Input(KeyCode code) {
        if (forza4Game == null) {
            return;
        }

        int columns = forza4Game.getTabella()[0].length;
        if (code == InputBindings.Forza4.MOVE_LEFT) {
            forza4SelectedColumn = Math.max(0, forza4SelectedColumn - 1);
            statusLine = "Colonna " + (forza4SelectedColumn + 1);
            renderForza4();
            return;
        }

        if (code == InputBindings.Forza4.MOVE_RIGHT) {
            forza4SelectedColumn = Math.min(columns - 1, forza4SelectedColumn + 1);
            statusLine = "Colonna " + (forza4SelectedColumn + 1);
            renderForza4();
            return;
        }

        if (forza4Finished || (code != InputBindings.Forza4.DROP_PRIMARY && code != InputBindings.Forza4.DROP_SECONDARY)) {
            return;
        }

        boolean inserted = forza4Game.inserisciPedina(forza4SelectedColumn, forza4TurnPlayerOne);
        if (!inserted) {
            statusLine = "Colonna piena";
            renderForza4();
            return;
        }

        int pedina = forza4TurnPlayerOne ? 1 : 2;
        if (hasForza4Win(pedina)) {
            forza4Finished = true;
            String winner = forza4TurnPlayerOne ? "Giocatore 1" : "Giocatore 2";
            statusLine = "Vince " + winner + "!";
            showSimpleSplash("Forza 4", "Complimenti, " + winner + "!", () -> {
                activeGame = CabinetGame.MENU;
                statusLine = "Forza 4: vince " + winner;
                renderMenu();
            });
        } else {
            forza4TurnPlayerOne = !forza4TurnPlayerOne;
            statusLine = "Turno " + (forza4TurnPlayerOne ? "Giocatore 1" : "Giocatore 2");
        }

        renderForza4();
    }

    private boolean hasForza4Win(int pedina) {
        int[][] board = forza4Game.getTabella();
        int rows = board.length;
        int cols = board[0].length;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c] != pedina) {
                    continue;
                }
                if (countLine(board, r, c, 0, 1, pedina) >= 4 ||
                        countLine(board, r, c, 1, 0, pedina) >= 4 ||
                        countLine(board, r, c, 1, 1, pedina) >= 4 ||
                        countLine(board, r, c, 1, -1, pedina) >= 4) {
                    return true;
                }
            }
        }
        return false;
    }

    private int countLine(int[][] board, int row, int col, int dr, int dc, int pedina) {
        int total = 1;
        total += countDirection(board, row, col, dr, dc, pedina);
        total += countDirection(board, row, col, -dr, -dc, pedina);
        return total;
    }

    private int countDirection(int[][] board, int row, int col, int dr, int dc, int pedina) {
        int rows = board.length;
        int cols = board[0].length;
        int count = 0;
        int r = row + dr;
        int c = col + dc;

        while (r >= 0 && r < rows && c >= 0 && c < cols && board[r][c] == pedina) {
            count++;
            r += dr;
            c += dc;
        }

        return count;
    }

    private void updateJoystickVisual() {
        // Logica Snap Tap / Last Input Priority

        // Asse X (A/D)
        if (xInputStack.isEmpty()) {
            joystickStick.setTranslateX(0);
        } else {
            // Prende l'ultimo tasto premuto
            KeyCode lastX = xInputStack.get(xInputStack.size() - 1);
            if (lastX == InputBindings.Common.LEFT) joystickStick.setTranslateX(-20);
            else if (lastX == InputBindings.Common.RIGHT) joystickStick.setTranslateX(20);
        }

        // Asse Y (W/S)
        if (yInputStack.isEmpty()) {
            joystickStick.setTranslateY(0);
        } else {
            // Prende l'ultimo tasto premuto
            KeyCode lastY = yInputStack.get(yInputStack.size() - 1);
            if (lastY == InputBindings.Common.UP) joystickStick.setTranslateY(-20);
            else if (lastY == InputBindings.Common.DOWN) joystickStick.setTranslateY(20);
        }
    }

    private VBox createSidePanel(String title, Scoreboard scoreboard) {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(20));
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setPrefWidth(300);
        // Stile steampunk semplice: bordi dorati/bronze, sfondo scuro
        panel.setStyle("-fx-border-color: #cd7f32; -fx-border-width: 5; -fx-background-color: #3e3e3e;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #cd7f32; -fx-font-size: 24px; -fx-font-weight: bold;");

        // Creiamo la visualizzazione della tabella a mano (VBox semplice)
        VBox tableBox = new VBox(5);
        tableBox.setStyle("-fx-background-color: #222; -fx-padding: 10;");

        // Salviamo il riferimento per aggiornamenti futuri
        if (title.equals("Dungeon Master")) {
            this.dungeonTableBox = tableBox;
        } else {
            this.spaceTableBox = tableBox;
        }

        updateTable(tableBox, scoreboard);

        panel.getChildren().addAll(titleLabel, tableBox);
        return panel;
    }

    private void updateTable(VBox tableBox, Scoreboard scoreboard) {
        tableBox.getChildren().clear();
        for (Scoreboard.ScoreEntry entry : scoreboard.getEntries()) {
            String scoreText = (scoreboard == dungeonScoreboard || scoreboard == spaceScoreboard)
                    ? formatSeconds(entry.getScore())
                    : String.valueOf(entry.getScore());
            Label row = new Label(entry.getName() + " : " + scoreText);
            row.setStyle("-fx-text-fill: white; -fx-font-family: 'Monospaced';");
            tableBox.getChildren().add(row);
        }
    }

    private void startSelectedGame() {
        if (menuIndex == 0) {
            activeGame = CabinetGame.DUNGEON;
            dungeonGame = DungeonMaster.newGame();
            statusLine = "Dungeon avviato";
            renderDungeon();
        } else if (menuIndex == 1) {
            activeGame = CabinetGame.SPACE;
            spaceGame = new SpaceInvaders();
            statusLine = "Space avviato";
            renderSpace();
        } else {
            activeGame = CabinetGame.FORZA4;
            forza4Game = new Forza4(6, 7);
            forza4TurnPlayerOne = true;
            forza4Finished = false;
            forza4SelectedColumn = 0;
            statusLine = "Forza 4 avviato";
            renderForza4();
        }
    }

    private void renderMenu() {
        updateControlsHint();
        StringBuilder builder = new StringBuilder();
        builder.append("RETRO ROOM - MENU\n\n");
        for (int i = 0; i < menuEntries.length; i++) {
            if (i == menuIndex) {
                builder.append("> ");
            } else {
                builder.append("  ");
            }
            builder.append(menuEntries[i]).append('\n');
        }
        if (!statusLine.isEmpty()) {
            builder.append("\n").append(statusLine);
        }
        showTextMode(builder.toString());
    }

    private void renderDungeon() {
        if (dungeonGame == null) {
            return;
        }
        updateControlsHint();
        String text = DungeonMaster.getHudLine(dungeonGame);
        if (!statusLine.isEmpty()) {
            text += " | " + statusLine;
        }
        showGridMode(DungeonMaster.getEmojiGrid(dungeonGame), text, GridTheme.ARCADE);
    }

    private void renderSpace() {
        if (spaceGame == null) {
            return;
        }

        updateControlsHint();

        String[][] grid = new String[spaceGame.getAltezza()][spaceGame.getLarghezza()];

        int width = spaceGame.getLarghezza();
        int height = spaceGame.getAltezza();
        int playerX = spaceGame.getPosizioneGiocatoreX();
        int playerY = spaceGame.getPosizioneGiocatoreY();
        int[] ex = spaceGame.getPosizioneNemicoX();
        int[] ey = spaceGame.getPosizioneNemicoY();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x == playerX && y == playerY) {
                    grid[y][x] = "🚀";
                    continue;
                }

                boolean enemy = false;
                for (int i = 0; i < ex.length; i++) {
                    if (x == ex[i] && y == ey[i]) {
                        enemy = true;
                        break;
                    }
                }
                grid[y][x] = enemy ? "👾" : "";
            }
        }

        StringBuilder out = new StringBuilder();
        out.append("Nemici: ")
                .append(spaceGame.getNemiciEliminati())
                .append('/')
                .append(spaceGame.getNemiciDaEliminare())
                .append(" Tempo:")
                .append(formatSeconds((int) spaceGame.getElapsedSeconds()));

        if (!statusLine.isEmpty()) {
            out.append(" | ").append(statusLine);
        }

        showGridMode(grid, out.toString(), GridTheme.ARCADE);
    }

    private void renderForza4() {
        if (forza4Game == null) {
            return;
        }

        updateControlsHint();
        int[][] board = forza4Game.getTabella();
        int rows = board.length;
        int cols = board[0].length;

        // Riga 0: cursore colonna (freccia verso il basso)
        String[][] grid = new String[rows + 1][cols];
        for (int c = 0; c < cols; c++) {
            grid[0][c] = c == forza4SelectedColumn ? "▼" : "";
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c] == 1) {
                    grid[r + 1][c] = "●";
                } else if (board[r][c] == 2) {
                    grid[r + 1][c] = "●";
                } else {
                    grid[r + 1][c] = "";
                }
            }
        }

        StringBuilder info = new StringBuilder();
        info.append("Selettore colonna: ").append(forza4SelectedColumn + 1).append(" | ")
                .append(forza4Finished
                        ? "Partita finita"
                        : "Turno: " + (forza4TurnPlayerOne ? "Giocatore 1" : "Giocatore 2"));

        if (!statusLine.isEmpty()) {
            info.append(" | ").append(statusLine);
        }

        showGridMode(grid, info.toString(), GridTheme.CONNECT4);
    }

    private void showTextMode(String text) {
        if (monitorGridBox != null) {
            monitorGridBox.setManaged(false);
            monitorGridBox.setVisible(false);
        }
        screenText.setManaged(true);
        screenText.setVisible(true);
        screenText.setText(text);
    }

    private void showGridMode(String[][] grid, String info, GridTheme theme) {
        if (monitorGridBox != null) {
            monitorGridBox.setManaged(true);
            monitorGridBox.setVisible(true);
        }
        screenText.setManaged(false);
        screenText.setVisible(false);
        currentGridTheme = theme;
        renderEmojiGrid(grid);
        monitorGridInfo.setText(info);
    }

    private void renderEmojiGrid(String[][] grid) {
        monitorGrid.getChildren().clear();
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                Label cell = new Label(grid[row][col]);
                cell.setMinSize(30, 30);
                cell.setPrefSize(30, 30);
                cell.setMaxSize(30, 30);
                cell.setAlignment(Pos.CENTER);

                if (currentGridTheme == GridTheme.CONNECT4) {
                    cell.setStyle(getConnect4CellStyle(grid, row, col));
                } else {
                    cell.setStyle(getArcadeCellStyle(grid, row, col));
                }

                monitorGrid.add(cell, col, row);
            }
        }
    }

    private String getArcadeCellStyle(String[][] grid, int row, int col) {
        int rows = grid.length;
        int cols = grid[0].length;

        double top = row == 0 ? 2 : 0;
        double right = col == cols - 1 ? 2 : 0;
        double bottom = row == rows - 1 ? 2 : 0;
        double left = col == 0 ? 2 : 0;

        return "-fx-font-size: 18;"
                + "-fx-text-fill: #f7f7f7;"
                + "-fx-background-color: transparent;"
                + "-fx-border-color: #53d6ff;"
                + "-fx-border-width: " + top + " " + right + " " + bottom + " " + left + ";";
    }

    private String getConnect4CellStyle(String[][] grid, int row, int col) {
        int rows = grid.length;
        String symbol = grid[row][col];

        if (row == 0) {
            return "-fx-font-size: 20;"
                    + "-fx-font-weight: bold;"
                    + "-fx-text-fill: #ffd65c;"
                    + "-fx-background-color: transparent;"
                    + "-fx-border-color: transparent;";
        }

        String tokenColor = "#d6e4ff";
        if (!symbol.isEmpty()) {
            tokenColor = "●".equals(symbol)
                    ? (isPlayerOneToken(row - 1, col) ? "#f7d046" : "#ff5a5a")
                    : tokenColor;
        }

        double bottom = row == rows - 1 ? 6 : 0;

        return "-fx-font-size: 20;"
                + "-fx-text-fill: " + tokenColor + ";"
                + "-fx-background-color: rgba(20, 30, 60, 0.55);"
                + "-fx-border-color: #70b6ff;"
                + "-fx-border-width: 0 2 " + bottom + " 2;";
    }

    private boolean isPlayerOneToken(int row, int col) {
        if (forza4Game == null) {
            return false;
        }
        int[][] board = forza4Game.getTabella();
        return row >= 0 && row < board.length && col >= 0 && col < board[0].length && board[row][col] == 1;
    }

    private String formatSeconds(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    private void showSimpleSplash(String gameName, String message, Runnable onFinished) {
        splashActive = true;
        showSplashOverlay(gameName, message, false);

        PauseTransition pause = new PauseTransition(Duration.seconds(1.4));
        pause.setOnFinished(e -> {
            hideSplashOverlay();
            splashActive = false;
            if (onFinished != null) {
                onFinished.run();
            }
        });
        pause.play();
    }

    private void showLeaderboardSplash(String gameName, String message, Consumer<String> onNameReady) {
        splashActive = true;
        showSplashOverlay(gameName, message, true);

        splashNameField.setOnAction(e -> {
            String value = splashNameField.getText() == null ? "" : splashNameField.getText().trim();
            String playerName = value.isEmpty() ? "Giocatore" : value;
            hideSplashOverlay();
            splashActive = false;
            onNameReady.accept(playerName);
        });

        Platform.runLater(() -> {
            splashNameField.requestFocus();
            splashNameField.selectAll();
        });
    }

    private void showSplashOverlay(String gameName, String message, boolean withNameInput) {
        splashTitleLabel.setText("*** VITTORIA ***\n" + gameName);
        splashMessageLabel.setText(message);
        splashNameField.setText("Giocatore");
        splashNameField.setVisible(withNameInput);
        splashNameField.setManaged(withNameInput);
        splashHintLabel.setText(withNameInput ? "Inserisci nome e premi INVIO" : "");
        splashOverlay.setVisible(true);
        splashOverlay.setManaged(true);
    }

    private void hideSplashOverlay() {
        splashNameField.setOnAction(null);
        splashOverlay.setVisible(false);
        splashOverlay.setManaged(false);
    }

    private void goHomeAfterLeaderboard(String message) {
        activeGame = CabinetGame.MENU;
        statusLine = message;
        renderMenu();
    }

    private void startConsoleListener() {
        Thread thread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            System.out.println("--- Console Leaderboard ---");
            System.out.println("Comandi: remove <dm|si> <nome>");

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\s+", 3);
                if (parts.length < 3 || !parts[0].equalsIgnoreCase("remove")) {
                    System.out.println("Comando non valido. Usa: remove <dm|si> <nome>");
                    continue;
                }

                String game = parts[1];
                String name = parts[2].trim();
                boolean removed;

                if (game.equalsIgnoreCase("dm")) {
                    removed = dungeonScoreboard.removeByName(name);
                    if (removed) {
                        Platform.runLater(() -> updateTable(dungeonTableBox, dungeonScoreboard));
                    }
                } else if (game.equalsIgnoreCase("si")) {
                    removed = spaceScoreboard.removeByName(name);
                    if (removed) {
                        Platform.runLater(() -> updateTable(spaceTableBox, spaceScoreboard));
                    }
                } else {
                    System.out.println("Gioco non valido: usa dm o si");
                    continue;
                }

                if (removed) {
                    System.out.println("Record rimosso: " + name);
                } else {
                    System.out.println("Nessun record trovato per: " + name);
                }
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    private void updateControlsHint() {
        if (controlsHintLabel == null) {
            return;
        }

        switch (activeGame) {
            case DUNGEON:
                controlsHintLabel.setText(InputBindings.dungeonHint());
                break;
            case SPACE:
                controlsHintLabel.setText(InputBindings.spaceHint());
                break;
            case FORZA4:
                controlsHintLabel.setText(InputBindings.forza4Hint());
                break;
            case MENU:
            default:
                controlsHintLabel.setText(InputBindings.menuHint());
                break;
        }
    }


    private VBox createCabinato() {
        VBox cabinato = new VBox(20);
        cabinato.setAlignment(Pos.CENTER);
        cabinato.setPadding(new Insets(20));
        
        // Monitor
        StackPane monitorFrame = new StackPane();
        monitorFrame.setPrefSize(600, 400);
        monitorFrame.setMaxSize(600, 400);
        monitorFrame.setStyle("-fx-background-color: #000000; -fx-border-color: #8b4513; -fx-border-width: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");
        
        Label screenText = new Label("PRESS START");
        screenText.setStyle("-fx-text-fill: #00ff00; -fx-font-family: 'Monospaced'; -fx-font-size: 22;");
        screenText.setWrapText(true);
        screenText.setMaxWidth(560);
        screenText.setAlignment(Pos.TOP_LEFT);
        this.screenText = screenText;

        monitorGrid = new GridPane();
        monitorGrid.setHgap(2);
        monitorGrid.setVgap(2);
        monitorGrid.setStyle("-fx-background-color: rgba(10, 16, 32, 0.35); -fx-padding: 8; -fx-background-radius: 6;");

        monitorGridInfo = new Label();
        monitorGridInfo.setStyle("-fx-text-fill: #00ff00; -fx-font-family: 'Monospaced'; -fx-font-size: 16;");
        monitorGridInfo.setWrapText(true);
        monitorGridInfo.setMaxWidth(560);

        monitorGridBox = new VBox(10, monitorGrid, monitorGridInfo);
        monitorGridBox.setAlignment(Pos.TOP_LEFT);
        monitorGridBox.setPadding(new Insets(14));
        monitorGridBox.setManaged(false);
        monitorGridBox.setVisible(false);

        monitorFrame.getChildren().add(screenText);
        monitorFrame.getChildren().add(monitorGridBox);

        splashTitleLabel = new Label("*** VITTORIA ***");
        splashTitleLabel.setStyle("-fx-text-fill: #ffde59; -fx-font-family: 'Monospaced'; -fx-font-size: 30; -fx-font-weight: bold;");
        splashTitleLabel.setAlignment(Pos.CENTER);

        splashMessageLabel = new Label();
        splashMessageLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-family: 'Monospaced'; -fx-font-size: 18;");
        splashMessageLabel.setWrapText(true);
        splashMessageLabel.setMaxWidth(520);
        splashMessageLabel.setAlignment(Pos.CENTER);

        splashNameField = new TextField("Giocatore");
        splashNameField.setMaxWidth(260);
        splashNameField.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 16;");

        splashHintLabel = new Label();
        splashHintLabel.setStyle("-fx-text-fill: #9dd7ff; -fx-font-family: 'Monospaced'; -fx-font-size: 14;");

        VBox splashContent = new VBox(14, splashTitleLabel, splashMessageLabel, splashNameField, splashHintLabel);
        splashContent.setAlignment(Pos.CENTER);
        splashContent.setPadding(new Insets(18));
        splashContent.setMaxWidth(560);
        splashContent.setStyle("-fx-background-color: rgba(0,0,0,0.86); -fx-border-color: #ffde59; -fx-border-width: 3; -fx-background-radius: 8; -fx-border-radius: 8;");

        splashOverlay = new StackPane(splashContent);
        splashOverlay.setPickOnBounds(true);
        splashOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.45);");
        splashOverlay.setVisible(false);
        splashOverlay.setManaged(false);

        monitorFrame.getChildren().add(splashOverlay);

        // Controlli
        HBox controls = new HBox(50);
        controls.setAlignment(Pos.CENTER);
        controls.setStyle("-fx-background-color: #8b4513; -fx-padding: 20; -fx-background-radius: 10;");
        
        // Joystick
        Circle joystickBase = new Circle(40, Color.BLACK);
        joystickStick = new Circle(20, Color.RED);
        StackPane joystick = new StackPane(joystickBase, joystickStick);
        
        // Pulsanti
        HBox buttons = new HBox(15);
        btnA = new Button("A");
        btnA.setStyle(BUTTON_STYLE_NORMAL);
        btnB = new Button("B");
        btnB.setStyle(BUTTON_STYLE_NORMAL);

        buttons.getChildren().addAll(btnA, btnB);
        controls.getChildren().addAll(joystick, buttons);

        controlsHintLabel = new Label();
        controlsHintLabel.setStyle("-fx-text-fill: #f5deb3; -fx-font-family: 'Monospaced'; -fx-font-size: 14;");
        controlsHintLabel.setWrapText(true);
        controlsHintLabel.setMaxWidth(700);
        controlsHintLabel.setAlignment(Pos.CENTER);
        updateControlsHint();

        cabinato.getChildren().addAll(monitorFrame, controls, controlsHintLabel);
        return cabinato;
    }

    public static void main(String[] args) {
        launch();
    }

}