import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Tworzenie napisu "Szachy"
        Text title = new Text("Szachy");
        title.setFont(Font.font("Arial", 50));
        title.setFill(Color.YELLOW);

        // Tworzenie przycisku "Start"
        Button startButton = createStyledButton("Start");
        startButton.setOnAction(event -> {
            // Po kliknięciu przechodzimy do ekranu wyboru gracza w tym samym oknie
            showPlayerSelectionScreen(primaryStage);
        });

        // Tworzenie przycisku "Dodaj Gracza"
        Button addPlayerButton = createStyledButton("Dodaj Gracza");
        addPlayerButton.setOnAction(event -> openAddPlayerWindow());

        // Tworzenie przycisku "Wyjście"
        Button exitButton = createStyledButton("Wyjście");
        exitButton.setOnAction(event -> {
            System.exit(0);
        });

        // Układ pionowy (VBox) dla napisu i przycisków
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(title, startButton, addPlayerButton, exitButton);
        layout.setStyle("-fx-background-color: black;");

        // Tworzenie sceny
        Scene scene = new Scene(layout, 400, 600);

        primaryStage.setTitle("Szachy - Menu Główne");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openAddPlayerWindow() {
        Stage addPlayerStage = new Stage();
        addPlayerStage.setTitle("Dodaj Gracza");

        // Tworzymy tekst "Podaj nazwę gracza:"
        Text promptText = new Text("Podaj nazwę gracza:");
        promptText.setFont(Font.font("Arial", 20));
        promptText.setFill(Color.YELLOW);

        // Tworzymy pole tekstowe do wprowadzania danych gracza
        TextField nameField = new TextField();
        nameField.setPromptText("Imię gracza");

        // Tworzymy przycisk "Zapisz"
        Button saveButton = createStyledButton("Zapisz");
        saveButton.setOnAction(event -> {
            String name = nameField.getText();

            if (name.isEmpty()) {
                nameField.setPromptText("Wypełnij pole!");
                return;
            }

            // Tworzenie nowego gracza z domyślną liczbą punktów = 0
            GRACZE newPlayer = new GRACZE(name);
            gracze.add(newPlayer);
            newPlayer.saveToFile();
            addPlayerStage.close(); // Zamknięcie okna po zapisaniu
        });

        // Układ pionowy (VBox) dla formularza
        VBox formLayout = new VBox(10);
        formLayout.setAlignment(Pos.CENTER);
        formLayout.getChildren().addAll(
                promptText,   // Dodajemy tekst "Podaj nazwę gracza:"
                nameField,    // Pole do wprowadzenia imienia
                saveButton    // Przycisk zapisz
        );
        formLayout.setStyle("-fx-background-color: black; -fx-padding: 20;");

        // Tworzenie sceny
        Scene addPlayerScene = new Scene(formLayout, 300, 300);
        addPlayerStage.setScene(addPlayerScene);
        addPlayerStage.show();
    }


    private void showPlayerSelectionScreen(Stage primaryStage) {
        // Odczyt zapisanych graczy z pliku
        loadPlayersFromFile();

        // Tworzymy listę graczy
        ListView<String> playerListView = new ListView<>();
        for (GRACZE player : gracze) {
            playerListView.getItems().add(player.getImie());
        }

        // Tworzymy przycisk "Wybierz"
        Button selectButton = createStyledButton("Wybierz");
        selectButton.setOnAction(event -> {
            // Po kliknięciu wybieramy gracza
            String selectedPlayer = playerListView.getSelectionModel().getSelectedItem();
            if (selectedPlayer != null) {
                GRACZE selectedGracz = findPlayerByName(selectedPlayer);
                if (selectedGracz != null) {
                    // Przypisz kolor (np. "Biały" lub "Czarny")
                    selectedGracz.setKolor("Biały");  // Możesz dodać logikę do wyboru koloru
                    System.out.println("Wybrano gracza: " + selectedGracz.getImie() + ", Kolor: " + selectedGracz.getKolor());
                    // Można otworzyć kolejne okno z planszą do gry (Board)
                }
            }
        });

        // Układ pionowy (VBox) dla listy graczy i przycisku
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(new Text("Wybierz gracza:"), playerListView, selectButton);
        layout.setStyle("-fx-background-color: black;");

        // Tworzymy scenę i ustawiamy ją na głównym oknie
        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);
    }

    private GRACZE findPlayerByName(String name) {
        for (GRACZE player : gracze) {
            if (player.getImie().equals(name)) {
                return player;
            }
        }
        return null;
    }

    private void loadPlayersFromFile() {
        // Ładowanie graczy z pliku
        try {
            gracze.clear();
            File file = new File("gracze.txt");
            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.startsWith("Imię: ")) {
                        String name = line.split(": ")[1];  // Pobieramy imię
                        GRACZE player = new GRACZE(name);
                        gracze.add(player);  // Dodajemy do listy
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
        button.setFont(Font.font("Arial", 30));
        button.setStyle("-fx-background-color: black; -fx-text-fill: yellow; -fx-border-color: yellow; -fx-border-width: 2px;");
        button.setOnMouseEntered(event -> button.setStyle("-fx-background-color: yellow; -fx-text-fill: black;"));
        button.setOnMouseExited(event -> button.setStyle("-fx-background-color: black; -fx-text-fill: yellow;"));
        return button;
    }
}
