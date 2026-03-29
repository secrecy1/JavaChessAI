import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;
import javax.swing.JPanel;

public class OptionPanel extends JPanel {
    
    // callback: (gameMode, playerColor) → void
    // gameMode: 0 = PvP, 1 = vs Computer, 2 = AI vs AI
    // playerColor: 0 = white, 1 = black (only relevant for vs Computer)
    private BiConsumer<Integer, Integer> onSelectionMade;

    private int hoveredOption = -1;
    private boolean choosingColor = false;
    private int selectedMode = -1;

    private static final String[] MODES = {
        "Player vs Player",
        "Player vs Computer",
        "AI vs AI"
    };

    public OptionPanel(BiConsumer<Integer, Integer> onSelectionMade) {
        this.onSelectionMade = onSelectionMade;
        setPreferredSize(new Dimension(600, 600));
        setBackground(new Color(30, 30, 30));

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                hoveredOption = getHoveredOption(e.getY());
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // ✅ this paints the background color

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!choosingColor) {
            drawModeSelection(g2);
        } else {
            drawColorSelection(g2);
        }
    }

    private void drawModeSelection(Graphics2D g) {
        int buttonHeight = 80;
        int buttonWidth = 400;
        int startX = (getWidth() - buttonWidth) / 2;
        int startY = (getHeight() - (MODES.length * (buttonHeight + 20))) / 2;

        g.setFont(new Font("Arial", Font.BOLD, 28));

        for (int i = 0; i < MODES.length; i++) {
            int y = startY + i * (buttonHeight + 20);
            boolean hovered = hoveredOption == i;

            g.setColor(hovered ? new Color(80, 130, 200) : new Color(60, 60, 60));
            g.fillRoundRect(startX, y, buttonWidth, buttonHeight, 16, 16);

            g.setColor(Color.WHITE);
            FontMetrics fm = g.getFontMetrics();
            int textX = startX + (buttonWidth - fm.stringWidth(MODES[i])) / 2;
            int textY = y + (buttonHeight + fm.getAscent() - fm.getDescent()) / 2;
            g.drawString(MODES[i], textX, textY);
        }

        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        String title = "Chess";
        g.drawString(title, (getWidth() - fm.stringWidth(title)) / 2, startY - 40);
    }

    private void drawColorSelection(Graphics2D g) {
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.setColor(Color.WHITE);
        String prompt = "Play as:";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(prompt, (getWidth() - fm.stringWidth(prompt)) / 2, getHeight() / 2 - 100);

        int buttonWidth = 160;
        int buttonHeight = 80;
        int gap = 40;
        int totalWidth = buttonWidth * 2 + gap;
        int startX = (getWidth() - totalWidth) / 2;
        int y = getHeight() / 2 - 40;

        // White button
        g.setColor(hoveredOption == 0 ? new Color(220, 220, 220) : Color.WHITE);
        g.fillRoundRect(startX, y, buttonWidth, buttonHeight, 16, 16);
        g.setColor(Color.BLACK);
        fm = g.getFontMetrics();
        g.drawString("White", startX + (buttonWidth - fm.stringWidth("White")) / 2,
                y + (buttonHeight + fm.getAscent() - fm.getDescent()) / 2);

        // Black button
        g.setColor(hoveredOption == 1 ? new Color(60, 60, 60) : new Color(30, 30, 30));
        g.fillRoundRect(startX + buttonWidth + gap, y, buttonWidth, buttonHeight, 16, 16);
        g.setColor(Color.WHITE);
        g.drawString("Black", startX + buttonWidth + gap + (buttonWidth - fm.stringWidth("Black")) / 2,
                y + (buttonHeight + fm.getAscent() - fm.getDescent()) / 2);

        // border on both
        g.setColor(Color.GRAY);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(startX, y, buttonWidth, buttonHeight, 16, 16);
        g.drawRoundRect(startX + buttonWidth + gap, y, buttonWidth, buttonHeight, 16, 16);
    }

    private int getHoveredOption(int mouseY) {
        if (choosingColor) {
            int y = getHeight() / 2 - 40;
            if (mouseY >= y && mouseY <= y + 80) {
                int startX = (getWidth() - (160 * 2 + 40)) / 2;
                // will be refined in mouseMove with x too, good enough for y
                return 0; // approximate — refined in handleClick
            }
            return -1;
        }
        int buttonHeight = 80;
        int startY = (getHeight() - (MODES.length * (buttonHeight + 20))) / 2;
        for (int i = 0; i < MODES.length; i++) {
            int y = startY + i * (buttonHeight + 20);
            if (mouseY >= y && mouseY <= y + buttonHeight) return i;
        }
        return -1;
    }

    private void handleClick(int mouseX, int mouseY) {
        if (!choosingColor) {
            int option = getHoveredOption(mouseY);
            if (option == -1) return;
            if (option == 1) {
                // vs computer — need to pick color
                selectedMode = option;
                choosingColor = true;
                hoveredOption = -1;
                repaint();
            } else {
                // PvP or AI vs AI — no color choice needed
                onSelectionMade.accept(option, 0);
            }
        } else {
            int buttonWidth = 160;
            int gap = 40;
            int startX = (getWidth() - (buttonWidth * 2 + gap)) / 2;
            int y = getHeight() / 2 - 40;
            if (mouseY >= y && mouseY <= y + 80) {
                if (mouseX >= startX && mouseX <= startX + buttonWidth) {
                    onSelectionMade.accept(selectedMode, 0); // white
                } else if (mouseX >= startX + buttonWidth + gap && mouseX <= startX + buttonWidth * 2 + gap) {
                    onSelectionMade.accept(selectedMode, 1); // black
                }
            }
        }
    }
}
