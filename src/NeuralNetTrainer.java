import dist.*;
import opt.*;
import opt.example.*;
import opt.ga.*;
import shared.*;
import shared.filt.LabelSplitFilter;
import shared.reader.CSVDataSetReader;
import func.nn.backprop.*;

import java.util.*;
import java.io.*;
import java.text.*;

public class NeuralNetTrainer implements Runnable {
    private Instance[] instances;

    private final int inputLayer = 13, hiddenLayer = 5, outputLayer = 1;
    private final BackPropagationNetworkFactory factory = new BackPropagationNetworkFactory();
    private int trainingIterations = 6000;
    private final GradientErrorMeasure measure = new SumOfSquaresError();
    private final WeightUpdateRule updateRule = new RPROPUpdateRule();
    private final String dataDir = "results/";
    private final String extension;
    private final String setName;
    private final int numAttrs = 13;
    private final DataSet set;

    private static final DecimalFormat df = new DecimalFormat("0.000");

    /**
     * 
     * @param iterations
     * @param set
     * @param extension
     * @param name
     */
    public NeuralNetTrainer(int iterations, DataSet set, String extension, String name) {
    	this.trainingIterations = iterations;
    	this.instances = set.getInstances();
    	this.set = set;
    	this.extension = extension;
    	this.setName = name;
    }
    
    public void run() {		
    	double correct = 0, incorrect = 0;

        BackPropagationNetwork network = factory.createClassificationNetwork(
           new int[] { inputLayer, hiddenLayer, outputLayer });

        ConvergenceTrainer trainer = new ConvergenceTrainer(
               new BatchBackPropagationTrainer(set, network,
            		   measure, updateRule));
        
        float start = System.nanoTime();
        trainer.train();
        double trainingTime = (System.nanoTime() - start)/Math.pow(10,9);
        start = System.nanoTime();
        for(int j = 0; j < instances.length; j++) {
        	double predicted, actual;
            predicted = Double.parseDouble(instances[j].getLabel().toString());
            actual = Double.parseDouble(network.getOutputValues().toString());

            double trash = Math.abs(predicted - actual) < 0.5 ? correct++ : incorrect++;

        }
        double testingTime = (System.nanoTime() - start)/Math.pow(10,9);
        BufferedWriter bw = null;
        try {
			bw = new BufferedWriter(new FileWriter(new File(dataDir + "nn_results_" + setName+extension+".txt")));
			bw.write("Correctly classified " + correct + " instances." +
		            "\nIncorrectly classified " + incorrect + " instances.\nPercent correctly classified: "
		            + df.format(correct/(correct+incorrect)*100) + "%\nTraining time: " + df.format(trainingTime)
		            + " seconds\nTesting time: " + df.format(testingTime) + " seconds\n");
			bw.newLine();
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}           
    }

}
