import javax.swing.JPanel;
import java.awt.*;

public class Panel extends JPanel {
    private final int WIDTH = 800;
    private final int HEIGHT = 800;
    private final int UNIT_SIZE = 100;
    private StringBuilder board;
    Panel()
    {
        setPreferredSize(new Dimension(800,800));
        board = new StringBuilder("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
    }
    @Override
    protected void paintComponent(Graphics g) {
        drawBoard(g);
    }

    private void drawBoard(Graphics g)
    {

        Graphics2D g2 = (Graphics2D) g;
        for( int i = 0; i < WIDTH;  i+= UNIT_SIZE) {
            for (int j = 0; j < HEIGHT; j += UNIT_SIZE) {


                if ((i / UNIT_SIZE + j / UNIT_SIZE) % 2 == 0)
                    g2.setPaint(new Color(210, 180, 140, 255));
                else
                    g2.setPaint(new Color(139, 69, 19));
                g2.fillRect(i, j, UNIT_SIZE, UNIT_SIZE);

            }
        }
    }
    private void drawPieces(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        for( int i = 0; i < WIDTH;  i+= UNIT_SIZE) {
            for (int j = 0; j < HEIGHT; j += UNIT_SIZE) {


                switch (board.charAt(i / UNIT_SIZE))
                {

                }

                g2.fillRect(i, j, UNIT_SIZE, UNIT_SIZE);

            }
        }
    }


}
