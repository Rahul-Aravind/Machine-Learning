import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class NaiveBayesClassifier {
	
	private FilePreprocessor trainDataFilePreprocessor;
	private FilePreprocessor testDataFilePreprocessor;
	private String testDataDir;
	private int testHamCount;
	private int testSpamCount;
	private static final String spam = "spam";
	private static final String ham = "ham";
	
	public NaiveBayesClassifier(FilePreprocessor filePreprocessor, String testDataDir) {
		this.trainDataFilePreprocessor = filePreprocessor;
		this.testDataFilePreprocessor = new FilePreprocessor();
		this.testDataDir = testDataDir;
		testHamCount = 0;
		testSpamCount = 0;
	}
	
	public ArrayList<String> extractTokens(String file) throws IOException {
		ArrayList<String> tokens = new ArrayList<>();
		Set<String> tokenSet = trainDataFilePreprocessor.getTokenSet();
		
		String fileContents = FileUtil.readTextFromFile(file);
		ArrayList<String> tokenList = testDataFilePreprocessor.tokenizer(fileContents);
		
		for(String token : tokenList) {
			if(tokenSet.contains(token)) {
				tokens.add(token);
			}
		}
		
		return tokens;
	}
	
	public void classify(ArrayList<String> tokens) {
		
		HashMap<String, HashMap<String, Double>> posProbMap = trainDataFilePreprocessor.getPosteriorMap();
		double priorHamProb = trainDataFilePreprocessor.getHamPriorProb();
		double priorSpamProb = trainDataFilePreprocessor.getSpamPriorProb();
		
		double totSpamProb;
		double totHamProb;
		
		totSpamProb = Math.log(priorSpamProb);
		totHamProb = Math.log(priorHamProb);
		
		for(String token : tokens) {
			totSpamProb += Math.log(posProbMap.get(spam).get(token));
			totHamProb += Math.log(posProbMap.get(ham).get(token));
		}
		
		if(totHamProb >= totSpamProb ) {
			testHamCount++;
		} else {
			testSpamCount++;
		}
	}
	
	public void runClassifier(String category) throws IOException {
		ArrayList<String> fileList = FileUtil.listFiles(testDataDir, category);
		
		for(String file : fileList) {
			ArrayList<String> tokens = extractTokens(testDataDir + category + File.separator + file);
			classify(tokens);
		}
		
		/*System.out.println("SC " + testSpamCount);
		System.out.println("HC " + testHamCount);*/
	}
	
	public double computeAccuracy(boolean spam) {
		if(spam) {
			return (double) testSpamCount * 100 / (testSpamCount + testHamCount);
		} else {
			return (double) testHamCount * 100 / (testSpamCount + testHamCount);
		}
	}
	
	public void resetSpamAndHamCounts() {
		testHamCount = 0;
		testSpamCount = 0;
	}

}
