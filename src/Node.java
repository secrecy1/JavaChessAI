import java.util.ArrayList;
import java.util.Comparator;
public class Node {
public ArrayList<Node> children;
public GameState gameState;
public int score;
public Node( GameState gameState)
{
    children = new ArrayList<>();
    Node bestChild;
    this.gameState = gameState;
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
              GameState copy = new GameState(gameState);
              copy.makeMove(r, c, copy.board[row][col]);
              if(copy.promotion)
              {
                  int promoy = (copy.turnColor == PieceColor.BLACK) ? 7 : 0;
                            // turnColor has already flipped after makeMove, so flip it back to get the pawn's color
                      PieceColor pawnColor = (copy.turnColor);                     Class[] promoOptions = {Queen.class, Rook.class, Bishop.class, Knight.class};

                            for (Class promoType : promoOptions) {
                                GameState promoCopy = new GameState(copy);
                                Piece promoPiece;
                                if (promoType == Queen.class)
                                    promoPiece = new Queen(promoCopy.promotionx, promoy, pawnColor);
                                else if (promoType == Rook.class)
                                    promoPiece = new Rook(promoCopy.promotionx, promoy, pawnColor);
                                else if (promoType == Bishop.class)
                                    promoPiece = new Bishop(promoCopy.promotionx, promoy, pawnColor);
                                else
                                    promoPiece = new Knight(promoCopy.promotionx, promoy, pawnColor);
                                promoCopy.board[promoy][promoCopy.promotionx] = promoPiece;
                                promoCopy.promotion = false;
                                promoCopy.FlipSides();
                                children.add(new Node(promoCopy));
                            }
              }
              else{
              children.add(new Node(copy));
              }
             }
         }
     }
      

    }

}

}
}
