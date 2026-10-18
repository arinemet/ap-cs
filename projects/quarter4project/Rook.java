public class Rook extends Sprite {
    public Rook(int row, int col, boolean white) {
        super(row, col, white);
    }

    @Override
    public char getSymbol() { return 'R'; }

    @Override
    public boolean isValidMove(int toRow, int toCol, Sprite[][] board) {
        if (!inBounds(toRow, toCol)) return false;
        if (toRow == row && toCol == col) return false;
        if (toRow != row && toCol != col) return false; // must be straight
        if (!pathClear(toRow, toCol, board)) return false;
        return targetEmptyOrEnemy(toRow, toCol, board);
    }
}
