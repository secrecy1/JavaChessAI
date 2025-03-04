public class Bishop extends Piece {
    public Bishop(int x, int y, PieceColor pieceColor) {
        super(x, y, pieceColor, 3);
    }

    @Override
    public boolean isValidMove(int newX, int newY, Piece[][] board) {
        // Check if new position is within the board bounds
        if (newX < 0 || newX >= 8 || newY < 0 || newY >= 8) {
            return false;
        }

        // Calculate deltas (change in x and y)
        int deltaX = newX - x;
        int deltaY = newY - y;

        // Check if the move is diagonal (absolute differences equal)
        if (Math.abs(deltaX) != Math.abs(deltaY)) {
            return false;
        }

        // Check if destination has a friendly piece
        Piece destPiece = board[newY][newX]; // Use [newY][newX] (y is reversed)
        if (destPiece != null && destPiece.pieceColor == this.pieceColor) {
            return false;
        }

        // Determine direction of movement for x and y
        int stepX = Integer.compare(deltaX, 0); // 1 or -1
        int stepY = Integer.compare(deltaY, 0); // 1 (down) or -1 (up) due to reversed y-axis

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