package icp.application;

import icp.Const;
import icp.application.classification.IERPClassifier;
import icp.application.classification.test.ObserverMessage;
import icp.online.app.EpochMessenger;
import icp.online.app.IDataProvider;

import java.util.Arrays;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

public class OnlineDetection extends Observable implements Observer {

    private final IERPClassifier classifier;
    private final double[] classificationResults;
    private final int[] classificationCounters;
    private final double[][][] sumEpoch;
    private final double[][][] avgEpoch;
    

  
    private Logger log;

    private double[] weightedResults;

    public OnlineDetection(IERPClassifier classifier, Observer observer) {
        super();
        this.addObserver(observer);
        this.classifier = classifier;
        this.classificationCounters = new int[Const.GUESSED_NUMBERS];
        this.classificationResults = new double[Const.GUESSED_NUMBERS];
        this.sumEpoch = new double[Const.USED_CHANNELS][Const.GUESSED_NUMBERS][IDataProvider.POCETHODNOTZAEPOCHOU];
        this.avgEpoch = new double[Const.USED_CHANNELS][Const.GUESSED_NUMBERS][IDataProvider.POCETHODNOTZAEPOCHOU];
     

        Arrays.fill(classificationCounters, 0);
        Arrays.fill(classificationResults, 0);
        for (int i = 0; i < sumEpoch.length; i++) {
        	for (int j = 0; j < sumEpoch[i].length; j++) {
        		Arrays.fill(sumEpoch[i][j], 0);
        		Arrays.fill(avgEpoch[i][j], 0);
        	}
        }
    }

    @Override
    public void update(Observable o, Object arg) throws IllegalArgumentException {
        if (arg instanceof EpochMessenger) {
            EpochMessenger epochMsg = (EpochMessenger) arg;
            int stimulusID = epochMsg.getStimulusIndex();
            if (stimulusID < Const.GUESSED_NUMBERS) {
            	classificationCounters[stimulusID]++;
            	for (int i = 0; i < Const.USED_CHANNELS; i++) {
            		for (int j = 0; j < IDataProvider.POCETHODNOTZAEPOCHOU; j++) {
            			sumEpoch[i][stimulusID][j] += epochMsg.getEpoch()[i][j]; // Pz
            			avgEpoch[i][stimulusID][j] = sumEpoch[i][stimulusID][j] / classificationCounters[stimulusID];
            		}
            	}
            	double classificationResult = this.classifier.classify( getAvgEpochWithStimulus(stimulusID, avgEpoch));
            	classificationResults[stimulusID] += classificationResult;
            	this.weightedResults = this.calcClassificationResults();
            	//System.out.println(Arrays.toString(classificationCounters));
            	setChanged();
            	notifyObservers(this);
            }
        } else if (arg instanceof ObserverMessage) {
            //TODO some action when data loading ends
        } else {
            throw new IllegalArgumentException("Unexpected reference received.");
        }
    }
    
    
    private double[][] getAvgEpochWithStimulus(int stimulusIndex, double[][][] epoch) {
    	double[][] epochStimulus = new double[Const.USED_CHANNELS][IDataProvider.POCETHODNOTZAEPOCHOU];
    	for (int i = 0; i < Const.USED_CHANNELS; i++) {
    		for (int j = 0; j < IDataProvider.POCETHODNOTZAEPOCHOU; j++) {
    			epochStimulus[i][j] = epoch[i][stimulusIndex][j]; 
    		}
    	}
    	return epochStimulus;
    	
    }

    private double[] calcClassificationResults() {
        double[] wResults = new double[Const.GUESSED_NUMBERS];
        for (int i = 0; i < wResults.length; i++) {
            if (classificationCounters[i] == 0) {
                wResults[i] = 0;
            } else {
                wResults[i] = classificationResults[i] / classificationCounters[i];
            }
        }

        return wResults;
    }

    public double[][] getPzAvg() {
        return this.avgEpoch[2];
    }

    public double[] getWeightedResults() {
        return weightedResults;
    }

}
