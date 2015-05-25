package icp.application.classification.test;

import java.util.Map;
import java.util.Random;

public class OptimizeMLP {
	
	
	public static void main(String[] args) throws InterruptedException {
		double accuracy = 0;
		double maxAccuracy = 0;
		Random random = new Random();
		while (accuracy < 0.7) { 
			int numberOfIters = 500 + random.nextInt(900);
			int middleNeurons =  4 + random.nextInt(8);
			TrainUsingOfflineProvider trainOfflineProvider = new TrainUsingOfflineProvider(numberOfIters, middleNeurons);
			System.out.println("New classifier: " + trainOfflineProvider.getClassifier());
			System.out.println("New feature extraction: " + trainOfflineProvider.getClassifier().getFeatureExtraction());
			TestClassificationAccuracy testAccuracy = new TestClassificationAccuracy( trainOfflineProvider.getClassifier());
			Map<String, Statistics> stats = testAccuracy.getStats();
			int okNumber = 0;
			for (Map.Entry<String, Statistics> entry : stats.entrySet()) {
				if (entry.getValue().getRank() == 1) {
					okNumber++;
			}
			
			}
			accuracy = (double) okNumber / stats.size();
			if (accuracy > maxAccuracy) {
				maxAccuracy = accuracy;
				System.out.println("New accuracy record: " + accuracy);
				trainOfflineProvider.getClassifier().save("data/bestOfAllTimes.txt");
				
			}
			else {
				System.out.println("No record: current accuracy = " + accuracy * 100 + ", max_accuracy = " + maxAccuracy * 100);
			}
		}
		
	}

}
