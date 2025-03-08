
public class Pawn extends Piece {
    boolean hasMoved = false;
    public Pawn(int x, int y, PieceColor pieceColor){

            super(x, y, pieceColor, 0);

    }
    @Override
    public boolean isValidMove(int newX, int newY, Piece[][] board)
    {
        // TODO: add en passant
        if(y - 1 < 0)
            return false;
       else if(newY < y && newX == x)
        {
            if (board[y - 1][x] != null)
                return false;
            else if (newY == y - 1 || !(hasMoved) && newY == y - 2) {
                return true;
            }
        }
        else if(newY == y - 1)
        {
            if(newX == x - 1)
                return true;
            else if(newX == x + 1)
                return true;
        }




        return false;
    }
    @Override
    public void move(int newX, int newY) {
        hasMoved = true;
         // Update x/y in base class
        super.move(newX,newY);
    }
}
