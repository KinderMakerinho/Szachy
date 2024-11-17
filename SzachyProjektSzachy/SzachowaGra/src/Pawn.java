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

        this.imageView.setFitWidth(90);
        this.imageView.setFitHeight(90);

        this.currentRow = row;
        this.currentColumn = col;
    }

    public ImageView getImageView() {
        return this.imageView;
    }


    public int getCurrentRow() {
        return this.currentRow;
    }


    public int getCurrentColumn() {
        return this.currentColumn;
    }


    public void setCurrentRow(int row) {
        this.currentRow = row;
    }


    public void setCurrentColumn(int col) {
        this.currentColumn = col;
    }


    public boolean isFirstMove() {
        return this.firstMove;
    }


    public void setFirstMove(boolean firstMove) {
        this.firstMove = firstMove;
    }

    public boolean isBlack() {
        return isBlack;
    }
}
