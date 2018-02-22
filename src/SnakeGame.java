
import jxl.Workbook;
import jxl.write.*;

import javax.swing.*;
import java.awt.*;
import java.awt.Label;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class SnakeGame extends JPanel implements ActionListener {
    
    //board details
    private final int B_WIDTH = 300;
    private final int B_HEIGHT = 300;
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = 900;
    private final int RANDOM_POS = 29;
    
    //used when user wants to play the game
    private final int DELAY = 140;
    
    //waiting limit before termination
    private final int TERMINATION_STEPS =100;
    
    //store the x and y coordinates of the snake
    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];
    
    //min and maxes for normailization
    private static double maxX;
    private static double maxY;
    private static double minX;
    private static double minY;
    
    
    //global variables for snake
    private boolean inGame =true;
    private int dots;
    private Timer timer;
    private int apple_x;
    private int apple_y;
    private boolean leftDirection =false;
    private boolean rightDirection =true;
    private boolean upDirection =false;
    private boolean downDirection =false;
    
    
    //used for inputs to the neural net to see which direction is free for the snake to move into
    public static boolean frontFree = false;
    public static boolean rightFree = false;
    public static boolean leftFree = false;
    
    
    
    //image resources
    private Image body;
    private Image apple;
    private Image head;
    
  
    
   
    //statistics for fitness in chromosone and user information
    public static int snakeLength;
    public static int maxSnakeLength;
    public static int stepsTaken;
    public static int iterationCounter;
    public static int applesEaten;
    public static int stepsTakenSinceLastFood; 
   
    
    //number of nodes for the neural net
    public static int noInputs = 14;
    public static int noHidden = 7;
    public static int noOutput = 4;
    
    
    //population size
    private int popSize = 500;
    
    
    //instance of the snake game
    public static SnakeGame snakeGame;
    
    //to terminate the learner
    public static boolean btnStoppedPushed;
    private static Console console;
    
    
    
    //load images
    private void loadImages() {

        ImageIcon bodyIc = new ImageIcon("body.jpg");
        body = bodyIc.getImage();

        ImageIcon appleIc = new ImageIcon("apple.png");
        apple = appleIc.getImage();

        ImageIcon headIc = new ImageIcon("head.png");
        head = headIc.getImage();
    }
    
    
    
    //intialise the neural net learner
    public SnakeGame() throws IOException, WriteException{
        
        btnStoppedPushed = false;
        
        inGame = true;
        snakeLength = 3;
        
        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        
        iterationCounter = 0;
        applesEaten = 0;
        stepsTaken = 0;
        stepsTakenSinceLastFood = 0;

        loadImages();
        initGame(); 
    }
    
    
    //used for the main class
    public static SnakeGame getClassInstance()
    {
        if (snakeGame == null)
        {
            try {
                //console lets us see the result from each iteration
                snakeGame = new SnakeGame();
                console = new Console();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }
            return snakeGame;
        }
        return snakeGame;
}
    
    
    public void runGame() throws InterruptedException{
        //Initialise the population
        Population population = new Population(popSize);
           
        Chromosone curChromosone; //current Chromosone we are working with
        iterationCounter = 0; //learning iteration
        maxSnakeLength = 3; //starts with length 3.
        
        //training the snake until the stop button is pushed
        while(!btnStoppedPushed){
            
            //get the current chromosone
            curChromosone = population.getCurChromosone();
            
            //initialise the neural net
            NeuralNetwork NN = new NeuralNetwork();
            
            
            //reset statistics of each chromosone
            snakeLength = 3;
            applesEaten = 0;
            
            //this runs the game
            while(inGame){
                
                //slow down the game
                if(iterationCounter % 5000 == 0 || snakeLength >25){
                    TimeUnit.MILLISECONDS.sleep(100);
                }
                
                //get the weights for the neural net
                NN.setWeightsOfNN(curChromosone.getIHWeights(), curChromosone.getHOWeights());
                
                //make the move the neural net wants you to go
                int currentOppositeDir = 0;
                if (leftDirection)
                    currentOppositeDir = 4;
                else if (rightDirection)
                    currentOppositeDir = 2;
                else if (upDirection)
                    currentOppositeDir = 3;
                else if (downDirection)
                    currentOppositeDir = 1;
                
                //check which direction is free for the snake to move and update the weights
                checkFreeDirections();
                
                //get the state of the game so the neural net can make a decision
                double[] inputs = getInputs();

                //get the move from the neural net
                int move = NN.getNextMove(currentOppositeDir, inputs);
                
                //set up the move the neural net has decided 
                manageMove(move); 
                
                move(); //move the snake
         
                //We have made are move from the neural net
                checkApple();
                
                //terminate game if no progress is made in finding the apple
                if (stepsTakenSinceLastFood > this.TERMINATION_STEPS)
                {
                    inGame = false;
                    curChromosone.setTerminated(true);
                }
                
                //check if the snake has hit any obstacle
                checkCollision();
                
                //show what it happening ever mod x amount of times or if the length is greater than 30
                if(iterationCounter % 5000 == 0 || snakeLength > 25){
                    repaint();
                }
                
            }
            
            //set the fitness for the chromosone
            curChromosone.setFitness(snakeLength, stepsTaken);
            
            //print out the details for the user
            System.out.println("Iteration: " + iterationCounter + " max snake: " + SnakeGame.snakeLength);
            
            //update when the snake has got a new max length
            if (snakeLength > maxSnakeLength)
            {
                maxSnakeLength = snakeLength;
                console.addToConsole("New Highscoore of: " + maxSnakeLength + ", in iteration: " + iterationCounter);
            }
            
            //increment the counter
            iterationCounter++;
            
            //train the next generation every time you have went through the population
            if (iterationCounter % popSize == 0){
                population.Train();
            }
            
            //increment the chromosone you are testing
            population.incrementCurChromosone();
            
            //initialise game
            initGame();    
        }
        
        //game is stopped
        console.addToConsole("Game Stopped!");
    }
    
    //check all possible cases for death and make sure that you set it to false
    private void checkFreeDirections() {
        frontFree = true;
        rightFree = true;
        leftFree = true; 
        
        //left side and going down
        if(x[0] == 1 &&  downDirection){
            rightFree = false;
        }
        //bottom side and going left
        if (y[0] == 1 && leftDirection ){
            leftFree = false;
        }
        //right side and going up
        if (x[0] == B_WIDTH-1 && upDirection){
            rightFree = false;
        }
        
        //top side and going right
        if (y[0] == B_HEIGHT-1 && rightDirection ){
            leftFree = false;
        }
        
        //left side and going up
        if(x[0] == 1 &&  upDirection){
            leftFree = false;
        }
        //bottom side and going right
        if (y[0] == 1 && rightDirection ){
            rightFree = false;
        }
        //right side and going down
        if (x[0] == B_WIDTH-1 && downDirection){
            leftFree = false;
        }
            
        //top side and going up
        if (y[0] == B_HEIGHT-1 && upDirection ){
            frontFree = false;
        }
        
        //left side and going left
        if(x[0] == 1 &&  leftDirection){
            frontFree = false;
        }
        //bottom side and going down
        if (y[0] == 1 && downDirection ){
            frontFree = false;
        }
        //right side and going right
        if (x[0] == B_WIDTH-1 && rightDirection){
            frontFree = false;
        }
  
    }



    private void manageMove(int move){
        //set the new direction
        if(move == 0 && !downDirection){
            upDirection = true;
            downDirection = false;
            leftDirection = false;
            rightDirection = false;
        }else if(move == 1 && !rightDirection){
            upDirection = false;
            downDirection = false;
            leftDirection = true;
            rightDirection = false;   
        }else if(move == 2 && !upDirection){
            upDirection = false;
            downDirection = true;
            leftDirection = false;
            rightDirection = false;
        }else if(move == 3 && !leftDirection){
            upDirection = false;
            downDirection = false;
            leftDirection = false;
            rightDirection = true;
        }
    }
    
    //these are the values that we need to get an output from the Neural Net
    private double[] getInputs() {
        double[] inputs = new double[14];
        inputs[0] = x[0];
        inputs[1] = y[0];
        inputs[2] = apple_x;
        inputs[3] = apple_y;
        inputs[4] = 1.0;
        inputs[5] = 1.0;
        inputs[6] = 1.0;
        inputs[7] = 1.0;
        double distToFoodX = Math.abs(x[0] - apple_x);
        inputs[8]  = distToFoodX;
        double distToFoodY = Math.abs(y[0] - apple_y);
        inputs[9]  = distToFoodY;
        if(upDirection){
            inputs[4] = 0.0;
        }
        if(leftDirection){
            inputs[5] = 0.0;
        }
        if(downDirection){
            inputs[6] = 0.0;
        }
        if(rightDirection){
            inputs[7] = 0.0;
        }
        if(leftFree){
            inputs[10] = 1.0; 
        }
        if(frontFree){
            inputs[11] = 1.0; 
        }
        if(rightFree){
            inputs[12] = 1.0; 
        }
        //bias
        inputs[13] = -1.0;
        
        
        
                
                
        
        
        return inputs;
    }

    private void normalizeInputs(double[] inputs){
        //need to normalise data
         maxX = -1;
         maxY = -1;
         minX = 99999;
         minY = 99999;       
        
         for(int i =0; i<=9; i++){
             double numerator;
             double demoninator;
             double normalized;
             
             if (i % 2 == 0){
                 numerator = inputs[i] - minX;
                 demoninator = maxX - minX;
                 normalized = numerator / demoninator;
                 inputs[i] = normalized;
             }else{
                 numerator = inputs[i] - minY;
                 demoninator = maxY - minY;
                 normalized = numerator / demoninator;
                 inputs[i] = normalized;
             }
         }
        
    }









    //Initialise game
    private void initGame(){
        
        //initial length of snake
        dots = 3;
        inGame= true;
        leftDirection = false;
        rightDirection = true;
        upDirection = false;
        downDirection = false;
        stepsTaken =0;
        SnakeGame.stepsTakenSinceLastFood = 0;
        
        
        //populate the snake
        for(int z =0; z<dots; z++){
            x[z]= 50 - z * 10;
            y[z]= 50;      
        }
        
        //update the positions for the Neural Net
        //place the apple
        placeApple();
        
        //start game timer
        timer = new Timer(DELAY, this);
        timer.start();
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        // TODO Auto-generated method stub
        
        if (inGame) {

            g.drawImage(apple, apple_x, apple_y, this);

            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                    g.drawImage(head, x[z], y[z], this);
                } else {
                    g.drawImage(body, x[z], y[z], this);
                }
            }

            Toolkit.getDefaultToolkit().sync();

        } else {

            gameOver(g);
        }
        
    }

    private void gameOver(Graphics g) {
        // TODO Auto-generated method stub
        
//        String msg = "Game Over";
//        Font small = new Font("Helvetica", Font.BOLD, 14);
//        FontMetrics metr = getFontMetrics(small);
//
//        g.setColor(Color.white);
//        g.setFont(small);
//        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
        
    }

    private void placeApple() {
        // TODO Auto-generated method stub
        
        //generates a random x and y coordinate for the apple
        int a = (int) (Math.random() * RANDOM_POS);
        apple_x = a * DOT_SIZE;
        
        a = (int) (Math.random() * RANDOM_POS);
        apple_y = a * DOT_SIZE;
        
        
        
    }
    
    private void move(){
        //moves the snake so that it follows the head
        for (int z = dots; z > 0; z--){
            x[z]=x[(z-1)];
            y[z]=y[(z-1)];
        }
        
        //moves the head to the direction the snake is going
        if (leftDirection){
            x[0] -=DOT_SIZE;
        }
        
        if (rightDirection){
            x[0] +=DOT_SIZE;
        }
        if (upDirection){
            y[0] -=DOT_SIZE;
        }
        if (downDirection){
            y[0] +=DOT_SIZE;
        }
        
        stepsTaken++;
        SnakeGame.stepsTakenSinceLastFood++;
        //updateSnakeInputs();
        
    } 



    private void checkCollision(){
        
        //checks if the snake has hit the wall or itself
        for(int z = dots; z > 0; z--){
            if((z > 4) && (x[0] == x[z]) && (y[0]==y[z])){
                inGame = false;
            }
        }
        
        if (y[0] >= B_HEIGHT){
            inGame = false;
        }
        
        if (y[0] < 0 ){
            inGame = false;
        }
        
        if (x[0] >= B_WIDTH ){
            inGame = false;
        }
        
        if (x[0] < 0){
            inGame = false;
        }
        
        if(!inGame){
            timer.stop();
        }
    }
    
    private void checkApple(){
        
        //apple is on the head of the snake
        if(x[0]== apple_x && y[0] == apple_y){
            //add something else here for NN to track score
            //add score
            
            SnakeGame.stepsTakenSinceLastFood = 0;
            snakeLength++;
            dots++;
            placeApple();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {

            checkApple();
            checkCollision();
            move();
        }

        repaint();
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }
    

}
