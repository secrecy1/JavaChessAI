
public class Pawn extends Piece {
    boolean hasMoved = false;
    boolean lastMoveWasDouble = false;
    public SpecialMove specialMove = SpecialMove.NONE;
    public Pawn(int x, int y, PieceColor pieceColor){

            super(x, y, pieceColor, 0);

    }
    @Override
    public boolean isValidMove(int newX, int newY, Piece[][] board) {
        specialMove = SpecialMove.NONE;
        int incr = (pieceColor == PieceColor.WHITE) ? 1 : -1;

        // Check if new position is within the board bounds
        if (newX < 0 || newX > 7 || newY < 0 || newY > 7) {
            return false;
        }

        // Moving straight forward
        if (newX == x) {
            // Check for one step move
            if (newY == y - incr && board[newY][x] == null) {

                return true;
            }
            else if(newY == y - incr && board[newY][x] != null)
            {
                return false;
            }

            // Check for two steps move (only if not moved yet)
            else if (newY == y - 2 * incr && !hasMoved) {
                int intermediateY = y - incr;
                if (board[newY][x] == null && board[intermediateY][x] == null)
                {

                    return true;

                }

            } else {

                return false;
            }
        }
        // Diagonal capture (including en passant later)
        else if (Math.abs(newX - x) == 1 && newY == y - incr) {
        Piece targetPiece = board[newY][newX];
    // Normal diagonal capture
    if (targetPiece != null && targetPiece.pieceColor != this.pieceColor) {
        return true;
    }

    // En passant
    Piece sidePiece = board[y][newX]; // pawn beside us

    if (sidePiece instanceof Pawn &&
        sidePiece.pieceColor != this.pieceColor &&
        ((Pawn) sidePiece).isLastMoveWasDouble()) {

        specialMove = SpecialMove.EN_PASSANT;
        return true;
    }

    return false;
	}

        // All other cases are invalid

            return false;

    }


    @Override
    public void move(int newX, int newY) {
        // Check if this is a two-step move: absolute change in y is 2
        if (Math.abs(newY - y) == 2) {
            lastMoveWasDouble = true;
        } else {
            lastMoveWasDouble = false;
        }
        hasMoved = true;
        super.move(newX, newY);
    }
    @Override
    public char getFenSymbol()
    {
        return pieceColor == PieceColor.WHITE ? 'P': 'p';
    }
    public Piece copy()
    {
        Pawn p = new Pawn(this.x, this.y, this.pieceColor);
        p.hasMoved = this.hasMoved;
    	p.lastMoveWasDouble = this.lastMoveWasDouble;
    	p.specialMove = this.specialMove;
    	return p;
    }
    public boolean isLastMoveWasDouble()
    {
        return lastMoveWasDouble;
    }
}


