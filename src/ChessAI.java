public class ChessAI {
    private GameState gameState;
    PieceColor color = PieceColor.BLACK;
    public ChessAI(GameState gameState, PieceColor color) 
    {
        this.gameState = gameState;
        this.color = color;
    }
    public void makeMove()
    {
        if(gameState.getOutcome() != -1) return; 
        Piece selectedPiece;
        int selectrow = (int)(Math.random() * 8);
        int selectcol = (int)(Math.random() * 8);
        selectedPiece = gameState.board[selectrow][selectcol];
        while(selectedPiece == null || selectedPiece.pieceColor != color || !gameState.hasAvailableMove(selectedPiece))
        {
            System.out.println("finding valid piece: "+ color + "| " + gameState.turnColor);
            selectrow = (int)(Math.random() * 8);
            selectcol = (int)(Math.random() * 8);
            selectedPiece = gameState.board[selectrow][selectcol];
        }
        int row = (int)(Math.random() * 8);
        int col = (int)(Math.random() * 8);
        while(!gameState.isValidMove(col, row, selectedPiece))
        {
            System.out.println("finding valid move | selected peice info: " + selectedPiece);
            row = (int)(Math.random() * 8);
            col = (int)(Math.random() * 8);
        }
        gameState.makeMove(row, col, selectedPiece);
        if(gameState.promotion)
        {
        int promoy = (gameState.turnColor == PieceColor.BLACK) ? 7: 0;
        // qkrb
        int promoChoice = (int)(Math.random() * 4);
        switch(promoChoice)
        {
            case 0:
                gameState.promoPiece = new Queen(gameState.promotionx, promoy, gameState.turnColor);
                break;
            case 1:
                gameState.promoPiece = new Knight(gameState.promotionx, promoy, gameState.turnColor);
                break;
            case 2: 
                gameState.promoPiece = new Rook(gameState.promotionx, promoy, gameState.turnColor);
                break;
            case 3:
                gameState.promoPiece = new Bishop(gameState.promotionx, promoy, gameState.turnColor);
                break;
            default:
                break;
        }
        gameState.board [promoy][gameState.promotionx] = gameState.promoPiece;
        gameState.promotion = false;
        gameState.FlipSides();
        }
    }
}
