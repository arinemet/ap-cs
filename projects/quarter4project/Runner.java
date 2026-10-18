import javax.swing.JFrame;

public class Runner {
    public static void main(String[] args) {
        JFrame f = new JFrame("Chess - Q4 Project");
        Screen s = new Screen();
        f.add(s);
        f.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setResizable(false);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        s.requestFocusInWindow();
    }
}
