import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Board extends Application {
    private static final int TILE_SIZE = 100; // Rozmiar jednego pola


    public void start(Stage primaryStage) {
        GridPane gridPane = new GridPane(); // Utworzenie siatki

        // Ustawienie pól szachownicy
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Rectangle tile = new Rectangle(TILE_SIZE, TILE_SIZE); // Utworzenie prostokąta jako pola
                if ((i + j) % 2 == 0) {
                    tile.setFill(Color.BEIGE); // Kolor jasny
                } else {
                    tile.setFill(Color.BROWN); // Kolor ciemny
                }
                gridPane.add(tile, j, i); // Dodanie pola do siatki
            }
        }

        Scene scene = new Scene(gridPane, TILE_SIZE * 8, TILE_SIZE * 8); // Utworzenie sceny
        primaryStage.setTitle("Szaszki"); // Tytuł okna
        primaryStage.setScene(scene); // Ustawienie sceny
        primaryStage.show(); // Wyświetlenie okna
    }

    public static void main(String[] args) {
        launch(args); // Uruchomienie aplikacji
    }
}
