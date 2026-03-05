package com.retroroom;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * JavaFX App
 */
public class App extends Application {

    private Scoreboard dungeonScoreboard;
    private Scoreboard spaceScoreboard;
    private VBox dungeonTableBox;
    private VBox spaceTableBox;

    // Controlli interattivi
    private Circle joystickStick;
    private Button btnA;
    private Button btnB;

    // Snap Tap / SOCD stacks
    private final List<KeyCode> xInputStack = new ArrayList<>();
    private final List<KeyCode> yInputStack = new ArrayList<>();

    private final String BUTTON_STYLE_NORMAL = "-fx-background-color: #d2691e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 50; -fx-min-width: 50; -fx-min-height: 50;";
    private final String BUTTON_STYLE_PRESSED = "-fx-background-color: #8b4500; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 50; -fx-min-width: 50; -fx-min-height: 50;";

    @Override
    public void start(Stage stage) {
        // Inizializza le classifiche
        dungeonScoreboard = new Scoreboard(10);
        dungeonScoreboard.addScore("Player 1", 2000);
        dungeonScoreboard.addScore("Player 2", 1500);

        spaceScoreboard = new Scoreboard(10);
        spaceScoreboard.addScore("Alien Hunter", 5000);
        spaceScoreboard.addScore("Defender", 3400);

        // Layout principale
        BorderPane root = new BorderPane();
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

        // Avvia il listener per i comandi da terminale
        startConsoleListener();
    }

    private void setupInputHandlers(Scene scene) {
        scene.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            switch (code) {
                case W:
                case S:
                    // Se non è già presente, lo aggiunge in cima (ultimo premuto = prioritario)
                    if (!yInputStack.contains(code)) {
                        yInputStack.add(code);
                        updateJoystickVisual();
                    }
                    break;
                case A:
                case D:
                    if (!xInputStack.contains(code)) {
                        xInputStack.add(code);
                        updateJoystickVisual();
                    }
                    break;
                case P:
                    btnA.arm();
                    btnA.setStyle(BUTTON_STYLE_PRESSED);
                    break;
                case L:
                    btnB.arm();
                    btnB.setStyle(BUTTON_STYLE_PRESSED);
                    break;
            }
        });

        scene.setOnKeyReleased(e -> {
            KeyCode code = e.getCode();
            switch (code) {
                case W:
                case S:
                    yInputStack.remove(code);
                    updateJoystickVisual();
                    break;
                case A:
                case D:
                    xInputStack.remove(code);
                    updateJoystickVisual();
                    break;
                case P:
                    btnA.disarm();
                    btnA.setStyle(BUTTON_STYLE_NORMAL);
                    break;
                case L:
                    btnB.disarm();
                    btnB.setStyle(BUTTON_STYLE_NORMAL);
                    break;
            }
        });
    }

    private void updateJoystickVisual() {
        // Logica Snap Tap / Last Input Priority

        // Asse X (A/D)
        if (xInputStack.isEmpty()) {
            joystickStick.setTranslateX(0);
        } else {
            // Prende l'ultimo tasto premuto
            KeyCode lastX = xInputStack.get(xInputStack.size() - 1);
            if (lastX == KeyCode.A) joystickStick.setTranslateX(-20);
            else if (lastX == KeyCode.D) joystickStick.setTranslateX(20);
        }

        // Asse Y (W/S)
        if (yInputStack.isEmpty()) {
            joystickStick.setTranslateY(0);
        } else {
            // Prende l'ultimo tasto premuto
            KeyCode lastY = yInputStack.get(yInputStack.size() - 1);
            if (lastY == KeyCode.W) joystickStick.setTranslateY(-20);
            else if (lastY == KeyCode.S) joystickStick.setTranslateY(20);
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
            Label row = new Label(entry.getName() + " : " + entry.getScore());
            row.setStyle("-fx-text-fill: white; -fx-font-family: 'Monospaced';");
            tableBox.getChildren().add(row);
        }
    }

    private void startConsoleListener() {
        Thread thread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            System.out.println("--- Console Comandi ---");
            System.out.println("Sintassi: add <gioco> <nome> <punti>");
            System.out.println("Giochi: 'dm' (Dungeon Master), 'si' (Space Invaders)");
            System.out.println("Esempio: add dm Mario 100");

            while (true) {
                if (scanner.hasNext()) {
                    String cmd = scanner.next();
                    if (cmd.equalsIgnoreCase("add")) {
                        String game = scanner.next();
                        String name = scanner.next();
                        int score = scanner.nextInt();

                        if (game.equalsIgnoreCase("dm")) {
                            dungeonScoreboard.addScore(name, score);
                            Platform.runLater(() -> updateTable(dungeonTableBox, dungeonScoreboard));
                            System.out.println("Aggiunto a Dungeon Master!");
                        } else if (game.equalsIgnoreCase("si")) {
                            spaceScoreboard.addScore(name, score);
                            Platform.runLater(() -> updateTable(spaceTableBox, spaceScoreboard));
                            System.out.println("Aggiunto a Space Invaders!");
                        } else {
                            System.out.println("Gioco non trovato. Usa 'dm' o 'si'.");
                        }
                    }
                }
            }
        });
        thread.setDaemon(true); // Il thread si chiude quando chiudi l'app
        thread.start();
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
        screenText.setStyle("-fx-text-fill: #00ff00; -fx-font-family: 'Monospaced'; -fx-font-size: 30;");
        monitorFrame.getChildren().add(screenText);

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

        cabinato.getChildren().addAll(monitorFrame, controls);
        return cabinato;
    }

    public static void main(String[] args) {
        launch();
    }

}