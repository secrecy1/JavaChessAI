
public class ChessAI {
    private GameState gameState;
    PieceColor color = PieceColor.BLACK;
    public ChessAI(GameState gameState, PieceColor color) 
    {
        this.gameState = gameState;
        this.color = color;
    }
    public void makeRandomMove()
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
    public void makeMove()
    {
        makeMinimaxMove();
    }
    public void makeMinimaxMove()
    {
        Node root = new Node(this.gameState, 3);

    }

    private int minimax(Node node, int depth, boolean maxPlayer) {
        if( depth == 0 || gameState.getGameStatus(gameState.board) != -1) return eval(gameState);
        if(maxPlayer)
        {
            int maxEval = Integer.MIN_VALUE;
            for(Node child: node.children)
            {
            int score = minimax(child, depth - 1, false);
            maxEval = Math.max(maxEval, score);
            }
        }
        else
        {
            int minEval = Integer.MAX_VALUE;
            for(Node child: node.children)
            {
            int score = minimax(child, depth - 1, true);
            minEval = Math.min(minEval, score);
            }
        }
        return -1;
    }
public int eval(GameState state) {
    int score = 0;
    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 8; col++) {
            Piece p = state.board[row][col];
            if (p == null) continue;
            int value = getPieceValue(p);
            score += (p.getColor() == PieceColor.WHITE) ? value : -value;
        }
    }
    return score;
}

private int getPieceValue(Piece p) {
    if (p instanceof Pawn)   return 1;
    if (p instanceof Knight) return 3;
    if (p instanceof Bishop) return 3;
    if (p instanceof Rook)   return 5;
    if (p instanceof Queen)  return 9;
    return 0; // King
}
}
