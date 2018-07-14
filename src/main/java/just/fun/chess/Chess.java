package just.fun.chess;

import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class Chess implements CommandLineRunner {

    @Value("${resourcesPath}")
    private String resourcesPath;
    @Value("${trainingFileName}")
    private String trainingFileName;
    @Value("${testFileName}")
    private String testFileName;

    public static void main(String[] args) {
        SpringApplication.run(Chess.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        int batchSize = 1000; // batch size for each epoch
        int rngSeed = 123;

        ChessFetcher trainFetcher = new ChessFetcher(resourcesPath, trainingFileName);
        ChessFetcher testFetcher = new ChessFetcher(resourcesPath, testFileName);


        DataSetIterator chessTrain = new ChessDataSetIterator(batchSize, ChessFetcher.NUM_EXAMPLES, trainFetcher);
        DataSetIterator chessTest = new ChessDataSetIterator(batchSize, ChessFetcher.NUM_EXAMPLES_TEST, testFetcher);

//        final int numRows = 28;
//        final int numColumns = 28;
//
        int outputNum = 12;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(rngSeed) //include a random seed for reproducibility
                // use stochastic gradient descent as an optimization algorithm
                .updater(new Nesterovs(0.006, 0.9))
                .list()
                .layer(0, new DenseLayer.Builder() //create the first, input layer with xavier initialization
                        .nIn(129)
                        .nOut(1000)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.XAVIER)
                        .build())
//                .layer(1, new DenseLayer.Builder() //create the first, input layer with xavier initialization
//                        .nIn(1000)
//                        .nOut(1000)
//                        .activation(Activation.RELU)
//                        .weightInit(WeightInit.XAVIER)
//                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MSE) //create hidden layer
                        .nIn(1000)
                        .nOut(outputNum)
                        .activation(Activation.SIGMOID)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .pretrain(false).backprop(true) //use backpropagation to adjust weights
                .build();
//
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
//        //print the score with every 1 iteration
////        model.setListeners(new StatsListener(statsStorage), new ScoreIterationListener(1));
        model.setListeners(new ScoreIterationListener(1));
//
        int numEpochs = 20;
        System.out.println("Train model....");
        for( int i=0; i<numEpochs; i++ ){
            System.out.println("Training model, epochs = " + i);
            model.fit(chessTrain);
        }

        File locationToSave = new File("MyMultiLayerNetwork.zip");      //Where to save the network. Note: the file is in .zip format - can be opened externally
        boolean saveUpdater = true;                                             //Updater: i.e., the state for Momentum, RMSProp, Adagrad etc. Save this if you want to train your network more in the future
        ModelSerializer.writeModel(model, locationToSave, saveUpdater);

        Evaluation eval = new Evaluation(outputNum); //create an evaluation object with 10 possible classes
        while(chessTest.hasNext()){
            DataSet next = chessTest.next();
            INDArray output = model.output(next.getFeatureMatrix()); //get the networks prediction
            eval.eval(next.getLabels(), output); //check the prediction against the true class
        }

        System.out.println(eval.stats());
    }
}