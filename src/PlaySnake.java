import java.awt.EventQueue;
import javax.swing.JFrame;

public class PlaySnake extends JFrame {
    
    public PlaySnake(){
        add(new SnakeGame());
        
        setResizable(false);
        pack();
        
        setTitle("Snake");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
public static void main(String[] args) {
        
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {                
                JFrame ex = new PlaySnake();
                ex.setVisible(true);                
            }
        });
    }


}
