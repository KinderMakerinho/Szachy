import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Board extends Application {
    private static final int ROZMIAR_POLA = 100;  // Rozmiar pojedynczego pola szachownicy
    private static final int SZEROKOSC_SZACHOWNICY = ROZMIAR_POLA * 8; // 8 pól w poziomie
    private static final int WYSOKOSC_SZACHOWNICY = ROZMIAR_POLA * 8; // 8 pól w pionie
    private Pawn selectedPawn = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        GridPane szachownica = new GridPane();
        szachownica.setPrefSize(SZEROKOSC_SZACHOWNICY, WYSOKOSC_SZACHOWNICY);  // Ustawienie stałej szerokości i wysokości szachownicy

        // Dodajemy pola szachownicy i numerację
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Rectangle tile = new Rectangle(ROZMIAR_POLA, ROZMIAR_POLA); // Używamy stałego rozmiaru
                if ((i + j) % 2 == 0) {
                    tile.setFill(Color.WHITE);
                } else {
                    tile.setFill(Color.BROWN);
                }

                StackPane stack = new StackPane();
                stack.getChildren().add(tile);

                int finalI = i;
                int finalJ = j;
                stack.setOnMouseClicked(event -> movePawn(stack, finalI, finalJ));

                szachownica.add(stack, j, i); // Dodajemy na szachownicę
            }

            // Dodanie numeracji wierszy
            Label rowLabel = new Label(String.valueOf(8 - i));
            StackPane rowStack = new StackPane(rowLabel);
            rowStack.setPrefSize(ROZMIAR_POLA, ROZMIAR_POLA);
            szachownica.add(rowStack, 0, i);
        }

        // Dodanie białych pionków na linię A2-H2 (wiersz 6)
        for (int col = 0; col < 8; col++) {
            addPawn(szachownica, 6, col, "file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/pngegg.png", false);
        }

        // Dodanie czarnych pionków na linię A6-H6 (wiersz 1)
        for (int col = 0; col < 8; col++) {
            addPawn(szachownica, 1, col, "file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/czarnypionek.png", true);
        }

        Scene scene = new Scene(szachownica, SZEROKOSC_SZACHOWNICY + ROZMIAR_POLA, WYSOKOSC_SZACHOWNICY); // Ustawiamy stałą szerokość i wysokość okna
        primaryStage.setTitle("Szachy");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addPawn(GridPane board, int row, int col, String imagePath, boolean isBlack) {
        Pawn pawn = new Pawn(imagePath, row, col, isBlack);
        StackPane stack = (StackPane) getNodeFromGridPane(board, col, row);
        stack.getChildren().add(pawn.getImageView());

        pawn.getImageView().setOnMouseClicked(event -> selectPawn(pawn));
    }

    private void selectPawn(Pawn pawn) {
        selectedPawn = pawn;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Wybrano pionek");
        alert.setHeaderText(null);
        alert.setContentText("Pionek został wybrany. Kliknij na pole, aby wykonać ruch.");
        alert.showAndWait();
    }

    private void movePawn(StackPane stack, int row, int column) {
        if (selectedPawn != null) {
            // Sprawdzanie ruchu zgodnie z zasadami
            int currentRow = selectedPawn.getCurrentRow();
            int currentColumn = selectedPawn.getCurrentColumn();

            // Pionek może poruszać się tylko w swojej kolumnie
            if (currentColumn != column) {
                System.out.println("Nieprawidłowy ruch: pionek może poruszać się tylko w swojej kolumnie.");
                return;
            }

            // Pierwszy ruch: dozwolony o 1 lub 2 pola
            if (selectedPawn.isFirstMove()) {
                if (Math.abs(row - currentRow) == 1 || Math.abs(row - currentRow) == 2) {
                    executeMove(stack, row, column);
                } else {
                    System.out.println("Nieprawidłowy ruch: pierwszy ruch dozwolony o 1 lub 2 pola.");
                }
            }
            // Kolejne ruchy: dozwolony o 1 pole
            else if (Math.abs(row - currentRow) == 1) {
                executeMove(stack, row, column);
            } else {
                System.out.println("Nieprawidłowy ruch: dozwolony ruch o 1 pole.");
            }
        }
    }

    private void executeMove(StackPane stack, int row, int column) {
        // Usuń pionka z bieżącego pola
        StackPane currentStack = (StackPane) selectedPawn.getImageView().getParent();
        if (currentStack != null) {
            currentStack.getChildren().remove(selectedPawn.getImageView());
        }

        // Przenieś pionka na nowe pole
        stack.getChildren().add(selectedPawn.getImageView());
        selectedPawn.setCurrentRow(row);
        selectedPawn.setCurrentColumn(column);

        System.out.println("Pionek przeniesiony na wiersz " + (8 - row) + ", kolumna " + (char) ('A' + column));

        // Ustaw flagę dla ruchu
        selectedPawn.setFirstMove(false);
        selectedPawn = null; // Reset zaznaczenia
    }

    private Node getNodeFromGridPane(GridPane grid, int col, int row) {
        for (Node node : grid.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }
}
