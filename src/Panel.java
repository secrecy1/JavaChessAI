import javax.imageio.ImageIO;
import javax.swing.*;
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
    private String fenString;
    private final Image[] whiteImages = new Image[6];
    private final Image[] blackImages = new Image[6];
    private final Image[] originalWhiteImages = new Image[6];
    private final Image[] originalBlackImages = new Image[6];

    private final double scalingFactor;
    BoardMouseListener bm;
    GameState gameState;
    boolean gameOverScreen;
    Thread gameLoop;
    public Panel() {

        GraphicsConfiguration gc = getGraphicsConfiguration();
        scalingFactor = gc != null ? gc.getDefaultTransform().getScaleX() : 1.0;
        setPreferredSize(new Dimension(600, 600));
        fenString = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        gameState = new GameState(fenString);
        gameState.turnColor = PieceColor.WHITE;
        setLayout(null);

       // gameOverPanel.setPreferredSize(new Dimension(unitSize*2,unitSize*2));

        bm = new BoardMouseListener();
        addMouseListener(bm);
        addMouseMotionListener(bm);
        initImages();
        scaleImages();

        gameLoop = new Thread(new GameLoop());
        gameLoop.start();

        gameOverScreen = false;


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


        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        calculateUnitSize();
        scaleImages();
        bm.drawBoard(g2d);
        if(gameState.backwards)
            bm.drawBackwards(g2d);
        else bm.drawPieces(g2d);
        if(gameState.gameOver)
            bm.drawGameOverScreen(g2d);
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



    private class BoardMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e)
        {
            if(gameState.gameOver)
            {
                gameOverScreen = true;
                removeMouseListener(bm);
                removeMouseListener(bm);
                repaint();

                
            }
           else if(e.getButton() == MouseEvent.BUTTON1 && !gameState.gameOver)
            {
                gameState.selectPiece(e, unitSize, scalingFactor);
                repaint();
            }
        }

        @Override
        public void mouseDragged(MouseEvent e)
        {
            if(gameState.gameOver)
            {
                gameOverScreen = true;
                removeMouseListener(bm);
                removeMouseListener(bm);
                repaint();
            }
           else {
                gameState.dragPiece(e, scalingFactor);
            }
                repaint();

        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
            if(gameState.gameOver)
            {
                gameOverScreen = true;
                removeMouseListener(bm);
                removeMouseListener(bm);
                repaint();

            }
           else if(e.getButton() == MouseEvent.BUTTON1 && !gameState.gameOver)
            {
                gameState.releasePiece(e,unitSize,scalingFactor);
                repaint();
            }
           repaint();
        }
        private void drawPieces(Graphics2D g2)
        {
            // Draw all non-selected pieces
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    Piece piece = gameState.board[row][col];
                    if (piece == null || piece == gameState.selectedPiece) continue;

                    Image pieceImage = (piece.getColor() == PieceColor.WHITE)
                            ? whiteImages[piece.IMAGEINDEX]
                            : blackImages[piece.IMAGEINDEX];
                    int x = col * unitSize;
                    int y = row * unitSize;
                    g2.drawImage(pieceImage, x, y, unitSize, unitSize, null);
                }
            }


            // Draw dragged piece on top (once)
            if (gameState.selectedPiece != null)
            {
                Image pieceImage = (gameState.selectedPiece.getColor() == PieceColor.WHITE)
                        ? whiteImages[gameState.selectedPiece.IMAGEINDEX]
                        : blackImages[gameState.selectedPiece.IMAGEINDEX];
                int x = gameState.currentMouseX - unitSize / 2;
                int y = gameState.currentMouseY - unitSize / 2;
                g2.drawImage(pieceImage, x, y, unitSize, unitSize, null);
            }
        }
        private void drawBackwards(Graphics2D g2)
        {
            // Draw all non-selected pieces
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    Piece piece = gameState.board[row][col];
                    if (piece == null || piece == gameState.selectedPiece) continue;

                    Image pieceImage = (piece.getColor() == PieceColor.WHITE)
                            ? whiteImages[piece.IMAGEINDEX]
                            : blackImages[piece.IMAGEINDEX];
                    int x = col * unitSize;
                    int y =  unitSize * 7 - row * unitSize;
                    g2.drawImage(pieceImage, x, y, unitSize, unitSize, null);
                }
            }


            // Draw dragged piece on top (once)
            if (gameState.selectedPiece != null) {
                Image pieceImage = (gameState.selectedPiece.getColor() == PieceColor.WHITE)
                        ? whiteImages[gameState.selectedPiece.IMAGEINDEX]
                        : blackImages[gameState.selectedPiece.IMAGEINDEX];
                //int x = currentMouseX - dragOffsetX - unitSize / 2;
                //int y = currentMouseY - dragOffsetY - unitSize / 2;
                int x = gameState.currentMouseX  - unitSize / 2;
                int y = gameState.currentMouseY  - unitSize / 2;
                g2.drawImage(pieceImage, x, y, unitSize, unitSize, null);
            }
        }
        private void drawBoard(Graphics2D g2) {
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    int x = col * unitSize;
                    int y = row * unitSize;

                   Color color = (row + col) % 2 == 0 ?
                            new Color(210, 180, 140) :
                            new Color(139, 69, 19);
                   if(gameState.backwards)
                        color = (row + col) % 2 == 0 ? new Color(139, 69, 19)
                                : new Color(210, 180, 140);

                   g2.setColor(color);
                   // if its backwards offset the row
                    if(gameState.backwards)
                        row = 7 - row;
                    if(gameState.selectedPiece != null && gameState.isValidMove(col,row))
                        g2.setColor(new Color(255, 255, 255));


                    g2.fillRect(x, y, unitSize, unitSize);
                    /* if(gameState.promotion)
                    {
                    
                    }*/


                }

            }

        }
        private void drawGameOverScreen(Graphics2D g2)
        {
            int bannerX,bannerY,BannerWidth,BannerHeight,arcWidth,arcHeight;
            bannerX = unitSize*4 - ((int) (unitSize*1.5));
            bannerY = unitSize*4 - ((int) (unitSize*1.5));
            BannerWidth = unitSize * 3;
            BannerHeight = unitSize * 3;
            arcWidth = 15;
            arcHeight = 15;

            g2.setColor(new Color(175, 125, 80));
            //drawing shape where text will be (x,y,width,height,arcWidth,arcHeight)
            g2.fillRoundRect(bannerX,
                    bannerY,
                     BannerWidth,
                     BannerHeight,
                    arcWidth,
                    arcHeight);
            Font font = new Font("Arial", Font.BOLD, (BannerWidth / 10));
            g2.setFont(font);
            String gameOverMessage;
            if(gameState.getOutcome() == 0) {
                g2.setColor(new Color(128, 128, 128));
                gameOverMessage = "Tie";
            }
           else if ((gameState.getOutcome() == 1))
           {
                g2.setColor(new Color(255, 255, 255));
               gameOverMessage = "White Wins!";
            } else
            {
                g2.setColor(new Color(0, 0, 0));
                gameOverMessage = "Black Wins";
            }

            g2.drawString(gameOverMessage,bannerX + (BannerWidth / 5),bannerY + (BannerWidth / 5));

        }
        private int convertToScaled(int n)
        {
            return (int) (n / scalingFactor);
        }


    }
    private class GameLoop implements Runnable {

        @Override
        public void run() {
            while (true) {
                repaint();
            }
        }
    }
}

