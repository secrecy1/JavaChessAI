public class King extends Piece {

    public SpecialMove castlingDirection = SpecialMove.NONE;
    public King(int x, int y, PieceColor pieceColor){

        super(x, y, pieceColor, 5);

    }
// In King.java - Check basic castling conditions
@Override
public boolean isValidMove(int newX, int newY, Piece[][] board) {
    castlingDirection = SpecialMove.NONE;
    int dx = newX - x;
    int dy = newY - y;
    
    // Regular king move (1 square)
    if (Math.abs(dx) <= 1 && Math.abs(dy) <= 1) {
        return (board[newY][newX] == null || board[newY][newX].pieceColor != this.pieceColor);
    }
    
    // Castling move (2 squares horizontally) - BASIC CHECK ONLY
    if (dy == 0 && Math.abs(dx) == 2 && !hasMoved) {

        return isBasicCastlingPossible(newX, newY, board);
    }
    
    return false;
}

private boolean isBasicCastlingPossible(int newX, int newY, Piece[][] board) {
    castlingDirection = SpecialMove.NONE;
    SpecialMove direction = (newX > x) ? SpecialMove.RIGHT_CASTLE : SpecialMove.LEFT_CASTLE;
    int rookX = (direction == SpecialMove.RIGHT_CASTLE) ? 7 : 0;
    
    // Check if rook exists and hasn't moved
    Piece rook = board[y][rookX];
    if (rook == null || !(rook instanceof Rook) || rook.hasMoved) {
        return false;
    }
    
    // Check if squares between are empty
    int start = Math.min(x, rookX) + 1;
    int end = Math.max(x, rookX);
    for (int col = start; col < end; col++) {
        if (board[y][col] != null) {
            return false;
        }
    }
    castlingDirection = direction;
    return true;
}
    @Override
    public void move(int newX, int newY) {

        super.move(newX, newY);
    }
    @Override
    public char getFenSymbol()
    {
        return pieceColor == PieceColor.WHITE ? 'K': 'k';
    }
    public Piece copy()
    {
        return new King(this.x, this.y, this.pieceColor);
    }

}
