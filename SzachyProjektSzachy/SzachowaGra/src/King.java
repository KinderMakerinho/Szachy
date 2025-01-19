public class King extends ChessPiece {
    private boolean hasMoved;


    public King(String imagePath, int row, int col, boolean isBlack) {
        super(imagePath, row, col, isBlack);
    }

    @Override
    public boolean isValidMove(int targetRow, int targetCol) {
        int rowDiff = Math.abs(targetRow - getCurrentRow());
        int colDiff = Math.abs(targetCol - getCurrentColumn());
        return rowDiff <= 1 && colDiff <= 1;  // Król może poruszać się o jedno pole w dowolnym kierunku
    }
    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

}
