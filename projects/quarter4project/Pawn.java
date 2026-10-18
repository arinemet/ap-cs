public class Pawn extends Sprite {
    public Pawn(int row, int col, boolean white) {
        super(row, col, white);
    }

    @Override
    public char getSymbol() { return 'P'; }

    @Override
    public boolean isValidMove(int toRow, int toCol, Sprite[][] board) {
        if (!inBounds(toRow, toCol)) return false;
        if (toRow == row && toCol == col) return false;

        // White moves toward row 0 (row decreases). Black moves toward row 7 (row increases).
        int dir = white ? -1 : 1;
        int startRow = white ? 6 : 1;

        int dr = toRow - row;
        int dc = toCol - col;
        Sprite target = board[toRow][toCol];

        // Forward 1
        if (dc == 0 && dr == dir && target == null) {
            return true;
        }

        // Forward 2 from starting row — both squares must be empty
        if (dc == 0 && dr == 2 * dir && row == startRow) {
            Sprite between = board[row + dir][col];
            if (between == null && target == null) return true;
        }

        // Diagonal capture — exactly 1 forward, 1 sideways, target must be enemy
        if (Math.abs(dc) == 1 && dr == dir && target != null && target.isWhite() != this.white) {
            return true;
        }

        return false;
    }
}
