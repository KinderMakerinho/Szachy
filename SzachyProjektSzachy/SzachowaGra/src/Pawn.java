public class Pawn extends ChessPiece {
    private boolean hasMoved;
    private ChessPiece[][] boardPieces; // Referencja do planszy

    // Konstruktor, który przyjmuje planszę jako parametr
    public Pawn(String imagePath, int row, int col, boolean isBlack, ChessPiece[][] boardPieces) {
        super(imagePath, row, col, isBlack);
        this.boardPieces = boardPieces; // Inicjalizacja planszy
        this.hasMoved = false;  // Pionek nie ruszył się na początku
    }

    public boolean isValidMove(int targetRow, int targetCol) {
        int direction = isBlack() ? 1 : -1; // Czarne pionki poruszają się w dół, białe w górę
        int startRow = getCurrentRow();
        int startCol = getCurrentColumn();

        // Ruch o jedno pole do przodu
        if (targetCol == startCol && targetRow == startRow + direction) {
            if (isTileEmptyOrOpponentPiece(targetRow, targetCol)) {
                return true;
            }
        }

        // Ruch o dwa pola do przodu tylko, jeśli pionek jeszcze się nie ruszył
        if (!hasMoved && targetCol == startCol && targetRow == startRow + 2 * direction) {
            if (isTileEmptyOrOpponentPiece(startRow + direction, startCol) &&
                    isTileEmptyOrOpponentPiece(targetRow, targetCol)) {
                return true;
            }
        }

        // Sprawdzenie bicia na ukos (tylko w przypadku przeciwnika)
        if (Math.abs(targetCol - startCol) == 1 && targetRow == startRow + direction) {
            if (isOpponentPiece(targetRow, targetCol)) {
                return true;  // Bicie na ukos, jeśli na polu znajduje się pionek przeciwnika
            }
        }

        return false; // Ruch nie jest dozwolony
    }


    // Metoda sprawdzająca, czy pole jest puste lub zawiera pionka przeciwnika
    private boolean isTileEmptyOrOpponentPiece(int row, int col) {
        ChessPiece pieceAtTarget = boardPieces[row][col];  // Pobierz figurę na danym polu
        if (pieceAtTarget == null) {
            return true;  // Pole jest puste
        } else if (pieceAtTarget.isBlack() != this.isBlack()) {
            return true;  // Pole zawiera figurę przeciwnika
        }
        return false;  // Pole zawiera figurę tego samego koloru
    }


    // Metoda sprawdzająca, czy pole jest zajęte przez pionka przeciwnika
    public boolean isOpponentPiece(int row, int col) {
        ChessPiece pieceAtTarget = boardPieces[row][col];
        return pieceAtTarget != null && pieceAtTarget.isBlack() != this.isBlack();
    }
    // Getter i Setter dla hasMoved
    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

}
