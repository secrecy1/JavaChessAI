import javax.swing.JFrame;

public class Frame extends JFrame {
    Frame()
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new Panel());
        pack();
        setTitle("Chess AI");
        setLocationRelativeTo(null);
        setResizable(true);
        setVisible(true);
    }
}
