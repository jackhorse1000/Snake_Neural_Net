import java.awt.EventQueue;
import java.io.IOException;

import javax.swing.JFrame;

import jxl.write.WriteException;

public class PlaySnake extends JFrame {
    
    public PlaySnake(){
        try {
            add(new SnakeGame());
        } catch (WriteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
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
