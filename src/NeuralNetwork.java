import java.util.Random;

public class NeuralNetwork {
    int noInputNeurons, noHiddenNeurons, noOutputNeurons;
    public double[][] IHWeights;
    public double[][] HOWeights;
    
    public NeuralNetwork(){
        noInputNeurons = SnakeGame.noInputs;
        noHiddenNeurons = SnakeGame.noHidden;
        noOutputNeurons = SnakeGame.noOutput;
        IHWeights = new double[noInputNeurons][noHiddenNeurons];
        HOWeights = new double[noHiddenNeurons][noOutputNeurons];
    }
    
    public void setWeightsOfNN(double[][] inputtedIHWeights, double[][] inputtedHOWeights){
        this.IHWeights = inputtedIHWeights;
        this.HOWeights = inputtedHOWeights;
    }
    
    public int getNextMove(int curDir, double[] inputs){
        double highestActual = 0;
        int toOutput = 0;
        
        for(int i =0; i<=noOutputNeurons-1; i++){
            double actual = calculateHiddenAndOutput(inputs).storedOutputNets[i];
            if(actual > highestActual && curDir != i){
                highestActual = actual;
                toOutput = i;
            }
        }
        
        return toOutput;
    }
    
    public void setWeightsOfNN(){
        //give initial weights
        for(int i =0; i<= this.noInputNeurons-1; i++){
            for(int j = 0; j<= this.noHiddenNeurons-1; j++){
                this.IHWeights[i][j] = randomDouble();
            }
        }
        
        for(int i = 0; i<= this.noHiddenNeurons-1; i++){
            for(int j = 0; j<= this.noOutputNeurons-1; j++){
                this.HOWeights[i][j] = randomDouble();
            }
        }
    }
    
    private StoredNetClass calculateHiddenAndOutput(double[] inputPattern){
        double[] hiddenFNets = new double[noHiddenNeurons];
        double[] outputFNets = new double[noOutputNeurons];
        
        double outputNet, hiddenNet;
        
        for (int k =0; k<= noOutputNeurons-1; k++){
            outputNet = 0.0;
            
            for(int j =0; j<=noHiddenNeurons-1; j++){
                hiddenNet = 0.0;
                
                for(int i =0; i<=noInputNeurons-1; i++){
                    hiddenNet = hiddenNet + (IHWeights[i][j]* inputPattern[i]);
                }
                
                if(j == (noHiddenNeurons -1)){
                    hiddenFNets[j] = -1.0;
                }else{
                    hiddenFNets[j] = getActivationFunctionValue(hiddenNet, "sig");
                }
                outputNet = outputNet + (HOWeights[j][k]* hiddenFNets[j]);
                
            }
            outputFNets[k] = getActivationFunctionValue(outputNet, "sig");
        }
        
        return new StoredNetClass(hiddenFNets, outputFNets); 
    }
    
    private double getActivationFunctionValue(double in, String activFunc) {
        double result;
        
        if(activFunc.equals("lin")){
            return in;
        }else{
            result = 1.0/(1.0 + (Math.exp(-in)));
            return result;
        }
    }

    public double randomDouble()
    {
        Random r = new Random();
        double min = -1 / Math.sqrt(this.noInputNeurons);
        double max = 1/ Math.sqrt(this.noInputNeurons);
        double randomVal = (r.nextDouble() * (max - min)) + min;
        return randomVal;
    }

}
