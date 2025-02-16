import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Panel extends JPanel {
    private static final int BOARD_SIZE = 8;
    private int unitSize = 75;
    private final StringBuilder board;
    private final Image[] whiteImages = new Image[6];
    private final Image[] blackImages = new Image[6];
    private final Image[] originalWhiteImages = new Image[6];
    private final Image[] originalBlackImages = new Image[6];
    private final double scalingFactor;

    public Panel() {
        GraphicsConfiguration gc = getGraphicsConfiguration();
        scalingFactor = gc != null ? gc.getDefaultTransform().getScaleX() : 1.0;
        setPreferredSize(new Dimension(600, 600));
        board = new StringBuilder("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
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
        int row = 0;
        int col = 0;

        for (int i = 0; i < board.length(); i++) {
            char c = board.charAt(i);

            if (row >= BOARD_SIZE) break;
            if (col >= BOARD_SIZE) {
                col = 0;
                row++;
                continue;
            }

            if (c == '/') {
                row++;
                col = 0;
                continue;
            }

            if (Character.isDigit(c)) {
                col += Character.getNumericValue(c);
                continue;
            }

            Image[] images = Character.isUpperCase(c) ? whiteImages : blackImages;
            char piece = Character.toLowerCase(c);

            switch (piece) {
                case 'p' -> drawImage(g2, images[0], col, row);
                case 'r' -> drawImage(g2, images[1], col, row);
                case 'n' -> drawImage(g2, images[2], col, row);
                case 'b' -> drawImage(g2, images[3], col, row);
                case 'q' -> drawImage(g2, images[4], col, row);
                case 'k' -> drawImage(g2, images[5], col, row);
            }
            col++;
        }
    }

    private void drawImage(Graphics2D g2, Image image, int col, int row) {
        int x = col * unitSize;
        int y = row * unitSize;
        g2.drawImage(image, x, y, null);
    }
}