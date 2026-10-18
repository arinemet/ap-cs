import java.util.ArrayList;
import java.util.List;

public class BoardGame {
    private Sprite[][] board = new Sprite[8][8];
    private boolean whiteTurn;
    private ArrayList<Sprite> capturedPieces;
    private boolean gameOver;
    private String winner; // "White", "Black", "Draw", or null

    // En passant target: the empty square a pawn may capture ONTO this turn,
    // i.e. the square a pawn skipped over when it just made a two-square push.
    // Valid only for the single move immediately following that push; (-1,-1)
    // means no en passant is currently available. Pawn.isValidMove is pure
    // geometry and cannot know the previous move, so this game-state lives here,
    // mirroring how castling state is handled in tryMove.
    private int enPassantRow = -1;
    private int enPassantCol = -1;

    public BoardGame() {
        reset();
    }

    public void reset() {
        board = new Sprite[8][8];
        capturedPieces = new ArrayList<Sprite>();
        whiteTurn = true;
        gameOver = false;
        winner = null;
        enPassantRow = -1;
        enPassantCol = -1;

        // Black back rank (row 0)
        board[0][0] = new Rook(0, 0, false);
        board[0][1] = new Knight(0, 1, false);
        board[0][2] = new Bishop(0, 2, false);
        board[0][3] = new Queen(0, 3, false);
        board[0][4] = new King(0, 4, false);
        board[0][5] = new Bishop(0, 5, false);
        board[0][6] = new Knight(0, 6, false);
        board[0][7] = new Rook(0, 7, false);
        // Black pawns row 1
        for (int c = 0; c < 8; c++) {
            board[1][c] = new Pawn(1, c, false);
        }
        // White pawns row 6
        for (int c = 0; c < 8; c++) {
            board[6][c] = new Pawn(6, c, true);
        }
        // White back rank (row 7)
        board[7][0] = new Rook(7, 0, true);
        board[7][1] = new Knight(7, 1, true);
        board[7][2] = new Bishop(7, 2, true);
        board[7][3] = new Queen(7, 3, true);
        board[7][4] = new King(7, 4, true);
        board[7][5] = new Bishop(7, 5, true);
        board[7][6] = new Knight(7, 6, true);
        board[7][7] = new Rook(7, 7, true);
    }

    public Sprite getPieceAt(int r, int c) {
        if (r < 0 || r > 7 || c < 0 || c > 7) return null;
        return board[r][c];
    }

    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    public ArrayList<Sprite> getCapturedPieces() {
        return capturedPieces;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public String getWinner() {
        return winner;
    }

    public boolean isInCheck(boolean whiteSide) {
        // Search algorithm: scan the full 2D board to locate the king.
        int kr = -1, kc = -1;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Sprite p = board[r][c];
                if (p != null && p instanceof King && p.isWhite() == whiteSide) {
                    kr = r;
                    kc = c;
                }
            }
        }
        if (kr == -1) return false; // no king found
        // Search algorithm: scan again to find whether any enemy piece attacks it.
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Sprite p = board[r][c];
                if (p != null && p.isWhite() != whiteSide) {
                    if (p.isValidMove(kr, kc, board)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * True if (fr,fc)->(tr,tc) is a legal en passant capture given the current
     * enPassant target. Pure pawn geometry (a forward diagonal step) lives in
     * Pawn.isValidMove, but that only triggers when the target square holds an
     * enemy piece; en passant lands on an EMPTY square, so BoardGame recognises
     * it here using the previous-move state it tracks. The captured pawn sits
     * beside the mover at (fr,tc), not on the landing square.
     */
    private boolean isEnPassantMove(int fr, int fc, int tr, int tc) {
        Sprite piece = board[fr][fc];
        if (!(piece instanceof Pawn)) return false;
        if (tr != enPassantRow || tc != enPassantCol) return false;
        if (board[tr][tc] != null) return false; // must land on the empty skipped square
        int dir = piece.isWhite() ? -1 : 1;
        if (tr - fr != dir) return false;
        if (Math.abs(tc - fc) != 1) return false;
        Sprite captured = board[fr][tc];
        if (!(captured instanceof Pawn)) return false;
        if (captured.isWhite() == piece.isWhite()) return false;
        return true;
    }

    /**
     * Tests if moving the piece at (fr,fc) to (tr,tc) is legal — i.e.
     * isValidMove (or a valid en passant) passes AND own king is not in check
     * after the move. Performs the move temporarily then undoes.
     */
    private boolean isLegalMove(int fr, int fc, int tr, int tc) {
        Sprite piece = board[fr][fc];
        if (piece == null) return false;
        boolean enPassant = isEnPassantMove(fr, fc, tr, tc);
        if (!piece.isValidMove(tr, tc, board) && !enPassant) return false;

        Sprite target = board[tr][tc];
        // En passant removes the enemy pawn beside the mover, not on the target.
        int epCapRow = fr, epCapCol = tc;
        Sprite epCaptured = enPassant ? board[epCapRow][epCapCol] : null;

        // Make
        board[tr][tc] = piece;
        board[fr][fc] = null;
        if (enPassant) board[epCapRow][epCapCol] = null;
        piece.setRow(tr);
        piece.setCol(tc);

        boolean inCheck = isInCheck(piece.isWhite());

        // Unmake
        board[fr][fc] = piece;
        board[tr][tc] = target;
        piece.setRow(fr);
        piece.setCol(fc);
        if (target != null) {
            target.setRow(tr);
            target.setCol(tc);
        }
        if (enPassant) board[epCapRow][epCapCol] = epCaptured;

        return !inCheck;
    }

    public List<int[]> getLegalMoves(int fromRow, int fromCol) {
        ArrayList<int[]> moves = new ArrayList<int[]>();
        Sprite piece = getPieceAt(fromRow, fromCol);
        if (piece == null) return moves;
        if (piece.isWhite() != whiteTurn) return moves;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (r == fromRow && c == fromCol) continue;
                if (isLegalMove(fromRow, fromCol, r, c)) {
                    moves.add(new int[]{r, c});
                }
            }
        }
        return moves;
    }

    private boolean hasAnyLegalMove(boolean whiteSide) {
        // Exhaustive board search for at least one legal move for the side to move.
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Sprite p = board[r][c];
                if (p == null || p.isWhite() != whiteSide) continue;
                for (int tr = 0; tr < 8; tr++) {
                    for (int tc = 0; tc < 8; tc++) {
                        if (tr == r && tc == c) continue;
                        if (isLegalMove(r, c, tr, tc)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public MoveResult tryMove(int fromRow, int fromCol, int toRow, int toCol) {
        MoveResult result = new MoveResult();
        result.fromRow = fromRow;
        result.fromCol = fromCol;
        result.toRow = toRow;
        result.toCol = toCol;

        if (gameOver) return result;
        if (fromRow < 0 || fromRow > 7 || fromCol < 0 || fromCol > 7) return result;
        if (toRow < 0 || toRow > 7 || toCol < 0 || toCol > 7) return result;

        Sprite piece = board[fromRow][fromCol];
        if (piece == null) return result;
        if (piece.isWhite() != whiteTurn) return result;

        boolean enPassant = isEnPassantMove(fromRow, fromCol, toRow, toCol);
        if (!piece.isValidMove(toRow, toCol, board) && !enPassant) return result;

        // Legality test (own king must not be in check after move)
        if (!isLegalMove(fromRow, fromCol, toRow, toCol)) return result;

        // Perform the move
        Sprite target = board[toRow][toCol];
        if (enPassant) {
            // The captured pawn sits beside the mover, on the square it just
            // double-pushed through, not on the landing square.
            Sprite epCaptured = board[fromRow][toCol];
            if (epCaptured != null) {
                capturedPieces.add(epCaptured);
                result.captured = epCaptured;
            }
            board[fromRow][toCol] = null;
        } else if (target != null) {
            capturedPieces.add(target);
            result.captured = target;
        }
        board[toRow][toCol] = piece;
        board[fromRow][fromCol] = null;
        piece.setRow(toRow);
        piece.setCol(toCol);

        // Castling: if a king moved two squares horizontally from its
        // starting square, slide the matching rook to the other side of
        // the king. King.isValidMove has already validated all castling
        // preconditions (rule 1-4); rule 5 was just enforced by the
        // isLegalMove check above.
        if (piece instanceof King && Math.abs(toCol - fromCol) == 2 && toRow == fromRow) {
            boolean kingside = toCol > fromCol;
            int rookFromCol = kingside ? 7 : 0;
            int rookToCol   = kingside ? 5 : 3;
            Sprite rook = board[toRow][rookFromCol];
            if (rook != null) {
                board[toRow][rookToCol] = rook;
                board[toRow][rookFromCol] = null;
                rook.setCol(rookToCol);
                rook.setHasMoved(true);
            }
        }

        // Track whether this piece has moved (needed for castling eligibility).
        piece.setHasMoved(true);

        // Pawn promotion
        if (piece instanceof Pawn) {
            if ((piece.isWhite() && toRow == 0) || (!piece.isWhite() && toRow == 7)) {
                Queen promoted = new Queen(toRow, toCol, piece.isWhite());
                board[toRow][toCol] = promoted;
            }
        }

        // Refresh the en passant target: it is offered for exactly one move, so
        // set it only when a pawn just made a two-square push, otherwise clear it.
        if (piece instanceof Pawn && Math.abs(toRow - fromRow) == 2) {
            enPassantRow = (fromRow + toRow) / 2;
            enPassantCol = fromCol;
        } else {
            enPassantRow = -1;
            enPassantCol = -1;
        }

        result.success = true;

        // Swap turn
        whiteTurn = !whiteTurn;

        // Compute check / checkmate / stalemate for new side-to-move
        boolean opponentInCheck = isInCheck(whiteTurn);
        boolean opponentHasMove = hasAnyLegalMove(whiteTurn);

        result.check = opponentInCheck && opponentHasMove;
        result.checkmate = opponentInCheck && !opponentHasMove;
        result.stalemate = !opponentInCheck && !opponentHasMove;

        if (result.checkmate) {
            gameOver = true;
            winner = whiteTurn ? "Black" : "White";
        } else if (result.stalemate) {
            gameOver = true;
            winner = "Draw";
        }

        return result;
    }

    public void forceEndGame(boolean whiteWins) {
        gameOver = true;
        winner = whiteWins ? "White" : "Black";
    }
}
