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
    private String selectedPlayer; // Wybrany lokalny gracz (np. "Tomeczek", "tOMEK")

    private Label whiteLabel;
    private Label blackLabel;
    private TextArea lobbyMessages;
    private Button startGameButton;
    private Board board;
    private boolean isGameStarted = false;

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

    private void handleServerMessage(String message) {
        Platform.runLater(() -> {
            try {
                if (message.startsWith("LOBBY_STATE:")) {
                    handleLobbyState(message);
                } else if (message.startsWith("START_GAME:")) {
                    handleStartGameMessage(message);
                } else if (message.startsWith("MOVE:")) {
                    handleMoveMessage(message);
                } else if (message.startsWith("TURN:")) {
                    handleTurnMessage(message);
                } else if (message.startsWith("ERROR:")) {
                    showErrorAlert(message.substring("ERROR:".length()));
                } else {
                    handleOtherMessage(message);
                }
            } catch (Exception e) {
                System.err.println("Błąd przetwarzania wiadomości od serwera: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void handleTurnMessage(String message) {
        String turn = message.substring("TURN:".length()).trim();
        boolean isMyTurn = turn.equals(chessClient.getPlayerColor());

        if (board != null) {
            board.setTurn(isMyTurn);
        }

        System.out.println("🔄 Otrzymano wiadomość od serwera: TURN " + turn);
        System.out.println("🔄 Zmieniono turę. Czy to moja tura? " + isMyTurn);
    }








    private void handleLobbyState(String message) {
        String[] parts = message.substring("LOBBY_STATE:".length()).split(",");
        for (String part : parts) {
            if (part.startsWith("BIAŁY=")) {
                String whitePlayerName = part.split("=", 2)[1].trim();
                whiteLabel.setText("Kolor Biały: " + (whitePlayerName.equals("Brak") ? "Brak" : whitePlayerName));
            } else if (part.startsWith("CZARNY=")) {
                String blackPlayerName = part.split("=", 2)[1].trim();
                blackLabel.setText("Kolor Czarny: " + (blackPlayerName.equals("Brak") ? "Brak" : blackPlayerName));
            }
        }
        boolean whiteAssigned = !whiteLabel.getText().contains("Brak");
        boolean blackAssigned = !blackLabel.getText().contains("Brak");
        startGameButton.setDisable(!(whiteAssigned && blackAssigned));
    }

    private void handleStartGameMessage(String message) {
        String[] parts = message.substring("START_GAME:".length()).split(",");
        String whitePlayer = null;
        String blackPlayer = null;

        for (String part : parts) {
            if (part.startsWith("BIAŁY=")) {
                whitePlayer = part.split("=")[1].trim();
            } else if (part.startsWith("CZARNY=")) {
                blackPlayer = part.split("=")[1].trim();
            }
        }

        if (selectedPlayer != null) {
            if (selectedPlayer.equals(whitePlayer)) {
                chessClient.setPlayerColor("WHITE");
            } else if (selectedPlayer.equals(blackPlayer)) {
                chessClient.setPlayerColor("BLACK");
            }
        }

        if (chessClient.getPlayerColor() == null) {
            System.err.println("Kolor gracza nie został ustawiony. Nie można uruchomić szachownicy.");
            return;
        }

        if (!isGameStarted) {
            System.out.println("Inicjalizacja szachownicy dla gracza: " + selectedPlayer
                    + " (" + chessClient.getPlayerColor() + ")");
            isGameStarted = true;
            startChessBoard();
        }
    }
    private void handleMoveMessage(String message) {
        if (board == null) {
            System.err.println("Szachownica nie została zainicjowana. Ruch pominięty.");
            return;
        }

        try {
            // Rozdziel komunikat na części
            String[] parts = message.split(":");
            if (parts.length != 3) {
                System.err.println("❌ Nieprawidłowy format wiadomości ruchu: " + message);
                return;
            }

            String color = parts[1]; // "WHITE" lub "BLACK"
            String moveDetails = parts[2]; // "2,3->3,3"

            // Jeśli ruch dotyczy przeciwnika, wykonaj go lokalnie
            if (!color.equals(chessClient.getPlayerColor())) {
                System.out.println("Wykonuję ruch przeciwnika: " + moveDetails);
                board.executeMove(message); // Przetwarzanie lokalne
            } else {
                System.out.println("Zignorowano własny ruch: " + moveDetails);
            }

        } catch (Exception e) {
            System.err.println("❌ Błąd przetwarzania ruchu: " + message);
            e.printStackTrace();
        }
    }










    private void showErrorAlert(String errorText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setHeaderText(null);
        alert.setContentText(errorText);
        alert.showAndWait();
    }

    private void handleOtherMessage(String message) {
        if (lobbyMessages != null) {
            lobbyMessages.appendText(message + "\n");
        } else {
            System.err.println("Nie można dodać wiadomości do lobbyMessages: " + message);
        }
    }

    private void startChessBoard() {
        Platform.runLater(() -> {
            try {
                if (chessClient.getPlayerColor() != null) {
                    board = new Board(chessClient, chessClient.getPlayerColor());
                    board.start(new Stage());
                } else {
                    System.err.println("Kolor gracza nie został ustawiony. Nie można uruchomić szachownicy.");
                }
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
                        String[] parts = line.split(": ");
                        if (parts.length > 1) {
                            String pureName = parts[1].replace(", Kolor", "").trim();
                            gracze.add(new GRACZE(pureName));
                        }
                    }
                }
                scanner.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showPlayerSelectionScreen(Stage primaryStage) {
        loadPlayersFromFile();

        ListView<String> playerListView = new ListView<>();
        for (GRACZE player : gracze) {
            // W liście do wyboru NIE może być ", Kolor"
            playerListView.getItems().add(player.getImie());
        }

        Button selectButton = createStyledButton("Wybierz Gracza");
        selectButton.setOnAction(event -> {
            String selectedPlayerName = playerListView.getSelectionModel().getSelectedItem();
            if (selectedPlayerName != null) {
                chessClient.setPlayerName(selectedPlayerName); // np. "tOMEK" lub "Tomeczek"
                selectedPlayer = selectedPlayerName;
                showLobby(primaryStage, selectedPlayerName);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Błąd");
                alert.setHeaderText(null);
                alert.setContentText("Wybierz gracza przed kontynuowaniem!");
                alert.showAndWait();
            }
        });

        VBox selectionLayout = new VBox(10, new Text("Wybierz Gracza:"), playerListView, selectButton);
        selectionLayout.setAlignment(Pos.CENTER);
        selectionLayout.setStyle("-fx-background-color: black;");

        Scene selectionScene = new Scene(selectionLayout, 400, 400);
        primaryStage.setScene(selectionScene);
    }

    private void showLobby(Stage primaryStage, String selectedPlayer) {
        // Etykiety dla graczy
        whiteLabel = new Label("Kolor Biały: Brak");
        whiteLabel.setTextFill(Color.WHITE);

        blackLabel = new Label("Kolor Czarny: Brak");
        blackLabel.setTextFill(Color.WHITE);

        // Pole tekstowe dla wiadomości w poczekalni
        lobbyMessages = new TextArea();
        lobbyMessages.setEditable(false);
        lobbyMessages.setPrefHeight(200);

        // Przyciski dołączania do gry
        Button joinWhiteButton = createStyledButton("Dołącz jako Biały");
        Button joinBlackButton = createStyledButton("Dołącz jako Czarny");
        startGameButton = createStyledButton("Rozpocznij Grę");
        startGameButton.setDisable(true);

        // Usuwamy ewentualny dopisek ", Kolor" z selectedPlayer (jeśli ktoś to wpisał w pliku)
        String pureName = selectedPlayer.replace(", Kolor", "").trim();

        joinWhiteButton.setOnAction(event -> chessClient.sendToServer("WHITE:" + pureName));
        joinBlackButton.setOnAction(event -> chessClient.sendToServer("BLACK:" + pureName));
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

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", 20));
        button.setStyle("-fx-background-color: black; -fx-text-fill: yellow;");
        button.setOnMouseEntered(event -> button.setStyle("-fx-background-color: yellow; -fx-text-fill: black;"));
        button.setOnMouseExited(event -> button.setStyle("-fx-background-color: black; -fx-text-fill: yellow;"));
        return button;
    }
}
