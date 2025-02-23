public abstract class Piece {
    protected int x, y;
    protected PieceColor pieceColor;
    protected boolean hasMoved;
    public final int IMAGEINDEX;

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


}
