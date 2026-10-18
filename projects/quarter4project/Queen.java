public class Queen extends Sprite {
    public Queen(int row, int col, boolean white) {
        super(row, col, white);
    }

    @Override
    public char getSymbol() { return 'Q'; }

    @Override
    public boolean isValidMove(int toRow, int toCol, Sprite[][] board) {
        if (!inBounds(toRow, toCol)) return false;
        if (toRow == row && toCol == col) return false;
        int dr = toRow - row;
        int dc = toCol - col;
        boolean straight = (dr == 0) ^ (dc == 0); // exactly one axis is zero
        boolean diagonal = Math.abs(dr) == Math.abs(dc);
        if (!straight && !diagonal) return false;
        if (!pathClear(toRow, toCol, board)) return false;
        return targetEmptyOrEnemy(toRow, toCol, board);
    }
}
