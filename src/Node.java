import java.util.ArrayList;
import java.util.List;

public class Node {
public ArrayList<Node> children;
private Piece[][] value;
public Node(Piece[][] value, GameState gameState)
{
    children = new ArrayList<>();
    this.value = value;
}
public void generateChildNodes()
{


}
}
