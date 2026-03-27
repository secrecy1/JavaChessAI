public class ChessAI { 
private GameState gameState;
PieceColor color = PieceColor.BLACK;
private static final int[][] PAWN_TABLE = { 
    { 0,  0,  0,  0,  0,  0,  0,  0},
    {50, 50, 50, 50, 50, 50, 50, 50},
    {10, 10, 20, 30, 30, 20, 10, 10},
    { 5,  5, 10, 25, 25, 10,  5,  5},
    { 0,  0,  0, 20, 20,  0,  0,  0},
    { 5, -5,-10,  0,  0,-10, -5,  5},
    { 5, 10, 10,-20,-20, 10, 10,  5},
    { 0,  0,  0,  0,  0,  0,  0,  0} 
};
private static final int[][] KNIGHT_TABLE = {
    {-50,-40,-30,-30,-30,-30,-40,-50},
    {-40,-20,  0,  0,  0,  0,-20,-40},
    {-30,  0, 10, 15, 15, 10,  0,-30},
    {-30,  5, 15, 20, 20, 15,  5,-30},
    {-30,  0, 15, 20, 20, 15,  0,-30},
    {-30,  5, 10, 15, 15, 10,  5,-30},
    {-40,-20,  0,  5,  5,  0,-20,-40},
    {-50,-40,-30,-30,-30,-30,-40,-50}
};
private static final int[][] BISHOP_TABLE = {
    {-20,-10,-10,-10,-10,-10,-10,-20},
    {-10,  0,  0,  0,  0,  0,  0,-10},
    {-10,  0,  5, 10, 10,  5,  0,-10},
    {-10,  5,  5, 10, 10,  5,  5,-10},
    {-10,  0, 10, 10, 10, 10,  0,-10},
    {-10, 10, 10, 10, 10, 10, 10,-10},
    {-10,  5,  0,  0,  0,  0,  5,-10},
    {-20,-10,-10,-10,-10,-10,-10,-20}
};
private static final int[][] ROOK_TABLE = {
    { 0,  0,  0,  0,  0,  0,  0,  0},
    { 5, 10, 10, 10, 10, 10, 10,  5},
    {-5,  0,  0,  0,  0,  0,  0, -5},
    {-5,  0,  0,  0,  0,  0,  0, -5},
    {-5,  0,  0,  0,  0,  0,  0, -5},
    {-5,  0,  0,  0,  0,  0,  0, -5},
    {-5,  0,  0,  0,  0,  0,  0, -5},
    { 0,  0,  0,  5,  5,  0,  0,  0}
};
private static final int[][] QUEEN_TABLE = {
    {-20,-10,-10, -5, -5,-10,-10,-20},
    {-10,  0,  0,  0,  0,  0,  0,-10},
    {-10,  0,  5,  5,  5,  5,  0,-10},
    { -5,  0,  5,  5,  5,  5,  0, -5},
    {  0,  0,  5,  5,  5,  5,  0, -5},
    {-10,  5,  5,  5,  5,  5,  0,-10},
    {-10,  0,  5,  0,  0,  0,  0,-10},
    {-20,-10,-10, -5, -5,-10,-10,-20}
};
private static final int[][] KING_TABLE = {
    {-30,-40,-40,-50,-50,-40,-40,-30},
    {-30,-40,-40,-50,-50,-40,-40,-30},
    {-30,-40,-40,-50,-50,-40,-40,-30},
    {-30,-40,-40,-50,-50,-40,-40,-30},
    {-20,-30,-30,-40,-40,-30,-30,-20},
    {-10,-20,-20,-20,-20,-20,-20,-10},
    { 20, 20,  0,  0,  0,  0, 20, 20},
    { 20, 30, 10,  0,  0, 10, 30, 20}
};
private static final int[][] KING_MIDGAME_TABLE = {
    {-30,-40,-40,-50,-50,-40,-40,-30},
    {-30,-40,-40,-50,-50,-40,-40,-30},
    {-30,-40,-40,-50,-50,-40,-40,-30},
    {-30,-40,-40,-50,-50,-40,-40,-30},
    {-20,-30,-30,-40,-40,-30,-30,-20},
    {-10,-20,-20,-20,-20,-20,-20,-10},
    { 20, 20,  0,  0,  0,  0, 20, 20},
    { 20, 30, 10,  0,  0, 10, 30, 20}
};
private static final int[][] KING_ENDGAME_TABLE = {
    {-50,-40,-30,-20,-20,-30,-40,-50},
    {-30,-20,-10,  0,  0,-10,-20,-30},
    {-30,-10, 20, 30, 30, 20,-10,-30},
    {-30,-10, 30, 40, 40, 30,-10,-30},
    {-30,-10, 30, 40, 40, 30,-10,-30},
    {-30,-10, 20, 30, 30, 20,-10,-30},
    {-30,-30,  0,  0,  0,  0,-30,-30},
    {-50,-30,-30,-30,-30,-30,-30,-50}
};

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

if(gameState.getOutcome() != -1) return; 
        makeMinimaxMove();
    }
    public void makeMinimaxMove()
    {

        Node root = new Node(this.gameState, 4);
        minimax(root, root.depth, color == PieceColor.WHITE, Integer.MIN_VALUE, Integer.MAX_VALUE);
        Node bestNode = root.children.get(0);
        int bestScore = (color == PieceColor.WHITE) ? (Integer.MIN_VALUE) : Integer.MAX_VALUE;
        for(Node children: root.children)
        {
            if(color == PieceColor.WHITE && children.score > bestScore)
            {
                bestScore = children.score;
                bestNode = children;
            }
            else if(color == PieceColor.BLACK && children.score < bestScore)
            {
                bestScore = children.score;
                bestNode = children;
            }

        }
       gameState.copy(bestNode.gameState); 

    }

    private int minimax(Node node, int depth, boolean maxPlayer, int alpha, int beta) {
        if( depth == 0 || node.gameState.getGameStatus(gameState.board) != -1) return eval(node.gameState);
        if(maxPlayer)
        {
            int maxEval = Integer.MIN_VALUE;
            for(Node child: node.children)
            {
            int score = minimax(child, depth - 1, false, alpha, beta);
            child.score = score;
            maxEval = Math.max(maxEval, score);
            alpha = Math.max(alpha, score);
            if(beta <= alpha)
                break;
            }
            return maxEval;
        }
        else
        {
            int minEval = Integer.MAX_VALUE;
            for(Node child: node.children)
            {
            int score = minimax(child, depth - 1, true, alpha, beta);
            child.score = score;
            minEval = Math.min(minEval, score);
            beta = Math.min(beta, score);
            if(beta <= alpha)
                break;
            }
        return minEval;
        }
    }
public int eval(GameState state) {
    int score = 0;
    int stateOfGame = state.getGameStatus(state.board);
    if(stateOfGame == 1) return Integer.MIN_VALUE;
    if(stateOfGame == 2) return Integer.MAX_VALUE;
    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 8; col++) {
            Piece p = state.board[row][col];
            if (p == null) continue;
            int value = getPieceValue(p);
            value += getPositionalBonus(p, row, col);
            score += (p.getColor() == PieceColor.WHITE) ? value : -value;

        }
    }

    return score;
}

private int getPieceValue(Piece p) {
    if (p instanceof Pawn)   return 100;
    if (p instanceof Knight) return 320;
    if (p instanceof Bishop) return 330;
    if (p instanceof Rook)   return 500;
    if (p instanceof Queen)  return 900;
    return 20000; // King
}
private int getPositionalBonus(Piece p, int row, int col) {
    // tables are from white's perspective, so flip row for black
    int r = (p.getColor() == PieceColor.WHITE) ? row : 7 - row;
    if (p instanceof Pawn)   return PAWN_TABLE[r][col];
    if (p instanceof Knight) return KNIGHT_TABLE[r][col];
    if (p instanceof Bishop) return BISHOP_TABLE[r][col];
    if (p instanceof Rook)   return ROOK_TABLE[r][col];
    if (p instanceof Queen)  return QUEEN_TABLE[r][col];
    if (p instanceof King)   return KING_TABLE[r][col];
    return 0;
}
}
