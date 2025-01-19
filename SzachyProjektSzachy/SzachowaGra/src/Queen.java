public class Queen extends ChessPiece {
    private boolean hasMoved;
    public Queen(String imagePath, int row, int col, boolean isBlack) {
        super(imagePath, row, col, isBlack);
    }

    @Override
    public boolean isValidMove(int targetRow, int targetCol) {
        int currentRow = getCurrentRow();
        int currentCol = getCurrentColumn();

        // Królowa porusza się jak wieża (po wierszach lub kolumnach)
        boolean isRookMoveValid = currentRow == targetRow || currentCol == targetCol;

        // Królowa porusza się jak goniec (po przekątnych)
        boolean isBishopMoveValid = Math.abs(targetRow - currentRow) == Math.abs(targetCol - currentCol);

        // Królowa może wykonać ruch jak wieża lub goniec
        return isRookMoveValid || isBishopMoveValid;
    }
    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

}
