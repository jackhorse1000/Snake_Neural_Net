import java.util.ArrayList;
import java.util.Random;



public class Population {
    
    private int populationSize = 50;
    private int generation; 
    private ArrayList<Chromosone> chromosones;
    private Chromosone fittest;
    private Chromosone secondFittest;
    private int indexLeastFit;
    
    private int curChromosone;
    
    private int mutationRate = 2;
    
    
   

    public Population(int populationSize){
        this.populationSize = populationSize;
        chromosones = new ArrayList<Chromosone>();
        
        for (int i =0; i<= populationSize-1; i++){
            chromosones.add( new Chromosone(true));
        }
        
        curChromosone = 0;

    }
    
    //gets the first fittest Chromosone
    public void getfittest() {
        double maxFit =0;
        Chromosone fittest = null;
        for (int i = 0; i< chromosones.size();i++){
            if(maxFit <= chromosones.get(i).getFitness()){
                maxFit = chromosones.get(i).getFitness();
                fittest = chromosones.get(i);
            }
        }
        
        if (fittest == null){
            System.out.println("The fittest was not found and is null"); 
            }
        
        this.fittest = fittest;
    }
    
    //gets the second fittest Chromosone
    public void getSecondfittest() {
        int maxFit1 = 0;
        int maxFit2 = 0;
        Chromosone secondFittest = null;
        for (int i = 0; i< chromosones.size();i++){
            if((maxFit1 <= chromosones.get(i).getFitness())){
                maxFit2 = maxFit1; 
                maxFit1 = i;
            } else if (chromosones.get(i).getFitness() > chromosones.get(maxFit2).getFitness()){
                maxFit2 =i;
            }
        }
        
        secondFittest = chromosones.get(maxFit2);
        
        if (fittest == null){
            System.out.println("The second fittest was not found and is null"); 
            }
        
        this.secondFittest = secondFittest;
    }
    
    public void getLeastFitChromosone(){
        int minFit = 1000000;
        for( int i =0; i<chromosones.size(); i++){
            if(minFit >= chromosones.get(i).getFitness()){
                minFit = i;
            }
        }
        
        this.indexLeastFit = minFit;
        
    }
    
    public void Train(){
        
//        Collections.sort(chromosones, comp);
//        ArrayList<Chromosone> nextGen = new ArrayList<>(); //this will eventually be the new population of chromozones.
//        for (int i = 0; i< hallOfFameSize; i++) //let the first fittest individuals take the first slots to the halloffamesize counter.
//        {
//            nextGen.add(chromosones.get(i).clone());
//        }
        
        //this will be the next population/generation of chromosones
        //ArrayList<Chromosone> nextGen = new ArrayList<>();
        //let the first fittest individuals take the first slots to the halloffamesize counter.
//        for (int i = 0; i< hallOfFameSize; i++) 
//        {
//            nextGen.add(chromosones.get(i).clone());
//        }
        
        //get the fittest and second fittest to crossover
        this.getfittest();
        this.getSecondfittest();
        

        
        //this will be the next generation of chromosones
        ArrayList<Chromosone> nextGen = new ArrayList<>(); 
        
        //previous 2 best will get the first place in the next generation
        nextGen.add(this.fittest);
        nextGen.add(this.secondFittest);
        
        //add new mutations of the 2 fittest to the next generation
        for (int i = 0; i< populationSize-2; i++) {
            Chromosone crossoverChromosone = crossover(fittest, secondFittest); 
            
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
                if(r.nextInt()% mutationRate ==0){
                
                //50% chance of being either mutation
                if(r.nextInt()%2 == 0){
                    mutateWeight = crossoverChromosone.getIHWeights()[i][j] + r.nextDouble() * 200;
                }else{
                    mutateWeight = crossoverChromosone.getIHWeights()[i][j] + r.nextDouble() * (-200);
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
                if(r.nextInt()%2 == 0){
                    mutateWeight = crossoverChromosone.getHOWeights()[i][j] + r.nextDouble() * 200;
                }else{
                    mutateWeight = crossoverChromosone.getHOWeights()[i][j] + r.nextDouble() * (-200);
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
