import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.ArrayList;

public class ChessAI { 

private GameState gameState;
PieceColor color = PieceColor.BLACK;
private boolean timeOut = false;
private long startTime;
private long timeLimitMs;
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
        // empty log file
 try (FileWriter fw = new FileWriter("Logs.txt", false)) {
            // Opening with 'false' and immediately closing wipes the file
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void makeRandomMove()
    {
if(gameState.getOutcome() != -1) return; 
        Piece selectedPiece;
        int selectrow = (int)(Math.random() * 8);
        int selectcol = (int)(Math.random() * 8);
        selectedPiece = gameState.board[selectrow][selectcol];
        while(selectedPiece == null || selectedPiece.pieceColor != color || gameState.hasAvailableMove(selectedPiece) == 0)
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

try (BufferedWriter bw = new BufferedWriter(new FileWriter("Logs.txt", true))) {
            bw.write(gameState.generateFen());
            bw.newLine();
        }
         catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void makeMinimaxMove()
    {
         startTime = System.currentTimeMillis();
         timeLimitMs = 5000;
        int maxDepth = 64;
        timeOut = false;
        Node bestNodeEver = null;
        
        for(int currentDepth = 1; currentDepth < maxDepth; currentDepth++)
        {

        Node root = new Node(this.gameState);
        root.generateChildNodes();
        if (timeOut) {
            System.out.println("Timed out during depth " + currentDepth + ". Using results from depth " + (currentDepth - 1));
            break; 
        }
        // If we made it here, this depth finished! Update bestNodeEver
        Node bestNodeForDepth = null;
        int bestScore = (color == PieceColor.WHITE) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
 boolean isWhite = color == PieceColor.WHITE;
        for (Node child : root.children) {
            int score = minimax(child, currentDepth - 1, !isWhite, alpha, beta);
            if (timeOut) break;
            child.score = score;
System.out.println("depth:" + currentDepth + " score:" + score);
            if (color == PieceColor.WHITE && child.score > bestScore) {
               bestScore = child.score;
                bestNodeForDepth = child;
                alpha = Math.max(alpha, score);
            } else if (color == PieceColor.BLACK && child.score < bestScore) {
                bestScore = child.score;
                bestNodeForDepth = child;
                beta = Math.min(beta, score);
            }
 if (beta <= alpha) break; // prune remaining siblings (we know this is better than the others no point in evaluating the other nodes
        }
        
        if (bestNodeForDepth != null && (Math.abs(bestNodeForDepth.score) > 100000) ) {
          boolean alreadyHaveMate = bestNodeEver != null && 
                Math.abs(bestNodeEver.score) >= 100000;
            
            if (!alreadyHaveMate)
                bestNodeEver = bestNodeForDepth;
        }

        }
        if (bestNodeEver != null) {
            ArrayList<String> savedHistory = new ArrayList<>(gameState.positionHistory);
            gameState.copy(bestNodeEver.gameState);
            gameState.checkGameStatus(gameState.board);
            gameState.positionHistory = savedHistory;
            gameState.positionHistory.add(gameState.generateFen());
            gameState.checkGameStatus(gameState.board);
        }
        else
        {
            makeRandomMove();
            System.out.println("best Node null made random move");
        }
    }

    private int minimax(Node node, int depth, boolean maxPlayer, int alpha, int beta) {
    if (System.currentTimeMillis() - startTime > timeLimitMs) {
        timeOut = true;
    }
    if (timeOut) return 0;
        if( depth == 0 || node.gameState.getGameStatus(node.gameState.board) != -1) return quiesce(alpha, beta, node.gameState, depth, maxPlayer);
        node.generateChildNodes();
        rankChildren(node.children, maxPlayer); 
        if(maxPlayer)
        {
            int maxEval = Integer.MIN_VALUE;
            for(Node child: node.children)
            {
            int score = minimax(child, depth - 1, false, alpha, beta);
            if (timeOut) return 0;
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
            if(timeOut) return 0;
            child.score = score;
            minEval = Math.min(minEval, score);
            beta = Math.min(beta, score);
            if(beta <= alpha)
                break;
            }
        return minEval;
        }
    }
public int eval(GameState state, int depth) {
    int score = 0;
    int stateOfGame = state.getGameStatus(state.board);
   if(stateOfGame == 1) return 100000 + depth * 10; 
    if(stateOfGame == 2) return -100000 - depth * 10;
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
public void rankChildren(ArrayList<Node> children, boolean isWhiteTurn)
{
children.sort((node1, node2) -> {
        int score1 = guessMoveScore(node1.gameState, isWhiteTurn);
        int score2 = guessMoveScore(node2.gameState, isWhiteTurn);
        
        // We want the HIGHEST scores first (Descending order)
        return Integer.compare(score2, score1); 
    });
}
// MVV-LVA
public int guessMoveScore(GameState gameState, boolean isWhiteTurn)
{
    // find victim cycle
    //
    int bestScore = 0;
    for(Piece[] row: gameState.board)
    {
        for(Piece col: row)
        {
            if(col != null && ((isWhiteTurn && col.pieceColor == PieceColor.BLACK) || (!isWhiteTurn && col.pieceColor == PieceColor.WHITE)))
            {
                int victimScore = getPieceValue(col);
                // look at all the agressors of the victim
                ArrayList<Piece> agressors = gameState.getAgressors(col);
                for(Piece pieces : agressors)
                {
        
                    int agressorScore = getPieceValue(pieces);
                    bestScore = Math.max(bestScore, (victimScore - agressorScore));

                }
                
            }
        }
    }
    return bestScore;
}
public int quiesce(int alpha, int beta, GameState state, int depth, boolean maxPlayer) {
    if (System.currentTimeMillis() - startTime > timeLimitMs) {
        timeOut = true;
        return 0;
    }

    int standPat = eval(state, depth);
    int bestValue = standPat;

    if (maxPlayer) {
        if (standPat >= beta) return standPat;
        if (standPat > alpha) alpha = standPat;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece target = state.board[row][col];
                // only enemy pieces are valid capture targets
                if (target == null || target.getColor() == state.turnColor) continue;

                ArrayList<Piece> attackers = state.getAgressors(target);
                for (Piece attacker : attackers) {
                    GameState copy = new GameState(state);
                    copy.makeMove(row, col, copy.board[attacker.y][attacker.x]);

                    int score = quiesce(alpha, beta, copy, depth - 1, false);
                    if (timeOut) return 0;

                    if (score > bestValue) bestValue = score;
                    if (score >= beta) return bestValue;
                    if (score > alpha) alpha = score;
                }
            }
        }
    } else {
        if (standPat <= alpha) return standPat;
        if (standPat < beta) beta = standPat;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece target = state.board[row][col];
                // only enemy pieces are valid capture targets
                if (target == null || target.getColor() == state.turnColor) continue;

                ArrayList<Piece> attackers = state.getAgressors(target);
                for (Piece attacker : attackers) {
                    GameState copy = new GameState(state);
                    copy.makeMove(row, col, copy.board[attacker.y][attacker.x]);

                    int score = quiesce(alpha, beta, copy, depth - 1, true);
                    if (timeOut) return 0;

                    if (score < bestValue) bestValue = score;
                    if (score <= alpha) return bestValue;
                    if (score < beta) beta = score;
                }
            }
        }
    }

    return bestValue;
}
}
