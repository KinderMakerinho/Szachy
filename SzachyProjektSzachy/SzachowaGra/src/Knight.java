public class Knight extends ChessPiece {
    private boolean hasMoved;
    public Knight(String imagePath, int row, int col, boolean isBlack) {
        super(imagePath, row, col, isBlack);
    }
    @Override
    public boolean isValidMove(int targetRow, int targetCol) {
        int rowDiff = Math.abs(targetRow - getCurrentRow());
        int colDiff = Math.abs(targetCol - getCurrentColumn());
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }
    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

}
