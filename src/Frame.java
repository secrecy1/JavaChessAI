import javax.swing.JFrame;
import java.awt.*;
import javax.swing.JPanel;

public class Frame extends JFrame {
    
    Frame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setTitle("Chess AI");

        CardLayout cardLayout = new CardLayout();
        JPanel container = new JPanel(cardLayout);

        OptionPanel optionPanel = new OptionPanel((mode, color) -> {
            // mode: 0=PvP, 1=vsComputer, 2=AIvsAI
            // color: 0=white, 1=black

            boolean playingComputer = (mode == 1);
            boolean twoAIs = (mode == 2);
            PieceColor playerColor = (color == 0) ? PieceColor.WHITE : PieceColor.BLACK;

            Panel gamePanel = new Panel(playingComputer, twoAIs, playerColor);
            container.add(gamePanel, "game");
            cardLayout.show(container, "game");
            pack();
        });

        container.add(optionPanel, "options");
        cardLayout.show(container, "options");

        add(container);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
