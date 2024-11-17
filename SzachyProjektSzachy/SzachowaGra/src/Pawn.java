import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Pawn {
    private ImageView imageView;
    private boolean firstMove = true;
    private int currentRow;
    private int currentColumn;
    private boolean isBlack;

    public Pawn(String imagePath, int row, int col, boolean isBlack) {
        this.isBlack = isBlack;
        // Wczytanie obrazu
        Image image = new Image(imagePath);
        this.imageView = new ImageView(image);

        // Ustawienie rozmiaru pionków na 90x90 px
        this.imageView.setFitWidth(90);
        this.imageView.setFitHeight(90);

        this.currentRow = row;
        this.currentColumn = col;
    }

    // Getter dla ImageView
    public ImageView getImageView() {
        return this.imageView;
    }

    // Getter dla aktualnego wiersza
    public int getCurrentRow() {
        return this.currentRow;
    }

    // Getter dla aktualnej kolumny
    public int getCurrentColumn() {
        return this.currentColumn;
    }

    // Setter dla aktualnego wiersza
    public void setCurrentRow(int row) {
        this.currentRow = row;
    }

    // Setter dla aktualnej kolumny
    public void setCurrentColumn(int col) {
        this.currentColumn = col;
    }

    // Metoda do wykonania ruchu
    public boolean move(int targetRow, int targetColumn) {
        int moveDistanceRow = Math.abs(this.currentRow - targetRow);
        int moveDistanceCol = Math.abs(this.currentColumn - targetColumn);

        // Sprawdzamy, czy pionek porusza się w dozwolony sposób
        if (isFirstMove()) {
            if (moveDistanceRow > 2 || moveDistanceCol > 0) {
                return false;  // Ruch nie jest dozwolony
            }
            setFirstMove(false);
        } else {
            if (moveDistanceRow != 1 || moveDistanceCol > 0) {
                return false;
            }
        }

        this.currentRow = targetRow;
        this.currentColumn = targetColumn;
        return true;
    }

    public boolean isFirstMove() {
        return this.firstMove;
    }

    public void setFirstMove(boolean firstMove) {
        this.firstMove = firstMove;
    }
}
