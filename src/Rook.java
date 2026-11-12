public class Rook extends Piece {
	public boolean hasMoved = false;
    public Rook(int x, int y, PieceColor pieceColor) {
        super(x, y, pieceColor, 1); // Rook has a value of 5
    }

    @Override
    public boolean isValidMove(int newX, int newY, Piece[][] board) {
        if (newX < 0 || newX >= 8 || newY < 0 || newY >= 8) {
            return false;
        }

        int deltaX = newX - x;
        int deltaY = newY - y;

        if ((deltaX != 0 && deltaY != 0) || (deltaX == 0 && deltaY == 0)) {
            return false; // No movement or diagonal movement
        }

        Piece destPiece = board[newY][newX]; // y-axis reversed
        if (destPiece != null && destPiece.pieceColor == this.pieceColor) {
            return false;
        }

        int stepX = Integer.compare(deltaX, 0); // 1 (right), -1 (left), or 0 (no x movement)
        int stepY = Integer.compare(deltaY, 0); // 1 (down), -1 (up), or 0 (no y movement)

        int currentX = x + stepX;
        int currentY = y + stepY;

        while (currentX != newX || currentY != newY) {
            if (board[currentY][currentX] != null) {
                return false;
            }
            currentX += stepX;
            currentY += stepY;
        }

        return true;
    }

    @Override
    public void move(int newX, int newY) {
        super.move(newX, newY);
    }
    @Override
    public char getFenSymbol()
    {
        return pieceColor == PieceColor.WHITE ? 'R': 'r';
    }
    public Piece copy()
    {
        return new Rook(this.x, this.y, this.pieceColor);
    }
}
