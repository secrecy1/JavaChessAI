public class Rook extends Piece {
    public Rook(int x, int y, PieceColor pieceColor){

        super(x, y, pieceColor, 1);

    }
    @Override
    public boolean isValidMove(int newX, int newY, Piece[][] board)
    {
        return false;
    }
    @Override
    public void move(int newX, int newY) {
        // Update x/y in base class
        super.move(x,y);
    }
}
