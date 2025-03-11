
public class Pawn extends Piece {
    boolean hasMoved = false;
    public Pawn(int x, int y, PieceColor pieceColor){

            super(x, y, pieceColor, 0);

    }
    @Override
    public boolean isValidMove(int newX, int newY, Piece[][] board) {
        int incr = (pieceColor == PieceColor.WHITE) ? 1 : -1;

        // Check if new position is within the board bounds
        if (newX < 0 || newX > 7 || newY < 0 || newY > 7) {
            return false;
        }

        // Moving straight forward
        if (newX == x) {
            // Check for one step move
            if (newY == y - incr) {
                return board[newY][x] == null;
            }
            // Check for two steps move (only if not moved yet)
            else if (newY == y - 2 * incr && !hasMoved) {
                int intermediateY = y - incr;
                return board[newY][x] == null && board[intermediateY][x] == null;
            } else {
                return false;
            }
        }
        // Diagonal capture (including en passant later)
        else if (Math.abs(newX - x) == 1 && newY == y - incr) {
            // Check if target square has an enemy piece
            Piece targetPiece = board[newY][newX];
            if (targetPiece != null && targetPiece.pieceColor != this.pieceColor) {
                return true;
            }
            // TODO: Add en passant logic here
            else {
                return false;
            }
        }
        // All other cases are invalid
        else {
            return false;
        }
    }
    @Override
    public void move(int newX, int newY) {
        hasMoved = true;
         // Update x/y in base class
        super.move(newX,newY);
    }
}
