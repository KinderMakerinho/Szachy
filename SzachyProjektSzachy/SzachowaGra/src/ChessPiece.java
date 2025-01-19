import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class ChessPiece {
    protected int currentRow;
    protected int currentColumn;
    protected boolean isBlack;
    protected ImageView imageView;
    private boolean hasMoved;  // Flaga wskazująca, czy figura wykonała ruch
    protected static ChessPiece[][] board;  // Plansza, będzie inicjowana w grze

    // Konstruktor klasy ChessPiece
    public ChessPiece(String imagePath, int row, int col, boolean isBlack) {
        this.isBlack = isBlack;
        // Wczytanie obrazu na podstawie ścieżki do pliku
        Image image = new Image(imagePath);
        this.imageView = new ImageView(image);

        // Ustawianie rozmiarów obrazu
        this.imageView.setFitWidth(90);  // Szerokość
        this.imageView.setFitHeight(90); // Wysokość

        // Ustawienie pozycji figury
        this.currentRow = row;
        this.currentColumn = col;
    }

    // Abstrakcyjna metoda do sprawdzania poprawności ruchu
    public abstract boolean isValidMove(int targetRow, int targetCol);

    // Getter dla imageView, aby móc uzyskać obrazek figury
    public ImageView getImageView() {
        return imageView;
    }

    // Getter i Setter dla wiersza
    public int getCurrentRow() {
        return currentRow;
    }

    public void setCurrentRow(int row) {
        this.currentRow = row;
    }

    // Getter i Setter dla kolumny
    public int getCurrentColumn() {
        return currentColumn;
    }

    public void setCurrentColumn(int col) {
        this.currentColumn = col;
    }

    // Setter dla pozycji (wiersz i kolumna) - zmienia pozycję figury
    public void setPosition(int row, int col) {
        // Zaktualizowanie pozycji figury
        this.currentRow = row;
        this.currentColumn = col;
        // Zaktualizowanie planszy
        board[row][col] = this; // Ustawiamy figurę na nowym polu
    }

    // Sprawdzenie koloru figury (czarny lub biały)
    public boolean isBlack() {
        return isBlack;
    }

    // Metoda sprawdzająca, czy przed figurą znajduje się inna figura na ścieżce ruchu
    public boolean isPathBlocked(int targetRow, int targetCol) {
        // Sprawdzamy, w jakim kierunku porusza się figura.
        if (this.currentRow == targetRow) {
            // Ruch poziomy: sprawdzamy po drodze wszystkie pola w tej samej linii
            int startCol = Math.min(this.currentColumn, targetCol) + 1;
            int endCol = Math.max(this.currentColumn, targetCol);

            for (int col = startCol; col < endCol; col++) {
                if (board[this.currentRow][col] != null) {
                    return true; // Zablokowana przez inną figurę
                }
            }
        } else if (this.currentColumn == targetCol) {
            // Ruch pionowy: sprawdzamy po drodze wszystkie pola w tej samej kolumnie
            int startRow = Math.min(this.currentRow, targetRow) + 1;
            int endRow = Math.max(this.currentRow, targetRow);

            for (int row = startRow; row < endRow; row++) {
                if (board[row][this.currentColumn] != null) {
                    return true; // Zablokowana przez inną figurę
                }
            }
        }
        return false; // Ścieżka wolna
    }

    // Metoda sprawdzająca, czy pole jest zajęte przez inną figurę
    public boolean isOccupied(int targetRow, int targetCol) {
        // Sprawdzamy, czy na danym polu znajduje się figura
        return board[targetRow][targetCol] != null;
    }
    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
}