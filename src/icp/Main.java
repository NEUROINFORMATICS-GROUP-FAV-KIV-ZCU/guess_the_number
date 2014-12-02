package icp;

import icp.application.OnlineDetection;
import icp.application.classification.FilterFeatureExtraction;
import icp.application.classification.IERPClassifier;
import icp.application.classification.IFeatureExtraction;
import icp.application.classification.MLPClassifier;
import icp.online.app.OnLineDataProvider;
import icp.online.gui.MainFrame;

/**
 * Hlavn� spou�t�c� t��da aplikace.
 * 
 */
public class Main {

	public static void main(String[] args) {
		//new SessionManager().startGui();
		IERPClassifier classifier = new MLPClassifier();
		classifier.load("data/classifier.txt");
		IFeatureExtraction fe = new FilterFeatureExtraction();
		classifier.setFeatureExtraction(fe);
				
		MainFrame gui = new MainFrame();
		OnlineDetection detection = new OnlineDetection(classifier, gui);
		
		
		String recorderIPAddress = "147.228.127.95";
		int port = 51244;
		
		OnLineDataProvider odp = new OnLineDataProvider(recorderIPAddress, port, detection);		
		
		
		
	}
}
