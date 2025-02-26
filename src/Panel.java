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
    BoardMouseListener bm;

    public Panel() {
        GraphicsConfiguration gc = getGraphicsConfiguration();
        scalingFactor = gc != null ? gc.getDefaultTransform().getScaleX() : 1.0;
        setPreferredSize(new Dimension(600, 600));
        fenString = new StringBuilder("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        board = fenToArray(fenString);
        bm = new BoardMouseListener();
        addMouseListener(bm);
        addMouseMotionListener(bm);
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
        bm.drawBoard(g2d);
        bm.drawPieces(g2d);
        g2d.dispose();
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
                case 'P' -> ans[row][col] = new Pawn(col,row ,PieceColor.WHITE);
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
        private Piece selectedPiece;
        private int originalRow, originalCol;
        private int dragOffsetX, dragOffsetY;
        private int currentMouseX, currentMouseY;

        @Override
        public void mousePressed(MouseEvent e) {
            if(e.getButton() == MouseEvent.BUTTON1)
            {
                // Convert to scaled coordinates
                int x = (int) (e.getX() / scalingFactor);
                int y = (int) (e.getY() / scalingFactor);

                int col = x / unitSize;
                int row = y / unitSize;

                selectedPiece = board[row][col];
                if (selectedPiece != null) {
                    // track original position
                    originalRow = row;
                    originalCol = col;
                    // Update current mouse position (scaled coordinates)
                    currentMouseX = (int) (e.getX() / scalingFactor);
                    currentMouseY = (int) (e.getY() / scalingFactor);
                    repaint();
                }
            }

        }

        @Override
        public void mouseDragged(MouseEvent e) {

                if (selectedPiece != null) {
                    // Update current mouse position (scaled coordinates)
                    currentMouseX = (int) (e.getX() / scalingFactor);
                    currentMouseY = (int) (e.getY() / scalingFactor);
                    repaint();
                }

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if(e.getButton() == MouseEvent.BUTTON1) {
                if (selectedPiece != null) {
                    // Convert to scaled coordinates and calculate target position
                    int x = (int) (e.getX() / scalingFactor);
                    int y = (int) (e.getY() / scalingFactor);

                    int targetCol = x / unitSize;
                    int targetRow = y / unitSize;

                    // Validate move using piece's movement rules
                    if (targetRow >= 0 && targetRow < 8 &&
                            targetCol >= 0 && targetCol < 8
                           // !(selectedPiece.isValidMove(targetRow, targetCol, board))
                    ) {

                        // Handle captures
                        Piece targetPiece = board[targetRow][targetCol];
                        if (targetPiece != null && targetPiece.getColor() != selectedPiece.getColor()) {
                            board[targetRow][targetCol] = null;
                        }

                        // Update board state
                        board[originalRow][originalCol] = null;
                        selectedPiece.move(targetCol, targetRow);
                        board[targetRow][targetCol] = selectedPiece;
                    }

                    // Reset selection
                    selectedPiece = null;
                    repaint();
                }
            }
        }
        private void drawPieces(Graphics2D g2) {
            // Draw all non-selected pieces
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    Piece piece = board[row][col];
                    if (piece == null || piece == selectedPiece) continue;

                    Image pieceImage = (piece.getColor() == PieceColor.WHITE)
                            ? whiteImages[piece.IMAGEINDEX]
                            : blackImages[piece.IMAGEINDEX];
                    int x = col * unitSize;
                    int y = row * unitSize;
                    g2.drawImage(pieceImage, x, y, unitSize, unitSize, null);
                }
            }

            // Draw dragged piece on top (once)
            if (selectedPiece != null) {
                Image pieceImage = (selectedPiece.getColor() == PieceColor.WHITE)
                        ? whiteImages[selectedPiece.IMAGEINDEX]
                        : blackImages[selectedPiece.IMAGEINDEX];
                int x = currentMouseX - dragOffsetX - unitSize / 2;
                int y = currentMouseY - dragOffsetY - unitSize / 2;
                g2.drawImage(pieceImage, x, y, unitSize, unitSize, null);
            }
        }
        private void drawBoard(Graphics2D g2) {
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    int x = col * unitSize;
                    int y = row * unitSize;

                    g2.setColor((row + col) % 2 == 0 ?
                            new Color(210, 180, 140) :
                            new Color(139, 69, 19));
                    if(selectedPiece != null && selectedPiece.isValidMove(row,col,board))
                        g2.setColor(new Color(255, 0, 0));

                    g2.fillRect(x, y, unitSize, unitSize);



                }

            }
        }
    }
}

