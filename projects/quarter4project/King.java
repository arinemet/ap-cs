public class King extends Sprite {
    public King(int row, int col, boolean white) {
        super(row, col, white);
    }

    @Override
    public char getSymbol() { return 'K'; }

    /**
     * King movement, including castling.
     *
     * Castling rules implemented here (the standard FIDE rules):
     *   1. Neither the king nor the chosen rook has previously moved.
     *   2. There are no pieces between the king and the rook.
     *   3. The king is not currently in check.
     *   4. The king does not pass through a square that is attacked by an enemy piece.
     *   5. The king does not land on a square that is attacked (this rule is
     *      enforced by BoardGame.isLegalMove via the normal "own king not in
     *      check after the move" filter, so it isn't re-checked here).
     *
     * A castle move is encoded as the king moving two squares horizontally;
     * BoardGame.tryMove detects this and slides the matching rook.
     */
    @Override
    public boolean isValidMove(int toRow, int toCol, Sprite[][] board) {
        if (!inBounds(toRow, toCol)) return false;
        if (toRow == row && toCol == col) return false;
        int dr = Math.abs(toRow - row);
        int dc = Math.abs(toCol - col);

        // Ordinary one-square king step.
        if (dr <= 1 && dc <= 1) {
            return targetEmptyOrEnemy(toRow, toCol, board);
        }

        // Castling: two squares horizontally from the starting square.
        if (!hasMoved && toRow == row && dr == 0 && dc == 2 && col == 4
                && (row == 0 || row == 7)) {
            boolean kingside = toCol > col;
            int rookCol = kingside ? 7 : 0;
            Sprite rookSq = board[row][rookCol];
            if (!(rookSq instanceof Rook)) return false;
            if (rookSq.isWhite() != this.white) return false;
            if (rookSq.hasMoved()) return false;

            // All squares strictly between king and rook must be empty.
            int step = kingside ? 1 : -1;
            for (int c = col + step; c != rookCol; c += step) {
                if (board[row][c] != null) return false;
            }

            // King must not currently be in check, and must not pass through
            // an attacked square. The landing square is checked separately by
            // BoardGame.isLegalMove (rule #5).
            if (isSquareAttacked(row, col, board, !white)) return false;
            int passCol = col + step;
            if (isSquareAttacked(row, passCol, board, !white)) return false;

            return true;
        }

        return false;
    }

    /**
     * Returns true if any piece of color {@code byWhite} can move to
     * (r,c) on the current board. Used to enforce the "king cannot
     * castle out of or through check" rules without consulting
     * BoardGame's higher-level helpers.
     */
    private boolean isSquareAttacked(int r, int c, Sprite[][] board, boolean byWhite) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Sprite p = board[i][j];
                if (p == null) continue;
                if (p.isWhite() != byWhite) continue;
                if (p == this) continue;
                if (p.isValidMove(r, c, board)) return true;
            }
        }
        return false;
    }
}
