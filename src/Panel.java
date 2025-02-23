import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Panel extends JPanel {
    private static final int BOARD_SIZE = 8;
    private int unitSize = 75;
    private StringBuilder fenString;
    private final Image[] whiteImages = new Image[6];
    private final Image[] blackImages = new Image[6];
    private final Image[] originalWhiteImages = new Image[6];
    private final Image[] originalBlackImages = new Image[6];
    private final double scalingFactor;
    private Piece[][] board = new Piece[8][8];

    public Panel() {
        GraphicsConfiguration gc = getGraphicsConfiguration();
        scalingFactor = gc != null ? gc.getDefaultTransform().getScaleX() : 1.0;
        setPreferredSize(new Dimension(600, 600));
        fenString = new StringBuilder("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        board = fenToArray(fenString);
        addMouseListener(new BoardMouseListener());
        initImages();
        scaleImages();
    }

    private void initImages() {
        try {
            // Load white pieces
            originalWhiteImages[0] = ImageIO.read(new File("Images/75px_white_pawn.png"));
            originalWhiteImages[1] = ImageIO.read(new File("Images/75px_white_rook.png"));
            originalWhiteImages[2] = ImageIO.read(new File("Images/75px_white_knight.png"));
            originalWhiteImages[3] = ImageIO.read(new File("Images/75px_white_bishop.png"));
            originalWhiteImages[4] = ImageIO.read(new File("Images/75px_white_queen.png"));
            originalWhiteImages[5] = ImageIO.read(new File("Images/75px_white_king.png"));

            // Load black pieces
            originalBlackImages[0] = ImageIO.read(new File("Images/75px_black_pawn.png"));
            originalBlackImages[1] = ImageIO.read(new File("Images/75px_black_rook.png"));
            originalBlackImages[2] = ImageIO.read(new File("Images/75px_black_knight.png"));
            originalBlackImages[3] = ImageIO.read(new File("Images/75px_black_bishop.png"));
            originalBlackImages[4] = ImageIO.read(new File("Images/75px_black_queen.png"));
            originalBlackImages[5] = ImageIO.read(new File("Images/75px_black_king.png"));
        } catch (IOException e) {
            throw new RuntimeException("Error loading images", e);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Apply high-DPI scaling
        g2d.scale(scalingFactor, scalingFactor);

        // Set quality rendering hints
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        calculateUnitSize();
        scaleImages();
        drawBoard(g2d);
        drawPieces(g2d);
        g2d.dispose();
    }

    private void drawBoard(Graphics2D g2) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                int x = col * unitSize;
                int y = row * unitSize;

                g2.setColor((row + col) % 2 == 0 ?
                        new Color(210, 180, 140) :
                        new Color(139, 69, 19));
                g2.fillRect(x, y, unitSize, unitSize);
            }
        }
    }

    private void calculateUnitSize() {
        int size = Math.min(getWidth(), getHeight());
        unitSize = (int) (size / (double) BOARD_SIZE);
    }

    private void scaleImages() {
        for (int i = 0; i < originalWhiteImages.length; i++) {
            whiteImages[i] = scaleImage(originalWhiteImages[i], unitSize, unitSize);
            blackImages[i] = scaleImage(originalBlackImages[i], unitSize, unitSize);
        }
    }

    private Image scaleImage(Image img, int targetWidth, int targetHeight) {
        int scaledWidth = (int) (targetWidth * scalingFactor);
        int scaledHeight = (int) (targetHeight * scalingFactor);

        BufferedImage scaledImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.drawImage(img, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        return scaledImage;
    }


    private void drawPieces(Graphics2D g2) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                Piece piece = board[row][col];


                if (piece == null) continue;


                Image pieceImage = (piece.getColor() == PieceColor.WHITE)
                        ? whiteImages[piece.IMAGEINDEX]
                        : blackImages[piece.IMAGEINDEX];

                int x = col * unitSize;
                int y = row * unitSize;


                g2.drawImage(pieceImage, x, y, unitSize, unitSize, null);
            }
        }
    }
    private void drawImage(Graphics2D g2, Image image, int col, int row) {
        int x = col * unitSize;
        int y = row * unitSize;
        g2.drawImage(image, x, y, null);
    }

    public static Piece[][] fenToArray(StringBuilder s) {
        Piece[][] ans = new Piece[8][8];
        int col = 0;
        int row = 0;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            // Stop parsing if we've filled all 8 rows
            if (row >= 8) break;

            if (Character.isDigit(c)) {
                // Handle empty squares
                int num = Character.getNumericValue(c);
                int end = Math.min(col + num, 8); // Prevent overflow
                Arrays.fill(ans[row], col, end, null);
                col = end;
                continue; // Skip rest of loop iteration
            } else if (c == '/') {
                // Move to next row
                row++;
                col = 0;
                continue; // Skip rest of loop iteration
            }

            // Only process valid piece characters here
            if (col >= 8) {
                // Wrap to next row if current is full
                row++;
                col = 0;
                if (row >= 8) break; // Final row check
            }


            switch (c) {
                case 'p' -> ans[row][col] = new Pawn(col,row,PieceColor.BLACK);
                case 'r' -> ans[row][col] = new Rook(col,row,PieceColor.BLACK);
                case 'n' -> ans[row][col] = new Knight(col,row,PieceColor.BLACK);
                case 'b' -> ans[row][col] = new Bishop(col,row,PieceColor.BLACK);
                case 'q' -> ans[row][col] = new Queen(col,row,PieceColor.BLACK);
                case 'k' -> ans[row][col] = new King(col,row,PieceColor.BLACK);
                case 'P' -> ans[row][col] = new Pawn(col,row,PieceColor.WHITE);
                case 'R' -> ans[row][col] = new Rook(col,row,PieceColor.WHITE);
                case 'N' -> ans[row][col] = new Knight(col,row,PieceColor.WHITE);
                case 'B' -> ans[row][col] = new Bishop(col,row,PieceColor.WHITE);
                case 'Q' -> ans[row][col] = new Queen(col,row,PieceColor.WHITE);
                case 'K' -> ans[row][col] = new King(col,row,PieceColor.WHITE);
            }
            col++;
        }

        return ans;
    }

    private class BoardMouseListener extends MouseAdapter {
        Piece selectedPiece;
        @Override
        public void mousePressed(MouseEvent e) {
            // Calculate clicked square
            int col = (e.getX())  / unitSize;
            int row = (e.getY()) /  unitSize;
            selectedPiece = board[row][col];
            System.out.print((char)(65 + col));
            System.out.println(8 - row);
            if (selectedPiece != null) {
                // Calculate offset from piece's top-left corner
              int dragOffsetX = e.getX() - (col * unitSize);
              int dragOffsetY = e.getY() - (row * unitSize);

            }


        }
        @Override
        public void mouseDragged(MouseEvent e) {
            if (selectedPiece != null) {
                selectedPiece.move(e.getX(),e.getY());
                repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (selectedPiece != null) {
                int targetCol = e.getX() / unitSize;
                int targetRow = e.getY() / unitSize;

                // Validate move (simplified example)
                if (true) {
                    // Remove from old position
                    board[selectedPiece.x][selectedPiece.y] = null;
                    // Update piece's position
                    selectedPiece.move(targetCol,targetRow);
                    // Place in new position
                    board[targetRow][targetCol] = selectedPiece;
                }

                selectedPiece = null;
                repaint();
            }
        }


    }
}

