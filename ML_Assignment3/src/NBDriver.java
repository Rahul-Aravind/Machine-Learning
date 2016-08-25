import java.io.IOException;

public class NBDriver {
	
	public static void main(String args[]) throws IOException {
		
		final String spam = "spam";
		final String ham = "ham";
		
		Input ip = new Input();
		String trainingDataDir = ip.getTrainingDataDir();
		String testDataDir = ip.getTestDataDir();
		
		FilePreprocessor fpWithStopWords = new FilePreprocessor();
		
		System.out.println("****************************************************");
		System.out.println("processing the training data set");
		fpWithStopWords.processData(trainingDataDir);
		
		System.out.println("processing of training data is completed");
		System.out.println("**************************************************");
		
		System.out.println();
		System.out.println("**************************************************");
		System.out.println("SPAM Filter using Naive Bayes Classifier (With stop words) ");
		NaiveBayesClassifier nbc = new NaiveBayesClassifier(fpWithStopWords, testDataDir);
		nbc.runClassifier(spam);
		System.out.println("Spam Accuracy: " + nbc.computeAccuracy(true) + "%");
		
		nbc.resetSpamAndHamCounts();
		nbc.runClassifier(ham);
		System.out.println("Ham Accuracy: " + nbc.computeAccuracy(false) + "%");
		System.out.println("****************************************************");
		
		FilePreprocessor fpWithoutStopWords = new FilePreprocessor();
		fpWithoutStopWords.enableStopWordRemoval();
		fpWithoutStopWords.processData(trainingDataDir);
		
		System.out.println("SPAM Filter using Naive Bayes Classifier (Without stop words) ");
		NaiveBayesClassifier nbc1 = new NaiveBayesClassifier(fpWithoutStopWords, testDataDir);
		nbc1.runClassifier(spam);
		System.out.println("Spam Accuracy: " + nbc1.computeAccuracy(true) + "%");
		
		nbc1.resetSpamAndHamCounts();
		nbc1.runClassifier(ham);
		System.out.println("Ham Accuracy: " + nbc1.computeAccuracy(false) + "%");
		System.out.println("****************************************************");
		
	}

}
