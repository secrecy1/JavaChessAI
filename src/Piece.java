public abstract class Piece {
    protected int x, y;
    protected PieceColor pieceColor;
    protected boolean hasMoved;
    public final int IMAGEINDEX;
    public SpecialMove specialMove = SpecialMove.NONE;
    public Piece(int x, int y, PieceColor pieceColor, int imageIndex) {
        this.IMAGEINDEX = imageIndex;
        this.x = x;
        this.y = y;
        this.pieceColor = pieceColor;
    }

    public abstract boolean isValidMove(int newX, int newY, Piece[][] board);

    public void move(int newX, int newY) {
        this.x = newX;
        this.y = newY;
        this.hasMoved = true;
    }

    public PieceColor getColor()
    {
        return pieceColor;
    }
    public abstract char getFenSymbol();
    public abstract Piece copy();
    public boolean hasMoved()
    {
        return hasMoved;
    }
    public SpecialMove getSpecialMove()
    {
        return specialMove;
    }
    // maybe if needed will refactor this to return the number of available moves instead of if there are any available moves
    public boolean hasAvailableMove(Piece[][] board)
    {
        int total = 0;
        for(int row = 0; row < 8; row++)
            for(int col = 0; col < 8; col++)
                total += this.isValidMove(row, col, board) ? 1 : 0;
        return total != 0;
    }
    @Override
    public String toString()
    {
        return "Type: " + this.getClass() + "| Color: " + this.pieceColor + "col(x): " + this.x + "| row(y): " + this.y;
    }


}
