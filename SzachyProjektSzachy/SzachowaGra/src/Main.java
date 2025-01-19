import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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
    private GRACZE whitePlayer = null;
    private GRACZE blackPlayer = null;

    public static void main(String[] args) {
        // Uruchamiamy serwer w osobnym wątku
        new Thread(() -> {
            ChessServer.main(new String[]{}); // Uruchomienie serwera w osobnym wątku
        }).start();
        launch(args); // Uruchomienie aplikacji GUI
    }

    @Override
    public void start(Stage primaryStage) {
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
        Label whiteLabel = new Label("Kolor Biały: Brak");
        whiteLabel.setTextFill(Color.WHITE);
        Button joinWhiteButton = createStyledButton("Dołącz jako Biały");
        joinWhiteButton.setOnAction(event -> {
            whitePlayer = gracze.stream().filter(p -> p.getImie().equals(selectedPlayer)).findFirst().orElse(null);
            whiteLabel.setText("Kolor Biały: " + whitePlayer.getImie());
            joinWhiteButton.setDisable(true);
        });

        Label blackLabel = new Label("Kolor Czarny: Brak");
        blackLabel.setTextFill(Color.WHITE);
        Button joinBlackButton = createStyledButton("Dołącz jako Czarny");
        joinBlackButton.setOnAction(event -> {
            blackPlayer = gracze.stream().filter(p -> p.getImie().equals(selectedPlayer)).findFirst().orElse(null);
            blackLabel.setText("Kolor Czarny: " + blackPlayer.getImie());
            joinBlackButton.setDisable(true);
        });

        Button startGameButton = createStyledButton("Rozpocznij Grę");
        startGameButton.setOnAction(event -> {
            if (whitePlayer != null && blackPlayer != null) {
                new Board().start(new Stage());
                primaryStage.close();
            }
        });

        VBox whiteBox = new VBox(10, whiteLabel, joinWhiteButton);
        whiteBox.setAlignment(Pos.CENTER);

        VBox blackBox = new VBox(10, blackLabel, joinBlackButton);
        blackBox.setAlignment(Pos.CENTER);

        VBox startBox = new VBox(10, startGameButton);
        startBox.setAlignment(Pos.CENTER);

        HBox lobbyLayout = new HBox(50, whiteBox, startBox, blackBox);
        lobbyLayout.setAlignment(Pos.CENTER);
        lobbyLayout.setStyle("-fx-background-color: black;");

        Scene lobbyScene = new Scene(lobbyLayout, 600, 400);
        primaryStage.setScene(lobbyScene);
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

        VBox formLayout = new VBox(10, nameField, saveButton);
        formLayout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(formLayout, 300, 200);
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
