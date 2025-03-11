public class King extends Piece {
    public King(int x, int y, PieceColor pieceColor){

        super(x, y, pieceColor, 5);

    }
    @Override
    public boolean isValidMove(int newX, int newY, Piece[][] board)
    {

        int dx = newX - x;
        int  dy = newY - y;
        if(dx == 0 && dy == 0)
            return false;
        return Math.abs(dx) <= 1 && Math.abs(dy) <= 1 && (board[newY][newX] == null || board[newY][newX].pieceColor != this.pieceColor);
    }
    @Override
    public void move(int newX, int newY) {
        super.move(newX, newY);
    }
}
