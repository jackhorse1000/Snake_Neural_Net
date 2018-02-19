import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;



public class Population {
    
    private int populationSize = 50;
    private int hallOfFameSize, tournamentSize; 
    private ArrayList<Chromosone> chromosones;
    private Chromosone fittest;
    private Chromosone secondFittest;
    
    private int curChromosone;
    
    //lower means more mutation
    private int mutationRate = 5;
    
    //amount of change when you mutate
    private int mutationWeight= 10;
    
    
   

    public Population(int populationSize){
        this.populationSize = populationSize;
        chromosones = new ArrayList<Chromosone>();
        
        for (int i =0; i<= populationSize-1; i++){
            chromosones.add( new Chromosone(true));
        }
        
        curChromosone = 0;
        hallOfFameSize = 2;
        tournamentSize = 3; 
        

    }
    
//    //gets the first fittest Chromosone
//    public void getfittest() {
//        double maxFit =0;
//        Chromosone fittest = null;
//        for (int i = 0; i< chromosones.size();i++){
//            if(maxFit <= chromosones.get(i).getFitness()){
//                maxFit = chromosones.get(i).getFitness();
//                fittest = chromosones.get(i);
//            }
//        }
//        
//        if (fittest == null){
//            System.out.println("The fittest was not found and is null"); 
//            }
//        
//        this.fittest = fittest;
//    }
//    
//    //gets the second fittest Chromosone
//    public void getSecondfittest() {
//        int maxFit1 = 0;
//        int maxFit2 = 0;
//        Chromosone secondFittest = null;
//        for (int i = 0; i< chromosones.size();i++){
//            if((maxFit1 <= chromosones.get(i).getFitness())){
//                maxFit2 = maxFit1; 
//                maxFit1 = i;
//            } else if (chromosones.get(i).getFitness() > chromosones.get(maxFit2).getFitness()){
//                maxFit2 =i;
//            }
//        }
//        
//        secondFittest = chromosones.get(maxFit2);
//        
//        if (fittest == null){
//            System.out.println("The second fittest was not found and is null"); 
//            }
//        
//        this.secondFittest = secondFittest;
//    }
//    
//    public void getLeastFitChromosone(){
//        int minFit = 1000000;
//        for( int i =0; i<chromosones.size(); i++){
//            if(minFit >= chromosones.get(i).getFitness()){
//                minFit = i;
//            }
//        }
//        
//
//        
//    }
    
    public void Train(){
        
        //sort to find the fittest chromosones      
        Comparator<Chromosone> comp = new Comparator<Chromosone>(){
            @Override
            public int compare(Chromosone c1, Chromosone c2)
            {
                if (c1.getFitness() < c2.getFitness())
                    return 1;
                if (c1.getFitness() > c2.getFitness())
                    return -1;
                return 0;
            }
        };
        
        
        Collections.sort(chromosones, comp);
        
        //this will be the next generation of chromosones
        ArrayList<Chromosone> nextGen = new ArrayList<>();   
        
        //add the fittest chromosones to the next gerneration
        for(int i =0; i<this.hallOfFameSize; i++){
            nextGen.add(chromosones.get(i));
        }
        

        
        //add new mutations of the 2 fittest to the next generation
        for (int i = 0; i< populationSize-2; i++) {
            //Use the fittest 2 chromosones to mutate the and add to the next generation
            Chromosone crossoverChromosone = crossover(nextGen.get(0), nextGen.get(1)); 
            
            //would like to mutate the Chromosone
            Chromosone mutate = mutate(crossoverChromosone);
            nextGen.add(mutate);
        }
        
        chromosones = nextGen;
        
    }
    
    private Chromosone mutate(Chromosone crossoverChromosone) {
        
        Random r = new Random();
        double mutateWeight;
        
        
        
        //for weights input to hidden
        for (int i =0; i < SnakeGame.noInputs; i++){
            for(int j=0; j < SnakeGame.noHidden; j++){
                //use the mutation rate to deside how often I would like
                //to mutate the chromosone
                //lower mutation rate number the more we mutate
                if((r.nextInt(this.populationSize)) % mutationRate ==0){
                
                //50% chance of being either mutation
                if(r.nextInt(this.populationSize)%2 == 0){
                    mutateWeight = crossoverChromosone.getIHWeights()[i][j] + r.nextDouble() * r.nextInt(mutationWeight) ;
                }else{
                    mutateWeight = crossoverChromosone.getIHWeights()[i][j] + r.nextDouble() * (-r.nextInt(mutationWeight));
                }
                //set the weight of the mutation
                crossoverChromosone.IHWeights[i][j] = mutateWeight;
                }
            }
        }
        
        
        
        //for weights hidden to output
        
        for (int i =0; i < SnakeGame.noHidden; i++){
            for(int j=0; j < SnakeGame.noOutput; j++){
                //use the mutation rate to deside how often I would like
                //to mutate the chromosone
                //lower mutation rate number the more we mutate
                if(r.nextInt()% mutationRate ==0){
                
                //50% chance of being either mutation
                if((r.nextInt(this.populationSize))%2 == 0){
                    mutateWeight = crossoverChromosone.getHOWeights()[i][j] + r.nextDouble() * r.nextInt(mutationWeight) ;
                }else{
                    mutateWeight = crossoverChromosone.getHOWeights()[i][j] + r.nextDouble() * (-r.nextInt(mutationWeight));
                }
                //set the weight of the mutation
                crossoverChromosone.HOWeights[i][j] = mutateWeight;
                }
            }
        }
        
        return crossoverChromosone;
    }

    public Chromosone crossover(Chromosone c1, Chromosone c2){
        
        //setup output Chromosone
        Chromosone output = new Chromosone();
        double[][] outputIHWeights = new double[SnakeGame.noInputs][SnakeGame.noHidden];
        double[][] outputHOWeights = new double[SnakeGame.noHidden][SnakeGame.noOutput];
        
        //crossover weights for Input to Hidden
        for(int i =0; i< SnakeGame.noInputs; i++){
            for(int j =0; j <SnakeGame.noHidden; j++){
                
                Random r = new Random();
                if (r.nextInt()%2 == 0){
                    outputIHWeights[i][j] = c1.getIHWeights()[i][j];
                }else{
                    outputIHWeights[i][j] = c2.getIHWeights()[i][j];
                }   
            }
        }
        
        
        //crossover weights for Hidden to Output
        for(int i =0; i< SnakeGame.noHidden; i++){
            for(int j =0; j <SnakeGame.noOutput; j++){
                
                Random r = new Random();
                if (r.nextInt()%2 == 0){
                    outputHOWeights[i][j] = c1.getHOWeights()[i][j];
                }else{
                    outputHOWeights[i][j] = c2.getHOWeights()[i][j];
                }
            }
        }
        
        output.setIHWeights(outputIHWeights);
        output.setHOWeights(outputHOWeights);

        return output;    
    }

    public void incrementCurChromosone(){
        if (this.curChromosone < populationSize-2){
            this.curChromosone++;
        }else{
            this.curChromosone = 0;
            }
    }
    
    public Chromosone getCurChromosone() {
        return chromosones.get(curChromosone);
    }

    public void setCurChromosone(int curChromosone) {
        this.curChromosone = curChromosone;
    }
    

}
