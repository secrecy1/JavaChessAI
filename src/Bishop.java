public class Bishop extends Piece {
    public Bishop(int x, int y, PieceColor pieceColor){

        super(x, y, pieceColor, 3);

    }
    @Override
    public boolean isValidMove(int newX, int newY, Piece[][] board)
    {
        // check diagonals
        if(newX - x ==  y - newY || newX + x ==  y + newY)
            return true;
        return false;
    }
    @Override
    public void move(int newX, int newY) {
        // Update x/y in base class
        super.move(newX,newY);

    }
}
