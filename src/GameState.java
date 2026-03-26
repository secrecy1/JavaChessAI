import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
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
    public boolean awaitingPromotion; 
    private Piece lastMovedPiece;
    public int promotionx;
    public Piece promoPiece;
    public PieceColor playerColor;
    public boolean playingComputer;
    public boolean moveFailed;
    private ChessAI AI;
    private ChessAI AI2;
    public boolean twoAIS;
    private int index = 0;
    private ChessAI[] AIS = new ChessAI[2];
    public GameState(String fen, boolean playingComputer, boolean twoAIS)
    {
        this.playingComputer = playingComputer;
        promoPiece = null;
        promotion = false;
        playerColor = PieceColor.WHITE;
        lastMovedPiece = null;
        outcome = -1;
        backwards = false;
        loadFromFEN(fen);
        gameOver = false;
        awaitingPromotion = false;
        promotionx = -1;
        this.twoAIS = twoAIS;
    }
public GameState(GameState other) {
    // game logic state
    this.turnColor = other.turnColor;
    this.backwards = other.backwards;
    this.whiteKingsideCastle = other.whiteKingsideCastle;
    this.whiteQueensideCastle = other.whiteQueensideCastle;
    this.blackKingsideCastle = other.blackKingsideCastle;
    this.blackQueensideCastle = other.blackQueensideCastle;
    this.enPassantTarget = other.enPassantTarget;
    this.halfMoveClock = other.halfMoveClock;
    this.fullMoveNumber = other.fullMoveNumber;
    this.gameOver = other.gameOver;
    this.outcome = other.outcome;
    this.promotion = other.promotion;
    this.promotionx = other.promotionx;
    this.awaitingPromotion = other.awaitingPromotion;
    this.playerColor = other.playerColor;
    this.playingComputer = other.playingComputer;
    this.twoAIS = other.twoAIS;
    this.moveFailed = other.moveFailed;

    // deep copy the board
    this.board = new Piece[8][8];
    for (int i = 0; i < 8; i++)
        for (int j = 0; j < 8; j++)
            if (other.board[i][j] != null)
                this.board[i][j] = other.board[i][j].copy();

    // copy piece references using the NEW board's pieces, not the original's
    this.selectedPiece = (other.selectedPiece != null)
        ? this.board[other.selectedPiece.y][other.selectedPiece.x] : null;
    this.promoPiece = (other.promoPiece != null)
        ? this.board[other.promoPiece.y][other.promoPiece.x] : null;
    this.lastMovedPiece = (other.lastMovedPiece != null)
        ? this.board[other.lastMovedPiece.y][other.lastMovedPiece.x] : null;

    // UI state (neutral defaults — not needed for tree search)
    this.currentMouseX = 0;
    this.currentMouseY = 0;
    this.originalRow = 0;
    this.originalCol = 0;

    // deliberately skip AI, AI2, AIS — copies should not trigger AI moves
    this.AI = null;
    this.AI2 = null;
    this.AIS = new ChessAI[2];
    this.index = 0;
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

        if(isKingInCheck(board,turnColor) && checkMate(turnColor, board))
        {

            System.out.println(generateFen());
            gameOver = true;
            outcome = (turnColor == PieceColor.WHITE) ? 2 : 1;
        }

        else if(halfMoveClock == 50 || staleMate(turnColor, board) || isInsufficientMaterial())
        {

            System.out.println(generateFen());
            gameOver = true;
            outcome = 0;
        }
    }
    public int getGameStatus(Piece[][] board)
    {

        if(isKingInCheck(board,turnColor) && checkMate(turnColor, board))
        {

            return (turnColor == PieceColor.WHITE) ? 2 : 1;
        }

        else if(halfMoveClock == 50 || staleMate(turnColor, board) || isInsufficientMaterial())
        {

            return 0;
        }
        return -1;


    }

    public int getOutcome()
    {

        return outcome;
    }

    public void selectPiece(MouseEvent e, int unitSize, double scalingFactor)
    {
        if(promotion) return;
        if(playingComputer && turnColor != playerColor) return;
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
            selectedPiece = board[row][col];
            if (selectedPiece == null) return;
            if(playingComputer && selectedPiece.pieceColor != playerColor) return;
                // track original position
                originalRow = row;
                originalCol = col;
                currentMouseX = (int) (e.getX() / scalingFactor);
                currentMouseY = (int) (e.getY() / scalingFactor);
                // Update current mouse position (scaled coordinates)

    }

    public void makeMove(int targetRow, int targetCol)
    {

        // Validate move using piece's movement rules
        if (targetRow >= 0 && targetRow < 8 &&
                targetCol >= 0 && targetCol < 8 &&
                (selectedPiece.isValidMove(targetCol, targetRow, board)) && selectedPiece.getColor() == turnColor
                && !moveLeavesKingInCheck(originalRow,originalCol,targetRow,targetCol, board, turnColor)
        ) {

            // castling
            if (selectedPiece instanceof King && 
            ((King) selectedPiece).castlingDirection != SpecialMove.NONE 
            && !isKingInCheck(board,turnColor))
            {
                King king = (King) selectedPiece;
                handleCastling(king.castlingDirection, originalRow, originalCol);
                king.castlingDirection = SpecialMove.NONE;
                // Reset immediately

            }
            //en passant
        else if(selectedPiece instanceof Pawn && ((Pawn) selectedPiece).specialMove == SpecialMove.EN_PASSANT)
        {
            if(lastMovedPiece instanceof Pawn && ((Pawn) lastMovedPiece).isLastMoveWasDouble())
                {
                        //move Pawn
                        handleEnPassant(targetCol, targetRow);

                }

        }
            //normal moves
            else {



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
                ((selectedPiece.getColor() == PieceColor.WHITE && selectedPiece.y == 0) ||
                (selectedPiece.getColor() == PieceColor.BLACK && selectedPiece.y == 7)))
                {
                    promotionx = selectedPiece.x;
                    promotion = true;
                    board[targetRow][targetCol] = null;
                    selectedPiece = null;
                    return;
                }
                FlipSides();
            }

        }
                selectedPiece = null;
                promotion = false;
    }

    private int convertToScaled(int n, double scalingFactor)
    {
        return (int) (n / scalingFactor);
    }
    public void dragPiece(MouseEvent e, double scalingFactor)
    {
        if(promotion) return;
        if (selectedPiece != null) {
            // Update current mouse position (scaled coordinates)
            currentMouseX = (int) (e.getX() / scalingFactor);
            currentMouseY = (int) (e.getY() / scalingFactor);

        }
    }
    public void releasePiece(MouseEvent e, int unitSize, double scalingFactor)
    {
        if(promotion) return;
        if (selectedPiece == null) return;
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
        makeMove(targetRow, targetCol);
    }
    public void makeMove(int targetRow, int targetCol, Piece selectedPiece)
    {
        if(selectedPiece == null) return;
        originalCol = selectedPiece.x;
        originalRow = selectedPiece.y;

        // Validate move using piece's movement rules
        if (targetRow >= 0 && targetRow < 8 &&
                targetCol >= 0 && targetCol < 8 &&
                (selectedPiece.isValidMove(targetCol, targetRow, board)) && selectedPiece.getColor() == turnColor
                && !moveLeavesKingInCheck(originalRow,originalCol,targetRow,targetCol, board, turnColor)
        ) {

            // castling
            if (selectedPiece instanceof King && 
            ((King) selectedPiece).castlingDirection != SpecialMove.NONE 
            && !isKingInCheck(board,turnColor))
            {
                King king = (King) selectedPiece;
                handleCastling(king.castlingDirection, originalRow, originalCol);
                king.castlingDirection = SpecialMove.NONE;
                // Reset immediately

            }
            //en passant
        else if(selectedPiece instanceof Pawn && ((Pawn) selectedPiece).specialMove == SpecialMove.EN_PASSANT)
        {
            if(lastMovedPiece instanceof Pawn && ((Pawn) lastMovedPiece).isLastMoveWasDouble())
                {
                        //move Pawn
                        handleEnPassant(targetCol, targetRow);

                }

        }
            //normal moves
            else {



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
                ((selectedPiece.getColor() == PieceColor.WHITE && selectedPiece.y == 0) ||
                (selectedPiece.getColor() == PieceColor.BLACK && selectedPiece.y == 7)))
                {
                    promotionx = selectedPiece.x;
                    promotion = true;
                    board[targetRow][targetCol] = null;
                    selectedPiece = null;
                    return;
                }
                FlipSides();
            }

        }
                selectedPiece = null;
                promotion = false;
    }

    public static boolean moveLeavesKingInCheck(int startRow, int startCol, int targetRow, int targetCol, Piece[][] board, PieceColor turnColor) {
        Piece[][] tempBoard = simulateMove(startRow, startCol, targetRow, targetCol, board);
        return isKingInCheck(tempBoard, turnColor);
    }
    public void startAIBattle()
    {

        AIS[0] = AI;
        AIS[1] = AI2;
        new Thread(() -> {
            while (!gameOver) 
            {
                AIS[index % 2].makeMove();
                index++;
            }
    }).start();

    }
    public void FlipSides()
    {
        promotionx = -1;
        if(!playingComputer) backwards = !backwards;
        turnColor = (turnColor == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
        checkGameStatus(board);
        if(playingComputer && AI != null && playerColor != turnColor && !twoAIS)
        {
            AI.makeMove();
        }
        
        lastMovedPiece = selectedPiece;

    }
private boolean isInsufficientMaterial() {
    // Collect all pieces still on the board
    List<Piece> whitePieces = new java.util.ArrayList<>();
    List<Piece> blackPieces = new java.util.ArrayList<>();

    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 8; col++) {
            Piece p = board[row][col];
            if (p == null) continue;
            if (p.getColor() == PieceColor.WHITE) whitePieces.add(p);
            else blackPieces.add(p);
        }
    }

    // Any pawns, rooks, or queens = sufficient material
    for (Piece p : whitePieces)
        if (p instanceof Pawn || p instanceof Rook || p instanceof Queen) return false;
    for (Piece p : blackPieces)
        if (p instanceof Pawn || p instanceof Rook || p instanceof Queen) return false;

    int wCount = whitePieces.size(); // includes king
    int bCount = blackPieces.size();

    // K vs K
    if (wCount == 1 && bCount == 1) return true;

    // K+B vs K  or  K+N vs K  (either side)
    if (wCount == 1 && bCount == 2) return true;
    if (wCount == 2 && bCount == 1) return true;

    // K+B vs K+B — draw only if bishops are on the same color square
    if (wCount == 2 && bCount == 2) {
        Piece wb = whitePieces.stream().filter(p -> p instanceof Bishop).findFirst().orElse(null);
        Piece bb = blackPieces.stream().filter(p -> p instanceof Bishop).findFirst().orElse(null);
        if (wb != null && bb != null) {
            boolean wBishopOnLight = (wb.x + wb.y) % 2 == 0;
            boolean bBishopOnLight = (bb.x + bb.y) % 2 == 0;
            return wBishopOnLight == bBishopOnLight;
        }
    }

    return false;
}


    // Simulate a move on a temporary board
    private static Piece[][] simulateMove(int startRow, int startCol, int targetRow, int targetCol, Piece[][] board) {
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
    public static boolean isKingInCheck(Piece[][] board, PieceColor kingColor) {
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
    private static boolean checkMate(PieceColor color, Piece[][] board)
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
                            if(piece.isValidMove(c,r,board) && !moveLeavesKingInCheck(row, col,r,c, board, color))
                                return false;
                }
            }
        }
        return true;
    }
private static boolean staleMate(PieceColor color, Piece[][] board)
{
    // Rule 1: king must NOT be in check
    if (isKingInCheck(board, color)) return false;

    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 8; col++) {
            Piece piece = board[row][col];
            if (piece != null && piece.pieceColor == color) {

                for (int r = 0; r < 8; r++) {
                    for (int c = 0; c < 8; c++) {
                        if (piece.isValidMove(c, r, board)
                            && !moveLeavesKingInCheck(row, col, r, c, board, color)) {
                            return false; // Found at least one legal move
                        }
                    }
                }
            }
        }
    }
    return true; // no legal moves + not in check
}

    private void handleCastling(SpecialMove castlingType, int kingRow, int kingCol) {
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

        if (rook == null) {
            return;
        }
        if (!(rook instanceof Rook)) {
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
  
        lastMovedPiece = selectedPiece;
        FlipSides();
    }
    public void handleEnPassant(int targetCol, int targetRow)
    {
        board[originalRow][originalCol] = null;
        selectedPiece.move(targetCol, targetRow);
        board[targetRow][targetCol] = selectedPiece;
        board[lastMovedPiece.y][lastMovedPiece.x] = null;
        FlipSides();
        //turnColor = (turnColor == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
        //backwards = !backwards;
	
    }
    public boolean isValidMove(int targetCol, int targetRow)
    {
        if (targetRow >= 0 && targetRow < 8 &&
                targetCol >= 0 && targetCol < 8 &&
                (selectedPiece.isValidMove(targetCol, targetRow, board)) && selectedPiece.getColor() == turnColor
                && !moveLeavesKingInCheck(originalRow,originalCol,targetRow,targetCol, board, turnColor)
        ) {
            if(selectedPiece instanceof Pawn && ((Pawn) selectedPiece).specialMove == SpecialMove.EN_PASSANT)
            {

                return lastMovedPiece instanceof Pawn && ((Pawn) lastMovedPiece).isLastMoveWasDouble() && lastMovedPiece.y == selectedPiece.y && Math.abs(lastMovedPiece.x - selectedPiece.x) == 1;

            }


            if (selectedPiece instanceof King) {
                King king = (King) selectedPiece;
                if (king.castlingDirection != SpecialMove.NONE) {
                    return !isKingInCheck(board, turnColor);



                }
            }

            return true;
        }
        return false;

        }
    public void addAI(ChessAI AI)
    {
        this.AI = AI;
    }

    public boolean isValidMove(int targetCol, int targetRow, Piece selectedPiece)
    {
       int originalRow = selectedPiece.y;
       int originalCol = selectedPiece.x;
        if (targetRow >= 0 && targetRow < 8 &&
                targetCol >= 0 && targetCol < 8 &&
                (selectedPiece.isValidMove(targetCol, targetRow, board)) && selectedPiece.getColor() == turnColor
                && !moveLeavesKingInCheck(originalRow,originalCol,targetRow,targetCol, board, turnColor)
        ) {
            if(selectedPiece instanceof Pawn && ((Pawn) selectedPiece).specialMove == SpecialMove.EN_PASSANT)
            {

                return lastMovedPiece instanceof Pawn && ((Pawn) lastMovedPiece).isLastMoveWasDouble() && lastMovedPiece.y == selectedPiece.y && Math.abs(lastMovedPiece.x - selectedPiece.x) == 1;

            }
            if (selectedPiece instanceof King) {
                King king = (King) selectedPiece;
                if (king.castlingDirection != SpecialMove.NONE) {
                    return !isKingInCheck(board, turnColor);
                }
            }
            return true;
        }
        return false;
        }

    // maybe if needed will refactor this to return the number of available moves instead of if there are any available moves
    public boolean hasAvailableMove(Piece selectedPiece)
    {
        int total = 0;
        for(int row = 0; row < 8; row++)
            for(int col = 0; col < 8; col++)
                total += isValidMove(row, col, selectedPiece) ? 1 : 0;
        return total != 0;
    }
public void addAI2(ChessAI AI)
{

this.AI2 = AI;

}
public void copy(GameState other)
{

    this.turnColor = other.turnColor;
    this.backwards = other.backwards;
    this.whiteKingsideCastle = other.whiteKingsideCastle;
    this.whiteQueensideCastle = other.whiteQueensideCastle;
    this.blackKingsideCastle = other.blackKingsideCastle;
    this.blackQueensideCastle = other.blackQueensideCastle;
    this.enPassantTarget = other.enPassantTarget;
    this.halfMoveClock = other.halfMoveClock;
    this.fullMoveNumber = other.fullMoveNumber;
    this.gameOver = other.gameOver;
    this.outcome = other.outcome;
    this.promotion = other.promotion;
    this.promotionx = other.promotionx;
    this.awaitingPromotion = other.awaitingPromotion;
    this.playerColor = other.playerColor;
    this.playingComputer = other.playingComputer;
    this.twoAIS = other.twoAIS;
    this.moveFailed = other.moveFailed;

    // deep copy the board
    this.board = new Piece[8][8];
    for (int i = 0; i < 8; i++)
        for (int j = 0; j < 8; j++)
            if (other.board[i][j] != null)
                this.board[i][j] = other.board[i][j].copy();

    // copy piece references using the NEW board's pieces, not the original's
    this.selectedPiece = (other.selectedPiece != null)
        ? this.board[other.selectedPiece.y][other.selectedPiece.x] : null;
    this.promoPiece = (other.promoPiece != null)
        ? this.board[other.promoPiece.y][other.promoPiece.x] : null;
    this.lastMovedPiece = (other.lastMovedPiece != null)
        ? this.board[other.lastMovedPiece.y][other.lastMovedPiece.x] : null;

}

}
