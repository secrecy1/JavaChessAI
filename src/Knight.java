public class Knight extends Piece{
    public Knight(int x, int y, PieceColor pieceColor){

        super(x, y, pieceColor, 2);

    }
    @Override
    public boolean isValidMove(int newX, int newY, Piece[][] board)
    {
        System.out.println(x);
        System.out.println(y);
      int dx = newX - x;
      int  dy = newY - y;
      if(dx == 0 || dy == 0)
          return false;
        if((Math.abs(dy) == 2 && Math.abs(dx) == 1) || (Math.abs(dy) == 1 && Math.abs(dx) == 2))
        {
            return board[newY][newX] == null || board[newY][newX].pieceColor != this.pieceColor;
        }
        return false;
    }
    @Override
    public void move(int newX, int newY) {
        super.move(newX, newY);
    }
}
