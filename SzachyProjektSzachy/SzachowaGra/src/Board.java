import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Board extends Application {
    private static final int ROZMIAR_POLA = 100;
    private static final int SZEROKOSC_SZACHOWNICY = ROZMIAR_POLA * 8;
    private static final int WYSOKOSC_SZACHOWNICY = ROZMIAR_POLA * 8;
    private Pawn selectedPawn = null;
    private Knight selectedKnight = null;
    private Bishop selectedBishop = null;
    private Rook selectedRook = null;
    private Queen selectedQueen = null;
    private King selectedKing = null;
    private GridPane szachownica;
    private Label turnIndicatorLabel;
    private ChessPiece[][] boardPieces = new ChessPiece[8][8];
    private boolean isWhiteTurn = true; // True, jeśli tura białych, false dla czarnych
    private ChessPiece selectedPiece = null;
    private boolean moveMade = false;  // Flaga informująca, czy ruch został wykonany
    private int numerRuchu = 1;
    private String playerColor; // Kolor gracza: "WHITE" lub "BLACK"

    private boolean isProcessingServerMove = false;

    // Fałsz oznacza ruch białych, prawda oznacza ruch czarnych
    private ChessClient chessClient;


    public Board(ChessClient chessClient, String playerColor) {
        this.chessClient = chessClient;
        this.playerColor = playerColor;

        if (playerColor == null) {
            System.err.println("Kolor gracza nie został ustawiony! Gra może działać niepoprawnie.");
        }
    }

    private static GRACZE gracz1;
    private static GRACZE gracz2;

    // Metoda do ustawienia graczy
    public static void initializePlayers(GRACZE g1, GRACZE g2) {
        gracz1 = g1;
        gracz2 = g2;
    }

    @Override
    public void start(Stage primaryStage) {
        // Tworzenie szachownicy
        szachownica = new GridPane();
        szachownica.setPrefSize(SZEROKOSC_SZACHOWNICY, WYSOKOSC_SZACHOWNICY);
        turnIndicatorLabel = new Label("Tura: Białe");

        // Generowanie pól szachownicy
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

                // Obsługa kliknięcia na kafelek
                stack.setOnMouseClicked(event -> {
                    // Pobierz figurę z tablicy boardPieces
                    ChessPiece piece = boardPieces[finalI][finalJ];

                    if (selectedPiece == null) {
                        // Jeśli nie ma zaznaczonej figury, ustaw zaznaczoną figurę
                        if (piece != null && ((isWhiteTurn && !piece.isBlack()) || (!isWhiteTurn && piece.isBlack()))) {
                            selectPiece(piece); // Zaznacz figurę
                        } else {
                            System.out.println("❌ Nie możesz zaznaczyć tego pola.");
                        }
                    } else {
                        // Jeśli figura jest zaznaczona, spróbuj wykonać ruch
                        movePiece(selectedPiece, stack, finalI, finalJ);
                    }
                });

                szachownica.add(stack, j, i); // Dodajemy do odpowiednich komórek w szachownicy
            }
        }
        // Dodanie pionków
        for (int col = 0; col < 8; col++) {
            // Dodanie białych pionków na 6. wierszu
            addPawn(szachownica, 6, col, "file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/bialypionek.png", false);
            boardPieces[6][col] = new Pawn("file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/bialypionek.png", 6, col, false, boardPieces);

            // Dodanie czarnych pionków na 1. wierszu
            addPawn(szachownica, 1, col, "file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/czarnypionekk.png", true);
            boardPieces[1][col] = new Pawn("file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/czarnypionekk.png", 1, col, true, boardPieces);
        }
// Dodanie skoczków
        addKnight(szachownica, 7, 1, "file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/bialyskoczek.png", false);
        boardPieces[7][1] = new Knight("file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/bialyskoczek.png", 7, 1, false);

        addKnight(szachownica, 7, 6, "file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/bialyskoczek.png", false);
        boardPieces[7][6] = new Knight("file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/bialyskoczek.png", 7, 6, false);

        addKnight(szachownica, 0, 1, "file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/czarnyskoczek.png", true);
        boardPieces[0][1] = new Knight("file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/czarnyskoczek.png", 0, 1, true);

        addKnight(szachownica, 0, 6, "file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/czarnyskoczek.png", true);
        boardPieces[0][6] = new Knight("file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/czarnyskoczek.png", 0, 6, true);

// Dodanie gonców
        addBishop(szachownica, 7, 2, "file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/bialygoniec.png", false);
        boardPieces[7][2] = new Bishop("file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/bialygoniec.png", 7, 2, false);

        addBishop(szachownica, 7, 5, "file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/bialygoniec.png", false);
        boardPieces[7][5] = new Bishop("file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/bialygoniec.png", 7, 5, false);

        addBishop(szachownica, 0, 2, "file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/czarnygoniec.png", true);
        boardPieces[0][2] = new Bishop("file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/czarnygoniec.png", 0, 2, true);

        addBishop(szachownica, 0, 5, "file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/czarnygoniec.png", true);
        boardPieces[0][5] = new Bishop("file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/czarnygoniec.png", 0, 5, true);

// Dodanie wież
        addRook(szachownica, 7, 0, "file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/bialawieza.png", false);
        boardPieces[7][0] = new Rook("file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/bialawieza.png", 7, 0, false);

        addRook(szachownica, 7, 7, "file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/bialawieza.png", false);
        boardPieces[7][7] = new Rook("file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/bialawieza.png", 7, 7, false);

        addRook(szachownica, 0, 0, "file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/czarnawieza.png", true);
        boardPieces[0][0] = new Rook("file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/czarnawieza.png", 0, 0, true);

        addRook(szachownica, 0, 7, "file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/czarnawieza.png", true);
        boardPieces[0][7] = new Rook("file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/czarnawieza.png", 0, 7, true);

// Dodanie hetmanów
        addQueen(szachownica, 7, 3, "file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/bialyhetman.png", false);
        boardPieces[7][3] = new Queen("file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/bialyhetman.png", 7, 3, false);

        addQueen(szachownica, 0, 3, "file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/czarnyhetman.png", true);
        boardPieces[0][3] = new Queen("file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/czarnyhetman.png", 0, 3, true);

// Dodanie królów
        addKing(szachownica, 7, 4, "file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/bialykrol.png", false);
        boardPieces[7][4] = new King("file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/bialykrol.png", 7, 4, false);

        addKing(szachownica, 0, 4, "file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/czarnykrol.png", true);
        boardPieces[0][4] = new King("file:/C:/Users/karol/Desktop/Szachy/SzachyProjektSzachy/SzachowaGra/src/Grafiki/czarnykrol.png", 0, 4, true);

        // Utworzenie sceny
        Scene scene = new Scene(szachownica, SZEROKOSC_SZACHOWNICY + ROZMIAR_POLA, WYSOKOSC_SZACHOWNICY + ROZMIAR_POLA);
        primaryStage.setTitle("Szachy");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public boolean isTileEmptyOrOpponentPiece(int row, int col, boolean isBlack) {
        ChessPiece piece = getPiece(row, col); // Pobranie figury na danym polu
        if (piece == null) {
            return true; // Jeśli pole jest puste
        } else {
            // Jeśli figura na polu jest przeciwnika, zwrócimy true
            return piece.isBlack() != isBlack;
        }
    }
    public void setTurn(boolean isMyTurn) {
        this.isWhiteTurn = isMyTurn;
        System.out.println("🔄 Zmieniono turę. Czy to moja tura? " + isMyTurn);
        turnIndicatorLabel.setText("Tura: " + (isMyTurn ? "Twoja" : "Przeciwnika"));
    }



    private void addKnight(GridPane board, int row, int col, String imagePath, boolean isBlack) {
        Knight knight = new Knight(imagePath, row, col, isBlack);
        StackPane stack = (StackPane) getNodeFromGridPane(board, col, row);
        ImageView imageView = knight.getImageView();  // Pobieramy ImageView skoczka
        imageView.setUserData(knight);  // Ustawiamy UserData na obiekt Skoczek
        stack.getChildren().add(imageView);  // Dodajemy ImageView do StackPane
        imageView.setOnMouseClicked(event -> selectPiece(knight));  // Obsługuje kliknięcie skoczka
    }

    private void addBishop(GridPane board, int row, int col, String imagePath, boolean isBlack) {
        Bishop bishop = new Bishop(imagePath, row, col, isBlack);
        StackPane stack = (StackPane) getNodeFromGridPane(board, col, row);
        ImageView imageView = bishop.getImageView();  // Pobieramy ImageView gońca
        imageView.setUserData(bishop);  // Ustawiamy UserData na obiekt Goniec
        stack.getChildren().add(imageView);  // Dodajemy ImageView do StackPane
        imageView.setOnMouseClicked(event -> selectPiece(bishop));  // Obsługuje kliknięcie gońca
    }

    private void addPawn(GridPane board, int row, int col, String imagePath, boolean isBlack) {
        Pawn pawn = new Pawn(imagePath, row, col, isBlack, boardPieces);
        StackPane stack = (StackPane) getNodeFromGridPane(board, col, row);
        ImageView imageView = pawn.getImageView();  // Pobieramy ImageView piona
        imageView.setUserData(pawn);  // Ustawiamy UserData na obiekt Pion
        stack.getChildren().add(imageView);  // Dodajemy ImageView do StackPane
        imageView.setOnMouseClicked(event -> selectPiece(pawn));  // Obsługuje kliknięcie piona
    }

    private void addRook(GridPane board, int row, int col, String imagePath, boolean isBlack) {
        Rook rook = new Rook(imagePath, row, col, isBlack);
        StackPane stack = (StackPane) getNodeFromGridPane(board, col, row);
        ImageView imageView = rook.getImageView();  // Pobieramy ImageView wieży
        imageView.setUserData(rook);  // Ustawiamy UserData na obiekt Wieża
        stack.getChildren().add(imageView);  // Dodajemy ImageView do StackPane
        imageView.setOnMouseClicked(event -> selectPiece(rook));  // Obsługuje kliknięcie wieży
    }

    private void addQueen(GridPane board, int row, int col, String imagePath, boolean isBlack) {
        Queen queen = new Queen(imagePath, row, col, isBlack);
        StackPane stack = (StackPane) getNodeFromGridPane(board, col, row);
        ImageView imageView = queen.getImageView();  // Pobieramy ImageView królowej
        imageView.setUserData(queen);  // Ustawiamy UserData na obiekt Królowa
        stack.getChildren().add(imageView);  // Dodajemy ImageView do StackPane
        imageView.setOnMouseClicked(event -> selectPiece(queen));  // Obsługuje kliknięcie królowej
    }
    private void addKing(GridPane board, int row, int col, String imagePath, boolean isBlack) {
        King king = new King(imagePath, row, col, isBlack);
        StackPane stack = (StackPane) getNodeFromGridPane(board, col, row);
        ImageView imageView = king.getImageView();  // Pobieramy ImageView króla
        imageView.setUserData(king);  // Ustawiamy UserData na obiekt Król
        stack.getChildren().add(imageView);  // Dodajemy ImageView do StackPane
        imageView.setOnMouseClicked(event -> selectPiece(king));  // Obsługuje kliknięcie króla
    }
    private void selectPiece(ChessPiece piece) {
        // Jeśli już mamy wybraną figurę i klikamy ponownie na tę samą, odznaczamy ją
        if (piece == selectedPiece) {
            resetSelection();  // Resetujemy zaznaczenie, jeśli kliknięto tę samą figurę
            return;  // Kończymy metodę, nie wykonujemy dalszych działań
        }

        // Sprawdzamy, czy wybrana figura należy do aktualnego gracza
        if (isWhiteTurn && piece.isBlack()) {
            System.out.println("To nie Twoja tura! Wybierz białą figurę.");
            return;  // Blokujemy wybór, jeśli nie jest to biała figura podczas tury białych
        }
        if (!isWhiteTurn && !piece.isBlack()) {
            System.out.println("To nie Twoja tura! Wybierz czarną figurę.");
            return;  // Blokujemy wybór, jeśli nie jest to czarna figura podczas tury czarnych
        }

        // Przypisanie wybranej figury do zmiennych
        selectedPiece = piece;  // Ustawiamy wybraną figurę

        // Obsługuje różne typy figur i przypisuje odpowiednią zmienną
        if (piece instanceof Pawn) {
            selectedPawn = (Pawn) piece;
            showSelectionAlert("Wybrano pionek");
        } else if (piece instanceof Knight) {
            selectedKnight = (Knight) piece;
            showSelectionAlert("Wybrano skoczka");
        } else if (piece instanceof Bishop) {
            selectedBishop = (Bishop) piece;
            showSelectionAlert("Wybrano gońca");
        } else if (piece instanceof Rook) {
            selectedRook = (Rook) piece;
            showSelectionAlert("Wybrano wieżę");
        } else if (piece instanceof Queen) {
            selectedQueen = (Queen) piece;
            showSelectionAlert("Wybrano hetmana");
        } else if (piece instanceof King) {
            selectedKing = (King) piece;
            showSelectionAlert("Wybrano króla");
        }

        // Resetujemy flagę, ponieważ użytkownik jeszcze nie wykonał ruchu
        moveMade = false;
    }
    private void resetSelection() {
        // Resetujemy wszystkie zmienne związane z zaznaczoną figurą
        selectedPiece = null;
        selectedPawn = null;
        selectedKnight = null;
        selectedBishop = null;
        selectedRook = null;
        selectedQueen = null;
        selectedKing = null;
    }
    private void showSelectionAlert(String message) {
        // Pokazuje alert, ale nie zmienia tury
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(message);
        alert.setHeaderText(null);
        alert.setContentText(message + ". Kliknij na pole, aby wykonać ruch.");

        // Po kliknięciu ok, pozwalamy graczowi wykonać ruch
        alert.showAndWait();
    }
    private void notifyServerAboutMove(int srcRow, int srcCol, int destRow, int destCol) {
        if (chessClient != null && !isProcessingServerMove) { // Upewniamy się, że ruch nie pochodzi od serwera
            String move = srcRow + "," + srcCol + "->" + destRow + "," + destCol;
            chessClient.sendToServer("MOVE:" + move);
            System.out.println("📤 Powiadomiono serwer o ruchu: " + move);
        }
    }

    public void executeMove(String moveMessage) {
        try {
            String[] parts = moveMessage.split(":");
            if (parts.length != 3) {
                System.err.println("❌ Nieprawidłowy format wiadomości ruchu: " + moveMessage);
                return;
            }

            String color = parts[1];
            String moveDetails = parts[2];

            String[] coords = moveDetails.split("->");
            String[] srcCoords = coords[0].split(",");
            String[] destCoords = coords[1].split(",");

            int srcRow = Integer.parseInt(srcCoords[0]);
            int srcCol = Integer.parseInt(srcCoords[1]);
            int destRow = Integer.parseInt(destCoords[0]);
            int destCol = Integer.parseInt(destCoords[1]);

            ChessPiece piece = getPiece(srcRow, srcCol);
            if (piece == null) {
                System.err.println("❌ Błąd: Brak figury na pozycji startowej (" + srcRow + "," + srcCol + ").");
                return;
            }

            StackPane targetTile = (StackPane) getNodeFromGridPane(szachownica, destCol, destRow);
            if (targetTile == null) {
                System.err.println("❌ Błąd: Nie znaleziono docelowego pola (" + destRow + "," + destCol + ").");
                return;
            }

            movePiece(piece, targetTile, destRow, destCol);
            updateBoardPieces(piece, srcRow, srcCol, destRow, destCol);
            System.out.println("✅ Wykonano ruch przeciwnika: " + moveDetails);
        } catch (Exception e) {
            System.err.println("❌ Błąd podczas wykonywania ruchu: " + moveMessage);
            e.printStackTrace();
        }
    }

    private Node getNodeFromGridPane(GridPane grid, int col, int row) {
        for (Node node : grid.getChildren()) {
            Integer columnIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);
            if (columnIndex != null && rowIndex != null && columnIndex == col && rowIndex == row) {
                return node;
            }
        }
        System.out.println("Nie znaleziono kafelka w wierszu: " + row + ", kolumnie: " + col);
        return null;
    }
    private void movePiece(ChessPiece piece, StackPane targetTile, int row, int col) {
        if (piece == null) {
            System.out.println("❌ Nie wybrano figury do ruchu.");
            return;
        }

        // Sprawdzenie, czy gracz nie próbuje ruszyć figurą przeciwnika
        if ((isWhiteTurn && piece.isBlack()) || (!isWhiteTurn && !piece.isBlack())) {
            System.out.println("❌ To nie Twoja figura! Wybierz swoją figurę.");
            return;
        }

        boolean moveSuccessful = false;
        int srcRow = piece.getCurrentRow();
        int srcCol = piece.getCurrentColumn();

        // 🛠️ Usunięcie figury ze starego pola w interfejsie graficznym
        StackPane oldTile = (StackPane) getNodeFromGridPane(szachownica, srcCol, srcRow);
        if (oldTile != null) {
            oldTile.getChildren().clear(); // Usunięcie figury ze starego pola

            // Przywrócenie odpowiedniego koloru pola
            Rectangle newTile = new Rectangle(ROZMIAR_POLA, ROZMIAR_POLA);
            newTile.setFill((srcRow + srcCol) % 2 == 0 ? Color.WHITE : Color.BROWN);
            oldTile.getChildren().add(newTile); // Dodajemy nowy kafelek jako tło
        } else {
            System.err.println("❌ Nie znaleziono starego pola dla figury: (" + srcRow + ", " + srcCol + ").");
        }

        // 🧩 Logika ruchu dla różnych figur
        if (piece instanceof Pawn) {
            Pawn selectedPawn = (Pawn) piece;
            if (isValidPawnMove(selectedPawn, row, col) && isPathClearForPawn(selectedPawn, row, col)) {
                // Obsługa bicia przeciwnika
                if (!isTileEmpty(row, col) && getPiece(row, col).isBlack() != selectedPawn.isBlack()) {
                    ChessPiece opponentPiece = getPiece(row, col);
                    removePiece(opponentPiece);
                }
                moveSuccessful = true;
                selectedPawn.setHasMoved(true);
            }
        } else if (piece instanceof Knight) {
            Knight selectedKnight = (Knight) piece;
            if (selectedKnight.isValidMove(row, col)) {
                // Obsługa bicia przeciwnika
                if (!isTileEmpty(row, col) && getPiece(row, col).isBlack() != selectedKnight.isBlack()) {
                    ChessPiece opponentPiece = getPiece(row, col);
                    removePiece(opponentPiece);
                }
                moveSuccessful = true;
            }
        } else if (piece instanceof Bishop) {
            Bishop selectedBishop = (Bishop) piece;
            if (selectedBishop.isValidMove(row, col) && isPathClear(selectedBishop, row, col)) {
                // Obsługa bicia przeciwnika
                if (!isTileEmpty(row, col) && getPiece(row, col).isBlack() != selectedBishop.isBlack()) {
                    ChessPiece opponentPiece = getPiece(row, col);
                    removePiece(opponentPiece);
                }
                moveSuccessful = true;
            }
        } else if (piece instanceof Rook) {
            Rook selectedRook = (Rook) piece;
            if (selectedRook.isValidMove(row, col) && isPathClear(selectedRook, row, col)) {
                // Obsługa bicia przeciwnika
                if (!isTileEmpty(row, col) && getPiece(row, col).isBlack() != selectedRook.isBlack()) {
                    ChessPiece opponentPiece = getPiece(row, col);
                    removePiece(opponentPiece);
                }
                moveSuccessful = true;
            }
        } else if (piece instanceof Queen) {
            Queen selectedQueen = (Queen) piece;
            if (selectedQueen.isValidMove(row, col) && isPathClear(selectedQueen, row, col)) {
                // Obsługa bicia przeciwnika
                if (!isTileEmpty(row, col) && getPiece(row, col).isBlack() != selectedQueen.isBlack()) {
                    ChessPiece opponentPiece = getPiece(row, col);
                    removePiece(opponentPiece);
                }
                moveSuccessful = true;
            }
        } else if (piece instanceof King) {
            King selectedKing = (King) piece;
            if (selectedKing.isValidMove(row, col)) {
                // Obsługa bicia przeciwnika
                if (!isTileEmpty(row, col) && getPiece(row, col).isBlack() != selectedKing.isBlack()) {
                    ChessPiece opponentPiece = getPiece(row, col);
                    removePiece(opponentPiece);
                }
                moveSuccessful = true;
            }
        }

        // ✅ Jeśli ruch jest poprawny
        if (moveSuccessful) {
            // 🔄 Aktualizuj pozycję figury na docelowym polu
            updatePiecePosition(piece, targetTile, row, col);

            // 🛠️ Aktualizacja boardPieces dla poprawności planszy
            updateBoardPieces(piece, srcRow, srcCol, row, col);

            // 🌐 Powiadom serwer o wykonanym ruchu
            notifyServerAboutMove(srcRow, srcCol, row, col);

            // 🔄 Zmień turę
            isWhiteTurn = !isWhiteTurn;
            System.out.println("🔄 Teraz tura " + (isWhiteTurn ? "białych" : "czarnych"));
        } else {
            System.out.println("❌ Ruch niemożliwy! Spróbuj ponownie.");
        }
    }

    private void updateBoardPieces(ChessPiece piece, int srcRow, int srcCol, int destRow, int destCol) {
        if (piece == null) {
            System.err.println("❌ Nie można zaktualizować planszy: brak figury na pozycji startowej (" + srcRow + ", " + srcCol + ").");
            return;
        }
        // Aktualizacja pozycji figury
        piece.setCurrentRow(destRow);
        piece.setCurrentColumn(destCol);

        // Aktualizacja tablicy boardPieces
        boardPieces[srcRow][srcCol] = null;
        boardPieces[destRow][destCol] = piece;

        System.out.println("🔄 Tablica boardPieces została zaktualizowana.");
    }

    private boolean isPathClear(ChessPiece piece, int targetRow, int targetCol) {
        int currentRow = piece.getCurrentRow();
        int currentCol = piece.getCurrentColumn();

        // Sprawdzamy, czy docelowa pozycja jest w zakresie planszy
        if (targetRow < 0 || targetRow >= 8 || targetCol < 0 || targetCol >= 8) {
            return false;  // Jeśli pozycja jest poza planszą, zwróć false
        }

        // Ruch w pionie (dla wieży, królowej)
        if (currentCol == targetCol) {
            int step = (targetRow > currentRow) ? 1 : -1;  // Kierunek ruchu w pionie
            for (int row = currentRow + step; row != targetRow; row += step) {
                if (row < 0 || row >= 8 || boardPieces[row][currentCol] != null) {  // Jeżeli na drodze znajduje się figura
                    return false;
                }
            }
        }
        // Ruch w poziomie (dla wieży, królowej)
        else if (currentRow == targetRow) {
            int step = (targetCol > currentCol) ? 1 : -1;  // Kierunek ruchu w poziomie
            for (int col = currentCol + step; col != targetCol; col += step) {
                if (col < 0 || col >= 8 || boardPieces[currentRow][col] != null) {  // Jeżeli na drodze znajduje się figura
                    return false;
                }
            }
        }
        // Ruch po przekątnej (dla gońca, królowej)
        else if (Math.abs(targetRow - currentRow) == Math.abs(targetCol - currentCol)) {
            int rowStep = (targetRow > currentRow) ? 1 : -1;
            int colStep = (targetCol > currentCol) ? 1 : -1;

            int row = currentRow + rowStep;
            int col = currentCol + colStep;

            while (row != targetRow && col != targetCol) {
                if (row < 0 || row >= 8 || col < 0 || col >= 8 || boardPieces[row][col] != null) {  // Jeżeli na drodze znajduje się figura
                    return false;
                }
                row += rowStep;
                col += colStep;
            }
        } else {
            return false;  // Jeśli ruch nie należy do dozwolonych (pionowy, poziomy lub przekątny), zwróć false
        }

        // Sprawdzamy, czy na docelowym polu znajduje się figura przeciwnika
        ChessPiece targetPiece = boardPieces[targetRow][targetCol];
        if (targetPiece != null && targetPiece.isBlack() != piece.isBlack()) {
            return true;  // Jeśli na docelowym polu znajduje się figura przeciwnika, zwróć true
        }

        // Jeśli nic nie blokuje drogi i pole jest puste, zwróć true
        return boardPieces[targetRow][targetCol] == null;
    }

    private void updatePiecePosition(ChessPiece piece, StackPane targetTile, int row, int col) {
        // Usuń figurę z poprzedniego kafelka
        StackPane oldTile = (StackPane) getNodeFromGridPane(szachownica, piece.getCurrentColumn(), piece.getCurrentRow());
        if (oldTile != null) {
            oldTile.getChildren().remove(piece.getImageView());
        }

        // Zaktualizuj tablicę boardPieces
        boardPieces[piece.getCurrentRow()][piece.getCurrentColumn()] = null;
        boardPieces[row][col] = piece;

        // Przenieś grafikę na nowy kafelek
        targetTile.getChildren().add(piece.getImageView());

        // Zaktualizuj pozycję figury
        piece.setCurrentRow(row);
        piece.setCurrentColumn(col);
    }


    private boolean isPathClearForPawn(Pawn pawn, int targetRow, int targetCol) {
        int currentRow = pawn.getCurrentRow();
        int currentCol = pawn.getCurrentColumn();

        // Pionek porusza się tylko w linii pionowej do przodu
        if (currentCol == targetCol) {
            int step = (targetRow > currentRow) ? 1 : -1; // Kierunek ruchu
            for (int row = currentRow + step; row != targetRow; row += step) {
                if (boardPieces[row][currentCol] != null) { // Jeżeli jest figura na drodze
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isTileEmpty(int row, int col) {
        ChessPiece pieceAtTarget = getPiece(row, col);  // Pobranie figury na danym polu
        return pieceAtTarget == null;  // Jeżeli na danym polu nie ma figury, zwróci true (puste pole)
    }

    // Sprawdzanie, czy pionek może wykonać ruch
    private boolean isValidPawnMove(Pawn pawn, int targetRow, int targetCol) {
        int currentRow = pawn.getCurrentRow();
        int currentCol = pawn.getCurrentColumn();
        int direction = pawn.isBlack() ? 1 : -1; // Kierunek ruchu dla czarnych i białych pionków

        // Sprawdzamy, czy pionek jest w swojej początkowej pozycji
        boolean isInitialPosition = (pawn.isBlack() && currentRow == 6) || (!pawn.isBlack() && currentRow == 1);

        // Ruch o jedno pole do przodu
        if (currentCol == targetCol && targetRow == currentRow + direction && isTileEmpty(targetRow, targetCol)) {
            return true;
        }

        // Ruch o dwa pola do przodu (dla pierwszego ruchu)
        if (!pawn.hasMoved() && currentCol == targetCol && targetRow == currentRow + 2 * direction) {
            // Sprawdzenie, czy oba pola są puste (sprawdzamy, czy są puste lub zawierają pionka przeciwnika)
            if (isTileEmptyOrOpponentPiece(currentRow + direction, currentCol, pawn.isBlack()) &&
                    isTileEmptyOrOpponentPiece(targetRow, targetCol, pawn.isBlack())) {
                // Ustawienie flagi hasMoved na true po wykonaniu ruchu o dwa pola
                pawn.setHasMoved(true);
                return true;
            }
        }

        // Zbicie na przekątnej (zajęte przez przeciwnika)
        if (Math.abs(currentCol - targetCol) == 1 && targetRow == currentRow + direction) {
            if (!isTileEmpty(targetRow, targetCol) && getPiece(targetRow, targetCol).isBlack() != pawn.isBlack()) {
                return true;
            }
        }

        return false;
    }
    private void removePiece(ChessPiece piece) {
        if (piece == null) {
            return; // Jeśli nie ma figury, nic nie usuwamy
        }

        System.out.println("Usuwanie figury: " + piece.getClass().getSimpleName() +
                " z pozycji: " + piece.getCurrentRow() + ", " + piece.getCurrentColumn());

        // Usuń grafikę figury z kafelka
        StackPane tile = (StackPane) getNodeFromGridPane(szachownica, piece.getCurrentColumn(), piece.getCurrentRow());
        if (tile != null && piece.getImageView() != null) {
            tile.getChildren().remove(piece.getImageView());
        }

        // Usuń figurę z tablicy boardPieces
        boardPieces[piece.getCurrentRow()][piece.getCurrentColumn()] = null;
    }


    private ChessPiece getPiece(int row, int col) {
        // Return the piece from the boardPieces array
        return boardPieces[row][col];
    }
    private void zapiszRuchDoPliku(String ruch) {
        try (FileWriter writer = new FileWriter("ruchy.txt", true)) {
            writer.write(ruch + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}