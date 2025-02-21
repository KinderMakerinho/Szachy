public class Rook extends ChessPiece {
    private boolean hasMoved;
    public Rook(String imagePath, int row, int col, boolean isBlack) {
        super(imagePath, row, col, isBlack);
    }

    @Override
    public boolean isValidMove(int targetRow, int targetCol) {
        // Wieża porusza się po wierszach lub kolumnach
        return targetRow == getCurrentRow() || targetCol == getCurrentColumn();
    }
    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

}
