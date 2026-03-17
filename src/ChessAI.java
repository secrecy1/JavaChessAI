public class ChessAI {
    private GameState gameState;
    public ChessAI(GameState gameState) {this.gameState = gameState;}
    public void makeMove()
    {
        gameState.makeMove(1, 3, gameState.board[0][3]);
        System.out.println(gameState.board[1][3]);
    }
}

