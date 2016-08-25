import java.io.IOException;

public class LRDriver {
	
	public static void main(String args[]) throws IOException {
		
		Input ip = new Input();
		
		System.out.println("****************************************************");
		System.out.println("processing the training data set");
		System.out.println();
		System.out.println("SPAM Filter using Logistic Regression classifier (With stop words)");
		FilePreprocessor lrcFilePreprocessor = new FilePreprocessor();
		LogisticRegressionClassifier lrc = new LogisticRegressionClassifier(ip, lrcFilePreprocessor);
		lrc.learnData();
		lrc.classifyHamTestData();
		lrc.classifySpamTestData();
		System.out.println("*****************************************************");
		
		System.out.println();
		System.out.println("SPAM Filter using Logistic Regression classifier (Without stop words)");
		
		FilePreprocessor lrcFilePreprocessorWithoutStopWords = new FilePreprocessor();
		lrcFilePreprocessorWithoutStopWords.enableStopWordRemoval();
		LogisticRegressionClassifier lrcWithoutStopWords = new LogisticRegressionClassifier(ip, lrcFilePreprocessorWithoutStopWords);
		lrcWithoutStopWords.learnData();
		lrcWithoutStopWords.classifyHamTestData();
		lrcWithoutStopWords.classifySpamTestData();
		
		System.out.println("*****************************************************");
		
	}

}
