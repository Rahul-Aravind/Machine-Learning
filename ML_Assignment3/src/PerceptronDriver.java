import java.io.IOException;

public class PerceptronDriver{
	
	public static void main(String args[]) throws IOException {
		
		Input ip = new Input();
		
		System.out.println("****************************************************");
		System.out.println("processing the training data set");
			
		System.out.println("*****************************************************");
		
		System.out.println();
		System.out.println("SPAM Filter using Perceptron classifier (With stop words)");
		FilePreprocessor pcFilePreprocessor = new FilePreprocessor();
		PerceptronClassifier pc = new PerceptronClassifier(ip, pcFilePreprocessor);
		pc.learnData();
		pc.classifyHamTestData();
		pc.classifySpamTestData();
		System.out.println("*****************************************************");
		
		System.out.println();
		System.out.println("SPAM Filter using Perceptron classifier (Without stop words)");
		
		FilePreprocessor pcFilePreprocessorWithoutStopWords = new FilePreprocessor();
		pcFilePreprocessorWithoutStopWords.enableStopWordRemoval();
		PerceptronClassifier pcWithoutStopWords = new PerceptronClassifier(ip, pcFilePreprocessorWithoutStopWords);
		pcWithoutStopWords.learnData();
		pcWithoutStopWords.classifyHamTestData();
		pcWithoutStopWords.classifySpamTestData();
		
		System.out.println("*****************************************************");
		
	}

}
