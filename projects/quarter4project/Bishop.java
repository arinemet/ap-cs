public class Bishop extends Sprite {
    public Bishop(int row, int col, boolean white) {
        super(row, col, white);
    }

    @Override
    public char getSymbol() { return 'B'; }

    @Override
    public boolean isValidMove(int toRow, int toCol, Sprite[][] board) {
        if (!inBounds(toRow, toCol)) return false;
        if (toRow == row && toCol == col) return false;
        int dr = Math.abs(toRow - row);
        int dc = Math.abs(toCol - col);
        if (dr != dc) return false; // must be diagonal
        if (!pathClear(toRow, toCol, board)) return false;
        return targetEmptyOrEnemy(toRow, toCol, board);
    }
}
