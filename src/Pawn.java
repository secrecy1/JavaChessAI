
public class Pawn extends Piece {
    boolean hasMoved = false;
    public Pawn(int x, int y, PieceColor pieceColor){

            super(x, y, pieceColor, 0);

    }
    @Override
    public boolean isValidMove(int newX, int newY, Piece[][] board)
    {
        // TODO: add en passant and becomming a queen
        int incr = (pieceColor == PieceColor.WHITE) ? 1: -1;
        if(y - 1 < 0 || y + 1 > 7)
            return false;
       else if((newY ==y - incr || newY == y - incr * 2) && newX == x)
        {
            if (board[y - incr][x] != null)
                return false;
            else if (newY == y - incr || !(hasMoved) && newY == y - incr * 2) {
                return true;
            }
        }
        else if(newY == y - incr)
        {
            if(board[newY][newX] !=null && newX == x - incr && board[newY][newX].pieceColor != this.pieceColor)
                return true;
            else if(board[newY][newX] !=null && newX == x + incr && board[newY][newX].pieceColor != this.pieceColor)
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
