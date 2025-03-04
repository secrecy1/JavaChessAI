public class Rook extends Piece {
    public Rook(int x, int y, PieceColor pieceColor) {
        super(x, y, pieceColor, 1); // Rook has a value of 5
    }

    @Override
    public boolean isValidMove(int newX, int newY, Piece[][] board) {
        // Check if new position is within the board bounds
        if (newX < 0 || newX >= 8 || newY < 0 || newY >= 8) {
            return false;
        }

        int deltaX = newX - x;
        int deltaY = newY - y;

        // Rook must move strictly horizontally or vertically (not both)
        if ((deltaX != 0 && deltaY != 0) || (deltaX == 0 && deltaY == 0)) {
            return false; // No movement or diagonal movement
        }

        // Check if destination has a friendly piece
        Piece destPiece = board[newY][newX]; // y-axis reversed
        if (destPiece != null && destPiece.pieceColor == this.pieceColor) {
            return false;
        }

        // Determine direction of movement (stepX and stepY)
        int stepX = Integer.compare(deltaX, 0); // 1 (right), -1 (left), or 0 (no x movement)
        int stepY = Integer.compare(deltaY, 0); // 1 (down), -1 (up), or 0 (no y movement)

        // Start checking from the next square in the path
        int currentX = x + stepX;
        int currentY = y + stepY;

        // Traverse the path to the destination (exclusive)
        while (currentX != newX || currentY != newY) {
            // Check if current position is occupied (using reversed y-axis)
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
}