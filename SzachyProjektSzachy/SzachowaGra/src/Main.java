import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main extends Application {

    private static List<GRACZE> gracze = new ArrayList<>();
    private ChessClient chessClient;

    private Label whiteLabel;
    private Label blackLabel;
    private TextArea lobbyMessages;
    private Button startGameButton;
    private Board board;
    private boolean isGameStarted = false; // Flaga, aby gra uruchomiła się tylko raz na klienta

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        chessClient = new ChessClient();
        chessClient.connectToServer("localhost", this::handleServerMessage);

        // Menu główne
        Text title = new Text("Szachy");
        title.setFont(Font.font("Arial", 50));
        title.setFill(Color.YELLOW);

        Button startButton = createStyledButton("Start Gry");
        startButton.setOnAction(event -> showPlayerSelectionScreen(primaryStage));

        Button addPlayerButton = createStyledButton("Dodaj Gracza");
        addPlayerButton.setOnAction(event -> openAddPlayerWindow());

        Button exitButton = createStyledButton("Wyjście");
        exitButton.setOnAction(event -> System.exit(0));

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(title, startButton, addPlayerButton, exitButton);
        layout.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(layout, 700, 700);
        primaryStage.setTitle("Szachy - Menu Główne");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showPlayerSelectionScreen(Stage primaryStage) {
        loadPlayersFromFile();

        ListView<String> playerListView = new ListView<>();
        for (GRACZE player : gracze) {
            playerListView.getItems().add(player.getImie());
        }

        Button selectButton = createStyledButton("Wybierz Gracza");
        selectButton.setOnAction(event -> {
            String selectedPlayer = playerListView.getSelectionModel().getSelectedItem();
            if (selectedPlayer != null) {
                showLobby(primaryStage, selectedPlayer);
            }
        });

        VBox selectionLayout = new VBox(10, new Text("Wybierz Gracza:"), playerListView, selectButton);
        selectionLayout.setAlignment(Pos.CENTER);
        selectionLayout.setStyle("-fx-background-color: black;");

        Scene selectionScene = new Scene(selectionLayout, 400, 400);
        primaryStage.setScene(selectionScene);
    }

    private void showLobby(Stage primaryStage, String selectedPlayer) {
        whiteLabel = new Label("Kolor Biały: Brak");
        whiteLabel.setTextFill(Color.WHITE);

        blackLabel = new Label("Kolor Czarny: Brak");
        blackLabel.setTextFill(Color.WHITE);

        lobbyMessages = new TextArea();
        lobbyMessages.setEditable(false);
        lobbyMessages.setPrefHeight(200);

        Button joinWhiteButton = createStyledButton("Dołącz jako Biały");
        Button joinBlackButton = createStyledButton("Dołącz jako Czarny");
        startGameButton = createStyledButton("Rozpocznij Grę");
        startGameButton.setDisable(true);

        joinWhiteButton.setOnAction(event -> chessClient.sendToServer("WHITE:" + selectedPlayer));

        joinBlackButton.setOnAction(event -> chessClient.sendToServer("BLACK:" + selectedPlayer));

        startGameButton.setOnAction(event -> chessClient.sendToServer("START_GAME"));

        VBox whiteBox = new VBox(10, whiteLabel, joinWhiteButton);
        whiteBox.setAlignment(Pos.CENTER);

        VBox blackBox = new VBox(10, blackLabel, joinBlackButton);
        blackBox.setAlignment(Pos.CENTER);

        VBox centerBox = new VBox(10, startGameButton, lobbyMessages);
        centerBox.setAlignment(Pos.CENTER);

        HBox layout = new HBox(50, whiteBox, centerBox, blackBox);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: black;");

        Scene lobbyScene = new Scene(layout, 700, 400);
        primaryStage.setScene(lobbyScene);
    }

    private void handleServerMessage(String message) {
        Platform.runLater(() -> {
            if (message.startsWith("LOBBY_STATE:")) {
                String[] parts = message.substring("LOBBY_STATE:".length()).split(",");
                boolean whiteAssigned = false;
                boolean blackAssigned = false;

                for (String part : parts) {
                    if (part.startsWith("BIAŁY=")) {
                        String whitePlayerName = part.split("=")[1].trim();
                        whiteLabel.setText("Kolor Biały: " + (whitePlayerName.equals("Brak") ? "Brak" : whitePlayerName));
                        whiteAssigned = !whitePlayerName.equals("Brak");
                    } else if (part.startsWith("CZARNY=")) {
                        String blackPlayerName = part.split("=")[1].trim();
                        blackLabel.setText("Kolor Czarny: " + (blackPlayerName.equals("Brak") ? "Brak" : blackPlayerName));
                        blackAssigned = !blackPlayerName.equals("Brak");
                    }
                }

                // Aktywacja przycisku, jeśli obaj gracze są przypisani
                startGameButton.setDisable(!(whiteAssigned && blackAssigned));
            } else if (message.equals("START_GAME") && !isGameStarted) {
                isGameStarted = true; // Ustawienie flagi, aby gra była uruchomiona tylko raz
                startChessBoard();
            } else if (message.startsWith("MOVE:")) {
                if (board != null) {
                    board.executeMove(message.substring(5)); // Przekazujemy ruch do szachownicy
                }
            } else {
                lobbyMessages.appendText(message + "\n");
            }
        });
    }

    private void startChessBoard() {
        Platform.runLater(() -> {
            try {
                board = new Board(chessClient);
                board.start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void openAddPlayerWindow() {
        Stage addPlayerStage = new Stage();
        addPlayerStage.setTitle("Dodaj Gracza");

        TextField nameField = new TextField();
        Button saveButton = createStyledButton("Zapisz");
        saveButton.setOnAction(event -> {
            String name = nameField.getText();
            if (!name.isEmpty()) {
                GRACZE newPlayer = new GRACZE(name);
                gracze.add(newPlayer);
                newPlayer.saveToFile();
                addPlayerStage.close();
            }
        });

        VBox layout = new VBox(10, nameField, saveButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout, 300, 200);
        addPlayerStage.setScene(scene);
        addPlayerStage.show();
    }

    private void loadPlayersFromFile() {
        try {
            gracze.clear();
            File file = new File("gracze.txt");
            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.startsWith("Imię: ")) {
                        gracze.add(new GRACZE(line.split(": ")[1]));
                    }
                }
                scanner.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", 20));
        button.setStyle("-fx-background-color: black; -fx-text-fill: yellow;");
        button.setOnMouseEntered(event -> button.setStyle("-fx-background-color: yellow; -fx-text-fill: black;"));
        button.setOnMouseExited(event -> button.setStyle("-fx-background-color: black; -fx-text-fill: yellow;"));
        return button;
    }
}
