import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Board extends Application {
    private static final int ROZMIAR_POLA = 100; // Rozmiar jednego pola

    public void start(Stage primaryStage) {
        GridPane szachownica = new GridPane(); // Utworzenie siatki

        // Ustawienie pól szachownicy z numeracją
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Rectangle tile = new Rectangle(ROZMIAR_POLA, ROZMIAR_POLA); // Utworzenie prostokąta jako pola
                if ((i + j) % 2 == 0) {
                    tile.setFill(Color.WHITE); // Kolor jasny
                } else {
                    tile.setFill(Color.BROWN); // Kolor ciemny
                }

                // Tworzenie etykiety na numerację w lewym dolnym rogu
                Label label = new Label();
                label.setStyle("-fx-font-size: 18;"); // Ustawienie rozmiaru czcionki
                StackPane.setAlignment(label, Pos.BOTTOM_LEFT); // Wyrównanie etykiety do lewego dolnego rogu

                // Dodanie liter w dolnym wierszu (rząd 7 w szachownicy)
                if (i == 7) {
                    label.setText(String.valueOf((char) ('A' + j))); // Litery A do H
                }

                // Dodanie numerów w lewej kolumnie (kolumna 0 w szachownicy)
                if (j == 0) {
                    label.setText(String.valueOf(8 - i)); // Cyfry 1 do 8
                }

                // Tworzenie kontenera dla pola i etykiety
                StackPane stack = new StackPane();
                stack.getChildren().addAll(tile, label); // Dodanie pola i etykiety razem
                szachownica.add(stack, j, i); // Dodanie pola z etykietą do siatki
            }
        }

        Scene scene = new Scene(szachownica, ROZMIAR_POLA * 8, ROZMIAR_POLA * 8); // Utworzenie sceny
        primaryStage.setTitle("Szachy"); // Tytuł okna
        primaryStage.setScene(scene); // Ustawienie sceny
        primaryStage.show(); // Wyświetlenie okna
    }

    public static void main(String[] args) {
        launch(args); // Uruchomienie aplikacji
    }
}
