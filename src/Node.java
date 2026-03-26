import java.util.ArrayList;
import java.util.List;

public class Node {
public ArrayList<Node> children;
private GameState gameState;
private int depth;
public Node( GameState gameState, int depth)
{
    children = new ArrayList<>();
    this.gameState = gameState;
    if(depth > 0)
        generateChildNodes();
}
public void generateChildNodes()
{
for(int row = 0; row < 8; row++)
{
    for(int col = 0; col < 8; col++)
    {
     Piece selectedPiece = gameState.board[row][col];
     if(selectedPiece == null) continue;
     for(int r = 0; r < 8; r++)
     {
         for(int c = 0; c < 8; c++)
         {
             if(gameState.isValidMove(c, r, selectedPiece))
             {
                 //TODO : make a copy gamestate and pass that in instead of the parent nodes gameState
              gameState.makeMove(r, c, selectedPiece);
              children.add(new Node( gameState, depth - 1));
             }
         }
     }
      

    }

}

}
}
