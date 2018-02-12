import java.util.Random;

public class Chromosone {
    
    //what will determine the fitness of the snake
    //number of steps taken //number of apples eaten
    //If the code had to terminate the snake 
    private double fitness;
    private int numberSteps;
    private int snakeLength;
    private boolean terminated;
    
    
    //some variable to hold the weights of the nodes
    double[][] IHWeights; //Input to hidden weights 
    double[][] HOWeights; //Hidden to output weights
    

            
    
    //used for when it is not the first iteration of the snake
    public Chromosone(){
        //snake has not been terminated
        this.terminated = false;
        
    }
    
    //used to set weights for the first run of the snake
    public Chromosone(boolean firstRun){
        
        //snake has not been terminated
        this.terminated = false;
        
    
        //set weights randomly //depending on the no. of input nodes and hidden nodes
        if (firstRun){
            initWeights();
        }
        
        
        
    }
    
    //function to initialise hidden and input weights
    public void initWeights(){
        
        this.IHWeights = new double[SnakeGame.noInputs][SnakeGame.noHidden]; //2D array of input node weights to hidden nodes
        this.HOWeights = new double[SnakeGame.noHidden][SnakeGame.noOutput]; //2D array of hidden node weights to output nodes
        
        //set random weights for the input to hidden nodes
        for (int i =0; i< SnakeGame.noInputs-1; i++){
            for (int j =0; j < SnakeGame.noHidden; j++){
                IHWeights[i][j]=randomValue();
            }
        }
        
        //set random weights for the hidden to output nodes
        for(int i =0; i< SnakeGame.noHidden; i++ ){
            for( int j = 0; j< SnakeGame.noOutput; j++){
                HOWeights[i][j] = randomValue();
            }
        }
        
        
    }
    
    //get a random value for the the weights.
    private double randomValue(){
        Random r = new Random();
        
        double base = 1/SnakeGame.noInputs;
        
        double randValue = r.nextDouble() * base;
        
        return randValue;
    }
    

    public double getFitness() {
        return fitness;
    }

    public void setFitness(int snakeLength, int stepsTaken) {
        //create some function to evaluate the snakes performance
        //snake length - apples eaten - time stayed alive for (possible contributing factors)
        //reward it more for being longer than staying alive
        this.snakeLength = snakeLength;
        this.numberSteps = stepsTaken;
        
        double output = 0.0;
        
        //rewarding the snake more for not being terminated and for getting to a bigger length
        //if the snake had to be killed
//        if(terminated){
//            output = (numberSteps/4) + snakeLength * 10000;
//        }else{
//            output = (numberSteps*4) + snakeLength * 10000;
//        }
        
        if(terminated){
            output = numberSteps/4 + snakeLength * 10000;
        }else{
            output = numberSteps*4 + snakeLength * 10000;
        }

        
        terminated = false;
        this.fitness = output;
    }
    
    
    public int getNumberSteps() {
        return numberSteps;
    }

    public void setNumberSteps(int numberSteps) {
        this.numberSteps = numberSteps;
    }

    public int getSnakeLength() {
        return snakeLength;
    }

    public void setSnakeLength(int snakeLength) {
        this.snakeLength = snakeLength;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public void setTerminated(boolean terminated) {
        this.terminated = terminated;
    }

    public double[][] getIHWeights() {
        return IHWeights;
    }

    public void setIHWeights(double[][] iHWeights) {
        IHWeights = iHWeights;
    }

    public double[][] getHOWeights() {
        return HOWeights;
    }

    public void setHOWeights(double[][] hOWeights) {
        HOWeights = hOWeights;
    }
    
    
    

}
