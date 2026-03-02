package com.retroroom;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;

public class PrimaryController {

    @FXML
    private ListView<String> dungeonLeaderboard;

    @FXML
    private ListView<String> spaceLeaderboard;

    @FXML
    private StackPane gameScreen;

    @FXML
    private void initialize() {
        // Initialize leaderboards with dummy data for preview
        if (dungeonLeaderboard != null) {
            dungeonLeaderboard.getItems().addAll("Player1 - 5000", "Player2 - 4500", "Player3 - 4000");
        }
        if (spaceLeaderboard != null) {
            spaceLeaderboard.getItems().addAll("AlienHunter - 9999", "StarLord - 8888", "Rocket - 7777");
        }
    }

    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
