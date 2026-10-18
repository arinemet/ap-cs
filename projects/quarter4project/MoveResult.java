public class MoveResult {
    public boolean success;
    public Sprite captured;       // null if no capture
    public boolean check;          // opponent now in check
    public boolean checkmate;
    public boolean stalemate;
    public int fromRow, fromCol, toRow, toCol;

    public MoveResult() {
        this.success = false;
        this.captured = null;
        this.check = false;
        this.checkmate = false;
        this.stalemate = false;
    }
}
