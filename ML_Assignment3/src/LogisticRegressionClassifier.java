import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class LogisticRegressionClassifier {
	
	private static final int iterations = 300;
	private static final double learningRate = 0.0001;
	private static final double regularizationParameter = 0.45;
	private static final String spam = "spam";
	private static final String ham = "ham";
	private Input input;
	private FilePreprocessor filePreprocessor;
	private ArrayList<String> hamFileList;
	private ArrayList<String> spamFileList;
	private HashMap<String, HashMap<String, Integer>> hamFileTokenMap;
	private HashMap<String, HashMap<String, Integer>> spamFileTokenMap;
	private Set<String> tokenSet;
	private ArrayList<String> uniqueTokenList;
	private ArrayList<FileStats> fileStatsList;
	private double[] weights;
	private double[] probabilities;
	private double[] errorWeights;
	private int testSpamCount;
	private int testHamCount;
	private double spamAccuracy;
	private double hamAccuracy;
	
	public LogisticRegressionClassifier(Input input, FilePreprocessor filePreprocessor) {
		this.input = input;
		this.filePreprocessor = filePreprocessor;
		hamFileTokenMap = new HashMap<>();
		spamFileTokenMap = new HashMap<>();
		tokenSet = new HashSet<>();
		fileStatsList = new ArrayList<>();
		testSpamCount = 0;
		testHamCount = 0;
	}
	
	public void featureExtractor() throws IOException {
		
		for(String file : hamFileList) {
			String fileContents = FileUtil.readTextFromFile(input.getTrainingDataDir() + ham + File.separator +  file);
			ArrayList<String> hamTokens = filePreprocessor.tokenizer(fileContents);
			
			hamFileTokenMap.put(file, new HashMap<String, Integer>());
			for(String word : hamTokens) {
				tokenSet.add(word);
				
				if(hamFileTokenMap.get(file).containsKey(word)) {
					hamFileTokenMap.get(file).put(word, hamFileTokenMap.get(file).get(word) + 1);
				} else {
					hamFileTokenMap.get(file).put(word, 1);
				}
			}
		}
		
		for(String file : spamFileList) {
			String fileContents = FileUtil.readTextFromFile(input.getTrainingDataDir() + spam + File.separator + file);
			ArrayList<String> spamTokens = filePreprocessor.tokenizer(fileContents);
			
			spamFileTokenMap.put(file, new HashMap<String, Integer>());
			for(String word : spamTokens) {
				tokenSet.add(word);
				
				if(spamFileTokenMap.get(file).containsKey(word)) {
					spamFileTokenMap.get(file).put(word, spamFileTokenMap.get(file).get(word) + 1);
				} else {
					spamFileTokenMap.get(file).put(word, 1);
				}
			}
		}
		
		//System.out.println(spamFileTokenMap);
		//System.out.println(hamFileTokenMap);
		
		putTokenMapInFileStatsList(hamFileTokenMap, ham);
		putTokenMapInFileStatsList(spamFileTokenMap, spam);
		
		uniqueTokenList = new ArrayList<String>(tokenSet);
		uniqueTokenList.add(0, "x0");
		
		//System.out.println(uniqueTokenList);
		
		weights = new double[uniqueTokenList.size()];
		constructWeightArray();
		
		probabilities = new double[spamFileList.size() + hamFileList.size()];
		
		for(int i = 0; i < iterations; i++) {
			computeProbabilities();
			calculateErrorWeights();
			updateWeights();
		}
	}
	
	public void updateWeights() {
		for(int i = 0; i < errorWeights.length; i++) {
			weights[i] = weights[i] + learningRate * (errorWeights[i] - (regularizationParameter * weights[i]));
		}
	}
	
	public void calculateErrorWeights() {
		errorWeights = new double[uniqueTokenList.size()];
		
		for(int i = 0; i < weights.length; i++) {
			for(int j = 0; j < fileStatsList.size(); j++) {
				String token = uniqueTokenList.get(i);
				if(fileStatsList.get(j).getTokenMap().containsKey(token)) {
					errorWeights[i] = errorWeights[i] + fileStatsList.get(j).getTokenMap().get(token) * (fileStatsList.get(j).getCategory() - probabilities[j]);
				}
			}
		}
	}
	
	public void computeProbabilities() {
		for(int i = 0; i < fileStatsList.size(); i++) {
			probabilities[i] = calculateProbability(fileStatsList.get(i));
		}
	}
	
	public double calculateProbability(FileStats fileStats) {
		double hypo = 0;
		
		for(int i = 1; i < uniqueTokenList.size(); i++) {
			if(fileStats.getTokenMap().containsKey(uniqueTokenList.get(i))) {
				hypo += weights[i] * fileStats.getTokenMap().get(uniqueTokenList.get(i));
			}
		}
		
		hypo += weights[0];
		double exp = Math.exp(-hypo);
		
		double prob = (double) 1 / (1 + exp);
		
		return prob;
	}
	
	public void constructWeightArray() {
		for(int i = 0; i < uniqueTokenList.size(); i++) {
			weights[i] = Math.random();
		}
	}
	
	public void putTokenMapInFileStatsList(HashMap<String, HashMap<String, Integer>> fileTokenMap, String category) {
		int classCategory = 0;
		if(category.equals(ham)) {
			classCategory = 1;
		}
		for (String file : fileTokenMap.keySet()) {
			FileStats fileStats = new FileStats(file, fileTokenMap.get(file), classCategory);
			fileStatsList.add(fileStats);
		}
	}
	
	public void learnData() throws IOException {
		hamFileList = FileUtil.listFiles(input.getTrainingDataDir(), ham);
		spamFileList = FileUtil.listFiles(input.getTrainingDataDir(), spam);
		
		featureExtractor();
	}
	
	public void classifyTestData(String testFile) throws IOException {
		HashMap<String, Integer> testTokenMap = new HashMap<String, Integer>();
		
		String fileContents = FileUtil.readTextFromFile(testFile);
		ArrayList<String> tokens = filePreprocessor.tokenizer(fileContents);
		
		for(String token : tokens) {
			Integer count;
			if((count = testTokenMap.get(token)) == null) {
				testTokenMap.put(token, 1);
			} else {
				testTokenMap.put(token, count + 1);
			}
		}
		
		double hypothesis = 0.0d;
		for(String token : testTokenMap.keySet()) {
			int idx = uniqueTokenList.indexOf(token);
			if(idx > 0) {
				double weight = weights[idx];
				int count = testTokenMap.get(token);
				hypothesis += weights[0] + count * weight;
			}
		}
		
		if(hypothesis > 0.0) {
			testHamCount++;
		} else {
			testSpamCount++;
		}
			
		
	}
	
	public void classifyHamTestData() throws IOException {
		ArrayList<String> testHamFileList = FileUtil.listFiles(input.getTestDataDir(), ham);
		for(String file : testHamFileList) {
			classifyTestData(input.getTestDataDir() + ham + File.separator + file);
		}
		
		hamAccuracy = (double) testHamCount / (testHamCount + testSpamCount);
		System.out.println("Ham Accuracy " + hamAccuracy * 100 + "%");
		resetSpamAndHamCounts();
	}
	
	public void classifySpamTestData() throws IOException {
		ArrayList<String> testSpamFileList = FileUtil.listFiles(input.getTestDataDir(), spam);
		for(String file : testSpamFileList) {
			classifyTestData(input.getTestDataDir() + spam + File.separator + file);
		}
		
		spamAccuracy = (double) testSpamCount / (testHamCount + testSpamCount);
		System.out.println("Spam Accuracy " + spamAccuracy * 100 + "%");
		resetSpamAndHamCounts();
	}
	
	public void resetSpamAndHamCounts() {
		testHamCount = 0;
		testSpamCount = 0;
	}

}
