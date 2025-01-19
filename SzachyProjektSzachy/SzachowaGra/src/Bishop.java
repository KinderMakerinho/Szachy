public class Bishop extends ChessPiece {
    private boolean hasMoved;
    public Bishop(String imagePath, int row, int col, boolean isBlack) {
        super(imagePath, row, col, isBlack);
    }

    @Override
    public boolean isValidMove(int targetRow, int targetCol) {
        // Goniec porusza się po przekątnych
        return Math.abs(targetRow - getCurrentRow()) == Math.abs(targetCol - getCurrentColumn());
    }
    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

}
