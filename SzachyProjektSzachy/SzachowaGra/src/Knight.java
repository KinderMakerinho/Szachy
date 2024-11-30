import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Knight {
    private static final int ROZMIAR_POLA = 100; // Dodajemy zmienną tutaj
    private ImageView imageView;
    private int currentRow;
    private int currentColumn;
    private boolean isBlack;

    public Knight(String imagePath, int row, int col, boolean isBlack) {
        this.imageView = new ImageView(imagePath);
        this.currentRow = row;
        this.currentColumn = col;
        this.isBlack = isBlack;
        imageView.setFitWidth(ROZMIAR_POLA);  // Używamy ROZMIAR_POLA tutaj
        imageView.setFitHeight(ROZMIAR_POLA); // Używamy ROZMIAR_POLA tutaj
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
}
