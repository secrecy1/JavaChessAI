public class ChessAI {
    private GameState gameState;
    public ChessAI(GameState gameState) {this.gameState = gameState;}
    public void makeMove()
    {
       int row = (int)(Math.random() * 8);
       int col =(int)(Math.random() * 8);
        gameState.makeMove(1, 3, gameState.board[0][3]);

        
    }
}

