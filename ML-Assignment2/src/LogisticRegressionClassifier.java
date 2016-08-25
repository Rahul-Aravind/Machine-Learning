import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class LogisticRegressionClassifier {

	public static String trainingData = new File("").getAbsolutePath() + "/train/";
	public static String testData = new File("").getAbsolutePath() + "/test/";
	public static String hamTrainData = new File("").getAbsolutePath() + "/train/" + "ham/";
	public static String spamTrainData = new File("").getAbsolutePath() + "/train/" + "spam/";
	public static String hamTestData = new File("").getAbsolutePath() + "/test/" + "ham/";
	public static String spamTestData = new File("").getAbsolutePath() + "/test/" + "spam/";

	public static HashMap<String, HashMap<String, Integer>> hamTokenMap;
	public static HashMap<String, HashMap<String, Integer>> spamTokenMap;
	public static ArrayList<FileStats> fileStats;
	public static Set<String> featureSet;
	public static ArrayList<String> featureList;
	public static int hamDocCount = 0;
	public static int spamDocCount = 0;
	public static int totalDocCount = 0;

	public static double[] featureWeights;
	public static double[] prob;
	public static double[] deltaWeightArray;

	public static double accuracy = 0.0;
	public static double spamCount = 0.0;
	public static double hamCount = 0.0;
	public static HashMap<String, Integer> testTokenMap;
	public static HashMap<String, HashMap<String, Integer>> fileNameTokenCountMap;

	public static int iter = 150;
	public static double learningRate = 0.25;
	public static double regularizationParameter = 0.1;

	public static Set<String> buildWordSet(ArrayList<String> words) {
		Set<String> wordSet = new HashSet<String>();
		for (String s : words) {
			wordSet.add(s);
		}
		return wordSet;
	}

	public static void executeLogisticRegression() {

		computeProb();
		calculateDerivativeWeights();
		updateWeights();
	}

	public static void constructFeatureMap(ArrayList<String> hamFileList, ArrayList<String> spamFileList)
			throws FileNotFoundException, IOException {
		hamDocCount = hamFileList.size();
		spamDocCount = spamFileList.size();
		totalDocCount = hamDocCount + spamDocCount;

		ArrayList<String> words = new ArrayList<>();
		ArrayList<String> hamWords = new ArrayList<>();
		ArrayList<String> spamWords = new ArrayList<>();

		for (String file : hamFileList) {
			hamTokenMap.put(file, new HashMap<String, Integer>());
			hamWords = FilePreprocessor.tokenize(FileUtil.readTextFromFile(hamTrainData + file));

			words.addAll(hamWords);
			for (String word : hamWords) {
				if (hamTokenMap.get(file).containsKey(word)) {
					hamTokenMap.get(file).put(word, (hamTokenMap.get(file).get(word) + 1));
				} else {
					hamTokenMap.get(file).put(word, 1);
				}
			}
		}

		for (String file : spamFileList) {
			spamTokenMap.put(file, new HashMap<String, Integer>());
			spamWords = FilePreprocessor.tokenize(FileUtil.readTextFromFile(spamTrainData + file));

			words.addAll(spamWords);
			for (String word : spamWords) {
				if (spamTokenMap.get(file).containsKey(word)) {
					spamTokenMap.get(file).put(word, (spamTokenMap.get(file).get(word) + 1));
				} else {
					spamTokenMap.get(file).put(word, 1);
				}
			}
		}
		
		//System.out.println(spamTokenMap);
		//System.out.println(hamTokenMap);

		featureSet = buildWordSet(words);
		featureList = new ArrayList<String>(featureSet);
		featureList.add(0, "XO");
		
		System.out.println(featureList);
		constructFileClassList(hamTokenMap, 1);
		constructFileClassList(spamTokenMap, 0);

		featureWeights = new double[featureList.size()];
		setWeightsForFeatures(featureList.size());
		prob = new double[totalDocCount];

		for (int i = 0; i < iter; i++) {
			executeLogisticRegression();
		}
	}
	
	public void execute() throws FileNotFoundException, IOException {
		System.out.println("Learning model parameters.......");
		System.out.println("********************************");

		ArrayList<String> hamFileList = FileUtil.getFilesList(trainingData, "ham");
		ArrayList<String> spamFileList = FileUtil.getFilesList(trainingData, "spam");

		fileStats = new ArrayList<FileStats>();
		featureSet = new HashSet<String>();
		hamTokenMap = new HashMap<String, HashMap<String, Integer>>();
		spamTokenMap = new HashMap<String, HashMap<String, Integer>>();
		constructFeatureMap(hamFileList, spamFileList);

		calculateHamAccuracy();
		calculateSpamAccuracy();
	}

	public static void setWeightsForFeatures(int vocabSize) {

		for (int i = 0; i < vocabSize; i++) {
			featureWeights[i] = Math.random();
		}
	}

	public static void constructFileClassList(HashMap<String, HashMap<String, Integer>> fileNameClassTokenCountMap,
			int classValue) {
		fileNameTokenCountMap = new HashMap<String, HashMap<String, Integer>>();
		for (String s : fileNameClassTokenCountMap.keySet()) {
			FileStats instance = new FileStats(s, fileNameClassTokenCountMap.get(s), classValue);
			fileStats.add(instance);
		}
	}

	public static double calculateExponentiation(FileStats instance) {
		double z = 0.0;
		for (int i = 1; i < featureList.size(); i++) {
			if (instance.getTokenCountMap().containsKey(featureList.get(i))) {
				z += featureWeights[i] * instance.getTokenCountMap().get(featureList.get(i));
			} else {
				z += 0;
			}
		}
		z += featureWeights[0];
		return Math.exp(-z);
	}

	public static double computeProb(double exp) {
		double prob = 0.0;
		prob = (double) 1.0 / (1.0 + exp);
		return prob;
	}

	public static void computeProb() {
		for (int m = 0; m < fileStats.size(); m++) {
			prob[m] = computeProb(calculateExponentiation(fileStats.get(m)));
		}
	}

	public static void calculateDerivativeWeights() {
		deltaWeightArray = new double[featureList.size()];
		for (int i = 0; i < featureWeights.length; i++) {
			for (int j = 0; j < fileStats.size(); j++) {
				if (fileStats.get(j).getTokenCountMap().get(featureList.get(i)) != null)
					deltaWeightArray[i] = deltaWeightArray[i]
							+ fileStats.get(j).getTokenCountMap().get(featureList.get(i))
									* (fileStats.get(j).getClassValue() - prob[j]);
			}
		}
	}

	static void updateWeights() {
		for (int i = 0; i < deltaWeightArray.length; i++) {
			featureWeights[i] = featureWeights[i]
					+ learningRate * (deltaWeightArray[i] - (regularizationParameter * featureWeights[i]));
		}
	}

	public static void runClassification(String path, String classVal) throws FileNotFoundException, IOException {
		testTokenMap = new HashMap<String, Integer>();

		ArrayList<String> testTokens = null;
		testTokens = FilePreprocessor.tokenize(FileUtil.readTextFromFile(path));

		for (String word : testTokens) {
			if (testTokenMap.containsKey(word)) {
				testTokenMap.put(word, (testTokenMap.get(word) + 1));
			} else {
				testTokenMap.put(word, 1);
			}
		}

		double classifierValue = 0.0d;
		for (String s : testTokenMap.keySet()) {
			if (featureList.indexOf(s) > 0) {
				int count = testTokenMap.get(s);
				double weight = featureWeights[featureList.indexOf(s)];
				classifierValue += featureWeights[0] + count * weight;
			}
		}
		if (classifierValue > 0.0) {
			hamCount++;
		} else {
			spamCount++;
		}

		if (classVal.equalsIgnoreCase("spam"))
			accuracy = (double) spamCount / (hamCount + spamCount);
		else
			accuracy = (double) hamCount / (hamCount + spamCount);

	}

	public void calculateSpamAccuracy() throws FileNotFoundException, IOException {
		ArrayList<String> testSpamFiles = FileUtil.getFilesList(testData, "spam");
		for (String s : testSpamFiles)
			runClassification(spamTestData + s, "spam");

		System.out.println("Accuracy for Spam ");
		System.out.printf("%.4f", accuracy * 100);
		System.out.print(" % " + "\n");

		spamCount = 0.0;
		hamCount = 0.0;
		accuracy = 0.0;
	}

	public void calculateHamAccuracy() throws FileNotFoundException, IOException {
		ArrayList<String> testHamFiles = FileUtil.getFilesList(testData, "ham");
		for (String s : testHamFiles) {
			runClassification(hamTestData + s, "ham");
		}
		
		System.out.println("Accuracy for Ham ");
		System.out.printf("%.4f", accuracy * 100);
		System.out.print(" % " + "\n");

		spamCount = 0.0;
		hamCount = 0.0;
		accuracy = 0.0;
	}

}
