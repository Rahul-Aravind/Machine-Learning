import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class Driver {

	public static void main(String[] args) throws FileNotFoundException, IOException {

		String trainingData = new File("").getAbsolutePath() + "/train/";
		String testData = new File("").getAbsolutePath() + "/test/";
		/*FilePreprocessor filePreprocessor = new FilePreprocessor();

		//With stopWords
		
		HashMap<String, HashMap<String, Double>> featureMapWithStopWords = filePreprocessor.learnTrainingData(trainingData, false);
		//System.out.println(featureMapWithStopWords);
		System.out.println(filePreprocessor.hamPriorProbability());
		System.out.println(filePreprocessor.spamPriorProbability());
		Set<String> distinctFeatures = filePreprocessor.getDistinctFeatureList();
		NaiveBayesClassifier nbc = new NaiveBayesClassifier(featureMapWithStopWords, filePreprocessor.hamPriorProbability(), filePreprocessor.spamPriorProbability(), distinctFeatures);

		System.out.println("*******************************************");
		System.out.println("Spam classifier using Naive Bayes (With Stop Words)");
		System.out.println("*******************************************");

		nbc.execute("ham" + "/");
		System.out.println("Accuracy for Ham ");
		System.out.printf("%.4f", nbc.calculateHamAccuracy());
		System.out.print(" % " + "\n");
		nbc = new NaiveBayesClassifier(featureMapWithStopWords, filePreprocessor.hamPriorProbability(),
				filePreprocessor.spamPriorProbability(), distinctFeatures);
		nbc.execute("spam" + "/");
		System.out.println("Accuracy for Spam ");
		System.out.printf("%.4f", nbc.calculateSpamAccuracy());
		System.out.print(" % " + "\n");

		// Without stop words
		HashMap<String, HashMap<String, Double>> featureMapWithoutStopWords = filePreprocessor.learnTrainingData(trainingData, true);
		distinctFeatures = filePreprocessor.getDistinctFeatureList();
		nbc = new NaiveBayesClassifier(featureMapWithoutStopWords, filePreprocessor.hamPriorProbability(),
				filePreprocessor.spamPriorProbability(), distinctFeatures);

		System.out.println("*******************************************");
		System.out.println("Spam classifier using Naive Bayes (Without stopWords) ");
		System.out.println("*******************************************");

		nbc.execute("ham" + "/");
		System.out.println("Accuracy for Hpam ");
		System.out.printf("%.4f", nbc.calculateHamAccuracy());
		System.out.print(" % " + "\n");
		nbc = new NaiveBayesClassifier(featureMapWithoutStopWords, filePreprocessor.hamPriorProbability(),
				filePreprocessor.spamPriorProbability(), distinctFeatures);

		nbc.execute("spam" + "/");
		System.out.println("Accuracy for Spam ");
		System.out.printf("%.4f", nbc.calculateSpamAccuracy());
		System.out.print(" % " + "\n");*/

		LogisticRegressionClassifier lrc = new LogisticRegressionClassifier();
		System.out.println();

		System.out.println("*************************************");
		System.out.println("Spam Classifier using Logistic Regression");
		System.out.println("********************************************");

		lrc.execute();

	}

}
