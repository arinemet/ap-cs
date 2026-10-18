/**
 * Abstract base class for every chess piece.
 * Each subclass implements its own movement rules in isValidMove.
 * Board orientation: row 0 is the top (black back rank), row 7 is the bottom (white back rank).
 * board[row][col] may be null when empty.
 */
public abstract class Sprite {
    protected int row, col;
    protected boolean white;
    /** True once this piece has moved at least once. Used by castling logic
     *  (only kings/rooks consult it, but storing it on the base class keeps
     *  the bookkeeping in BoardGame.tryMove uniform). */
    protected boolean hasMoved;

    public Sprite(int row, int col, boolean white) {
        this.row = row;
        this.col = col;
        this.white = white;
        this.hasMoved = false;
    }

    public boolean hasMoved() { return hasMoved; }
    public void setHasMoved(boolean v) { this.hasMoved = v; }

    public int getRow() { return row; }
    public void setRow(int r) { this.row = r; }

    public int getCol() { return col; }
    public void setCol(int c) { this.col = c; }

    public boolean isWhite() { return white; }

    /**
     * Pure geometry/path/occupancy check. Must NOT mutate state.
     * Does NOT consider whether moving leaves own king in check —
     * BoardGame handles that.
     */
    public abstract boolean isValidMove(int toRow, int toCol, Sprite[][] board);

    /** 'K','Q','R','B','N','P' */
    public abstract char getSymbol();

    /** e.g. "wK.png" or "bP.png". Concrete in parent so subclasses don't repeat themselves. */
    public String getImageFileName() {
      String bettersymbol = getPieceName().toLowerCase();
        return bettersymbol + "-" + (white ? "w" : "b") + ".png";
    }

    /** e.g. "King", "Queen", ... — concrete using getSymbol() */
    public String getPieceName() {
        switch (getSymbol()) {
            case 'K': return "King";
            case 'Q': return "Queen";
            case 'R': return "Rook";
            case 'B': return "Bishop";
            case 'N': return "Knight";
            case 'P': return "Pawn";
            default:  return "Unknown";
        }
    }

    // ---------- helpers usable by subclasses ----------

    /** True if (toRow,toCol) is on the board. */
    protected boolean inBounds(int toRow, int toCol) {
        return toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8;
    }

    /** True if target square is empty or holds an enemy piece (i.e. legal landing square ignoring path). */
    protected boolean targetEmptyOrEnemy(int toRow, int toCol, Sprite[][] board) {
        Sprite t = board[toRow][toCol];
        return t == null || t.isWhite() != this.white;
    }

    /**
     * True if every square STRICTLY between (row,col) and (toRow,toCol)
     * is empty. The target square itself is NOT checked here.
     * Only valid for straight/diagonal lines.
     */
    protected boolean pathClear(int toRow, int toCol, Sprite[][] board) {
        int dr = Integer.signum(toRow - row);
        int dc = Integer.signum(toCol - col);
        int r = row + dr;
        int c = col + dc;
        while (r != toRow || c != toCol) {
            if (board[r][c] != null) return false;
            r += dr;
            c += dc;
        }
        return true;
    }
}
