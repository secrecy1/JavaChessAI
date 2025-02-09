import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class Panel extends JPanel {
    private final int WIDTH = 600;
    private final int HEIGHT = 600;
    private final int UNIT_SIZE = 75;
    private StringBuilder board;
    private Image[] whiteImages = new Image[6];
    private Image[] blackImages = new Image[6];
    Panel()
    {
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        board = new StringBuilder("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

        initImages();
    }
    private void initImages()
    {

        try {
            whiteImages[0] = ImageIO.read(new File("Images/75px_white_pawn.png"));
            whiteImages[1] = ImageIO.read(new File("Images/75px_white_rook.png"));
            whiteImages[2] = ImageIO.read(new File("Images/75px_white_knight.png"));
            whiteImages[3] = ImageIO.read(new File("Images/75px_white_bishop.png"));
            whiteImages[4] = ImageIO.read(new File("Images/75px_white_queen.png"));
            whiteImages[5] = ImageIO.read(new File("Images/75px_white_king.png"));
            blackImages[0] = ImageIO.read(new File("Images/75px_black_pawn.png"));
            blackImages[1] = ImageIO.read(new File("Images/75px_black_rook.png"));
            blackImages[2] = ImageIO.read(new File("Images/75px_black_knight.png"));
            blackImages[3] = ImageIO.read(new File("Images/75px_black_bishop.png"));
            blackImages[4] = ImageIO.read(new File("Images/75px_black_queen.png"));
            blackImages[5] = ImageIO.read(new File("Images/75px_black_king.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    protected void paintComponent(Graphics g) {
        drawBoard(g);
        drawPieces(g);
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

    private void drawPieces(Graphics g) {
        Image[] images;
        Graphics2D g2 = (Graphics2D) g;
        int row = 0;
        int col = 0;
        int k = 0;

        while (k < board.length()) { // Use a while loop to iterate through the FEN string
            char c = board.charAt(k);

            if (Character.isLetter(c)) { // Check if it is a letter
                if (Character.isUpperCase(c)) {
                    images = whiteImages;
                } else {
                    images = blackImages;
                }

                char piece = Character.toLowerCase(c); // Get lowercase for switch

                switch (piece) {
                    case 'p':
                        g2.drawImage(images[0], col * UNIT_SIZE, row * UNIT_SIZE, null);
                        break;
                    case 'r':
                        g2.drawImage(images[1], col * UNIT_SIZE, row * UNIT_SIZE, null);
                        break;
                    case 'n':
                        g2.drawImage(images[2], col * UNIT_SIZE, row * UNIT_SIZE, null);
                        break;
                    case 'b':
                        g2.drawImage(images[3], col * UNIT_SIZE, row * UNIT_SIZE, null);
                        break;
                    case 'q':
                        g2.drawImage(images[4], col * UNIT_SIZE, row * UNIT_SIZE, null);
                        break;
                    case 'k':
                        g2.drawImage(images[5], col * UNIT_SIZE, row * UNIT_SIZE, null);
                        break;
                }
                col++; // Move to the next column
            } else if (Character.isDigit(c)) { // Correctly handle empty squares
                col += Character.getNumericValue(c); // Increment column by the number
            } else if (c == '/') {
                row++; // Move to the next row
                col = 0; // Reset column
            }
            k++; // Move to the next character in the FEN string
        }
    }
    private boolean isUpper(char c)
    {
        return c < 'a' && c > '9';
    }
    private boolean isLower(char c)
    {
        return c > 'a' && c < 'z';
    }
    private char toLower(char c)
    {
        if(isUpper(c))
            return (char)(c+32);
        return c;
    }



}

