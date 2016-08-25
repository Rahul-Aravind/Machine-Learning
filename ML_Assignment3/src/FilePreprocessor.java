import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FilePreprocessor {
	
	private static final String spam = "spam";
	private static final String ham = "ham";
	private Input input;
	private String trainingDataDir;
	private int spamTokenCount;
	private int hamTokenCount;
	private ArrayList<String> spamTokenList;
	private ArrayList<String> hamTokenList;
	private HashMap<String, Integer> spamTokenMap;
	private HashMap<String, Integer> hamTokenMap;
	private Set<String> tokenSet;
	private double spamPriorProb;
	private double hamPriorProb;
	private int spamFileCount;
	private int hamFileCount;
	private HashMap<String, HashMap<String, Double>> posteriorMap;
	private boolean removeStopWord = false;
	private ArrayList<String> stopWords;
	
	public FilePreprocessor() {
		input = new Input();
		trainingDataDir = input.getTrainingDataDir();
		input.getTestDataDir();
		spamTokenList = new ArrayList<>();
		hamTokenList = new ArrayList<>();
		spamTokenMap = new HashMap<>();
		hamTokenMap = new HashMap<>();
		tokenSet = new HashSet<>();
		posteriorMap = new HashMap<>();
		spamTokenCount = 0;
		hamTokenCount = 0;
		spamFileCount = 0;
		hamFileCount = 0;
		stopWords = new ArrayList<>();
	}
	
	public void collectStopWords() throws IOException {
		String text = FileUtil.readTextFromFile(input.getStopWordFile());
		String[] stopWordsArray = text.split(" ");

		for (String s : stopWordsArray)
			stopWords.add(s);
	}
	
	public void enableStopWordRemoval() throws IOException {
		removeStopWord = true;
		
		if(removeStopWord) {
			collectStopWords();
		}
	}
	
	public String EmailPruner(String fileContents) throws IOException {

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
	
	public ArrayList<String> tokenizer(String fileContents) throws IOException {
		
		
		
		String content = EmailPruner(fileContents);
		String[] listOfWords = content.split("\\s+|\\/|\\\\|\\-|,");
		ArrayList<String> wordArrayList = new ArrayList<String>();
		
		for (String word : listOfWords) {
			
			if(removeStopWord) {
				if(!stopWords.contains(word)) {
					wordArrayList.add(word);
				}
			} else {
				wordArrayList.add(word);
			}
		}

		return wordArrayList;
	}
	
	public void extractTokens(ArrayList<String> fileList, String category) throws IOException {
		
		for(String file : fileList) {
			String fileContents = FileUtil.readTextFromFile(trainingDataDir + category + "/" + file);
			ArrayList<String> tokenList = tokenizer(fileContents);
			
			if(category.equals(spam)) {
				for(String token : tokenList) {
					spamTokenCount = spamTokenCount + 1;
					spamTokenList.add(token);
					
					Integer count;
					if((count = spamTokenMap.get(token)) == null) {
						spamTokenMap.put(token, 1);
					} else {
						spamTokenMap.put(token, count + 1);
					}
				}
			}
			else {
				for(String token : tokenList) {
					hamTokenCount = hamTokenCount + 1;
					hamTokenList.add(token);
					
					Integer count;
					if((count = hamTokenMap.get(token)) == null) {
						hamTokenMap.put(token, 1);
					} else {
						hamTokenMap.put(token, count + 1);
					}
				}
			}
		}
		
		if(category.equals(spam)) {
			tokenSet.addAll(spamTokenList);
		} else {
			tokenSet.addAll(hamTokenList);
		}
	}
	
	public void computePriorProbability(boolean isSpam) {
		if(isSpam) {
			spamPriorProb = (double) spamFileCount / (spamFileCount + hamFileCount);
		} else {
			hamPriorProb = (double) hamFileCount / (hamFileCount + spamFileCount);
		}
	}
	
	public void computePosteriorProbability(HashMap<String, Integer> tokenMap, ArrayList<String> tokenList, String category) {
		int tokenCount = tokenList.size();
		int uniqueTokenCount = tokenSet.size();
		
		HashMap<String, Double> posProbMap = new HashMap<>();
		for(String token : tokenSet) {
			Integer count;
			double posProb;
			if((count = tokenMap.get(token)) == null) {
				count = 0;
				posProb = (double) (count + 1) / (double) (tokenCount + uniqueTokenCount);
				posProbMap.put(token, posProb);
			} else {
				posProb = (double) (count + 1) / (double) (tokenCount + uniqueTokenCount);
				posProbMap.put(token, posProb);
			}
		}
		
		posteriorMap.put(category, posProbMap);
	}
	
	public HashMap<String, HashMap<String, Double>> processData(String trainingDataDir) throws IOException {
		
		ArrayList<String> spamFileList = FileUtil.listFiles(trainingDataDir, spam);
		ArrayList<String> hamFileList = FileUtil.listFiles(trainingDataDir, ham);
		
		spamFileCount = spamFileList.size();
		hamFileCount = hamFileList.size();
		
		//Token extraction
		extractTokens(spamFileList, spam);
		extractTokens(hamFileList, ham);
		
		//compute prior probability
		computePriorProbability(true);
		computePriorProbability(false);
		
		// compute posterior probability
		computePosteriorProbability(spamTokenMap, spamTokenList, spam);
		computePosteriorProbability(hamTokenMap, hamTokenList, ham);
		
		//System.out.println("Spam token map " + spamTokenMap);
		//System.out.println("Ham token map " + hamTokenMap);
		//System.out.println("Posterior map " + posteriorMap);
		
		return posteriorMap;
	}

	public int getSpamTokenCount() {
		return spamTokenCount;
	}

	public void setSpamTokenCount(int spamTokenCount) {
		this.spamTokenCount = spamTokenCount;
	}

	public int getHamTokenCount() {
		return hamTokenCount;
	}

	public void setHamTokenCount(int hamTokenCount) {
		this.hamTokenCount = hamTokenCount;
	}

	public double getSpamPriorProb() {
		return spamPriorProb;
	}

	public void setSpamPriorProb(double spamPriorProb) {
		this.spamPriorProb = spamPriorProb;
	}

	public double getHamPriorProb() {
		return hamPriorProb;
	}

	public void setHamPriorProb(double hamPriorProb) {
		this.hamPriorProb = hamPriorProb;
	}

	public HashMap<String, HashMap<String, Double>> getPosteriorMap() {
		return posteriorMap;
	}

	public void setPosteriorMap(HashMap<String, HashMap<String, Double>> posteriorMap) {
		this.posteriorMap = posteriorMap;
	}

	public Set<String> getTokenSet() {
		return tokenSet;
	}

	public void setTokenSet(Set<String> tokenSet) {
		this.tokenSet = tokenSet;
	}
	
	

}
