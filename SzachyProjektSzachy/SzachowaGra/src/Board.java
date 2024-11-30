import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javafx.scene.image.ImageView;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//male zmiany

public class Board extends Application {
    private static final int ROZMIAR_POLA = 100;
    private static final int SZEROKOSC_SZACHOWNICY = ROZMIAR_POLA * 8;
    private static final int WYSOKOSC_SZACHOWNICY = ROZMIAR_POLA * 8;
    private Pawn selectedPawn = null;
    private Knight selectedKnight = null;

    private GRACZE gracz1;
    private GRACZE gracz2;

    public static void main(String[] args) {

        launch(args);
    }


    public static void launch(String[] args, GRACZE gracz1, GRACZE gracz2) {
        Application.launch(Board.class, args);
    }

    @Override
    public void start(Stage primaryStage) {

        this.gracz1 = gracz1;
        this.gracz2 = gracz2;


        GridPane szachownica = new GridPane();
        szachownica.setPrefSize(SZEROKOSC_SZACHOWNICY, WYSOKOSC_SZACHOWNICY);


        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Rectangle tile = new Rectangle(ROZMIAR_POLA, ROZMIAR_POLA);
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

                szachownica.add(stack, j, i);
            }


            Label rowLabel = new Label(String.valueOf(8 - i));
            StackPane rowStack = new StackPane(rowLabel);
            rowStack.setPrefSize(ROZMIAR_POLA, ROZMIAR_POLA);
            szachownica.add(rowStack, 0, i);
        }


        for (int col = 0; col < 8; col++) {
            addPawn(szachownica, 6, col, "file:/C:/Users/paveb/Desktop/PO1 PROJEKT/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/bialypionek.png", false);
            addPawn(szachownica, 1, col, "file:/C:/Users/paveb/Desktop/PO1 PROJEKT/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/czarnypionekk.png", true);
        }
        addKnight(szachownica, 0, 1, "file:/C:/Users/paveb/Desktop/PO1 PROJEKT/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/czarnyskoczek.png", true);
        addKnight(szachownica, 0, 6, "file:/C:/Users/paveb/Desktop/PO1 PROJEKT/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/czarnyskoczek.png", true);
        addKnight(szachownica, 7, 1, "file:/C:/Users/paveb/Desktop/PO1 PROJEKT/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/bialyskoczek.png", false);
        addKnight(szachownica, 7, 6, "file:/C:/Users/paveb/Desktop/PO1 PROJEKT/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/bialyskoczek.png", false);


        Scene scene = new Scene(szachownica, SZEROKOSC_SZACHOWNICY + ROZMIAR_POLA, WYSOKOSC_SZACHOWNICY);
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
            int currentRow = selectedPawn.getCurrentRow();
            int currentColumn = selectedPawn.getCurrentColumn();

            if (currentColumn != column) {
                System.out.println("Nieprawidłowy ruch: pionek może poruszać się tylko w swojej kolumnie.");
                return;
            }

            if (selectedPawn.isFirstMove()) {
                if (Math.abs(row - currentRow) == 1 || Math.abs(row - currentRow) == 2) {
                    executeMove(stack, row, column);
                } else {
                    System.out.println("Nieprawidłowy ruch: pierwszy ruch dozwolony o 1 lub 2 pola.");
                }
            } else if (Math.abs(row - currentRow) == 1) {
                executeMove(stack, row, column);
            } else {
                System.out.println("Nieprawidłowy ruch: dozwolony ruch o 1 pole.");
            }
        }
    }

    private void addKnight(GridPane board, int row, int col, String imagePath, boolean isBlack) {
        Knight knight = new Knight(imagePath, row, col, isBlack);
        StackPane stack = (StackPane) getNodeFromGridPane(board, col, row);
        stack.getChildren().add(knight.getImageView());
        knight.getImageView().setOnMouseClicked(event -> selectKnight(knight));
    }
    private void selectKnight(Knight knight) {
        selectedKnight = knight; // Zapisujemy wybranego skoczka
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Wybrano skoczka");
        alert.setHeaderText(null);
        alert.setContentText("Skoczek został wybrany. Kliknij na pole, aby wykonać ruch.");
        alert.showAndWait();
    }

    private void executeMove(StackPane stack, int row, int column) {
        if (selectedPawn != null) {

            StackPane currentStack = (StackPane) selectedPawn.getImageView().getParent();
            if (currentStack != null) {
                currentStack.getChildren().remove(selectedPawn.getImageView());
            }


            stack.getChildren().add(selectedPawn.getImageView());
            selectedPawn.setCurrentRow(row);
            selectedPawn.setCurrentColumn(column);
            selectedPawn.setFirstMove(false);


            selectedPawn = null;


            saveGamePositionsToFile();
        }
    }



    private void saveGamePositionsToFile() {

        if (selectedPawn == null) {
            return;
        }

        File gameFile = new File("file:/C:/Users/paveb/Desktop/PO1 PROJEKT/Szachy/SzachyProjektSzachy/SzachowaGra/src/zapisywaniedancyhgame_positions.txt");
        try (FileWriter writer = new FileWriter(gameFile, true)) {
            writer.write("Pozycje pionków po grze:\n");


            for (Node node : ((GridPane) selectedPawn.getImageView().getParent()).getChildren()) {
                if (node instanceof StackPane) {
                    StackPane stack = (StackPane) node;
                    for (Node child : stack.getChildren()) {
                        if (child instanceof ImageView) {
                            int row = GridPane.getRowIndex(stack);
                            int col = GridPane.getColumnIndex(stack);
                            writer.write("Pionek na pozycji: " + row + ", " + col + "\n");
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Błąd podczas zapisywania pozycji do pliku.");
        }
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