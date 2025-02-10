import javax.swing.JFrame;
import java.awt.*;

public class Frame extends JFrame {
    Frame()
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        Panel panel = new Panel();
        setLayout(new BorderLayout());
        add(panel,BorderLayout.CENTER);
        pack();
        setTitle("Chess AI");
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
//