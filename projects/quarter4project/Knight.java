public class Knight extends Sprite {
    public Knight(int row, int col, boolean white) {
        super(row, col, white);
    }

    @Override
    public char getSymbol() { return 'N'; }

    @Override
    public boolean isValidMove(int toRow, int toCol, Sprite[][] board) {
        if (!inBounds(toRow, toCol)) return false;
        int dr = Math.abs(toRow - row);
        int dc = Math.abs(toCol - col);
        boolean lShape = (dr == 2 && dc == 1) || (dr == 1 && dc == 2);
        if (!lShape) return false;
        // Knight can jump — no path check.
        return targetEmptyOrEnemy(toRow, toCol, board);
    }
}
