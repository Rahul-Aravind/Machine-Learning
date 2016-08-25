import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PerceptronClassifier {
	
	private static final int iterations = 500;
	private static final double learningRate = 0.003;
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
	private HashMap<String, Double> weights;
	private int testSpamCount;
	private int testHamCount;
	private double spamAccuracy;
	private double hamAccuracy;
	
	public PerceptronClassifier(Input input, FilePreprocessor filePreprocessor) {
		this.input = input;
		this.filePreprocessor = filePreprocessor;
		hamFileTokenMap = new HashMap<>();
		spamFileTokenMap = new HashMap<>();
		tokenSet = new HashSet<>();
		fileStatsList = new ArrayList<>();
		testSpamCount = 0;
		testHamCount = 0;
		weights = new HashMap<>();
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
		
		//System.out.println("Ham File token map " + hamFileTokenMap);
		//System.out.println("Spam File token map " + spamFileTokenMap);
				
		putTokenMapInFileStatsList(hamFileTokenMap, ham);
		putTokenMapInFileStatsList(spamFileTokenMap, spam);
		
		uniqueTokenList = new ArrayList<String>(tokenSet);
		uniqueTokenList.add(0, "x0");
		
		//System.out.println("Unique token set " + uniqueTokenList);
		
		constructWeightArray();
		
		for(int i = 0; i < iterations; i++) {
			trainWeights();
			//System.out.println("Weights " + weights);
		}
	}
	
	public void updateWeights(FileStats fileStats, double observed) {
		HashMap<String, Integer> tokenMap = fileStats.getTokenMap();
		
		for(String feature : tokenMap.keySet()) {
			double weight = weights.get(feature);
			weight = weight + learningRate * (fileStats.getCategory() - observed) * tokenMap.get(feature);
			weights.put(feature, weight);
		}
		double x_o_fw = weights.get("x0");
		x_o_fw = x_o_fw + learningRate * (fileStats.getCategory() - observed);
		weights.put("x0", x_o_fw);
	}
	
	public void trainWeights() {
		for(int i = 0; i < fileStatsList.size(); i++) {
			double observed = calculateProbability(fileStatsList.get(i));
			updateWeights(fileStatsList.get(i), observed);
		}
	}
	
	public double calculateProbability(FileStats fileStats) {
		double hypo = 0;
		
		for(String feature : fileStats.getTokenMap().keySet()) {
			if(weights.containsKey(feature)) {
				//System.out.println("Feature " + feature + " Weight " + weights.get(feature) + " count " + fileStats.getTokenMap().get(feature));
				hypo = hypo + weights.get(feature) * fileStats.getTokenMap().get(feature);
			} else {
				hypo += 0;
			}
		}
		
		hypo += weights.get("x0");
		//System.out.println("#DEBUG hypothesis " + fileStats.getCategory() + " " + hypo);
		
		if(hypo > 0) {
			hypo = 1;
		} else {
			hypo = 0;
		}
		
		return hypo;
	}
	
	public void constructWeightArray() {
		for(String feature : uniqueTokenList) {
			weights.put(feature, 0.0);
		}
		
	}
	
	public void putTokenMapInFileStatsList(HashMap<String, HashMap<String, Integer>> fileTokenMap, String category) {
		double classCategory = 0;
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
		
		//System.out.println("Ham List size " + hamFileList.size());
		//System.out.println("Spam List size " + spamFileList.size());
		
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
			if(uniqueTokenList.contains(token)) {
				double weight = weights.get(token);
				int count = testTokenMap.get(token);
				hypothesis = hypothesis + count * weight;
			}
		}
		
		hypothesis += weights.get("x0");
		
		//System.out.println("hypo " + hypothesis);
		
		if(hypothesis > 0.0) {
			testHamCount++;
		} else {
			testSpamCount++;
		}
			
		
	}
	
	public void classifyHamTestData() throws IOException {
		//System.out.println("Ham test");
		ArrayList<String> testHamFileList = FileUtil.listFiles(input.getTestDataDir(), ham);
		
		for(String file : testHamFileList) {
			classifyTestData(input.getTestDataDir() + ham + File.separator + file);
		}
		
		hamAccuracy = (double) testHamCount / (testHamCount + testSpamCount);
		System.out.println("Ham Accuracy " + hamAccuracy * 100 + "%");
		resetSpamAndHamCounts();
	}
	
	public void classifySpamTestData() throws IOException {
		//System.out.println("Spam Test ");
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
