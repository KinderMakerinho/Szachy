import javafx.scene.image.ImageView;

public class Biskupek {
    private ImageView imageView;
    private int currentRow;
    private int currentColumn;
    private boolean isBlack;

    public Biskupek(String imagePath, int currentRow, int currentColumn, boolean isBlack) {
        this.imageView = new ImageView(imagePath);  // Używamy ImageView z JavaFX
        this.imageView.setFitWidth(80);  // Dopasowanie rozmiaru obrazu do pola szachownicy
        this.imageView.setFitHeight(80);
        this.currentRow = currentRow;
        this.currentColumn = currentColumn;
        this.isBlack = isBlack;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public int getCurrentRow() {
        return currentRow;
    }

    public void setCurrentRow(int currentRow) {
        this.currentRow = currentRow;
    }

    public int getCurrentColumn() {
        return currentColumn;
    }

    public void setCurrentColumn(int currentColumn) {
        this.currentColumn = currentColumn;
    }

    public boolean isBlack() {
        return isBlack;
    }

    public String getColor() {
        return isBlack ? "black" : "white";
    }
}
