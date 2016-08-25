import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FilePreprocessor {

	HashMap<String, HashMap<String, Double>> posteriorProb = new HashMap<String, HashMap<String, Double>>();

	int spamTokenCount;
	int hamTokenCount;
	int totalHamCount;
	int totalSpamCount;

	double hamPriorProb;
	double spamPriorProb;

	HashMap<String, Integer> SpamWordsMap = new HashMap<String, Integer>();
	HashMap<String, Integer> HamWordsMap = new HashMap<String, Integer>();
	ArrayList<String> spamTokenList = new ArrayList<String>();
	ArrayList<String> hamTokenList = new ArrayList<String>();
	Set<String> distinctFeature = new HashSet<String>();

	static ArrayList<String> stopWordsList = new ArrayList<String>();
	String stopwordsFile = new File("").getAbsolutePath() + "/stopwords.txt";
	String trainData = new File("").getAbsolutePath() + "/train/";
	static boolean remove = false;

	public Set<String> getDistinctFeatureList() {
		return distinctFeature;
	}

	public static String pruneEmail(String fileContents) throws IOException {
		// replace all the 's with ""
		fileContents = fileContents.replaceAll("'s", "");

		// replace all special characters with ""
		fileContents = fileContents.replaceAll("[^\\w .]", "");

		// replace all digits with ""
		fileContents = fileContents.replaceAll("[0-9]+", "");

		// Remove all the '.' at the end of the line and replace it with space
		fileContents = fileContents.replaceAll("\\.", " ");

		return fileContents;

	}

	public void loadStopWords() throws FileNotFoundException, IOException {
		String stopWords = FileUtil.readTextFromFile(stopwordsFile);
		String[] tokens = stopWords.split(" ");
		stopWordsList = new ArrayList<String>();

		for (String s : tokens)
			stopWordsList.add(s);
	}

	public double spamPriorProbability() {
		spamPriorProb = (double) totalSpamCount / (totalSpamCount + totalHamCount);
		return spamPriorProb;
	}

	public double hamPriorProbability() {
		hamPriorProb = (double) totalHamCount / (totalSpamCount + totalHamCount);
		return hamPriorProb;
	}

	public static ArrayList<String> tokenize(String fileContents) throws IOException {

		String contents = pruneEmail(fileContents);
		String[] tokens = contents.split("\\s+|\\/|\\\\|\\-|,");
		ArrayList<String> words = new ArrayList<String>();
		for (String token : tokens) {
				if(remove && !stopWordsList.contains(token)) words.add(token);
				else words.add(token);
		}
		return words;
	}

	

	void constructFeatureMap(ArrayList<String> files, String className) throws IOException, FileNotFoundException {
		ArrayList<String> tokenWords = new ArrayList<String>();

		for (int i = 0; i < files.size(); i++) {
			String fileContents = FileUtil.readTextFromFile(trainData + className + File.separator + files.get(i));
			tokenWords = tokenize(fileContents);

			if (className.equalsIgnoreCase("ham")) {
				for (String s : tokenWords) {
					hamTokenCount++;
					hamTokenList.add(s);

					if (HamWordsMap.containsKey(s)) {
						HamWordsMap.put(s, HamWordsMap.get(s) + 1);
					} else {
						HamWordsMap.put(s, 1);
					}
				}
			} else {
				for (String s : tokenWords) {
					spamTokenCount++;
					spamTokenList.add(s);
					if (SpamWordsMap.containsKey(s)) {
						SpamWordsMap.put(s, SpamWordsMap.get(s) + 1);
					} else {
						SpamWordsMap.put(s, 1);
					}
				}
			}
		}

		if (className.equals("ham")) {
			distinctFeature.addAll(hamTokenList);
		} else {
			distinctFeature.addAll(spamTokenList);
		}
	}

	public HashMap<String, HashMap<String, Double>> getPosProbMap() {
		return posteriorProb;
	}
	
	void calculatePosteriorProbability(HashMap<String, Integer> token, String className, ArrayList<String> words,
			Set<String> distinctWords) {

		Integer wordsLen = words.size();
		Integer distinctLen = distinctWords.size();
		Integer occurrences = 0;
		double posProb = 0.0d;

		HashMap<String, Double> posProbMap = new HashMap<String, Double>();
		for (String s : distinctWords) {
			if (token.containsKey(s)) {
				occurrences = token.get(s);
				posProb = (double) (occurrences + 1) / (double) (wordsLen + distinctLen);
				posProbMap.put(s, posProb);
			} else {
				occurrences = 0;
				posProb = (double) (occurrences + 1) / (double) (wordsLen + distinctLen);
				posProbMap.put(s, posProb);
			}
		}

		posteriorProb.put(className, posProbMap);
	}

	public HashMap<String, HashMap<String, Double>> learnTrainingData(String trainingData, boolean remove) throws FileNotFoundException, IOException {

		trainData = trainingData;
		ArrayList<String> hamFiles = new ArrayList<String>();
		ArrayList<String> spamFiles = new ArrayList<String>();
		
		if(remove) {
			loadStopWords();
		}

		hamFiles = FileUtil.getFilesList(trainData, "ham");
		totalHamCount = hamFiles.size();

		spamFiles = FileUtil.getFilesList(trainData, "spam");
		totalSpamCount = spamFiles.size();

		constructFeatureMap(hamFiles, "ham");
		constructFeatureMap(spamFiles, "spam");

		hamPriorProbability();
		spamPriorProbability();

		calculatePosteriorProbability(HamWordsMap, "ham", hamTokenList, distinctFeature);
		calculatePosteriorProbability(SpamWordsMap, "spam", spamTokenList, distinctFeature);
		return getPosProbMap();
	}

}
