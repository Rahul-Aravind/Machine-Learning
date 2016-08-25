import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class NaiveBayesClassifier {

	String testPath = new File("").getAbsolutePath() + "/test/";
	HashMap<String, HashMap<String, Double>> posProbMap;
	double priorHamProb;
	double priorSpamProb;
	Set<String> distinctFeatures;
	int hamCount = 0;
	int spamCount = 0;

	public NaiveBayesClassifier(HashMap<String, HashMap<String, Double>> posteriorProb, double priorHam,
			double priorSpam, Set<String> featureSet) {
		posProbMap = posteriorProb;
		priorHamProb = priorHam;
		priorSpamProb = priorSpam;
		this.distinctFeatures = featureSet;
	}

	public ArrayList<String> extractTokens(String filename) throws FileNotFoundException, IOException {
		ArrayList<String> tokens = new ArrayList<String>();
		String fileContents;

		fileContents = FileUtil.readTextFromFile(filename);
		ArrayList<String> tokenStrings = FilePreprocessor.tokenize(fileContents);
		for (String s : tokenStrings) {
			// System.out.println(s);
			if (distinctFeatures.contains(s))
				tokens.add(s);
		}
		return tokens;
	}

	public void execute(String className) throws FileNotFoundException, IOException {
		File file = new File(testPath + className);
		String[] fileNames = file.list();
		for (String fileName : fileNames) {
			ArrayList<String> tokens = extractTokens(testPath + className + "/" + fileName);
			classify(tokens, posProbMap, priorHamProb, priorSpamProb);
		}
		
		System.out.println("SC " +spamCount);
		System.out.println("HC " + hamCount);
	}

	public void classify(ArrayList<String> tokens, HashMap<String, HashMap<String, Double>> cMap, double priorProbHam,
			double priorProbSpam) {
		double[] score = new double[2];
		String hamClass = "ham";
		String spamClass = "spam";

		score[0] = Math.log(priorProbHam);
		score[1] = Math.log(priorProbSpam);

		for (String s : tokens) {
			score[0] += Math.log(cMap.get(hamClass).get(s));
			score[1] += Math.log(cMap.get(spamClass).get(s));
		}

		if (score[0] >= score[1]) {
			hamCount++;
		} else {
			spamCount++;
		}
	}

	public double calculateSpamAccuracy() {
		return (double) spamCount * 100 / (hamCount + spamCount);
	}

	public double calculateHamAccuracy() {
		return (double) hamCount * 100 / (hamCount + spamCount);
	}

}
