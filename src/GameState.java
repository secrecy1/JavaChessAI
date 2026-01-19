import java.awt.event.MouseEvent;
import java.util.Arrays;

public class GameState
{
    public PieceColor turnColor;
    public boolean backwards;
    public Piece[][] board;
    boolean whiteKingsideCastle, whiteQueensideCastle, blackKingsideCastle, blackQueensideCastle;
    String enPassantTarget; // e.g., "a3"
    int halfMoveClock;
    int fullMoveNumber;
    public Piece selectedPiece;
    private int originalRow, originalCol;
    public int currentMouseX;
    public int currentMouseY;
    public boolean promotion;
    boolean gameOver;
    int outcome;
    private int lastMoveStartRow, lastMoveStartCol, lastMoveTargetRow, lastMoveTargetCol;
    public boolean awaitingPromotion; 
    private Piece lastMovedPiece;
    public GameState(String fen)
    {
        promotion = false;
        lastMoveStartRow = -1;
        lastMoveStartCol = -1;
        lastMoveTargetRow = -1;
        lastMoveTargetCol = -1;
        lastMovedPiece = null;
        outcome = -1;
        backwards = false;
        loadFromFEN(fen);
        gameOver = false;
        awaitingPromotion = false;
    }
    private void loadFromFEN(String fen)
    {
        //1. board
        String[] fields = fen.split(" ");
        Piece[][] ans = new Piece[8][8];
        int col = 0;
        int row = 0;
        for (int i = 0; i < fields[0].length(); i++) {
            char c = fields[0].charAt(i);
            // Stop parsing if we've filled all 8 rows
            if (row >= 8) break;
            if (Character.isDigit(c)) {
                // Handle empty squares
                int num = Character.getNumericValue(c);
                int end = Math.min(col + num, 8); // Prevent overflow
                Arrays.fill(ans[row], col, end, null);
                col = end;
                continue; // Skip rest of loop iteration
            } else if (c == '/') {
                // Move to next row
                row++;
                col = 0;
                continue; // Skip rest of loop iteration
            }
            // Only process valid piece characters here
            if (col >= 8) {
                // Wrap to next row if current is full
                row++;
                col = 0;
                if (row >= 8) break; // Final row check
            }
            switch (c) {
                case 'p' -> ans[row][col] = new Pawn(col,row,PieceColor.BLACK);
                case 'r' -> ans[row][col] = new Rook(col,row,PieceColor.BLACK);
                case 'n' -> ans[row][col] = new Knight(col,row,PieceColor.BLACK);
                case 'b' -> ans[row][col] = new Bishop(col,row,PieceColor.BLACK);
                case 'q' -> ans[row][col] = new Queen(col,row,PieceColor.BLACK);
                case 'k' -> ans[row][col] = new King(col,row,PieceColor.BLACK);
                case 'P' -> ans[row][col] = new Pawn(col,row ,PieceColor.WHITE);
                case 'R' -> ans[row][col] = new Rook(col,row,PieceColor.WHITE);
                case 'N' -> ans[row][col] = new Knight(col,row,PieceColor.WHITE);
                case 'B' -> ans[row][col] = new Bishop(col,row,PieceColor.WHITE);
                case 'Q' -> ans[row][col] = new Queen(col,row,PieceColor.WHITE);
                case 'K' -> ans[row][col] = new King(col,row,PieceColor.WHITE);
            }
            col++;
        }

        board = ans;
        //2. Active color
        turnColor = fields[1].equals("w") ? PieceColor.WHITE : PieceColor.BLACK;
        //3. castling rights
        whiteKingsideCastle = fields[2].contains("K");
        whiteQueensideCastle = fields[2].contains("Q");
        blackKingsideCastle = fields[2].contains("k");
        blackQueensideCastle = fields[2].contains("q");
        //4. enPassant
        enPassantTarget = fields[3].equals("-") ? null : fields[3];
        // 5. Half-Move Clock
        halfMoveClock = Integer.parseInt(fields[4]);

        // 6. Full-Move Number
        fullMoveNumber = Integer.parseInt(fields[5]);
    }
    private String generateFen()
    {
        StringBuilder fen = new StringBuilder();
        // 1. Piece Placement
        for (int row = 0; row < 8; row++) {
            int empty = 0;
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece == null) {
                    empty++;
                } else {
                    if (empty > 0) {
                        fen.append(empty);
                        empty = 0;
                    }
                    fen.append(piece.getFenSymbol());
                }
            }
            if (empty > 0) fen.append(empty);
            if (row < 7) fen.append("/");
        }
        // 2. Active Color
        fen.append(" ").append(turnColor == PieceColor.WHITE ? "w" : "b");

        StringBuilder castling = new StringBuilder();
        if (whiteKingsideCastle) castling.append('K');
        if (whiteQueensideCastle) castling.append('Q');
        if (blackKingsideCastle) castling.append('k');
        if (blackQueensideCastle) castling.append('q');
        fen.append(" ").append(!castling.isEmpty() ? castling : "-");


        // 4. En Passant Target
        fen.append(" ").append(enPassantTarget != null ? enPassantTarget : "-");

        // 5. Half-Move Clock
        fen.append(" ").append(halfMoveClock);

        // 6. Full-Move Number
        fen.append(" ").append(fullMoveNumber);


        return fen.toString();

    }
    public void updateMoveCounters(Piece piece, int targetRow, int targetCol) {
        if (piece instanceof Pawn || board[targetRow][targetCol] != null) {
            halfMoveClock = 0;
        } else {
            halfMoveClock++;
        }

        if (turnColor == PieceColor.BLACK) {
            fullMoveNumber++;
        }
    }
    public void checkGameStatus(Piece[][] board)
    {

        if(halfMoveClock == 50)
        {
            gameOver = true;
            outcome = 0;
        }
        else if(isKingInCheck(board,turnColor) && checkMate(turnColor))
        {
            gameOver = true;
            outcome = (turnColor == PieceColor.WHITE) ? 2 : 1;
        }



    }
    public int getOutcome()
    {

        return outcome;
    }

    public void selectPiece(MouseEvent e, int unitSize, double scalingFactor)
    {

            int x = convertToScaled(e.getX(),scalingFactor);
            int y = convertToScaled(e.getY(),scalingFactor);
            int row;
            int col = x / unitSize;
            row = y / unitSize;
            // if the board is backwards offset the row
            if(backwards)
            {
                row = 7 - y / unitSize;

                currentMouseX = (int) (e.getX() / scalingFactor);
                currentMouseY = (int) (e.getY() / scalingFactor);
            }

    }
    private int convertToScaled(int n, double scalingFactor)
    {
        return (int) (n / scalingFactor);
    }
    public void dragPiece(MouseEvent e, double scalingFactor)
    {
        if (selectedPiece != null) {
            // Update current mouse position (scaled coordinates)
            currentMouseX = (int) (e.getX() / scalingFactor);
            currentMouseY = (int) (e.getY() / scalingFactor);

        }
    }
    public void releasePiece(MouseEvent e, int unitSize, double scalingFactor)
    {
            if (selectedPiece != null) {
                int x = convertToScaled(e.getX(),scalingFactor);
                int y = convertToScaled(e.getY(),scalingFactor);
                // Calculate target position
                int targetRow;
                int targetCol;
                targetCol =  x / unitSize;
                targetRow =  y / unitSize;
                //if its backwards we need the opposite row kind of like reflecting of the y-axis
                if(backwards)
                    targetRow = 7 - y / unitSize;

                // Validate move using piece's movement rules
                if (targetRow >= 0 && targetRow < 8 &&
                        targetCol >= 0 && targetCol < 8 &&
                        (selectedPiece.isValidMove(targetCol, targetRow, board)) && selectedPiece.getColor() == turnColor
                        && !moveLeavesKingInCheck(originalRow,originalCol,targetRow,targetCol)
                ) {

                System.out.println("_-__-__");
                System.out.println(selectedPiece instanceof Pawn);
                System.out.println(selectedPiece.specialMove);
                    // castling
                    if (selectedPiece instanceof King && 
                    ((King) selectedPiece).castlingDirection != SpecialMove.NONE 
                    && !isKingInCheck(board,turnColor))
                    {
                        King king = (King) selectedPiece;
                        System.out.println(king.castlingDirection);
                        handleCastling(king.castlingDirection, originalRow, originalCol);
                        king.castlingDirection = SpecialMove.NONE;
                        // Reset immediately

                    }
                    //en passant
                else if(selectedPiece instanceof Pawn && ((Pawn) selectedPiece).specialMove == SpecialMove.EN_PASSANT)
                {
                            System.out.println("*************************");
                            System.out.println("En passant");
                            System.out.println(lastMovedPiece instanceof Pawn);
                            System.out.println(((Pawn) lastMovedPiece).isLastMoveWasDouble());

                            System.out.println( lastMovedPiece.x);
                            System.out.println( lastMovedPiece.y);
                            System.out.println("Last moved Piece color: "+ lastMovedPiece.pieceColor);
                            System.out.println("Selected Piece color: " + selectedPiece.pieceColor);
                            System.out.println("-----------------------");
                            if(lastMovedPiece instanceof Pawn && ((Pawn) lastMovedPiece).isLastMoveWasDouble())
                            {
                                System.out.println("worked");
                                //move Pawn
                                handleEnPassant(targetCol, targetRow);

                            }

                }
                    //normal moves
                    else {

                        System.out.println(selectedPiece.hasMoved);


                        // Handle captures
                        Piece targetPiece = board[targetRow][targetCol];

                        updateMoveCounters(selectedPiece, targetRow, targetCol);
                        if (targetPiece != null && targetPiece.getColor() != selectedPiece.getColor()) {
                            board[targetRow][targetCol] = null;

                        }


                        // Update board state
                        
                        board[originalRow][originalCol] = null;

                        selectedPiece.move(targetCol, targetRow);
                        board[targetRow][targetCol] = selectedPiece;
                        // check for promotion
                        if(selectedPiece instanceof Pawn && 
                        (selectedPiece.getColor() == PieceColor.WHITE && selectedPiece.y == 0) ||
                        (selectedPiece.getColor() == PieceColor.BLACK && selectedPiece.y == 7))
                        {
                            promotion = true;
                            System.out.println("Promotion detected");
                               /* 
                            JPanel promotionPanel = new JPanel();
                            promotionPanel.setLayout(new GridLayout(1, 4));

                            JButton queen = new JButton("Q");
                            JButton rook  = new JButton("R");
                            JButton bishop= new JButton("B");
                            JButton knight= new JButton("N");

promotionPanel.add(queen);
promotionPanel.add(rook);
promotionPanel.add(bishop);
promotionPanel.add(knight);*/
                        }
                        if(!awaitingPromotion)
                        {
                        turnColor = (turnColor == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
                        backwards = !backwards;
                        checkGameStatus(board);
                        lastMovedPiece = selectedPiece;
                        }
                    }
                }




                // Reset selection

                selectedPiece = null;

            }

    }
    public boolean moveLeavesKingInCheck(int startRow, int startCol, int targetRow, int targetCol) {
        Piece[][] tempBoard = simulateMove(startRow, startCol, targetRow, targetCol);
        return isKingInCheck(tempBoard, turnColor);
    }

    // Simulate a move on a temporary board
    private Piece[][] simulateMove(int startRow, int startCol, int targetRow, int targetCol) {
        // 1. Create a deep copy of the board
        Piece[][] tempBoard = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null) {
                    tempBoard[i][j] = board[i][j].copy();

                }
            }
        }

        // 2. Execute the move on the temp board
        Piece movedPiece = tempBoard[startRow][startCol];
        tempBoard[startRow][startCol] = null;
        tempBoard[targetRow][targetCol] = movedPiece;
        if (movedPiece != null) movedPiece.move(targetCol, targetRow); // Update piece coordinates

        return tempBoard;
    }
    public boolean isKingInCheck(Piece[][] board, PieceColor kingColor) {
        // 1. Find the king's position
        int kingX = -1, kingY = -1;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece instanceof King && piece.getColor() == kingColor) {
                    kingX = col;
                    kingY = row;
                    break;
                }
            }
            if (kingX != -1) break;
        }
        if (kingX == -1) return false; // Should never happen

        // 2. Check if any opponent's pieces attack the king
        PieceColor opponentColor = (kingColor == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece != null && piece.getColor() == opponentColor) {
                    if (piece.isValidMove(kingX, kingY, board)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private boolean checkMate(PieceColor color)
    {
        for(int row = 0; row < 8; row++)
        {
            for (int col = 0; col < 8; col++)
            {
                Piece piece = board[row][col];
                if(piece != null && piece.pieceColor == color)
                {
                    for(int r = 0; r < 8; r++)
                        for(int c = 0; c <8;c++)
                            if(piece.isValidMove(c,r,board) && !moveLeavesKingInCheck(row, col,r,c))
                                return false;
                }
            }
        }
        return true;
    }
    private void handleCastling(SpecialMove castlingType, int kingRow, int kingCol) {
        System.out.println("Handling castling: " + castlingType + " at (" + kingCol + "," + kingRow + ")");
        int rookCol, rookTargetCol, kingTargetCol;
        if (castlingType == SpecialMove.LEFT_CASTLE) {
            rookCol = 0;
            rookTargetCol = kingCol - 1;  // Rook moves to d-file
            kingTargetCol = kingCol - 2;  // King moves to c-file
        } else { // RIGHT_CASTLE
            rookCol = 7;
            rookTargetCol = kingCol + 1;  // Rook moves to f-file
            kingTargetCol = kingCol + 2;  // King moves to g-file
        }
        // Get rook from ORIGINAL position
        Piece rook = board[kingRow][rookCol];
        Piece king = board[kingRow][kingCol];

        System.out.println("Rook found at (" + rookCol + "," + kingRow + "): " + (rook != null ? rook.getClass().getSimpleName() : "null"));
        if (rook == null) {
            System.out.println("ERROR: Rook is null! Castling aborted.");
            return;
        }
        if (!(rook instanceof Rook)) {
            System.out.println("ERROR: Piece at rook position is not a rook! Castling aborted.");
            return;
        }
        // Move rook
        board[kingRow][rookCol] = null;
        board[kingRow][rookTargetCol] = rook;
        rook.move(rookTargetCol, kingRow);
        // King will be moved in the main move logic
        // king Move
        board[kingRow][kingCol] = null;
        board[kingRow][kingTargetCol] = king;
        king.move(kingTargetCol,kingRow);

        // We just need to update the target position for the king
        // The main move logic will handle moving the king to kingTargetCol
        //King move
  
        System.out.println("Castling completed successfully");
        lastMovedPiece = selectedPiece;

        turnColor = (turnColor == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
        backwards = !backwards;
    }
    public void handleEnPassant(int targetCol, int targetRow)
    {
        board[originalRow][originalCol] = null;
        selectedPiece.move(targetCol, targetRow);
        board[targetRow][targetCol] = selectedPiece;
        board[lastMovedPiece.y][lastMovedPiece.x] = null;
        turnColor = (turnColor == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
        backwards = !backwards;
	
    }
    public boolean isValidMove(int targetCol, int targetRow)
    {
        if (targetRow >= 0 && targetRow < 8 &&
                targetCol >= 0 && targetCol < 8 &&
                (selectedPiece.isValidMove(targetCol, targetRow, board)) && selectedPiece.getColor() == turnColor
                && !moveLeavesKingInCheck(originalRow,originalCol,targetRow,targetCol)
        ) {
            if(selectedPiece instanceof Pawn && ((Pawn) selectedPiece).specialMove == SpecialMove.EN_PASSANT)
            {

                return lastMovedPiece instanceof Pawn && ((Pawn) lastMovedPiece).isLastMoveWasDouble() && lastMovedPiece.y == selectedPiece.y && Math.abs(lastMovedPiece.x - selectedPiece.x) == 1;

            }


            if (selectedPiece instanceof King) {
                King king = (King) selectedPiece;
                System.out.println(king.castlingDirection);
                if (king.castlingDirection != SpecialMove.NONE) {
                    return !isKingInCheck(board, turnColor);



                }
            }

            return true;
        }
        return false;

        }





}
